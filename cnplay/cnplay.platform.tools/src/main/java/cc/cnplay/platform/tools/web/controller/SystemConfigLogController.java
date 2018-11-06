package cc.cnplay.platform.tools.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.domain.Identity;
import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.SysConfig;
import cc.cnplay.platform.domain.SysConfigLog;
import cc.cnplay.platform.service.SysConfigLogService;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.web.controller.AbsController;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("system/config/log")
public class SystemConfigLogController extends AbsController
{

	private final static ObjectMapper om = new ObjectMapper();

	public static final String[] configKeys = { "pwdParameter", // 参数1：密码参数
			"bitConfByPWD", // 参数2：密码是否
			"bitConfByFinger",// 参数3：密码及指纹是否加密
			"lockModel", // 参数4：锁具模式
			"openLockNum", // 参数5：开锁次数
			"openLockDate", // 参数6：开锁时间限制
			"mutiOpenLock", // 参数7：开启多把锁
			"lockWaveAlarm", // 参数8：锁震动状态上动上报：1.允许;0.不允许
			"lockLockedState", // 参数9：密码错误锁定状态上动上报：1.允许;0.不允许
			"lockHighTempAlarm" };// 参数10：锁过温状态上动上报：1.允许;0.不允许

	@Autowired
	private SysConfigLogService logService;
	@Autowired
	private SystemConfigService configService;

	@SuppressWarnings({ "unchecked" })
	@RequestMapping("/list")
	@ResponseBody
	@RightAnnotation(name = "系统调试/重要参数", component = "platform.tools.view.SysConfigLogPanel", debug = true, sort = 90200)
	public Json<List<Map<String, Object>>> listAll()
	{
		Json<List<Map<String, Object>>> result = new Json<List<Map<String, Object>>>();
		try
		{
			List<SysConfigLog> list = logService.findAllDesc();
			List<Map<String, Object>> grid = new ArrayList<Map<String, Object>>();
			grid.add(getCurrent());
			for (SysConfigLog log : list)
			{
				Map<String, Object> map = om.readValue(log.getJsonValue(), Map.class);
				map.put("id", log.getId());
				map.put("createTimeStr", DateUtil.format(log.getCreateTime()));
				grid.add(map);
			}
			result.OK(grid, null);
		}
		catch (Exception e)
		{
			logger.error(e);
			result.NG("操作失败，请重试！");
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping("/load")
	@ResponseBody
	public Json<?> load()
	{
		try
		{
			return new Json<Map>(getCurrent());
		}
		catch (Exception e)
		{
			logger.error(e);
			return new Json<Object>(null, false, "数据获取失败");
		}
	}

	private Map<String, Object> getCurrent()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", Identity.randomID());
		map.put("createTimeStr", DateUtil.format(new Date()));
		for (String key : configKeys)
		{
			SysConfig config = configService.findByName(key);
			if (config != null)
			{
				map.put(key, config.getValue());
			}
			else
			{
				map.put(key, null);
			}
		}
		return map;
	}

	@RightAnnotation(name = "系统调试/重要参数/保存", button = true, resource = "/system/config/log/load", debug = true, sort = 90201)
	@RequestMapping("/save")
	@ResponseBody
	public Json<?> save()
	{
		try
		{
			Map<String, String> map = new HashMap<String, String>();
			for (String key : configKeys)
			{
				map.put(key, this.getParameter(key));
			}
			// logService.saveConfigAndLog(pwdParameter, bitConfByPWD, bitConfByFinger, lockModel, openLockNum, openLockDate, mutiOpenLock);
			logService.saveConfigAndLog(map);
			return new Json<Object>(null, true);
		}
		catch (Exception e)
		{
			logger.error(e);
			return new Json<Object>(null, false, "数据保存失败");
		}
	}
}
