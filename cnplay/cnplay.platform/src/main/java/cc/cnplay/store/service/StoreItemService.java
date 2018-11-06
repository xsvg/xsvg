package cc.cnplay.store.service;

import java.util.Date;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.vo.StoreInVO;
import cc.cnplay.store.vo.StoreOutVO;

public interface StoreItemService extends GenericService<StoreItem, String> {

	DataGrid<StoreItem> findPageLikeName(Date startDate, Date endDate,
			String orgId, String dywOwner, int page, int pageSize);

	DataGrid<StoreInVO> findInPageLikeName(Date startDate, Date endDate,
			String orgId, String dywOwner, int page, int pageSize);

	DataGrid<StoreOutVO> findOutPageLikeName(Date startDate, Date endDate,
			String orgId, String dywOwner, int page, int pageSize);
	
	StoreInVO in(StoreInVO form);

	StoreInVO getInVoById(String id);

	StoreOutVO getOutVoById(String id);

	StoreOutVO out(StoreOutVO form);

	StoreOutVO getOutById(String id);

}
