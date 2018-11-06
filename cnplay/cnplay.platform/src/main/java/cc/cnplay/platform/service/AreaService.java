package cc.cnplay.platform.service;

import java.util.List;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.service.GenericService;
import cc.cnplay.platform.domain.Area;

public interface AreaService extends GenericService<Area, String>
{

	List<Area> findByParentId(String parentId);

	Area getByLevelCode(String levelCode);

	void removeLogic(String orgid) throws CnplayRuntimeException;

	boolean existRoot();

	Area save(Area area);

	String getLevelCodeById(String id);

	Area getRoot();

	boolean isRoot(Area org);

	void checked(String[] areaId, List<Area> list);
}
