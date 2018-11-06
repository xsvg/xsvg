package cc.cnplay.platform.service;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Status;

public interface PluginsService extends GenericService<Plugins, String>
{
	public Json<?> updateStatus(String id,Status status);
	
	boolean existPlugin(String name);
}
