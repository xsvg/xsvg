package cc.cnplay.store.dao;

import java.util.List;

import cc.cnplay.core.dao.GenericDao;
import cc.cnplay.store.domain.StoreArea;

public interface StoreAreaDao extends GenericDao<StoreArea, String> {
	StoreArea getByLevelCode(String orgId, String levelCode);

	String getLevelCodeById(String id);

	/**
	 * 取根据机构
	 * 
	 * @return
	 */
	StoreArea getRoot(String orgId);

	List<StoreArea> findByParentId(String orgId, String parentId);

	boolean existCode(StoreArea org);

	boolean existName(StoreArea org);

	StoreArea getByCode(String orgId, String code);

	boolean existChild(String orgId, String parentId);

}
