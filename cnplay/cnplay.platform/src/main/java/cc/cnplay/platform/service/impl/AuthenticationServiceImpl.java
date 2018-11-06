package cc.cnplay.platform.service.impl;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.service.InitializeService;
import cc.cnplay.core.spring.service.IUrlMatcher;
import cc.cnplay.core.util.StringUtil;
import cc.cnplay.core.util.ZipUtils;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.RightType;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.SysConfigName;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.AuthenticationService;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.service.UserEmpowerService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.vo.LoginUser;

@Component
public class AuthenticationServiceImpl implements AuthenticationService, InitializeService
{

	protected final Logger logger = Logger.getLogger(this.getClass());

	@Resource
	private UserService userService;

	@Resource
	private UserEmpowerService userEmpowerService;

	@Resource
	private RightService rightService;

	@Resource
	private SystemConfigService configService;

	@Resource
	private IUrlMatcher urlMatcher;

	@Resource
	private ApplicationContext applicationContext;

	public int getSort()
	{
		return 0;
	}

	public void init()
	{
		try
		{
			unzip();
			logger.info("解压静态资源成功");
		}
		catch (Exception ex)
		{
			Constants.Installed = -1;
			Constants.InstalledMessage = "程序启动异常：解压静态资源失败!";
			logger.error("解压静态资源失败", ex);
		}
		try
		{
			logger.info("插件初始化...");
			initPlugins();
		}
		catch (java.lang.Throwable ex)
		{
			logger.error("插件初始化异常", ex);
		}
	}

	private void unzip() throws ZipException, IOException
	{
		String staticPath = Constants.webappAbsolutePath + File.separator + "static";
		ZipUtils.unZipDirectory(staticPath + File.separator + "zip", staticPath);
	}

	@Override
	public void initPlugins()
	{
		Map<String, Object> controllerMap = applicationContext.getBeansWithAnnotation(Controller.class);
		initPlugins(controllerMap);
	}

	/**
	 * 模块初始化
	 * 
	 * @param controllerMap
	 */
	private void initPlugins(Map<String, Object> controllerMap)
	{
		Collection<Object> values = controllerMap.values();
		for (Object ctrl : values)
		{
			Class<?> clazz = AopUtils.getTargetClass(ctrl);
			// Class<?> clazz = ctrl.getClass();
			if (isPlugins(clazz))
			{
				initPlugins(clazz);
			}
		}
		rightService.checkRight(rightIds);
	}

	private List<String> jarFiles = new ArrayList<String>();

	/**
	 * 模块初始化
	 * 
	 * @param clazz
	 */
	private void initPlugins(Class<?> clazz)
	{
		String pluginsFileName = "/";
		String pluginsName = "";
		String pluginsVersion = "";
		String jarPath = getPath(clazz);
		try
		{
			Attributes pluginsAttributes = null;
			JarFile jarFile = new JarFile(jarPath);
			File file = new File(jarFile.getName());
			pluginsFileName = file.getName();
			Manifest manifest = jarFile.getManifest();
			if (manifest != null)
			{
				pluginsAttributes = jarFile.getManifest().getMainAttributes();
			}
			jarFile.close();

			if (pluginsAttributes != null)
			{
				pluginsVersion = pluginsAttributes.getValue("pluginsVersion");
				pluginsName = pluginsAttributes.getValue("pluginsName");
			}
			if (StringUtils.isEmpty(pluginsName))
			{
				pluginsName = pluginsFileName.substring(0, pluginsFileName.length() - 4);
				pluginsVersion = pluginsName.substring(pluginsName.indexOf('-') + 1);
				pluginsName = pluginsName.substring(0, pluginsName.indexOf('-'));
			}
		}
		catch (Throwable e)
		{// 不是jar包
			jarFiles.add(jarPath);
			pluginsFileName = "/";
			pluginsName = "cnplay";
			pluginsVersion = "1.0.0";
			logger.error("getAttributes error " + e.getMessage());
		}
		try
		{
			Plugins plugins = rightService.getByField(Plugins.class, "name", pluginsName);
			if (plugins == null)
			{
				plugins = new Plugins();
			}
			plugins.setFileName(pluginsFileName);
			plugins.setVersionNum(pluginsVersion);
			plugins.setName(pluginsName);
			rightService.save(plugins);
			if (plugins.getStatus() == Status.Normal)
			{
				initRight(plugins, clazz);
			}
			if (jarFiles.indexOf(jarPath) == -1)
			{
				jarFiles.add(jarPath);
				File dir = new File(Constants.extjsPluginsAbsolutePath + File.separator + pluginsName);
				dir.deleteOnExit();
				ZipUtils.unzip(jarPath, pluginsName, Constants.extjsPluginsAbsolutePath);
			}
		}
		catch (Throwable e)
		{
			Constants.Installed = -1;
			Constants.InstalledMessage = "程序启动异常：" + StringUtil.toString(e);
			logger.error("模块初始化异常");
			logger.error(clazz.getName() + " initPlugins error ", e);
		}
	}

