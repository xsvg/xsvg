package cc.cnplay.platform.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.Listener;
import cc.cnplay.core.service.InitializeService;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.domain.SysConfig;
import cc.cnplay.platform.domain.SysConfigName;
import cc.cnplay.platform.service.SystemConfigService;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Service
@Transactional
public class SystemConfigServiceImpl extends AbsGenericService<SysConfig, String> implements SystemConfigService, InitializeService
{

	private List<Listener<SysConfig>> afterSaveListener = new ArrayList<Listener<SysConfig>>();

	/**
	 * 添加缓存监听器
	 * @param listener
	 */
	public void addAfterSaveConfListener(Listener<SysConfig> listener)
	{
		afterSaveListener.add(listener);
	}

	/**
	 * 执行监听器
	 * @param sysConf
	 */
	private void triggerAfterSaveListener(SysConfig sysConf)
	{
		if (afterSaveListener != null && afterSaveListener.size() > 0)
		{
			for (Listener<SysConfig> listener : afterSaveListener)
			{
				listener.onListener(this, sysConf);
			}
		}
	}

	@Override
	public String getByName(String name)
	{
		return getByName(name, "");
	}

	@Override
	public String getByName(String name, String defaultValue)
	{
		String value = Constants.getConfig(name);
		if (StringUtils.isEmpty(value))
		{
			SysConfig v = this.getByField("name", name);
			if (v != null)
			{
				value = v.getValue();
			}
		}
		return value != null ? value : defaultValue;
	}

	@Override
	public int getByName(String name, int defaultValue)
	{
		String value = getByName(name);
		try
		{
			return Integer.parseInt(value);
		}
		catch (Exception ex)
		{
			return defaultValue;
		}
	}

	@Override
	public SysConfig findByName(String value)
	{
		return this.getByField("name", value);
	}

	@Override
	public SysConfig save(String name, String value, boolean userNotConf)
	{
		SysConfig sc = findByName(name);
		if (sc == null)
		{
			sc = new SysConfig();
			sc.setModuleName(SysConfigName.module_runtime);
			sc.setName(name);
			sc.setSort(0);
			dao().save(sc);
		}
		sc.setValue(value);
		sc.setUserConf(userNotConf);
		this.save(sc);
		triggerAfterSaveListener(sc);
		return sc;
	}

	@Override
	public SysConfig initDefault(String moduleName, String name, String value, String memo, int sort)
	{
		return this._initDefault(moduleName, name, value, memo, sort, false);
	}

	@Override
	public SysConfig initDefault(String moduleName, String name, String value, String memo, int sort, boolean userNotConf)
	{
		return this._initDefault(moduleName, name, value, memo, sort, userNotConf);
	}

	private SysConfig _initDefault(String moduleName, String name, String value, String memo, int sort, boolean userNotConf)
	{
		SysConfig sc = findByName(name);
		if (sc == null)
		{
			sc = new SysConfig();
			sc.setModuleName(moduleName);
			sc.setName(name);
			sc.setValue(value);
			if (memo != null)
			{
				sc.setMemo(memo);
			}
			sc.setSort(sort);
			dao().save(sc);
		}
		if (userNotConf)
		{
			sc.setUserConf(userNotConf);
			dao().save(sc);
		}

		return sc;
	}

	public int getSort()
	{
		return 10;
	}

	@PreDestroy
	public void destroy()
	{
		//SmsFactory.instance().close();
	}

	// @PostConstruct
	public void init()
	{
		Constants.expReportFmt = getByName(Constants.expReportFmtConf, Constants.expReportFmt);
		_initDefault(SysConfigName.module_runtime, SysConfigName.system_title, "抵质押品库房管理系统", "系统标题", 0, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.system_name, " ", "系统名称", 0, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.home_desktop_panel, "", "桌面面板", 1, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.home_logo_img, "", "LOGO地址", 2, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.home_logobg_img, "", "LOGO背景", 3, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.home_logout_text, "注消登录", "注消登录显示名称", 3, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.home_font_style, "color: red;", "标题CSS样式", 4, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.authorization_login_not_tip, "你的登录已经过期，请重新登录！", "用户登录信息过期提示信息", 5, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.authorization_check_not_tip, "您的访问受限！", "权限校验失败提示信息", 6, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.Login_Try_No, "5", "每天可以尝试登录失败的次数,达到失败次数本日不能登录,需等第二日自动解锁或管理员解锁", 7, false);

		_initDefault(SysConfigName.module_runtime, SysConfigName.userCodeMinLength, "1", "用户编码最小长度", 8, true);
		_initDefault(SysConfigName.module_runtime, SysConfigName.userCodeMaxLength, "12", "用户编码最大长度", 9, true);
		_initDefault(SysConfigName.module_runtime, SysConfigName.UpdatePwdNeed, "1", "新增或重置密码后的用户必须改密码：0.不需修改密码；1.需要修改密码", 10, false);
		_initDefault(SysConfigName.module_runtime, SysConfigName.AllNeedCheck, "0", "复核全局配置：0.根据功能配置；1.必须复核；2.不需复核", 10, false);
		//_initDefault(SysConfigName.module_runtime, SysConfigName.fingerDriver, DefaultDriver.class.getName(), "指纹验证方式", 1080, false);
		SysConfig config = this.findByName(SysConfigName.smsDevice);
		if (config == null)
		{
			//_initDefault(SysConfigName.module_runtime, SysConfigName.smsDriver, SmsFileDriver.class.getName(), "inboxSerialPort=COM10+COM11;inboxBaudRate=115200;inboxServerAddr=127.0.0.1;inboxPort=9090;gsmServerAddr=127.0.0.1;gsmServerPort=9092;gsmServerInstance=1;", 10, false);
		}
		smsDriverNewInstance();
	}

	@Override
	public void smsDriverNewInstance()
	{
		
	}

	@Override
	public void saveForm(SysConfig sysConfig)
	{
		SysConfig sc = dao().getById(SysConfig.class, sysConfig.getId());
		if (sc == null)
		{
			sc = new SysConfig();
			sc.setModuleName(SysConfigName.module_runtime);
		}
		sysConfig.setModuleName(sc.getModuleName());
		// BeanUtils.copyProperties(sc, sysConfigVO);
		this.save(sysConfig);
		triggerAfterSaveListener(sc);
	}

	@Override
	public DataGrid<SysConfig> findUserPage(int page, int pageSize)
	{
		Search search = new Search(SysConfig.class);
		search.addFilterEqual("userConf", Boolean.FALSE);
		search.addSortAsc("sort");
		search.addSortDesc("createTime");
		search.setFirstResult(getFirstResult(page, pageSize));
		search.setMaxResults((pageSize > 0 ? pageSize : 20));
		@SuppressWarnings("unchecked")
		SearchResult<SysConfig> result = dao().searchAndCount(search);
		List<SysConfig> olList = result.getResult();
		DataGrid<SysConfig> dg = new DataGrid<SysConfig>((int) result.getTotalCount(), olList, pageSize, page);
		return dg;
	}
}
