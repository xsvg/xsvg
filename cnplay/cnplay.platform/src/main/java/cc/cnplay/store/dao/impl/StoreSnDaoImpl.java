package cc.cnplay.store.dao.impl;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Search;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.store.dao.StoreSnDao;
import cc.cnplay.store.domain.StoreSn;

@Repository
public class StoreSnDaoImpl extends GenericDaoImpl<StoreSn, String> implements StoreSnDao {

	@Override
	public StoreSn nextSn(String rfid) {
		Search search = new Search(StoreSn.class);
		search.addFilterEqual("rfid", rfid);
		search.setMaxResults(1);
		StoreSn sn = (StoreSn) this.searchUnique(search);
		if (sn == null) {
			sn = new StoreSn();
			DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		}
		return sn;
	}
}
