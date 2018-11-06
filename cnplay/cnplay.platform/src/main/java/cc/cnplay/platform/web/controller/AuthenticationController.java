package cc.cnplay.platform.web.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.spring.service.PasswordEncoderService;
import cc.cnplay.core.util.BeanUtils;
import cc.cnplay.core.util.JWTUtils;
import cc.cnplay.core.util.RandomCode;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Platform;
import cc.cnplay.platform.annotation.OperateLog;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.CheckLog;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.CheckLogService;
import cc.cnplay.platform.service.OrgCfgService;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.service.UserEmpowerService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.vo.LoginConf;
import cc.cnplay.platform.vo.LoginUser;
import cc.cnplay.platform.vo.Token;
import cc.cnplay.platform.vo.TokenUser;

@Controller
@RequestMapping
public class AuthenticationController extends AbsController {
	@Resource
	private UserService userService;

	@Resource
	private RightService rightService;

	@Resource
	private SystemConfigService configService;

	@Resource
	private OrgCfgService orgCfgService;

	@Resource
	private CheckLogService checkLogService;

	@Resource
	private UserEmpowerService userEmpowerService;

	@Autowired
	private PasswordEncoderService passwordEncoder;

	@OperateLog(name = "用户登录前取配置")
	@RequestMapping(value = "/loginConf", method = RequestMethod.POST)
	public @ResponseBody Json<LoginConf> loginConf(String username) {
		Json<LoginConf> json = new Json<LoginConf>();
		User user = userService.getByUsername(username);
		LoginConf loginConf = new LoginConf();
		if (user != null) {
			if (user.isSysAuthType()) {
				loginConf = orgCfgService.getLoginConf(user.getOrganization()
						.getId());
			}
			if (loginConf.isVerifyPwd()) {
				loginConf.setMsg("请输入密码");
			}
			if (loginConf.isVerifyFinger()) {
				loginConf.setMsg("请使用指纹登录");
			}
			if (loginConf.isVerifyPwd() && loginConf.isVerifyFinger()) {
				loginConf.setMsg("请输入密码，并录入指纹");
			}
		}
		json.OK(loginConf, "");
		return json;
	}

	@Ignore
	@OperateLog(name = "手机登录", memo = "支持用户名，密码，指纹")
	@RequestMapping(value = "/loginByApp", method = RequestMethod.POST)
	public @ResponseBody Json<Token> loginByApp(@RequestBody TokenUser tUser) {
		logout();
		LoginUser login = new LoginUser();
		login.setUsername(tUser.getUsername());
		login.setPassword(tUser.getPassword());
		login.setDisplayName("用户登录");
		Json<Token> ret = new Json<Token>();
		Json<LoginUser> json = userService.login(login);
		ret.setMsg(json.getMsg());
		ret.setCode(json.getCode());
		if (json.getSuccess()) {
			TokenUser tu = new TokenUser();
			BeanUtils.copyProperties(tu, json.getRows());
			long exp = System.currentTimeMillis() / 1000L + (24 * 60 * 60);
			String accToken = JWTUtils.sign(tu, exp);
			exp = System.currentTimeMillis() / 1000L + (7 * 24 * 60 * 60);
			String refToken = JWTUtils.sign(tu, exp);
			userEmpowerService.cancelByUser(login.getId());
			ret.OK(new Token(accToken, refToken), json.getMsg());
		} else {
			ret.setSuccess(false);
		}
		return ret;
	}

	@Ignore
	@OperateLog(name = "用户登录", memo = "支持用户名，密码，指纹")
	@RequestMapping(value = "/loginSubmit", method = RequestMethod.POST)
	public @ResponseBody Json<LoginUser> login(LoginUser login) {
		logout();
		login.setDisplayName("用户登录");
		Json<LoginUser> json = userService.login(login);
		if (json.getSuccess()) {
			userEmpowerService.cancelByUser(login.getId());
			setUserSession(json.getRows());
		}
		return json;
	}

	@Ignore
	@OperateLog(name = "确认登录", memo = "支持用户名，密码，指纹")
	@RequestMapping(value = "/loginConfirm", method = RequestMethod.POST)
	public @ResponseBody Json<LoginUser> loginConfirm(LoginUser login){
		login.setDisplayName("确认登录");
		this.getSession().removeAttribute(Platform.getConfirmKey(getRequest()));
		Json<LoginUser> json = userService.login(login);
		if (json.getSuccess()) {
			User user = this.getSessionUser();
			if (json.getRows().getId().equalsIgnoreCase(user.getId())) {
				json.setSuccess(false);
				json.setRows(null);
				json.setMsg("确认用户不能是当前登录用户！");
			} else {
				this.getSession().setAttribute(
						Platform.getConfirmKey(getRequest()), json.getRows());
			}
		}
		return json;
	}

