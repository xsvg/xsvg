package cc.cnplay.platform.service;

import cc.cnplay.core.Listener;
import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.domain.SysConfig;

public interface SystemConfigService extends GenericService<SysConfig, String>//, InitializeService
{

	String getByName(String name);

	SysConfig findByName(String name);

	void saveForm(SysConfig sysConfig);

	SysConfig save(String name, String value, boolean userNotConf);

	SysConfig initDefault(String moduleName, String name, String value, String memo, int sort);

	SysConfig initDefault(String moduleName, String name, String value, String memo, int sort, boolean userNotConf);

	String getByName(String name, String defaultValue);

	void smsDriverNewInstance();

	DataGrid<SysConfig> findUserPage(int page, int pageSize);

	int getByName(String name, int defaultValue);

	/**
	 * 添加保存后的监听器
	 * @param listener
	 */
	void addAfterSaveConfListener(Listener<SysConfig> listener);
}
