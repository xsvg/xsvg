package cc.cnplay.store.dao;

import cc.cnplay.core.dao.GenericDao;
import cc.cnplay.store.domain.StoreSn;

public interface StoreSnDao extends GenericDao<StoreSn, String> {

	StoreSn nextSn(String rfid);

}
