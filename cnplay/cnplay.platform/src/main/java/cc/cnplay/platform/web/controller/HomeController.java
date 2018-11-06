package cc.cnplay.platform.web.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.spring.service.PasswordEncoderService;
import cc.cnplay.core.vo.Item;
import cc.cnplay.core.vo.ItemInt;
import cc.cnplay.core.vo.Json;
import cc.cnplay.core.vo.TreeChildrenModel;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.annotation.OperateLog;
import cc.cnplay.platform.domain.Area;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.RightType;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.SysConfigName;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.AreaService;
import cc.cnplay.platform.service.AuthenticationService;
import cc.cnplay.platform.service.OrganizationService;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.vo.HomeInfo;
import cc.cnplay.platform.vo.SettingPassword;

@Controller
@RequestMapping(value = "/home")
public class HomeController extends AbsController
{
	/**
	 * 
	 */
	@Autowired
	private UserService userService;

	@Resource
	private RightService rightService;

	@Resource
	private SystemConfigService scService;

	@Resource
	private AuthenticationService authenticationService;

	@Resource
	private OrganizationService organizationService;

	@Resource
	private AreaService areaService;

	@Autowired
	private PasswordEncoderService passwordEncoder;

	@OperateLog(name = "加载首面信息", value = "加载首面信息", memo = "加载首面信息")
	@RequestMapping(value = "/info")
	public @ResponseBody Json<HomeInfo> home()
	{
		Constants.expReportFmt = scService.getByName(Constants.expReportFmtConf, Constants.expReportFmt);
		scService.smsDriverNewInstance();
		HomeInfo homeInfo = new HomeInfo();
		User user = userService.getNormalByUsername(getSessionUsername());
		homeInfo.setUsername(user.getUsername());
		homeInfo.setUserFullname(user.getName());
		homeInfo.setTitle(scService.getByName(SysConfigName.system_title));
		homeInfo.setName(scService.getByName(SysConfigName.system_name));
		homeInfo.setDesktopPanel(scService.getByName(SysConfigName.home_desktop_panel));
		homeInfo.setFontStyle(scService.getByName(SysConfigName.home_font_style));
		homeInfo.setLogoImg(scService.getByName(SysConfigName.home_logo_img));
		homeInfo.setTopbgImg(scService.getByName(SysConfigName.home_logobg_img));
		homeInfo.setLogoutText(scService.getByName(SysConfigName.home_logout_text));
		homeInfo.setLogoutUrl("/logout");
		homeInfo.setNeedModifyPswd(userService.isNeedModifyPwd(user));
		return new Json<HomeInfo>(homeInfo);
	}

	@OperateLog(name = "修改密码", value = "用户修改密码", memo = "描述")
	@RequestMapping(value = "/settingPassword")
	public @ResponseBody Json<Boolean> settingPassword() throws IllegalAccessException, InvocationTargetException
	{

		SettingPassword setting = new SettingPassword();
		BeanUtils.copyProperties(setting, getParameterMap());
//			if (setting.isPasswordEncoder())
//			{
//				String newPswd = setting.getNewpass();
//				String deaultPswd = passwordEncoder.encode(Constants.PASSWORD_DEFAULT);
//				if (StringUtils.equalsIgnoreCase(newPswd, deaultPswd))
//				{
//					Json<Boolean> json = new Json<Boolean>();
//					json.NG("新密码为默认密码,为提高安全请修改密码。");
//					return json;
//				}
//			}
//			else
//			{
//				String newPswd = setting.getNewpass();
//				if (StringUtils.equalsIgnoreCase(newPswd, Constants.PASSWORD_DEFAULT))
//				{
//					Json<Boolean> json = new Json<Boolean>();
//					json.NG("新密码为默认密码,为提高安全请修改密码。");
//					return json;
//				}
//			}
		setting.setUsername(getSessionUsername());
		return userService.settingPassword(setting);
	}

	@Ignore
	@RequestMapping(value = "/disabled")
	public @ResponseBody Json<Boolean> disabled(String url)
	{
		boolean disabled = this.getSessionUser().isDebug() ? false : authenticationService.disabled(this.getSessionUserId(), url);
		return new Json<Boolean>(disabled);
	}

	@Ignore
	@RequestMapping(value = "/menu")
	@ResponseBody
	public Json<List<Right>> menu(String id)
	{
		Json<List<Right>> rst = new Json<List<Right>>();
		try
		{
			List<Right> menuList = null;
			if (this.getSessionUser().isDebug())
			{
				menuList = rightService.findByParentId(id, true);
			}
			else
			{
				menuList = authenticationService.findMenu(id, this.getSessionUserId());
				rightService.removeReject(menuList);
			}
			for (Right right : menuList)
			{
				RightType rightType = RightType.valueOf(right.getType());
				right.setLeaf(rightType != RightType.DIR);
			}
			rst.OK(menuList, "");
		}
		catch (Throwable e)
		{
			logger.error("", e);
			rst.NG("操作失败");
		}
		return rst;
	}

