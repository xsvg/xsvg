package cc.cnplay.store.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.domain.StoreMove;
import cc.cnplay.store.vo.StoreInVO;
import cc.cnplay.store.vo.StoreOutVO;

public interface StoreItemService extends GenericService<StoreItem, String> {

	DataGrid<StoreItem> findPageLikeName(Date startDate, Date endDate, String orgId, String dywOwner, String storeman,
			int page, int pageSize);

	DataGrid<StoreInVO> findInPageLikeName(Date startDate, Date endDate, String orgId, String dywOwner, int page,
			int pageSize);

	DataGrid<StoreItem> findInTmpPage(String orgId, int page, int pageSize);

	DataGrid<StoreOutVO> findOutPageLikeName(Date startDate, Date endDate, String orgId, String dywOwner, int page,
			int pageSize);

	StoreInVO in(StoreInVO form);
	
	StoreInVO inModify(StoreInVO form);

	StoreInVO getInVoById(String id);

	StoreOutVO getOutVoById(String id);

	StoreOutVO out(StoreOutVO form);

	StoreOutVO getOutByItemId(String id);

	StoreItem getInVoByRfid(String rfid);

	List<StoreItem> findByDywOwner(String orgId, String dywOwner, String dywOwnerId);

	DataGrid<StoreItem> findPageByStoreman(String storeman, int page, int pageSize);

	boolean moveto(StoreMove form);

	HSSFWorkbook export(List<StoreItem> storeman) throws Exception;

	boolean existSn(String sn);

}
