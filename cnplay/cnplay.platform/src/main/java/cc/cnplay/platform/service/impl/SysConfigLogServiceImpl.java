package cc.cnplay.platform.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import cc.cnplay.core.Listener;
import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.platform.domain.SysConfig;
import cc.cnplay.platform.domain.SysConfigLog;
import cc.cnplay.platform.domain.SysConfigName;
import cc.cnplay.platform.service.SysConfigLogService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class SysConfigLogServiceImpl extends AbsGenericService<SysConfigLog, String> implements SysConfigLogService
{
	private final static ObjectMapper om = new ObjectMapper();

	private List<Listener<SysConfig>> confCacheListener=new ArrayList<Listener<SysConfig>>();
	
	/**
	 * 添加缓存监听器
	 * @param listener
	 */
	public void addCacheConfListener(Listener<SysConfig> listener){
		confCacheListener.add(listener);
	}
	
	/**
	 * 执行监听器
	 * @param sysConf
	 */
	private void triggerCacheListener(SysConfig sysConf){
		if(confCacheListener!=null&&confCacheListener.size()>0){
			for(Listener<SysConfig> listener:confCacheListener){
				listener.onListener(this, sysConf);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findByJsonClazz(Class<T> jsonClazz)
	{
		Search search = new Search(SysConfigLog.class);
		search.addFilterEqual("jsonClazz", jsonClazz.getName());
		search.addSortDesc("createTime");
		List<SysConfigLog> logList = dao().search(search);
		List<T> valueList = new ArrayList<T>();
		for (SysConfigLog log : logList)
		{
			try
			{
				valueList.add(om.readValue(log.getJsonValue(), jsonClazz));
			}
			catch (Throwable e)
			{
				logger.error("", e);
			}
		}
		return valueList;
	}

	@Override
	public <T> void saveJsonClazz(T value) throws CnplayRuntimeException
	{
		try
		{
			SysConfigLog confLog = new SysConfigLog();
			confLog.setJsonClazz(value.getClass().getName());
			confLog.setJsonValue(om.writeValueAsString(value));
			dao().save(confLog);
		}
		catch (JsonProcessingException e)
		{
			throw new CnplayRuntimeException("转换JSON异常", e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SysConfigLog> findAllDesc()
	{
		Search search = new Search(SysConfigLog.class);
		search.addFilterEqual("jsonClazz", HashMap.class.getName());
		search.addSortDesc("createTime");
		return dao().search(search);
	}
//
//	@Override
//	public void saveConfigAndLog(String pwdParameter, String bitConfByPWD, String bitConfByFinger, String lockModel, String openLockNum, String openLockDate, String mutiOpenLock) throws Exception
//	{
//		Date now = new Date();
//		SysConfigLog log = new SysConfigLog();
//		log.setCreateTime(now);
//		Map<String, String> map = new HashMap<String, String>();
//		log.setJsonClazz(map.getClass().getName());
//
//		SysConfig config = dao().getByField(SysConfig.class, "name", "pwdParameter");
//		map.put("pwdParameter", config.getValue());
//		config.setValue(pwdParameter);
//		config.setUpdateTime(now);
//		dao().save(config);
//
//		config = dao().getByField(SysConfig.class, "name", "bitConfByPWD");
//		map.put("bitConfByPWD", config.getValue());
//		config.setValue(bitConfByPWD);
//		config.setUpdateTime(now);
//		dao().save(config);
//
//		config = dao().getByField(SysConfig.class, "name", "bitConfByFinger");
//		map.put("bitConfByFinger", config.getValue());
//		config.setValue(bitConfByFinger);
//		config.setUpdateTime(now);
//		dao().save(config);
//
//		config = dao().getByField(SysConfig.class, "name", "lockModel");
//		map.put("lockModel", config.getValue());
//		config.setValue(lockModel);
//		config.setUpdateTime(now);
//		dao().save(config);
//
//		config = dao().getByField(SysConfig.class, "name", "openLockNum");
//		map.put("openLockNum", config.getValue());
//		config.setValue(openLockNum);
//		config.setUpdateTime(now);
//		dao().save(config);
//
//		config = dao().getByField(SysConfig.class, "name", "openLockDate");
//		map.put("openLockDate", config.getValue());
//		config.setValue(openLockDate);
//		config.setUpdateTime(now);
//		dao().save(config);
//
//		config = dao().getByField(SysConfig.class, "name", "mutiOpenLock");
//		map.put("mutiOpenLock", config.getValue());
//		config.setValue(mutiOpenLock);
//		config.setUpdateTime(now);
//		dao().save(config);
//
//		log.setJsonValue(om.writeValueAsString(map));
//		dao().save(log);
//	}

	@Override
	public void saveConfigAndLog(Map<String, String> map) throws CnplayRuntimeException
	{
		try
		{
			Date now = new Date();
			SysConfigLog log = new SysConfigLog();
			log.setCreateTime(now);
			log.setJsonClazz(map.getClass().getName());
			log.setJsonValue(om.writeValueAsString(map));
			dao().save(log);
			SysConfig config = new SysConfig();
			for (String key : map.keySet())
			{
				config = dao().getByField(SysConfig.class, "name", key);
				if (config == null)
				{
					config = new SysConfig();
				}
				config.setModuleName(SysConfigName.module_runtime);
				config.setUserConf(Boolean.TRUE);
				config.setName(key);
				config.setValue(map.get(key));
				config.setUpdateTime(now);
				dao().save(config);
			}
			triggerCacheListener(config);
		}
		catch (JsonProcessingException e)
		{
			throw new CnplayRuntimeException(e.getMessage(), e);
		}
	}

}
