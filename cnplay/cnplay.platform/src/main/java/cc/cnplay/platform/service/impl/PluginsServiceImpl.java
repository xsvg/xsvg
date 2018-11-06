package cc.cnplay.platform.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.dao.PluginsDao;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.service.PluginsService;

import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class PluginsServiceImpl extends AbsGenericService<Plugins, String> implements PluginsService
{

	@Resource
	private PluginsDao pluginsDao;

	@SuppressWarnings("rawtypes")
	public Json<?> updateStatus(String id,Status status){
		Json json = new Json();
		Plugins plugins = pluginsDao.getOne(id);
		if(plugins==null){
			json.setMsg("记录不存在，可能已被删除");
			return json;
		}
		plugins.setStatus(status);
		pluginsDao.save(plugins);
		json.setSuccess(true);
		return json;
	}

	@Override
	public boolean existPlugin(String name) {
		Search search = new Search(Plugins.class);
		search.addFilterLike("name", name+"%" );
		
		@SuppressWarnings("unchecked")
		List<Plugins> userLst = dao().search(search);
		return !userLst.isEmpty();
	}
	
	
}
