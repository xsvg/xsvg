package cc.cnplay.platform.service;

import java.util.List;
import java.util.Map;

import cc.cnplay.core.Listener;
import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.service.GenericService;
import cc.cnplay.platform.domain.SysConfig;
import cc.cnplay.platform.domain.SysConfigLog;

public interface SysConfigLogService extends GenericService<SysConfigLog, String>
{

	<T> List<T> findByJsonClazz(Class<T> jsonClazz);

	<T> void saveJsonClazz(T value);

	List<SysConfigLog> findAllDesc();

	//void saveConfigAndLog(String pwdParameter, String bitConfByPWD, String bitConfByFinger, String lockModel, String openLockNum, String openLockDate, String mutiOpenLock) throws Exception;

	void saveConfigAndLog(Map<String, String> map) throws CnplayRuntimeException;
	
	/**
	 * 添加缓存监听器
	 * @param listener
	 */
	void addCacheConfListener(Listener<SysConfig> listener);
}
