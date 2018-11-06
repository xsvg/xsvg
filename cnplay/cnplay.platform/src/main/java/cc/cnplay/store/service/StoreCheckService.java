package cc.cnplay.store.service;

import java.util.Date;
import java.util.List;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.store.domain.StoreCheck;
import cc.cnplay.store.domain.StoreCheckItem;

public interface StoreCheckService extends GenericService<StoreCheck, String> {

	DataGrid<StoreCheck> findPage(Date startDate, Date endDate, String orgId,
			String operator, int page, int pageSize);

	StoreCheck getCheck(String id);

	StoreCheck check(String orgId, String operator, List<String> rfidList);

	void removeCheck(String id);

	DataGrid<StoreCheckItem> temPagee(String id, int page, int pageSize);

	StoreCheck saveCheckItem(StoreCheck form);

}
