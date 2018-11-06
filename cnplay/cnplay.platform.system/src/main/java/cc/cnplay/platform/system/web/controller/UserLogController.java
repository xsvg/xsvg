package cc.cnplay.platform.system.web.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserLog;
import cc.cnplay.platform.domain.UserOperLog;
import cc.cnplay.platform.service.UserLogService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping("/system/log")
public class UserLogController extends AbsController
{
	@Autowired
	private UserService userService;

	@Autowired
	private UserLogService userLogService;

	@Ignore
	@ResponseBody
	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统管理/系统日志", component = "platform.system.view.SystemLog", resource = "/system/log/export*", sort = 80700)
	public DataGrid<UserLog> list(String orgId, String name, String startDate, String endDate)
	{
		DataGrid<UserLog> rst = new DataGrid<UserLog>();
		
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
		{
			//如果时间参数为空时，默认查询当前一个月数据
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	        Calendar c = Calendar.getInstance(); 
	        //开始时间
	        c.setTime(new Date()); 
	        c.add(Calendar.MONTH, -1); 
	        Date m = c.getTime(); 
	        startDate = format.format(m); 
	        //结束时间
	        Date d = new Date();
	        endDate = format.format(d);
		}
		Date startDateTime = null;
		Date endDateTime = null;
		if (StringUtils.isNotEmpty(startDate))
		{
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate))
		{
			endDateTime = DateUtil.dateLess(endDate);
		}
		rst = userLogService.findPageLikeName(startDateTime, endDateTime, this.getSessionUser().getOrganization(), orgId, name, this.getPage(), this.getPageSize());
		return rst;
	}

	@Ignore
	@RequestMapping(value = "/exportSysLog", produces = "application/json")
	@Description("导出系统日志")
	@ResponseBody
	public Json<String> exportSysLog(String orgId, String name, String startDate, String endDate, HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			if (StringUtils.isNotBlank(name))
			{
				name = URLDecoder.decode(URLDecoder.decode(name, "utf-8"), "utf-8");
			}
			Date startDateTime = null;
			Date endDateTime = null;
			if (StringUtils.isNotEmpty(startDate))
			{
				startDateTime = DateUtil.dateGreater(startDate);
			}
			if (StringUtils.isNotEmpty(endDate))
			{
				endDateTime = DateUtil.dateLess(endDate);
			}
			DataGrid<UserLog> rst = userLogService.findPageLikeName(startDateTime, endDateTime, this.getSessionUser().getOrganization(), orgId, name, 1, Constants.expReportMax);
//			if (rst.getSize() > 1)
//			{
//			}
			List<UserLog> lst = rst.getRows();
			List<Map<String, ?>> listMap = new ArrayList<Map<String, ?>>();
			for (UserLog item : lst)
			{
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("username", item.getUsername());
				m.put("name", item.getName()); // 功能名称
				m.put("orgName", item.getOrgName());// 机构名称
				m.put("proceed", item.getProceed());
				m.put("createTimeStr", item.getCreateTimeStr());// 创建时间
				m.put("memo", item.getMemo()); // 备注
				listMap.add(m);
				if (listMap.size() >= 50000)
				{
					break;
				}
			}
			this.expAttachment(request, response, "系统日志", listMap, "/static/report/SystemLog.jrxml");
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
			// rst.NG("导出失败，机构信息被引用！");
		}
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/loadById")
	// @RightAnnotation(name = "系统管理/系统日志/单个读取", button = true, sort = 80700)
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
			logger.equals(e.getMessage());
			rst.NG("操作失败，请重试！");
		}
		return rst;

	}

	@RequestMapping(value = "/remove")
	// @RightAnnotation(name = "系统管理/系统日志/删除日志", button = true, sort = 80700)
	@ResponseBody
	public Json<String> remove(String id)
	{
		Json<String> rst = new Json<String>();
		try
		{
			userLogService.removeByIds(id.split(separator));
			rst.OK("", "删除成功");
		}
		catch (Exception e)
		{
			logger.equals(e.getMessage());
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/userLogList")
	@RightAnnotation(name = "系统管理/用户管理日志", component = "platform.system.view.UserOperLog", sort = 80399)
	public DataGrid<UserOperLog> userLogList(String name, String orgId, String startDate, String endDate)
	{
		if (StringUtils.isBlank(orgId))
		{
			orgId = this.getSessionUser().getOrganization().getId();
		}
		DataGrid<UserOperLog> rst = new DataGrid<UserOperLog>();
		Date sDate = DateUtil.parseShortDate(startDate);
		Date eDate = DateUtil.parseShortDate(endDate);
		rst = userLogService.findUserLogListPageLikeName(orgId, name, sDate, eDate, this.getPage(), this.getPageSize());
		return rst;
	}

	@RequestMapping(value = "/exportLog", produces = "application/json")
	@Description("导出用户管理日志")
	@ResponseBody
	public Json<String> exportLog(String name, String orgId, String startDate, String endDate, HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			if (StringUtils.isNotBlank(name))
			{
				name = URLDecoder.decode(URLDecoder.decode(name, "utf-8"), "utf-8");
			}
			Date sDate = DateUtil.parseShortDate(startDate);
			Date eDate = DateUtil.parseShortDate(endDate);
			if (StringUtils.isBlank(orgId))
			{
				orgId = this.getSessionUser().getOrganization().getId();
			}
			DataGrid<UserOperLog> rst = userLogService.findUserLogListPageLikeName(orgId, name, sDate, eDate, 1, Constants.expReportMax);
//			if (rst.getSize() > 1)
//			{
//				throw new RuntimeException("导出数据行数超长！应 <=" + Constants.expReportMax);
//			}
			List<UserOperLog> lst = rst.getRows();
			List<Map<String, ?>> listMap = new ArrayList<Map<String, ?>>();
			for (UserOperLog item : lst)
			{
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("name", item.getName()); // 功能名称
				m.put("username", item.getUserName());// 被操作用户
				m.put("orgName", item.getOrgName());// 机构名称
				m.put("createUsername", item.getCreateUsername());// 操作用户
				m.put("checkUsername", item.getCheckUsername());// 复核用户
				m.put("createTimeStr", item.getCreateTimeStr());// 创建时间
				m.put("memo", item.getMemo()); // 备注
				listMap.add(m);
				if (listMap.size() >= 50000)
				{
					break;
				}
			}
			this.expAttachment(request, response, "用户管理日志", listMap, "/static/report/UserOperatorLog.jrxml");
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
			// rst.NG("导出失败，机构信息被引用！");
		}
		return null;
	}

}