	/**
	 * 判断类是否定义功能注解RightAnnotation
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean isPlugins(Class<?> clazz)
	{
		RightAnnotation ra = clazz.getAnnotation(RightAnnotation.class);
		if (ra != null)
			return true;
		Method[] methods = clazz.getMethods();
		for (Method method : methods)
		{
			ra = method.getAnnotation(RightAnnotation.class);
			if (ra != null)
				return true;
		}
		return false;
	}

	/**
	 * 初始化权限
	 * 
	 * @param plugins
	 * @param clazz
	 */
	private void initRight(Plugins plugins, Class<?> clazz)
	{
		RequestMapping rmp = clazz.getAnnotation(RequestMapping.class);
		String prefix = "";
		if (rmp != null)
		{
			String[] urls = rmp.value();
			if (urls != null && urls.length > 0)
			{
				prefix = urls[0];
			}
		}

		Method[] methods = clazz.getMethods();
		for (Method method : methods)
		{
			RightAnnotation ra = method.getAnnotation(RightAnnotation.class);
			if (ra != null)
			{
				logger.debug((clazz.getName() + "." + method.getName() + ra));
				Right right = new Right();
				RequestMapping rmm = method.getAnnotation(RequestMapping.class);
				String url = "";
				if (rmm != null)
				{
					String[] urls = rmm.value();
					if (urls != null && urls.length > 0)
					{
						url = urls[0];
					}
				}
				right.setName(ra.name());
				right.setPlugins(plugins);
				right.setUrl(prefix + url);
				right.setFromClass(clazz.getName());
				right.setComponent(ra.component());
				right.setText(ra.name());
				String[] names = right.getText().split(Constants.RIGHT_SEPARATOR);
				right.setText(names[names.length - 1]);
				right.setIcon(ra.icon());
				right.setIconCls(ra.iconCls());
				right.setNeedCheck(ra.needCheck());
				right.setResource(ra.resource());
				right.setMemo(ra.name());
				right.setDebug(Boolean.toString(ra.debug()));
				if (ra.button())
				{
					right.setType(RightType.BUTTON.name());
				}
				else if (StringUtils.isEmpty(ra.component()))
				{
					right.setType(RightType.URL.name());
					right.setComponent(right.getUrl());
				}
				else
				{
					right.setType(RightType.COMPONENT.name());
				}
				right.setSort(ra.sort());
				if (ra.delete())
				{
					right.setStatus(Status.Delete);
				}
				right.setLeaf(true);
				@Memo("保存目录")
				String parentId = saveRightDir(plugins, clazz, ra, names);
				Right find = rightService.getByName(ra.name());
//				if (find == null)
//				{
//					find = rightService.getByFromClassAndText(right.getFromClass(), right.getText());
//				}
//				if (find == null)
//				{
//					find = rightService.getBy(right.getUrl(), right.getComponent(), right.getFromClass());
//				}
				if (find != null)
				{
					if (RightType.valueOf(find.getType()) == RightType.DIR)
					{
						find.setLeaf(right.getLeaf());
						find.setType(right.getType());
					}
					if (find.getSort() <= 0)
					{
						find.setSort(right.getSort());
					}
					if (right.getStatus() == Status.Delete)
					{
						find.setStatus(Status.Delete);
					}
					find.setResource(right.getResource());
					find.setDebug(right.getDebug());
					find.setComponent(right.getComponent());
					find.setUrl(right.getUrl());
					find.setName(right.getName());
					find.setUpdateTime(new Date());
					right = find;
				}
				right.setParentId(parentId);
				this.saveRight(right);
			}
		}
	}

	private static final List<String> rightIds = new ArrayList<String>();

	private void saveRight(Right right)
	{
		rightService.save(right);
		rightIds.add(right.getId());
	}

