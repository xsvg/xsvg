package cc.cnplay.platform.dao;

import java.util.List;

import cc.cnplay.core.dao.GenericDao;
import cc.cnplay.platform.domain.Area;

public interface AreaDao extends GenericDao<Area, String>
{
	Area getByLevelCode(String levelCode);

	String getLevelCodeById(String id);

	/**
	 * 取根据机构
	 * 
	 * @return
	 */
	Area getRoot();

	List<Area> findByParentId(String parentId);

	List<Area> findByStartsWithLevelCode(String levelCode);

	boolean exitLevelCode(String levelCode);

	String getNextLevelCodeByParent(String parentId);

	boolean exitName(Area area);

	boolean exitCode(Area area);
}