	@Ignore
	@RequestMapping(value = "/status")
	public @ResponseBody Json<List<Item>> status()
	{
		List<Item> statusList = new ArrayList<Item>();
		Item item = new Item();
		item.setId(Status.Normal.name());
		item.setText("正常");
		statusList.add(item);
		item = new Item();
		item.setId(Status.Banned.name());
		item.setText("挂起");
		statusList.add(item);
		item = new Item();
		item.setId(Status.Delete.name());
		item.setText("删除");
		statusList.add(item);
		return new Json<List<Item>>(statusList);
	}

	@OperateLog(name = "插件查询", value = "插件查询", memo = "描述")
	@RequestMapping(value = "/plugins")
	public @ResponseBody Json<List<Plugins>> plugins()
	{
		List<Plugins> plugins = rightService.findAll(Plugins.class);
		return new Json<List<Plugins>>(plugins);
	}

	// 1：用户ID； 2：密码； 4：指纹
	@RequestMapping(value = "/authType")
	@ResponseBody
	@Ignore
	public Json<List<ItemInt>> authType()
	{
		List<ItemInt> statusList = new ArrayList<ItemInt>();
		statusList.add(new ItemInt(0, "系统配置"));
		statusList.add(new ItemInt(3, "用户密码"));
		// statusList.add(new ItemInt(3, "用户ID+密码"));
		// statusList.add(new ItemInt(5, "用户ID+指纹"));
		// statusList.add(new ItemInt(7, "用户ID+密码+指纹"));
		return new Json<List<ItemInt>>(statusList);
	}

	@Ignore
	@RequestMapping(value = "/areaTree")
	@ResponseBody
	public Json<List<Area>> areaTree(String id, String[] areaId)
	{
		List<Area> list = areaService.findByParentId(id);
		areaService.checked(areaId, list);
		return new Json<List<Area>>(list);
	}

	@Ignore
	@RequestMapping(value = "/orgTree")
	@ResponseBody
	public Json<List<Organization>> orgTree(String id, String[] orgId)
	{
		List<Organization> list = organizationService.findByParentId(id);
		organizationService.checked(orgId, list);
		return new Json<List<Organization>>(list);
	}

	@Ignore
	@RequestMapping(value = "/loadUserOrgTree")
	@ResponseBody
	public Json<List<Organization>> loadUserOrgTree(String id, String[] orgId)
	{
		User loginUser = this.getSessionUser();
		List<Organization> list = organizationService.findByParentId(loginUser, id);
		organizationService.checked(orgId, list);
		return new Json<List<Organization>>(list);
	}

	@Ignore
	@RequestMapping(value = "/loadOrgChildrenTree")
	@ResponseBody
	public Json<List<TreeChildrenModel>> loadOrgChildrenTree()
	{
		User loginUser = this.getSessionUser();
		User tmpUser = userService.getById(User.class, loginUser.getId());
		TreeChildrenModel vo = new TreeChildrenModel();
		vo.setId(tmpUser.getOrganization().getId());
		vo.setText(tmpUser.getOrganization().getName());
		vo.setLeaf(false);
		vo.setChecked(true);
		List<Organization> list = organizationService.findByParentId(loginUser.getOrganization().getId());
		List<TreeChildrenModel> children = new ArrayList<TreeChildrenModel>();
		for (Organization o : list)
		{
			TreeChildrenModel tvo = new TreeChildrenModel();
			tvo.setId(o.getId());
			tvo.setParentId(vo.getId());
			tvo.setText(o.getName());
			tvo.setLeaf(true);
			children.add(tvo);
		}
		vo.setChildren(children);
		vo.setLeaf(children.size() <= 0);
		List<TreeChildrenModel> voList = new ArrayList<TreeChildrenModel>();
		voList.add(vo);
		return new Json<List<TreeChildrenModel>>(voList);
	}

	@Ignore
	@RequestMapping(value = "/loadUser")
	public Json<List<User>> listUser(String name, String orgId)
	{

		Json<List<User>> rst = new Json<List<User>>();
		List<User> userList;
		try
		{
			if (StringUtils.isEmpty(orgId))
			{
				User user = this.getSessionUser();
				Organization org = user.getOrganization();
				if (org != null)
				{
					orgId = org.getId();
				}
			}
			userList = userService.findList(name, orgId);
			rst.OK(userList, "");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}
}