	private String saveRightDir(Plugins plugins, Class<?> clazz, RightAnnotation ra, String[] names)
	{
		String parentId = "";
		String fullName = "";
		for (int i = 0; i < names.length - 1; i++)
		{
			String name = names[i];
			fullName = (StringUtils.isEmpty(fullName) ? "" : (fullName + "/")) + name;
			if (StringUtils.isNotEmpty(name))
			{
//				String id = Converter.createId(name);
				Right tmp = rightService.getByName(fullName);
//				if (tmp == null)
//				{
//					tmp = rightService.getByField("text", name);
//				}
				if (tmp == null)
				{
					tmp = new Right();
//					tmp.setId(id);
					tmp.setSort(ra.sort());
					tmp.setParentId(parentId);
					tmp.setPlugins(plugins);
					tmp.setFromClass(clazz.getName());
					tmp.setText(name);
					tmp.setType(RightType.DIR.name());
					tmp.setDebug(Boolean.toString(ra.debug()));
					tmp.setName(fullName);
					tmp.setMemo(fullName);
				}
				else
				{
					tmp.setParentId(parentId);
					if (tmp.getSort() <= 0)
					{
						tmp.setSort(ra.sort());
					}
					tmp.setDebug(Boolean.toString(ra.debug()));
					tmp.setName(fullName);
					tmp.setMemo(fullName);
				}
				this.saveRight(tmp);
				parentId = tmp.getId();
			}
		}
		return parentId;
	}

	private String getPath(Class<?> clazz)
	{
		String path = null;
		Enumeration<Permission> permissions = clazz.getProtectionDomain().getPermissions().elements();
		while (permissions.hasMoreElements())
		{
			Permission permission = permissions.nextElement();
			if (permission instanceof FilePermission)
			{
				path = permission.getName();
				break;
			}
		}
		if (StringUtils.isEmpty(path))
		{
			path = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
		}
		if (path.endsWith("-"))
		{
			path = path.substring(0, path.length() - 1);
		}
		try
		{
			path = URLDecoder.decode(path, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error(e.getMessage(), e);
		}
		return path;
	}

	private List<Right> allRights = new ArrayList<Right>();

	public boolean check(User sessionUser, String url)
	{
		if (sessionUser.isDebug())
		{
			return true;
		}
		String urls = configService.getByName(Constants.RightFilter, "");
		if (StringUtils.isNotBlank(urls))
		{
			if (matcher(url, urls))
			{
				return false;
			}
		}
		if (userService.isRoot(sessionUser.getId()))
		{
			return true;
		}
		User user = userService.getById(sessionUser.getId());
		if (user != null)
		{
			if (allRights.size() == 0)
			{
				allRights = rightService.findAll();
			}
			List<Right> rightList = allRights;
			List<String> rightIds = new ArrayList<String>();
			for (Right r : rightList)
			{
				if (url.equals(r.getUrl()))
				{
					rightIds.add(r.getId());
				}
			}
			if (rightIds.size() > 0)
			{
				return rightService.findUserMatcherRight(user, rightIds);
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	public boolean checkOld(User sessionUser, String url)
	{
		if (sessionUser.isDebug())
		{
			return true;
		}
		String urls = configService.getByName(Constants.RightFilter, "");
		if (StringUtils.isNotBlank(urls))
		{
			if (matcher(url, urls))
			{
				return false;
			}
		}
		User user = userService.getById(sessionUser.getId());
		if (user != null)
		{
			List<Right> rightList = rightService.findAuthByUserId(user.getId());
			for (Right r : rightList)
			{
				if (matcher(url, r.getUrl()) || matcher(url, r.getResource()))
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean matcher(String url, String resource)
	{
		return rightService.matcher(url, resource);
	}

	@Override
	public boolean isEmpower(String userId)
	{
		return userEmpowerService.isEmpower(userId);
	}

	@Override
	public List<Right> findMenu(String parentId, String userId)
	{
		if (parentId == null)
		{
			parentId = "";
		}
		List<Right> menuList = rightService.findAuthByUserIdAndParentId(userId, parentId);
		Collections.sort(menuList, new Comparator<Right>()
		{
			public int compare(Right arg0, Right arg1)
			{
				if (arg0.getSort().intValue() == arg1.getSort().intValue())
				{
					return arg0.getCreateTime().getTime() > arg1.getCreateTime().getTime() ? 1 : -1;
				}
				return arg0.getSort() > arg1.getSort() ? 1 : -1;
			}
		});
		return menuList;
	}

	@Memo("按钮是否隐藏,true:隐藏，false:可见")
	@Override
	public boolean disabled(String userId, String url)
	{
		List<Right> rightLst = rightService.findAuthByUserId(userId);
		for (Right r : rightLst)
		{
			String urlDb = org.apache.commons.lang3.StringUtils.defaultString(r.getUrl(), "");
			if (urlDb.equals(url))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isNeedCheck(String url)
	{
		int allNeedCheck = configService.getByName(SysConfigName.AllNeedCheck, 0);
		if (allNeedCheck == 2)
		{
			return false;
		}
		Right right = rightService.getByField("url", url);
		if (right != null && (allNeedCheck == 1 || right.getNeedCheck()) && RightType.valueOf(right.getType()) == RightType.BUTTON)
		{
			return true;
		}
		return false;
	}

	@Override
	public Json<LoginUser> login(LoginUser loginUser) 
	{
		return userService.login(loginUser);
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}
}
