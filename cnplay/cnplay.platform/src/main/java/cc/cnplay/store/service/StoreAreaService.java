package cc.cnplay.store.service;

import java.util.List;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.store.domain.StoreArea;

public interface StoreAreaService extends GenericService<StoreArea, String> {
	/**
	 * 显示顶级组织及以下
	 * 
	 * @param parentId
	 * @return
	 */

	List<StoreArea> findByParentId(String orgId, String parentId);

	StoreArea getByLevelCode(String orgId, String levelCode);

	StoreArea save(StoreArea orgForm);

	String getLevelCodeById(String id);

	/**
	 * 取根机构
	 * 
	 * @return
	 */
	StoreArea getRoot(String orgId);

	boolean isRoot(StoreArea org);

	StoreArea getByCode(String orgId, String code);

	boolean existChild(String orgId, String id);

	void checked(String[] areaId, List<StoreArea> list);

}
