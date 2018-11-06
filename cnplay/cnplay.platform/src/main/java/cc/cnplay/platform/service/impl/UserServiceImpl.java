package cc.cnplay.platform.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cc.cnplay.core.Listener;
import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.Identity;
import cc.cnplay.core.service.InitializeService;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.spring.service.PasswordEncoderService;
import cc.cnplay.core.util.BeanUtils;
import cc.cnplay.core.util.Converter;
import cc.cnplay.core.util.StringUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.cache.OnlineUserCache;
import cc.cnplay.platform.dao.AreaDao;
import cc.cnplay.platform.dao.OrgConfDao;
import cc.cnplay.platform.dao.OrganizationDao;
import cc.cnplay.platform.dao.RightDao;
import cc.cnplay.platform.dao.RoleDao;
import cc.cnplay.platform.dao.UserDao;
import cc.cnplay.platform.domain.Area;
import cc.cnplay.platform.domain.Attachment;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.OrganizationConfig;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.RightReject;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.SysConfigName;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserFinger;
import cc.cnplay.platform.domain.UserLog;
import cc.cnplay.platform.domain.UserOperLog;
import cc.cnplay.platform.domain.UserRight;
import cc.cnplay.platform.domain.UserRole;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.util.ExcelImportHelp;
import cc.cnplay.platform.vo.LoginConf;
import cc.cnplay.platform.vo.LoginUser;
import cc.cnplay.platform.vo.SettingPassword;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class UserServiceImpl extends AbsGenericService<User, String> implements UserService, InitializeService
{
	@Resource
	private PasswordEncoderService passwordEncoder;

	@Resource
	private OnlineUserCache onlineCache;

	@Resource
	private UserDao userDao;

	@Resource
	private RightDao rightDao;

	@Resource
	private RoleDao roleDao;

	@Resource
	private OrgConfDao orgConfDao;

	@Resource
	private SystemConfigService configService;

	@Resource
	private OrganizationDao organizationDao;

	@Resource
	private AreaDao areaDao;

	@Override
	public int getSort()
	{
		return 10;
	}

	@Override
	public void destroy()
	{

	}

	// @PostConstruct
	public void init()
	{
		defaultOrg();
		defaultUser();
	}

	public void defaultOrg()
	{
		try
		{
			Area area = areaDao.getRoot();
			if (area == null)
			{
				area = new Area();
				area.setCode("1000");
				area.setName("顶级机构");
				area.setLevelCode(Area.LevelCode);
			}
			Organization orgRoot = organizationDao.getRoot();
			if (orgRoot == null)
			{
				orgRoot = new Organization();
				orgRoot.setCode("1000");
				orgRoot.setName("顶级机构");
				orgRoot.setLevelCode(Organization.LevelCode);
			}
			area.setOrgId(orgRoot.getId());
			areaDao.save(area);
			areaDao.flush();
			organizationDao.save(orgRoot);
			// organizationDao.defaultArea(area);
		}
		catch (Throwable ex)
		{
			logger.error("初始化机构", ex);
		}
	}

	public Listener<User> removeUserListener;

	public Listener<User> getRemoveUserListener()
	{
		return removeUserListener;
	}

	@Override
	public void setRemoveUserListener(Listener<User> removeUserListener)
	{
		this.removeUserListener = removeUserListener;
	}

	public void defaultUser()
	{
		try
		{
			Organization orgRoot = organizationDao.getRoot();
			User root = userDao.getByUsername(User.ROOT);
			if (root == null)
			{
				root = new User();
				root.setUsername(User.ROOT);
				root.setPassword(passwordEncoder.encode(Constants.PASSWORD_DEFAULT));
				root.setName("高级管理员root");
				root.setStatus(Status.Normal);
				root.setOrganization(orgRoot);
				root.setAuthType(3);
				root.setUpdatePwdNeed(true);
			}
			else
			{
				List<UserFinger> ufList = userDao.findUserFingerByUserid(root.getId());
				if (ufList.size() == 0)
				{
					root.setAuthType(3);
				}
			}
			if (root.getOrganization() == null)
			{
				root.setOrganization(orgRoot);
			}
			dao().save(root);
			User admin = userDao.getByUsername(User.ADMIN);
			if (admin == null)
			{
				admin = new User();
				admin.setUsername(User.ADMIN);
				admin.setPassword(passwordEncoder.encode(Constants.PASSWORD_DEFAULT));
				admin.setName("高级管理员admin");
				admin.setStatus(Status.Normal);
				admin.setOrganization(orgRoot);
				admin.setAuthType(3);
				admin.setUpdatePwdNeed(true);
			}
			else
			{
				List<UserFinger> ufList = userDao.findUserFingerByUserid(admin.getId());
				if (ufList.size() == 0)
				{
					admin.setAuthType(3);
				}
			}
			if (admin.getOrganization() == null)
			{
				admin.setOrganization(orgRoot);
			}
			dao().save(admin);
		}
		catch (Throwable ex)
		{
			logger.error("初始化用户", ex);
		}
	}

	@Override
	public Role defaultRole()
	{
		Role role = dao().getById(Role.class, Role.SuperAdminId);
		List<Role> roleList = dao().findAll(Role.class);
		if (role == null && roleList.size() == 0)
		{
			role = new Role();
			role.setId(Role.SuperAdminId);
			role.setName("系统管理员");
			dao().save(role);
		}
		if (role != null)
		{
			List<RoleRight> rrList = userDao.findRoleRightByRoleId(role.getId());
			if (rrList.isEmpty())
			{
				List<Right> rightList = dao().findAll(Right.class);
				for (Right r : rightList)
				{
					RoleRight rr = new RoleRight();
					rr.setRight(r);
					rr.setRole(role);
					dao().save(rr);
				}
			}
		}
		for (Role e : roleList)
		{
			if (e.getOrg() == null)
			{
				e.setOrg(organizationDao.getRoot());
			}
			List<RoleRight> rrList = userDao.findRoleRightByRoleId(e.getId());
			for (int i = rrList.size() - 1; i >= 0; i--)
			{
				Right ri = rrList.get(i).getRight();
				for (int j = i - 1; j >= 0; j--)
				{
					Right rj = rrList.get(j).getRight();
					if (rightDao.exitRightReject(ri.getId(), rj.getId()))
					{
						rightDao.remove(rrList.get(i));
					}
				}
			}
		}
		return role;
	}

	@Override
	public User getNormalByUsername(String username)
	{
		User user = userDao.getByUsername(username);
		if (user != null && user.getStatus() == Status.Normal)
		{
			return user;
		}
		return null;
	}

	@Override
	public User getByUsername(String username)
	{
		User user = userDao.getByUsername(username);
		return user;
	}

	@Override
	public Json<LoginUser> login(LoginUser login)
	{
//		if (!login.isPasswordEncoder() && Base64.isBase64(login.getPassword()))
//		{
//			String pwd = Converter.toString(Base64.decodeBase64(login.getPassword()));
//			login.setPassword(pwd);
//			long pwdTime = 0;
//			if (pwd.length() > 14)
//			{
//				pwdTime = Converter.parseLong(pwd.substring(pwd.length() - 14, pwd.length()));
//			}
//			long nows = Converter.parseLong(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
//			if (nows - pwdTime <= 3)
//			{
//				login.setPassword(pwd.substring(0, pwd.length() - 14));
//			}
//		}
		Date now = new Date();
		Json<LoginUser> json = new Json<LoginUser>();
		json.setSuccess(false);
		json.setCode(Json.CodeEnd);
		User user = userDao.getByUsername(login.getUsername());
		if (user != null)
		{
			Organization org = user.getOrganization();
			if (org != null)
			{
				user.setOrgId(org.getId());
				user.setOrgName(org.getName());
			}
			if (user.getStatus() == Status.Banned)
			{
				json.NG("该帐号已被锁定，请联系管理员");
				return json;
			}
			if (user.getStatus() == Status.Delete)
			{
				// json.NG("该帐号已被删除");
				json.NG("用户或密码错误！");
				return json;
			}
//			LoginConf loginConf = new LoginConf();
//			if (user.isSysAuthType())
//			{
//				loginConf = orgConfDao.getLoginConf(user.getOrganization().getId());
//			}
//			if (loginConf.isVerifyPwd() && StringUtils.isBlank(login.getPassword()))
//			{
//				json.NG("需要密码认证！");
//				return json;
//			}
//			if (loginConf.isVerifyFinger() && StringUtils.isBlank(login.getFingerData()))
//			{
//				json.NG("请录入指纹！");
//				json.setCode(Json.CodeIng);
//				json.setRows(login);
//				return json;
//			}

			// 检查密码
			//if (loginConf.isVerifyPwd())
			{
				String password = login.getPassword();
				if (!login.isPasswordEncoder())
				{
					password = passwordEncoder.encode(password);
				}
				boolean b = user.getPassword().equalsIgnoreCase(password);
				if (!b)
				{
					json.NG("用户或密码错误！");
				}
				else
				{
					json.OK(login, "认证成功！");
				}
			}
//			else if (loginConf.isVerifyFinger())
//			{
//				json.setSuccess(true);
//			}
			// 按指纹验证
//			if (json.getSuccess() && loginConf.isVerifyFinger())
//			{
//				int b = checkFinger(user, login.getFingerDev(), login.getFingerData());
//				if (user.isStress())
//				{
//					Alarm am = new Alarm();
//					am.setUsera(user);
//					am.setUserb(user);
//					am.setAlarmTime(new Date());
//					am.setAlarmType(0);
//					am.setStatus(0);
//					am.setOrg(user.getOrganization());
//					am.setMemo(user.getUsername() + "指纹胁迫报警");
//					logger.info(am.getMemo());
//					am.setAddress(user.getMobile());
//					userDao.save(am);
//				}
//				if (b == 1)
//				{
//					json.OK(login, "指纹验证成功！");
//				}
//				else
//				{
//					json.NG("指纹验证失败！");
//					OrganizationConfig conf = orgConfDao.findByOrg2Root(user.getOrganization().getId(), LoginConf.webFingerType);
//					if (conf != null)
//						login.setFingerDev(conf.getValue());
//					json.setRows(login);
//				}
//			}
			UserLog uol = new UserLog();
			uol.setName(login.getDisplayName());
			BeanUtils.copyProperties(login, user);
			login.setLoginTime(now);

			if (json.getSuccess())
			{
				if (user.getLoginFailNum() != 0)
				{
					user.setLoginFailNum(0);
					user.setLoginTime(new Date());
					userDao.save(user);
				}
				uol.setProceed("登录成功");
			}
			else
			{
				int loginTryNo = configService.getByName(SysConfigName.Login_Try_No, 5);
				user.setLoginFailNum(user.getLoginFailNum() + 1);
				if (user.getLoginFailNum() >= loginTryNo)
				{// 自动锁定
					user.setStatus(Status.Banned);
					json.setMsg("用户被锁定，请24小时后再登录！");
				}
				else
				{
					json.setMsg(json.getMsg() + "您还可以尝试" + (loginTryNo - user.getLoginFailNum()) + "次");
				}
				user.setLoginTime(new Date());
				userDao.save(user);
				uol.setProceed("登录失败");
			}

			uol.setUser(user);
			uol.setRemoteAddr(getRequesRemoteAddr());
			uol.setRemoteUser(getRequesRemoteUser());
			uol.setMemo(json.getMsg());
			uol.setUsername(login.getUsername());
			userDao.save(uol);
		}
		else
		{
			json.NG("用户或密码错误");
		}
		return json;
	}

	protected HttpServletRequest getRequest()
	{
		if (RequestContextHolder.getRequestAttributes() == null)
			return null;
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	protected String getRequesRemoteAddr()
	{
		HttpServletRequest request = getRequest();
		if (request != null)
		{
			return request.getRemoteAddr();
		}
		return "";
	}

	protected String getRequesRemoteUser()
	{
		HttpServletRequest request = getRequest();
		if (request != null)
		{
			return request.getRemoteUser();
		}
		return "";
	}

	@Override
	public Json<Boolean> settingPassword(SettingPassword setting)
	{
		Json<Boolean> json = new Json<Boolean>();
		if (StringUtils.isNotEmpty(setting.getNewpass()) && setting.getNewpass().equals(setting.getNewpassCheck()))
		{
			if (!setting.isPasswordEncoder())
			{
				setting.setPassword(passwordEncoder.encode(setting.getPassword()));
				setting.setNewpass(passwordEncoder.encode(setting.getNewpass()));
			}
			User old = getNormalByUsername(setting.getUsername());
			if (old != null)
			{
				if (old.getPassword().equalsIgnoreCase(setting.getPassword()))
				{
					old.setPassword(setting.getNewpass());
					old.setUpdatePwdTime(new Date());
					old.setUpdatePwdNeed(false);
					save(old);
					json.setMsg("修改成功");
					json.setSuccess(true);
				}
				else
				{
					json.setMsg("请输入正确的密码！");
				}
			}
			else
			{
				json.setMsg("找不到此用户！");
			}
		}
		else
		{
			json.setMsg("新密码和确认密码必须一样！");
		}
		return json;
	}

	@Override
	public void updatePwd(String ids, UserOperLog uol)
	{
		String[] lst = ids.split(",");
		for (String s : lst)
		{
			User u = userDao.getById(User.class, s);
			u.setPassword(passwordEncoder.encode(Constants.PASSWORD_DEFAULT));
			u.setUpdatePwdTime(new Date());
			u.setUpdatePwdNeed(true);
			userDao.save(u);
			uol.setUser(u);
			uol.setName("用户管理-重置密码");
			uol.setId(Identity.randomID());
			userDao.save(uol);
		}
	}

	@Override
	public void updateStatus(String ids, String status, UserOperLog uol) throws CnplayRuntimeException
	{
		// int tryNo = systemConfigService.getByName(SysConfigName.Login_Try_No, 5);
		String[] lst = ids.split(",");
		Status st = Status.valueOf(status);
		String[] arrAdm = { User.ROOT, User.ADMIN };

		for (String id : lst)
		{
			User u = userDao.getById(User.class, id);
			if (st == Status.Delete && ArrayUtils.contains(arrAdm, u.getUsername()))
			{
				throw new CnplayRuntimeException("不能删除管理员！");
			}
			if (st == Status.Delete)
			{
				boolean delete = true;
				if (removeUserListener != null)
				{
					try
					{
						delete = removeUserListener.onListener(this, u);
					}
					catch (Throwable ex)
					{
						throw new CnplayRuntimeException(ex.getMessage());
					}
				}
				if (!delete)
				{
					throw new CnplayRuntimeException("不能删除用户[" + (u.getUsername() + " - " + u.getName()) + "]，用户被其它模块引用！");
				}
			}
			u.setStatus(st);
			if (st == Status.Banned)
			{
				uol.setName("用户管理-锁定用户");
			}
			else if (st == Status.Normal)
			{
				uol.setName("用户管理-恢复用户");
				u.setLoginFailNum(0);
			}
			else if (st == Status.Delete)
			{
				uol.setName("用户管理-删除用户");
			}
			userDao.save(u);
			uol.setUser(u);
			uol.setId(Identity.randomID());
			userDao.save(uol);
		}

	}

	@Override
	public User getUserById(String id)
	{
		User user = userDao.getById(id);
		if (user == null)
		{
			return new User();
		}
		else
		{
			Organization org = user.getOrganization();
			user.setOrgId(org.getId());
			user.setOrgName(org.getName());
		}
		return user;
	}

	@Override
	public DataGrid<User> findByName(User user, String name, String orgId, int pageNum, int pageSize)
	{
		String levelCode = organizationDao.getLevelCodeById(user.getOrganization().getId());
		DataGrid<User> dg = userDao.findList(user.getId(), name, levelCode, orgId, pageNum, pageSize);
		List<User> userList = dg.getRows();
		List<String> userIds = new ArrayList<String>();
		for (User u : userList)
		{
			userIds.add(u.getId());
		}
		Search search = new Search(UserRole.class);
		search.addFilterIn("user.id", userIds);
		@SuppressWarnings("unchecked")
		List<UserRole> urLst = dao().search(search);

		for (User u : userList)
		{
			StringBuffer sb = new StringBuffer();
			for (int i = urLst.size() - 1; i >= 0; i--)
			{
				UserRole ur = urLst.get(i);
				if (u.equals(ur.getUser()))
				{
					urLst.remove(i);
					Role r = ur.getRole();
					if (r != null)
					{
						sb.append(r.getName() + "、");
					}
				}
			}
			u.setDisplayName(sb.length() > 0 ? sb.substring(0, sb.length() - 1).toString() : "");
			Organization org = u.getOrganization();
			if (org != null)
			{
				u.setOrgId(org.getId());
				u.setOrgName(org.getName());
			}
			Search searchFinger = new Search(UserFinger.class);
			searchFinger.addFilterEqual("user.id", u.getId());
			u.setIsCollectFinger(dao().count(searchFinger) > 0);
		}
		return dg;
	}

	@Override
	public List<User> findList(String name, String orgId)
	{
		Search search = new Search(User.class);
		search.addFilterNotEqual("status", Status.Delete);
		if (StringUtils.isNotBlank(name))
		{
			search.addFilterLike("name", "%" + name + "%");
		}
		search.addFilterEqual("org.id", orgId);
		@SuppressWarnings("unchecked")
		List<User> userLst = dao().search(search);
		for (User u : userLst)
		{
			Organization org = u.getOrganization();
			if (org != null)
			{
				u.setOrgId(org.getId());
				u.setOrgName(org.getName());
			}
		}
		return userLst;
	}

	@Override
	public User saveUser(User userVO, UserOperLog uol)
	{
		if (StringUtils.isNotBlank(userVO.getUsername()))
		{
			String regEx = "(^[A-Za-z0-9]+$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(userVO.getUsername());
			boolean rs = mat.find();
			if (!rs)
				throw new CnplayRuntimeException("请输入由数字或字母组成的用户帐号！");

			if (userVO.getUsername().length() > 12)
				throw new CnplayRuntimeException("用户帐号有效长度为1-12位！");
		}
		{
			String regEx = "(^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(userVO.getMobile());
			boolean rs = mat.find();
			if (!rs)
				throw new CnplayRuntimeException("请输入有效的手机号码！");
		}
		if (StringUtils.isNotBlank(userVO.getIdCard()))
		{
			String regEx = "(^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(userVO.getIdCard());
			boolean rs = mat.find();
			if (!rs)
				throw new CnplayRuntimeException("请输入有效的身份证号码！");
		}
		uol.setName("用户管理-修改用户");
		User user = dao().getById(User.class, userVO.getId());
		if (user != null)
		{
			// 此处与画面输入项一致
			// user.setUsername(userVO.getUsername());
			user.setName(userVO.getName());
			user.setMobile(userVO.getMobile());
			user.setIdCard(userVO.getIdCard());
			user.setJobName(userVO.getJobName());
			user.setSex(userVO.getSex());
			user.setMemo(userVO.getMemo());
			user.setAuthType(userVO.getAuthType());
		}
		else
		{
			uol.setName("用户管理-新增用户");
			// 保存用户
			if (existUser(userVO))
			{
				throw new CnplayRuntimeException("用户帐号(" + userVO.getUsername() + ")已存在！");
			}
			user = userVO;
			user.setPassword(passwordEncoder.encode(Constants.PASSWORD_DEFAULT));
			user.setUpdatePwdTime(new Date());
			user.setUpdatePwdNeed(true);
		}
		String orgId = userVO.getOrgId();
		if (StringUtils.isNotBlank(orgId))
		{
			Organization org = dao().getById(Organization.class, orgId);
			user.setOrganization(org);
		}
		User rst = dao().merge(user);
		uol.setUser(rst);
		dao().merge(uol);
		return rst;
	}

	// user+status为正常的
	private boolean existUser(User user)
	{
		Search search = new Search(User.class);
		search.addFilterNotEqual("status", Status.Delete);
		search.addFilterEqual("username", user.getUsername());
		@SuppressWarnings("unchecked")
		List<User> userLst = dao().search(search);
		if (userLst != null && userLst.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isRootOrg(String userId)
	{
		User user = userDao.getById(userId);
		if (user != null)
		{
			Organization org = organizationDao.getRoot();
			return org != null && org.equals(user.getOrganization());
		}
		return false;
	}

	@Override
	public List<Role> findRoleWithUser(String userid, String loginUserid)
	{
		boolean isRoot = isRootOrg(loginUserid);
		List<Role> roleLst = roleDao.findList(isRoot);
		List<String> userRoles = new ArrayList<String>();
		List<UserRole> urLst = dao().findByField(UserRole.class, "user.id", userid);
		for (UserRole ur : urLst)
		{
			Role r = ur.getRole();
			if (r != null)
			{
				userRoles.add(r.getId());
			}
		}
		for (Role role : roleLst)
		{
			if (userRoles.contains(role.getId()))
			{
				role.setChecked(true);
			}
			role.setLeaf(true);
		}
		return roleLst;
	}

	@Override
	public void updateRoleRight(String userid, String[] roleids, String[] rightids, UserOperLog uol) throws CnplayRuntimeException
	{
		User user = dao().getById(User.class, userid);
		uol.setName("用户管理-设置角色权限");
		if (user == null)
		{
			throw new CnplayRuntimeException(String.format("找不到此用户%s", userid));
		}
		uol.setUser(user);
		dao().save(uol);
		// 角色先删再加
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user.id", userid);
		List<UserRole> urLst = dao().findByMap(UserRole.class, map);
		for (UserRole ur : urLst)
		{
			dao().removeById(UserRole.class, ur.getId());
		}
		if (roleids != null)
		{
			for (String roleid : roleids)
			{
				Role role = dao().getById(Role.class, roleid);
				UserRole ur = new UserRole();
				ur.setUser(user);
				ur.setRole(role);
				dao().save(ur);
			}
		}

		// 权限先删再加
		map = new HashMap<String, Object>();
		map.put("user.id", userid);
		List<UserRight> rightLst = dao().findByMap(UserRight.class, map);
		for (UserRight ur : rightLst)
		{
			dao().removeById(UserRight.class, ur.getId());
		}
		if (rightids != null)
		{
			for (String rightid : rightids)
			{
				Right right = dao().getById(Right.class, rightid);
				UserRight ur = new UserRight();
				ur.setUser(user);
				ur.setRight(right);
				dao().save(ur);
			}
		}

	}

	@Override
	public boolean checkUserFinger(User user, String fingerDev, String fingerData)
	{
		return checkFinger(user, fingerDev, fingerData) == 1;
	}

	@Override
	public int checkFinger(User user, String fingerDev, String fingerData)
	{
		return 1;
	}

//	@Override
//	public int checkFingerOld(User user, String fingerDev, String fingerData)
//	{
//		if (user == null || StringUtils.isEmpty(fingerData))
//		{
//			return -2;
//		}
//		List<UserFinger> list = findFinger(user, fingerDev);
//		String[] fingerDatas = new String[list.size()];
//		for (int i = 0; i < list.size(); i++)
//		{
//			UserFinger uf = list.get(i);
//			fingerDatas[i] = uf.getFingerData();
//		}
//		if (fingerDatas.length > 0)
//		{
//			try
//			{
//				String fingerDevClazz = findFingerDevClazz(fingerDev);
//				Finger finger = new Finger(fingerDevClazz, fingerData, fingerDatas);
//				return finger.verify() ? 1 : -1;
//			}
//			catch (Throwable ex)
//			{
//				logger.error("指纹校验异常", ex);
//				return -5;
//			}
//		}
//		return 0;
//	}

	@Override
	public List<UserFinger> findFinger(User user, String fingerDev)
	{
		user = dao().getById(User.class, user.getId());
		List<UserFinger> list = userDao.findUserFingerByUserid(user.getId());// user.getUserFinger();
		List<UserFinger> results = new ArrayList<UserFinger>();
		for (int i = list.size() - 1; i >= 0; i--)
		{
			UserFinger finger = list.get(i);
			if (fingerDev.equalsIgnoreCase(finger.getFingerDev()))
			{
				results.add(finger);
			}
		}
		return results;
	}

	@Override
	public String findRoleNamesByUser(String userId)
	{
		StringBuffer sb = new StringBuffer();
		List<UserRole> urLst = dao().findByField(UserRole.class, "user.id", userId);
		for (UserRole ur : urLst)
		{
			Role r = ur.getRole();
			if (r != null)
			{
				sb.append(r.getName() + "、");
			}
		}
		if (sb.length() > 0)
		{
			return sb.substring(0, sb.length() - 1);
		}
		return "";
	}

	@Override
	public User getByOrgAndFingerData(String orgId, String fingerDev, String fingerData)
	{
		List<User> userList = dao().findByField(User.class, "organization.id", orgId);
		for (User user : userList)
		{
			if (checkUserFinger(user, fingerDev, fingerData))
			{
				return user;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String validateRightReject(String[] roleIds)
	{
		StringBuffer sb = new StringBuffer("");
		if (roleIds == null || roleIds.length == 0)
		{
			return sb.toString();
		}
		Map<String, Right> rightMap = new HashMap<String, Right>();
		for (String roleId : roleIds)
		{
			List<RoleRight> tmpList = roleDao.findRoleRightByRoleId(roleId);
			for (RoleRight rr : tmpList)
			{
				rightMap.put(rr.getRight().getId(), rr.getRight());
			}
		}
		if (rightMap.size() < 2)
		{
			return sb.toString();
		}
		Search search = new Search(RightReject.class);
		search.addFilterIn("refRightId", rightMap.keySet());
		search.addFilterIn("rejectRightId", rightMap.keySet());
		List<RightReject> rrList = dao().search(search);
		for (RightReject rr : rrList)
		{
			Right right1 = rightMap.get(rr.getRefRightId());
			Right right2 = rightMap.get(rr.getRejectRightId());
			sb.append("<br/>" + right1.getText() + " 与 " + right2.getText() + " 互斥,请检查角色权限设置");
		}
		return sb.toString();
	}

	/**
	 * 解析显示导入数据
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Json<List<User>> loadImportUser(HttpServletRequest request, String roleConfig, UserOperLog uol)
	{
		Json<List<User>> json = new Json();
		List<User> list = new ArrayList<User>();
		List<String> strList = new ArrayList<String>();
		String attaId = request.getParameter("attaId");
		InputStream in = null;
		try
		{
			Attachment atta = userDao.getById(Attachment.class, attaId);
			try
			{
				// 读取并解析导入文件
				strList = ExcelImportHelp.getTargetList(getImportFileStream(atta), atta.getSuffix());
			}
			catch (Exception e)
			{
				logger.error("导入过程出现错误", e);
				json.setMsg("非法的导入文件或格式错误");
				return json;
			}
			logger.info("导入用户总行数：" + strList.size());
			if (strList.size() == 0)
			{
				json.setMsg("未找到用户信息，请按格式进行填写！");
				return json;
			}

			for (String str : strList)
			{
				String errorMsg = "";
				User user = new User();
				List<UserRole> userRoleList = new ArrayList<UserRole>();
				if (str == null || str.equals(""))
				{
					continue;
				}
				String[] strArray = str.split(",");
				if (strArray.length >= 2)
				{
					if (strArray[1].equals("用户信息表") || strArray[1].equals("用户编号"))
					{
						continue;
					}
					user.setUsername(strArray[1].trim());// 用户编号
				}
				if (strArray.length >= 3)
				{
					if (StringUtil.isNotEmpty(strArray[2].trim()))
					{
						user.setName(strArray[2].trim());// 用户名称
					}
					else
					{
						errorMsg = errorMsg + "该用户名称不能为空!";
					}
				}
				if (strArray.length >= 4)
				{
					if (StringUtil.isNotEmpty(strArray[3].trim()))
					{
						user.setOrgId(strArray[3].trim());// 保存机构名称
					}
					else
					{
						errorMsg = errorMsg + "该用户所属机构不能为空!";
					}
				}
				else
				{
					errorMsg = errorMsg + "该用户所属机构不能为空!";
				}
				if (strArray.length >= 5)
					user.setJobName(strArray[4].trim());// 保存角色名称
				if (strArray.length >= 6)
					user.setMobile(strArray[5].trim());// 手机号
				if (strArray.length >= 7)
					user.setMemo(strArray[6].trim());// 备注
				// 校验用户信息
				errorMsg = errorMsg + checkUserInfo(user);
				// 检验该用户是否存在
				if (existUser(user))
				{
					errorMsg = errorMsg + "该用户编号在数据库中已存在!";
				}
				// 用户编号是否重复 在导入的数据中
				if (!checkSameUserSn(user, list))
				{
					errorMsg = errorMsg + "用户导入数据中用户编号已存在!";
				}
				// 检验该机构是否存在
				List<Organization> existOrg = dao().findByField(Organization.class, "code", user.getOrgId());
				if (existOrg.size() <= 0)
				{
					errorMsg = errorMsg + "机构不存在，机构名称：" + user.getOrgId() + "!";
				}
				else
				{
					// 机构赋值
					user.setOrganization(existOrg.get(0));
				}
				// 保存用户同时配置角色 ---检验该角色是否存在
				roleConfig = request.getParameter("roleConfig");
				if (roleConfig.equals("1"))
				{
					String msg = "";
					if (user.getJobName() != null && !user.getJobName().equals(""))
					{
						String[] rolename = user.getJobName().split(";");

						for (String log : rolename)
						{
							List<Role> existRole = dao().findByField(Role.class, "name", log.trim());
							if (existRole.size() <= 0)
							{
								msg = msg + "角色不存在，角色名称：" + log + "!";
							}
							else
							{
								// 保存UserRole表
								UserRole userRole = new UserRole();
								userRole.setUser(user);
								userRole.setRole(existRole.get(0));
								userRoleList.add(userRole);
							}
						}
					}
					errorMsg = errorMsg + msg;
				}

				// 保存信息
				if (StringUtils.isBlank(errorMsg))
				{
					// 正确数据直接保存
					// 默认密码
					user.setPassword(passwordEncoder.encode(Constants.PASSWORD_DEFAULT));
					user.setUpdatePwdTime(new Date());
					user.setUpdatePwdNeed(true);
					// 清空用户岗位
					user.setJobName("");
					// 保存用户
					User us = dao().save(user);
					// 写用户新增日志
					uol.setId(Identity.randomID());
					uol.setName("用户管理-新增用户");
					uol.setUser(us);
					dao().save(uol);

					if (roleConfig.equals("1"))
					{
						// 保存用户角色权限
						dao().saveAll(userRoleList);
						// 写用户授于角色权限日志
						uol.setId(Identity.randomID());
						uol.setName("用户管理-设置角色权限");
						uol.setUser(us);
						dao().save(uol);
					}
				}
				else
				{
					// 错误数据 界面显示错误的消息 暂时征用CreateUsername 显示errorMsg
					user.setCreateUsername(errorMsg);
					list.add(user);
				}
			}

			// 回传界面失败的数据
			if (list.size() > 0)
			{
				json.setRows(list);
				json.setMsg("用户信息导入失败，请查看详细错误信息！");
				json.setSuccess(true);
			}
			else
			{
				json.setRows(list);
				json.setMsg("用户信息导入成功！");
				json.setSuccess(true);
			}
		}
		catch (Exception ex)
		{
			logger.error("解析过程出现错误", ex);
			json.setMsg("用户信息导入失败，请确认！");
			json.setSuccess(true);
			return json;
		}
		finally
		{
			IOUtils.closeQuietly(in);
		}
		return json;
	}

	/**
	 * 校验导入的用户数据中是否有与User相同的用户编号
	 * 
	 * @param user
	 * @param list
	 * @return
	 */
	private boolean checkSameUserSn(User user, List<User> list)
	{
		boolean ret = true;
		for (User u : list)
		{
			if (u.getUsername().equals(user.getUsername()))
			{
				ret = false;
				break;
			}
		}
		return ret;
	}

	/**
	 * 校验用户信息
	 * 
	 * @param user
	 * @return
	 */
	public String checkUserInfo(User user)
	{
		String errorMsg = "";
		if (StringUtils.isNotBlank(user.getUsername()))
		{
			String regEx = "(^[A-Za-z0-9]+$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(user.getUsername());
			boolean rs = mat.find();
			if (!rs)
				errorMsg = errorMsg + "用户编号不符合规范，请输入由数字或字母组成的用户编号!";

			if (user.getUsername().length() > 12)
				errorMsg = errorMsg + "用户编号长度需小于12!";
		}
		else
		{
			errorMsg = errorMsg + "用户编号不能为空!";
		}
		// 用户名称
		if (!StringUtils.isNotBlank(user.getName()))
		{
			errorMsg = errorMsg + "用户名称不能为空!";
		}

		// 校验手机号
		if (StringUtils.isNotBlank(user.getMobile()))
		{
			String regEx = "(^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(user.getMobile());
			boolean rs = mat.find();
			if (!rs)
				errorMsg = errorMsg + "用户手机号不符合规范!";
		}
		// else{ errorMsg = errorMsg+"用户手机号不能为空，请修改，用户编号："+user.getUsername()+"\r\n"; }
		// 校验身份证号 if (StringUtils.isNotBlank(user.getIdCard())) { String regEx = "(^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$)"; Pattern pat = Pattern.compile(regEx); Matcher mat = pat.matcher(user.getIdCard()); boolean rs = mat.find(); if (!rs) errorMsg = errorMsg+"用户身份证号不符合规范，请修改，用户编号："+user.getUsername()+"\r\n"; }

		return errorMsg;
	}

	/**
	 * 保存导入的数据
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String saveImportUser(String json, UserOperLog uol)
	{
		String errorMsg = "";
		try
		{
			List<UserRole> userRoleList = new ArrayList<UserRole>();
			// 解析JSON数据
			ObjectMapper om = new ObjectMapper();
			@SuppressWarnings("unchecked")
			List<LinkedHashMap<String, Object>> list = om.readValue(json, List.class);
			for (LinkedHashMap<String, Object> map : list)
			{
				Iterator it = map.entrySet().iterator();
				User user = new User();
				UserRole userRole = new UserRole();
				while (it.hasNext())
				{
					Map.Entry entry = (Map.Entry) it.next();
					if (entry.getKey().equals("id"))
					{
						user.setId(entry.getValue().toString());
					}
					// 用户编号
					if (entry.getKey().equals("username"))
					{
						user.setUsername(entry.getValue().toString());
					}
					// 用户名称
					if (entry.getKey().equals("name"))
					{
						user.setName(entry.getValue().toString());
					}
					// 机构ID赋值
					if (entry.getKey().equals("orgId"))
					{
						List<Organization> orgList = dao().findByField(Organization.class, "code", entry.getValue().toString());
						if (orgList.size() > 0)
						{
							user.setOrganization(orgList.get(0));
						}
					}
					// 用户赋角色权限
					if (entry.getKey().equals("jobName"))
					{
						List<Role> roleList = dao().findByField(Role.class, "name", entry.getValue().toString());
						if (roleList.size() > 0)
						{
							// 保存UserRole表
							userRole.setUser(user);
							userRole.setRole(roleList.get(0));
							userRoleList.add(userRole);
						}
					}
				}
				// 获取导入的用户是否必须修改密码：0.不需修改密码；1.需要修改密码
				int need = configService.getByName(SysConfigName.UpdatePwdNeed, 1);
				if (need == 1)
				{
					user.setUpdatePwdNeed(true);
				}
				// 默认密码
				user.setPassword(passwordEncoder.encode(Constants.PASSWORD_DEFAULT));
				user.setUpdatePwdTime(new Date());
				// 保存用户
				User us = dao().save(user);
				// 写用户新增日志
				uol.setName("用户管理-新增用户");
				uol.setUser(us);
				dao().save(uol);

				// 保存用户角色权限
				dao().save(userRole);
				// 写用户授于角色权限日志
				uol.setId(Identity.randomID());
				uol.setName("用户管理-设置角色权限");
				uol.setUser(us);
				dao().save(uol);
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			errorMsg = "用户信息保存失败";
		}
		return errorMsg;
	}

	/**
	 * 导出用户信息
	 */
	public List<Map<String, ?>> findReportData(Organization org, String orgId, String name)
	{
		String levelCode = organizationDao.getLevelCodeById(org.getId());
		List<User> list = userDao.findList("", name, levelCode, orgId);
		List<String> userIds = new ArrayList<String>();
		for (User u : list)
		{
			userIds.add(u.getId());
		}
		Search search = new Search(UserRole.class);
		search.addFilterIn("user.id", userIds);
		@SuppressWarnings("unchecked")
		List<UserRole> urLst = dao().search(search);

		List<Map<String, ?>> listMap = new ArrayList<Map<String, ?>>();
		for (User u : list)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			StringBuffer sb = new StringBuffer();
			for (int i = urLst.size() - 1; i >= 0; i--)
			{
				UserRole ur = urLst.remove(i);
				if (u.equals(ur.getUser()))
				{
					Role r = ur.getRole();
					if (r != null)
					{
						sb.append(r.getName() + "、");
					}
				}
			}
			m.put("rolename", sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "");
			m.put("username", u.getUsername());// 用户编号
			m.put("name", u.getName());// 用户名称
			// m.put("sex", u.getSex());//用户性别
			// m.put("jobName", u.getJobName());//岗位名称
			m.put("mobile", u.getMobile());// 手机号
			// m.put("phone", u.getPhone());//座机
			// m.put("idCard", u.getIdCard());//身份证号
			m.put("memo", u.getMemo());
			if (u.getOrganization() != null)
			{
				m.put("orgsn", u.getOrganization().getCode());// 机构编号
				m.put("orgname", u.getOrganization().getName());// 机构名称
			}
			else
			{
				m.put("orgsn", "");// 机构编号
				m.put("orgname", "");
			}
			listMap.add(m);
		}

		return listMap;
	}

	@Override
	public boolean exitRight(String userId, String component)
	{
		Right right = dao().getByField(Right.class, "component", component);
		return userDao.findUserHasRight(right.getId(), userId);
//		List<UserRight> urList = userDao.searchFilterEqual(UserRight.class, "right.id", right.getId());
//		for (UserRight ur : urList)
//		{
//			if (ur.getUser().getStatus() == Status.Normal)
//			{
//				if (userId.equals(ur.getUser().getId()))
//					return true;
//			}
//		}
//		List<RoleRight> rrList = userDao.searchFilterEqual(RoleRight.class, "right.id", right.getId());
//		for (RoleRight rr : rrList)
//		{
//			List<UserRole> list = userDao.findUserRoles(rr.getRole());
//			for (UserRole ur : list)
//			{
//				if (ur.getUser().getStatus() == Status.Normal)
//				{
//					if (userId.equals(ur.getUser().getId()))
//						return true;
//				}
//			}
//		}
//		return false;
	}

	private InputStream getImportFileStream(Attachment atta) throws Exception
	{
		String uploadPathType = null;
		String path = null;
		String smbUser = null;
		String smbPwd = null;
		File serverPath = null;
		String domainIp = null;
		NtlmPasswordAuthentication authentication = null;
		SmbFile smbFile = null;
		boolean configCorrect = false;
		InputStream in = null;
		try
		{
			uploadPathType = configService.getByName("upload.path.type");
			path = configService.getByName("upload.path");
			domainIp = configService.getByName("upload.smb.domain.ip");
			smbUser = configService.getByName("upload.smb.user");
			smbPwd = configService.getByName("upload.smb.pwd");
			if (StringUtils.equals(uploadPathType, "0"))
			{
				if (!StringUtils.isEmpty(path))
				{
					serverPath = new File(path);
					if (!serverPath.exists())
					{
						serverPath.mkdirs();
					}
				}
			}
			else
			{

				InetAddress address = InetAddress.getByName(domainIp);
				UniAddress uniAddress = new UniAddress(address);
				authentication = new NtlmPasswordAuthentication(domainIp, smbUser, smbPwd);
				SmbSession.logon(uniAddress, authentication);
				smbFile = new SmbFile(path, authentication);
				if (!smbFile.exists())
				{
					logger.error("配置上传文件保存共享路径错误:{path=" + path + ",user=" + smbUser + "+password=" + smbPwd + ",domain=" + domainIp + "}");
				}
			}
			configCorrect = true;
		}
		catch (Exception e)
		{
			logger.error("配置上传文件保存共享路径错误:{path=" + path + ",user=" + smbUser + "+password=" + smbPwd + ",domain=" + domainIp + "}");
		}
		if (!configCorrect)
		{
			uploadPathType = "0";
			path = Constants.webappAbsolutePath + File.separator + "upload";
			if (!StringUtils.isEmpty(path))
			{
				serverPath = new File(path);
				if (!serverPath.exists())
				{
					serverPath.mkdirs();
				}
			}
			configCorrect = true;
		}

		if (StringUtils.equals(uploadPathType, "0"))
		{
			in = new FileInputStream(new File(serverPath, atta.getId() + "." + atta.getSuffix()));
		}
		else
		{
			SmbFile saveFile = new SmbFile(smbFile, atta.getId() + "." + atta.getSuffix());
			in = new SmbFileInputStream(saveFile);
		}
		return in;
	}

	@Override
	public User getByMobile(String mobile)
	{
		return userDao.getByMobile(mobile);
	}

	@Memo("是否要修改密码或密码已经过期")
	@Override
	public boolean isNeedModifyPwd(User user)
	{
		boolean verifyPwd = isVerifyPwd(user);
		if (verifyPwd)
		{
			OrganizationConfig oc = orgConfDao.findByOrg2Root(user.getOrganization().getId(), "passwordTimeout");
			int pwdModifyCyc = 12;
			if (oc != null)
			{
				pwdModifyCyc = Converter.parseInt(oc.getValue());
				pwdModifyCyc = pwdModifyCyc < 1 ? 12 : pwdModifyCyc;
			}
			Calendar c = Calendar.getInstance();
			c.setTime(user.getUpdatePwdTime());
			if (c.get(Calendar.MONTH) + pwdModifyCyc < 12)
			{
				c.set(Calendar.MONTH, c.get(Calendar.MONTH) + pwdModifyCyc);
			}
			else
			{
				c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
				c.set(Calendar.MONTH, (c.get(Calendar.MONTH) + pwdModifyCyc) % 12);
			}
			boolean timeout = System.currentTimeMillis() > c.getTime().getTime();
			int need = configService.getByName(SysConfigName.UpdatePwdNeed, 1);
			return (need == 1 && user.getUpdatePwdNeed() && verifyPwd) || timeout;
		}
		else
		{
			return false;
		}
	}

	@Memo("是否提示用户修改密码")
	@Override
	public boolean isDefaultPwd(User user)
	{

		int need = configService.getByName(SysConfigName.UpdatePwdNeed, 1);
		boolean verifyPwd = isVerifyPwd(user);
		return need == 1 && user.getUpdatePwdNeed() && verifyPwd;
	}

	private boolean isVerifyPwd(User user)
	{
		boolean verifyPwd = false;
		if (!user.isSysAuthType())
		{// 验证方式：用户+密码
			verifyPwd = true;
		}
		else
		{// 验证方式：系统配置
			LoginConf lc = orgConfDao.getLoginConf(user.getOrganization().getId());
			OrganizationConfig oc = orgConfDao.findByOrg2Root(user.getOrganization().getId(), "loginByDevice");
			int loginByDevice = 0;
			if (oc != null)
			{
				loginByDevice = Converter.parseInt(oc.getValue());
			}
			verifyPwd = (lc.isVerifyPwd() || (loginByDevice & 2) == 2);
		}
		return verifyPwd;
	}

	@Override
	public boolean isRoot(String id)
	{
		return userDao.isRoot(id);
	}
}
