package cc.cnplay.store.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.util.BeanUtils;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.dao.OrganizationDao;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.store.domain.StoreCheck;
import cc.cnplay.store.domain.StoreCheckItem;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.service.StoreCheckService;
import cc.cnplay.store.vo.TagVo;

@Service
public class StoreCheckServiceImpl extends AbsGenericService<StoreCheck, String> implements StoreCheckService {

	private List<StoreItem> findByOrgId(String orgId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_item ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE store_item.status = " + StoreItem.STATUS_IN);
		sb.append(" and store_area.org_id = '" + orgId + "'");
		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" store_item.*,");
		sqllist.append(" store_area.`name` as areaName");
		sqllist.append(sb.toString());
		sqllist.append(" ORDER BY  store_item.create_time DESC");
		List<StoreItem> list = dao().findBySQL(StoreItem.class, sqllist.toString());
		return list;
	}

	@Override
	public StoreCheck check(String orgId, String operator) {
		StoreCheck check = new StoreCheck();
		check.setOrgId(orgId);
		check.setOperator(operator);
		check.setCheckDate(new Date());
		check.setCheckDateStr(DateFormatUtils.format(check.getCheckDate(), "yyyy-MM-dd"));
		Organization org = this.getByField(Organization.class, "id", orgId);
		check.setOrgName(org.getName());
		List<StoreItem> itemList = findByOrgId(orgId);
		check.setItemList(new ArrayList<StoreCheckItem>());
		int checked = 0;
		List<TagVo> tagList = this.get(operator);
		if (tagList == null || tagList.size() == 0) {
			return null;
		}
		Map<String, TagVo> rfidSet = new HashMap<String, TagVo>();
		for (TagVo vo : tagList) {
			rfidSet.put(vo.getEpc().trim().toUpperCase(), vo);
		}
		for (StoreItem item : itemList) {
			StoreCheckItem sci = new StoreCheckItem();
			BeanUtils.copyProperties(sci, item);
			sci.setId(StoreCheckItem.randomID());
			sci.setCheckId(check.getId());
			check.getItemList().add(sci);
			if (rfidSet.containsKey(item.getRfid().toUpperCase())) {
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
	public StoreCheck getCheck(String checkId) {
		StoreCheck check = this.getById(checkId);
		if (check != null) {
			List<StoreCheckItem> itemList = this.findCheckItem(checkId);
			check.setItemList(itemList);
		}
		return check;
	}

	private List<StoreCheckItem> findCheckItem(String checkId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append(" t.*,");
		sb.append(" store_area.`name` as areaName");
		sb.append(" FROM store_check_item t ");
		sb.append(" INNER JOIN store_area ON store_area.id = t.area_id ");
		sb.append(" WHERE t.check_id = '" + checkId + "'");
		sb.append(" ORDER BY  t.check_flag DESC");
		List<StoreCheckItem> list = dao().findBySQL(StoreCheckItem.class, sb.toString());
		return list;
	}

	@Override
	public void removeCheck(String id) {
		StoreCheck check = this.getById(id);
		List<StoreCheckItem> itemList = dao().findByField(StoreCheckItem.class, "checkId", id);
		check.setItemList(itemList);
		this.removeById(id);
		dao().remove(itemList);
	}

	@Resource
	private OrganizationDao orgDao;

	private String lowerOrgIdSql(String orgId) {
		String lavelCode = orgDao.getLevelCodeById(orgId);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT org.id FROM p_organization org");
		sb.append(" where org.level_code LIKE '" + lavelCode + "%'");
		return sb.toString();
	}

	@Override
	public DataGrid<StoreCheck> findPage(Date startDate, Date endDate, String orgId, String operator, int page,
			int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_check t");
		sb.append(" WHERE 1 = 1");
		sb.append(" and t.org_id in (" + lowerOrgIdSql(orgId) + ")");
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate, "yyyy-MM-dd 00:00:00");
			sb.append(" and t.check_date >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils.format(endDate, "yyyy-MM-dd 23:59:59");
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
		List<StoreCheck> list = dao().findBySQL(StoreCheck.class, sqllist.toString());
		DataGrid<StoreCheck> dg = new DataGrid<StoreCheck>((int) total, list, pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<StoreCheckItem> temPagee(String checkId, int page, int pageSize) {
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
		List<StoreCheckItem> list = dao().findBySQL(StoreCheckItem.class, sqllist.toString());
		DataGrid<StoreCheckItem> dg = new DataGrid<StoreCheckItem>((int) total, list, pageSize, page);
		return dg;
	}

	@Transactional
	@Override
	public StoreCheck saveCheckItem(String username, StoreCheck form) {
		for (StoreCheckItem item : form.getItemList()) {
			item.setCheckId(form.getId());
		}
		dao().saveAll(form.getItemList());
		dao().save(form);
		tagListMap.remove(username);
		return form;
	}

	private static final Map<String, List<TagVo>> tagListMap = new HashMap<String, List<TagVo>>();

	@Override
	public void put(String username, List<TagVo> voList) {
		tagListMap.put(username, voList);
	}

	@Override
	public List<TagVo> get(String username) {
		return tagListMap.get(username);
	}
}
