package cc.cnplay.platform.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cc.cnplay.platform.domain.Plugins;

public interface PluginsDao extends JpaRepository<Plugins,String>
{

}
