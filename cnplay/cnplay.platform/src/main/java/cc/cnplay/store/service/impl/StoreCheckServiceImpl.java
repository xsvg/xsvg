package cc.cnplay.store.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.util.BeanUtils;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.store.domain.StoreCheck;
import cc.cnplay.store.domain.StoreCheckItem;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.service.StoreCheckService;

@Service
public class StoreCheckServiceImpl extends
		AbsGenericService<StoreCheck, String> implements StoreCheckService {

	@Override
	public StoreCheck check(String orgId, String operator, List<String> rfidList) {
		StoreCheck check = new StoreCheck();
		check.setOrgId(orgId);
		check.setOperator(operator);
		check.setCheckDate(new Date());
		List<StoreItem> itemList = this.findByField(StoreItem.class, "orgId",
				orgId);
		check.setItemList(new ArrayList<StoreCheckItem>());
		int checked = 0;
		for (StoreItem item : itemList) {
			StoreCheckItem sci = new StoreCheckItem();
			BeanUtils.copyProperties(sci, item);
			sci.setId(StoreCheckItem.randomID());
			sci.setCheckId(check.getId());
			check.getItemList().add(sci);
			if (rfidList.contains(item.getRfid())) {
				sci.setCheckFlag(StoreCheckItem.CHECKFLAG_OK);
				checked++;
			} else {
				sci.setCheckFlag(StoreCheckItem.CHECKFLAG_NOT);
			}
		}
		Collections.sort(check.getItemList(), new Comparator<StoreCheckItem>() {
			@Override
			public int compare(StoreCheckItem o1, StoreCheckItem o2) {
				return o1.getCheckFlag().compareTo(o2.getCheckFlag());
			}
		});
		check.setCountStore(itemList.size());
		check.setCountCheck(checked);
		return check;
	}

	@Override
	public StoreCheck getCheck(String id) {
		StoreCheck check = this.getById(id);
		List<StoreCheckItem> itemList = this.findByField(StoreCheckItem.class,
				"checkId", id);
		check.setItemList(itemList);
		return check;
	}

	@Override
	public void removeCheck(String id) {
		StoreCheck check = this.getById(id);
		List<StoreCheckItem> itemList = dao().findByField(StoreCheckItem.class,
				"checkId", id);
		check.setItemList(itemList);
		this.removeById(id);
		dao().remove(itemList);
	}

	@Override
	public DataGrid<StoreCheck> findPage(Date startDate, Date endDate,
			String orgId, String operator, int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_check t");
		sb.append(" WHERE 1 = 1");
		sb.append(" and t.org_id = '" + orgId + "'");
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate,
					"yyyy-MM-dd 00:00:00");
			sb.append(" and t.check_date >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils
					.format(endDate, "yyyy-MM-dd 23:59:59");
			sb.append(" and t.check_date <= '" + date + "'");
		}
		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" t.* ");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(t.id) total");
		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  t.check_date DESC");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreCheck> list = dao().findBySQL(StoreCheck.class,
				sqllist.toString());
		DataGrid<StoreCheck> dg = new DataGrid<StoreCheck>((int) total, list,
				pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<StoreCheckItem> temPagee(String checkId, int page,
			int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_check_item t");
		sb.append(" WHERE 1 = 1");
		sb.append(" and t.check_id = '" + checkId + "'");
		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" t.* ");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(t.id) total");
		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  t.check_flag");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreCheckItem> list = dao().findBySQL(StoreCheckItem.class,
				sqllist.toString());
		DataGrid<StoreCheckItem> dg = new DataGrid<StoreCheckItem>((int) total,
				list, pageSize, page);
		return dg;
	}

	@Transactional
	@Override
	public StoreCheck saveCheckItem(StoreCheck form) {
		for (StoreCheckItem item : form.getItemList()) {
			item.setCheckId(form.getId());
		}
		dao().saveAll(form.getItemList());
		dao().save(form);
		return form;
	}
}