	@Ignore
	@OperateLog(name = "复核数据-用户登录", memo = "支持用户名，密码，指纹")
	@RequestMapping(value = "/loginCheck", method = RequestMethod.POST)
	@RightAnnotation(name = "复核权限", component = CheckLog.PowerId, button = true)
	public @ResponseBody Json<LoginUser> loginCheck(LoginUser login) {
		login.setDisplayName("复核登录");
		this.getSession().removeAttribute(Platform.getCheckKey(getRequest()));
		CheckLog checkLog = (CheckLog) getSession().getAttribute(
				Platform.getCheckInfoKey(getRequest()));
		if (checkLog == null) {
			Json<LoginUser> json = new Json<LoginUser>(login);
			json.setSuccess(false);
			json.setRows(null);
			json.setMsg("复核超时！会话中已找不到待复核信息！");
			return json;
		} else {
			Json<LoginUser> json = userService.login(login);
			if (json.getSuccess()) {
				User user = this.getSessionUser();
				if (userService.exitRight(json.getRows().getId(),
						CheckLog.PowerId)) {
					if (json.getRows().getOrganization().getId()
							.equalsIgnoreCase(user.getOrganization().getId())) {
						if (!json.getRows().getId()
								.equalsIgnoreCase(user.getId())) {
							checkLog.setCheckUser(json.getRows());
							checkLog.setCheckTime(new Date());
							checkLog.setStatus(Status.Banned);
							checkLog.setOrganization(user.getOrganization());// 20150922
							checkLog.setMemo(rightService
									.getFullMenuName(checkLog.getUrl()));
							checkLogService.save(checkLog);
							this.getSession().setAttribute(
									Platform.getCheckKey(getRequest()),
									json.getRows());
						} else {
							json.setSuccess(false);
							json.setRows(null);
							json.setMsg("复核人与当前登录用户不能为同一人！");
						}
					} else {
						json.setSuccess(false);
						json.setRows(null);
						json.setMsg("复核人与当前登录用户不在同一机构！");
					}
				} else {
					json.setMsg("用户" + json.getRows().getUsername() + "无复核权限");
					json.setSuccess(false);
					json.setRows(null);
				}
			}
			return json;
		}

	}

	@OperateLog(name = "退出登录")
	@RequestMapping(value = "/logout")
	public String logout() {
		this.removeUserSession();
		this.getRequest().setAttribute("message", "退出成功");
		this.getRequest().setAttribute("p", System.currentTimeMillis());
		return "/index";
	}

	@Ignore
	@OperateLog(name = "系统调试-用户登录", memo = "调试人员登录")
	@RequestMapping(value = "/logonSubmit", method = RequestMethod.POST)
	public @ResponseBody Json<LoginUser> logon(LoginUser login) {
		logout();
		login.setDisplayName("调试登录");
		Json<LoginUser> json = new Json<LoginUser>(login);
		if (StringUtils.equals(login.getUsername(), User.ROOT)
				|| StringUtils.equals(login.getUsername(), User.ADMIN)) {
			User user = userService.getByUsername(login.getUsername());
			if (user != null) {
				String password = passwordEncoder.encode(login.getPassword());
				if (user.getStatus() != Status.Normal) {
					json.setSuccess(false);
					json.setMsg("用户被锁定或删除！");
				} else if (StringUtils.equalsIgnoreCase(password,
						user.getPassword())) {
					user.setDebug(true);
					json.setSuccess(true);
					setUserSession(user);
				} else {
					json.setSuccess(false);
					json.setMsg("用户名或密码错误!");
				}
			} else {
				json.setSuccess(false);
				json.setMsg("仅允许admin或root帐号登录!");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("仅允许admin或root帐号登录!");
		}
		return json;
	}

	@Ignore
	@RequestMapping(value = "/logonRandom")
	@Deprecated
	public @ResponseBody Json<String> logonRandom(LoginUser login) {
		return new Json<String>(RandomCode.getCode());
	}
}
