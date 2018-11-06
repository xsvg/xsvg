package cc.cnplay.platform.system.web.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.domain.Identity;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Item;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserFinger;
import cc.cnplay.platform.domain.UserOperLog;
import cc.cnplay.platform.service.FingerService;
import cc.cnplay.platform.service.OrgCfgService;
import cc.cnplay.platform.service.OrganizationService;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.vo.RightVO;
import cc.cnplay.platform.web.controller.AbsController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/system/user")
public class UserController extends AbsController
{
	@Autowired
	private UserService userService;

	@Autowired
	private RightService rightService;

	@Autowired
	private FingerService fingerService;

	@Resource
	private OrgCfgService orgCfgService;

	@Resource
	private OrganizationService orgService;

	@ResponseBody
	@RequestMapping(value = "/listAll")
	@RightAnnotation(name = "系统管理/用户管理", component = "platform.system.view.UserPanel", resource = "/system/user/exportUser", sort = 80300)
	public DataGrid<User> listAll(String query, String orgid)
	{
		DataGrid<User> dg = new DataGrid<User>();
		try
		{
			User user = this.getSessionUser();
//			if (StringUtils.isBlank(orgid))
//			{
//				Organization org = user.getOrganization();
//				if (org != null)
//				{
//					orgid = org.getId();
//				}
//			}
			dg = userService.findByName(user, query, orgid, this.getPage(), this.getPageSize());
		}
		catch (Exception e)
		{
			logger.error(e);
			dg.NG("操作失败，请重试！");
		}
		return dg;
	}

	@ResponseBody
	@RequestMapping(value = "/loadById")
	public Json<User> loadById(String id)
	{
		Json<User> rst = new Json<User>();
		try
		{
			User uf = userService.getUserById(id);
			rst.OK(uf, "");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("操作失败，请重试！");
		}
		return rst;

	}

	@ResponseBody
	@RequestMapping(value = "/updatePwd")
	@RightAnnotation(name = "系统管理/用户管理/重置密码", button = true, sort = 80301)
	public Json<String> updatePwd(String ids)
	{
		User user = this.getSessionUser();
		Json<String> rst = new Json<String>("操作成功");
		if (ids.indexOf(user.getId()) >= 0)
		{
			rst.NG("不允许当前登录用户修改本人信息");
			return rst;
		}
		UserOperLog uol = createUserOperLog();
		userService.updatePwd(ids, uol);
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/updateStatus")
	@RightAnnotation(name = "系统管理/用户管理/状态变更", button = true, sort = 80302)
	public Json<String> updateStatus(String ids, String status)
	{
		Json<String> rst = new Json<String>("操作成功");
		try
		{
			User user = this.getSessionUser();
			if (ids.indexOf(user.getId()) >= 0)
			{
				rst.NG("不允许当前登录用户修改本人信息");
				return rst;
			}
			UserOperLog uol = createUserOperLog();
			userService.updateStatus(ids, status, uol);
		}
		catch (CnplayRuntimeException e)
		{
			rst.NG(e.getMessage());
		}
		catch (Exception e)
		{
			logger.error("", e);
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	// @RequestMapping(value = "/removeUser")
	// @RightAnnotation(name = "系统管理/用户管理/删除用户", button = true, sort = 80302)
	public @ResponseBody Json<String> removeUser(String[] id)
	{
		Json<String> rst = new Json<String>("操作成功");
		try
		{
			User users = this.getSessionUser();

			List<User> userList = userService.findByIds(id);
			List<UserOperLog> logList = new ArrayList<UserOperLog>();
			for (User user : userList)
			{
				if (user.getId().equalsIgnoreCase(users.getId()))
				{
					rst.NG("不允许当前登录用户修改本人信息");
					return rst;
				}
				UserOperLog uol = createUserOperLog();
				uol.setName("用户管理-删除用户");
				uol.setId(Identity.randomID());
				uol.setUser(user);
				logList.add(uol);
			}
			userService.removeAll(userList);
			userService.saveAll(logList);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			rst.NG("删除失败，选择的用户中有操作记录或被引用 ！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/saveUser")
	@RightAnnotation(name = "系统管理/用户管理/新增修改", button = true, resource = "/system/user/loadById", sort = 80300)
	public Json<User> saveUser(User userVO)
	{
		Json<User> rst = new Json<User>();
		try
		{
			User user = this.getSessionUser();
			if (user.getId().equalsIgnoreCase(userVO.getId()))
			{
				rst.NG("不允许当前登录用户修改本人信息");
				return rst;
			}
			UserOperLog uol = createUserOperLog();
			User u = userService.saveUser(userVO, uol);
			rst.OK(u, "");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG(e.getMessage());
		}
		return rst;
	}

	private UserOperLog createUserOperLog()
	{
		UserOperLog uol = new UserOperLog();
		uol.setRemoteUser(getRequesRemoteUser());
		uol.setRemoteAddr(getRequesRemoteAddr());
		uol.setUserCreate(getSessionUser());
		uol.setUserCheck(getCheckUser());
		return uol;
	}

	@ResponseBody
	@RequestMapping(value = "/findRoleByUser")
	public Json<List<Role>> findRoleByUser(String userid)
	{
		Json<List<Role>> rst = new Json<List<Role>>();
		try
		{
			User user = this.getSessionUser();
			if (user.getId().equalsIgnoreCase(userid))
			{
				rst.NG("不允许当前登录用户设置角色");
				return rst;
			}
			else
			{
				List<Role> roleLst = userService.findRoleWithUser(userid, user.getId());
				rst.OK(roleLst, "");
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/findRightByUser")
	public Json<List<RightVO>> findRightByUser(String userid, String id)
	{
		Json<List<RightVO>> rst = new Json<List<RightVO>>();
		try
		{
			List<RightVO> rightLst = rightService.findRightWithUser(userid, id);
			rst.OK(rightLst, "");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/updateRoleRight")
	@RightAnnotation(name = "系统管理/用户管理/设置角色权限", button = true, resource = "/system/user/findRoleByUser;/system/user/findRightByUser", sort = 80304)
	public Json<String> updateRoleRight(String userid, String[] roleids, String[] rightids)
	{
		Json<String> rst = new Json<String>();
		try
		{
			User user = this.getSessionUser();
			if (user.getId().equalsIgnoreCase(userid))
			{
				rst.NG("不允许当前登录用户设置角色");
				return rst;
			}
			if (StringUtils.isBlank(userid))
			{ //
				rst.NG("用户ID不能为空");
				return rst;
			}
			String validate = userService.validateRightReject(roleids);
			if (StringUtils.isNoneEmpty(validate))
			{
				rst.NG(validate);
				return rst;
			}
			UserOperLog uol = createUserOperLog();
			userService.updateRoleRight(userid, roleids, rightids, uol);
			rst.OK("", "角色权限更新成功");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e);
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	@RequestMapping(value = "/fingerList")
	@RightAnnotation(name = "系统管理/用户管理/指纹管理", button = true, sort = 80305, resource = "/system/user/finger*")
	public @ResponseBody List<UserFinger> findUserFingerList(String userid)
	{
		List<UserFinger> fingerList = fingerService.findByUserid(userid);
		return fingerList;
	}

	@ResponseBody
	@RequestMapping(value = "/fingerItemList")
	public Json<List<Item>> fingerItemList()
	{
		Json<List<Item>> rst = new Json<List<Item>>();
		List<Item> rstLst = new ArrayList<Item>();
		try
		{
			rst.OK(rstLst, "");
		}
		catch (Exception e)
		{
			logger.equals(e.getMessage());
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	/*
	 * @RequestMapping(value = "/fingerSave")
	 * 
	 * @RightAnnotation(name = "系统管理/用户管理/采集指纹", button = true, sort = 80306) public @ResponseBody Json<Boolean> fingerSave(String fingerData, String fingerDev, String strees, String userId, String fingerNum) { Json<Boolean> rst = new Json<Boolean>(); try { User user = fingerService.getById(User.class, userId); UserFinger userFinger = new UserFinger(); userFinger.setUser(user); userFinger.setFingerDev(fingerDev); userFinger.setFingerData(fingerData); userFinger.setFingerNum(fingerNum); userFinger.setStress(Boolean.valueOf(strees)); UserOperLog uol = createUserOperLog(); uol.setName("用户管理-采集指纹"); uol.setUser(user); fingerService.save(userFinger); fingerService.save(uol); rst.OK(true, "指纹采集成功！"); } catch (Exception e) { logger.error(e); rst.NG("指纹采集失败，请重试！"); } return rst; }
	 */

	@RequestMapping(value = "/fingerSave")
	@RightAnnotation(name = "系统管理/用户管理/采集指纹", button = true, sort = 80306)
	public @ResponseBody Json<Boolean> fingerSave(String userId, String fingerArr)
	{
		Json<Boolean> rst = new Json<Boolean>();
		ObjectMapper om = new ObjectMapper();
		try
		{
			List<UserFinger> list = om.readValue(fingerArr, new TypeReference<List<UserFinger>>()
			{
			});
			User user = fingerService.getById(User.class, userId);

			// 操作日志
			UserOperLog uol = createUserOperLog();
			uol.setName("用户管理-采集指纹");
			uol.setUser(user);
			fingerService.save(uol);
			fingerService.saveFingers(userId, list);
			rst.OK(true, "指纹采集成功！");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("指纹采集失败，请重试！");
		}
		return rst;
	}

	@RequestMapping(value = "/fingerRemove")
	@RightAnnotation(name = "系统管理/用户管理/删除指纹", button = true, sort = 80307)
	public @ResponseBody Json<String> remove(String id)
	{
		Json<String> rst = new Json<String>();
		try
		{
			String[] ids = id.split(separator);
			for (String fid : ids)
			{
				UserFinger uf = fingerService.getById(fid);
				if (uf != null)
				{
					UserOperLog uol = createUserOperLog();
					uol.setName("用户管理-删除指纹");
					uol.setUser(uf.getUser());
					fingerService.save(uol);
					fingerService.removeById(uf.getId());
				}
			}
			rst.OK("", "用户指纹删除成功！");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("指纹删除失败，请重试！");
		}
		return rst;
	}

	/**
	 * 解析导入数据并界面显示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Throwable
	 */
	@RequestMapping(value = "/loadImportUser")
	@RightAnnotation(name = "系统管理/用户管理/用户导入", button = true, sort = 80308, resource = "/system/user/loadImportUser*")
	@Description("导入用户")
	public @ResponseBody Json<Boolean> loadImportUser(HttpServletRequest request, HttpServletResponse response)
	{
		return new Json<Boolean>(true);
	}

	/**
	 * 解析导入数据并界面显示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Throwable
	 */
	@RequestMapping(value = "/loadImportUserCheck")
	public @ResponseBody String loadImportUserCheck(HttpServletRequest request, HttpServletResponse response) throws Throwable
	{
		@SuppressWarnings("rawtypes")
		Json rst = new Json();
		try
		{
			// String roleConfig = request.getParameter("roleConfig");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html; charset=utf-8");
			UserOperLog uol = createUserOperLog();

			rst = userService.loadImportUser(request, null, uol);
			// rst.OK(rstLst, "");

			ObjectMapper om = new ObjectMapper();
			om.writeValue(response.getOutputStream(), rst);

		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("用户信息解析失败！");
		}
		return null;
	}

	@RequestMapping(value = "/exportUser")
	@Description("导出用户")
	public @ResponseBody Json<String> exportUser(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			String name = request.getParameter("query");
			if (StringUtils.isNotBlank(name))
			{
				name = URLDecoder.decode(URLDecoder.decode(name, "utf-8"), "utf-8");
			}
			String orgId = request.getParameter("orgId");
			List<Map<String, ?>> listMap = userService.findReportData(this.getSessionUser().getOrganization(), orgId, name);
			if (listMap.size() <= Constants.expReportMax)
			{
				this.expAttachment(request, response, "用户导出信息", listMap, "/static/report/UserExportInfo.jrxml");
			}
			else
			{
				writeResponse(response, new CnplayRuntimeException("导出数据行数超长！应 <=" + Constants.expReportMax));
			}
		}
		catch (CnplayRuntimeException e)
		{
			logger.error("导出失败", e);
			writeResponse(response, e);
		}
		catch (Throwable e)
		{
			logger.error("导出失败", e);
			writeResponse(response, new CnplayRuntimeException("导出数据失败，请不要一次导出太多数据！"));
			// rst.NG("导出失败，用户信息被引用！");
		}
		return null;
	}
}
