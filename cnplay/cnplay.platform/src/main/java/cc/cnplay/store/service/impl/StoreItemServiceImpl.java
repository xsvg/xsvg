package cc.cnplay.store.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.store.domain.StoreIn;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.domain.StoreOut;
import cc.cnplay.store.service.StoreItemService;
import cc.cnplay.store.vo.StoreInVO;
import cc.cnplay.store.vo.StoreOutVO;

@Service
public class StoreItemServiceImpl extends AbsGenericService<StoreItem, String>
		implements StoreItemService {

	@Override
	public DataGrid<StoreItem> findPageLikeName(Date startDate, Date endDate,
			String orgId, String dywOwner, int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_item ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE store_item.status = " + StoreItem.STATUS_IN);
		sb.append(" and store_area.org_id = '" + orgId + "'");
		if (StringUtils.isNoneEmpty(dywOwner)) {
			sb.append(" and store_item.dyw_owner LIKE '%" + dywOwner + "%'");
		}
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate,
					"yyyy-MM-dd 00:00:00");
			sb.append(" and store_item.create_time >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils
					.format(endDate, "yyyy-MM-dd 23:59:59");
			sb.append(" and store_item.create_time <= '" + date + "'");
		}
		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" store_item.*,");
		sqllist.append(" store_area.`name` as areaName");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(store_item.id) total");
		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  store_item.create_time DESC");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreItem> list = dao().findBySQL(StoreItem.class,
				sqllist.toString());
		DataGrid<StoreItem> dg = new DataGrid<StoreItem>((int) total, list,
				pageSize, page);
		return dg;

		// Search search = new Search(StoreItem.class);
		// if (StringUtils.isNotBlank(dywOwner)) {
		// search.addFilterILike("dywOwner", "%" + dywOwner + "%");
		// }
		// if (startDate != null)
		// search.addFilterGreaterOrEqual("createTime", startDate);
		// if (endDate != null)
		// search.addFilterLessOrEqual("createTime", endDate);
		// if (StringUtils.isNotBlank(orgId)) {
		// search.addFilterEqual("orgId", orgId);
		// }
		// search.addFilterEqual("status", StoreItem.STATUS_IN);
		//
		// search.addSortDesc("createTime");
		// search.setFirstResult(getFirstResult(page, pageSize));
		// search.setMaxResults((pageSize > 0 ? pageSize : 20));
		// @SuppressWarnings("unchecked")
		// SearchResult<StoreItem> result = dao().searchAndCount(search);
		// List<StoreItem> list = result.getResult();
		// for (StoreItem item : list) {
		// StoreArea area = dao().getById(item.getAreaId());
		// if (area != null)
		// item.setAreaName(area.getName());
		// }
		// DataGrid<StoreItem> dg = new DataGrid<StoreItem>(
		// (int) result.getTotalCount(), list, pageSize, page);
		// return dg;
	}

	@Override
	public DataGrid<StoreInVO> findInPageLikeName(Date startDate, Date endDate,
			String orgId, String dywOwner, int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_in ");
		sb.append(" INNER JOIN store_item ON store_item.id = store_in.item_id ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE 1 = 1");
		sb.append(" and store_area.org_id = '" + orgId + "'");
		if (StringUtils.isNoneEmpty(dywOwner)) {
			sb.append(" and store_item.dyw_owner LIKE '%" + dywOwner + "%'");
		}
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate,
					"yyyy-MM-dd 00:00:00");
			sb.append(" and store_in.create_time >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils
					.format(endDate, "yyyy-MM-dd 23:59:59");
			sb.append(" and store_in.create_time <= '" + date + "'");
		}

		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" store_in.*,");
		sqllist.append(" store_item.*,");
		sqllist.append(" store_area.`name` as areaName");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(store_in.id) total");
		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  store_in.create_time DESC");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreInVO> list = dao().findBySQL(StoreInVO.class,
				sqllist.toString());
		DataGrid<StoreInVO> dg = new DataGrid<StoreInVO>((int) total, list,
				pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<StoreOutVO> findOutPageLikeName(Date startDate,
			Date endDate, String orgId, String dywOwner, int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_out ");
		sb.append(" INNER JOIN store_item ON store_item.id = store_out.item_id ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE 1 = 1");
		sb.append(" and store_area.org_id = '" + orgId + "'");
		if (StringUtils.isNoneEmpty(dywOwner)) {
			sb.append(" and store_item.dyw_owner LIKE '%" + dywOwner + "%'");
		}
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate,
					"yyyy-MM-dd 00:00:00");
			sb.append(" and store_out.create_time >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils
					.format(endDate, "yyyy-MM-dd 23:59:59");
			sb.append(" and store_out.create_time <= '" + date + "'");
		}

		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" store_out.*,");
		sqllist.append(" store_item.*,");
		sqllist.append(" store_area.`name` as areaName");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(store_out.id) total");

		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  store_out.create_time DESC");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreOutVO> list = dao().findBySQL(StoreOutVO.class,
				sqllist.toString());
		DataGrid<StoreOutVO> dg = new DataGrid<StoreOutVO>((int) total, list,
				pageSize, page);
		return dg;

	}

	@Transactional
	@Override
	public StoreInVO in(StoreInVO form) {
		StoreIn in = new StoreIn();
		StoreItem item = new StoreItem();
		BeanUtils.copyProperties(form, in);
		BeanUtils.copyProperties(form, item);
		item.setId(StoreItem.randomID());
		in.setItemId(item.getId());
		item.setStatus(StoreItem.STATUS_IN);
		dao().save(in);
		dao().save(item);
		return form;
	}

	@Override
	public StoreInVO getInVoById(String id) {
		StoreIn in = dao().getById(StoreIn.class, id);
		if (in != null) {
			StoreItem item = dao().getById(StoreItem.class, in.getItemId());
			StoreInVO vo = new StoreInVO();
			BeanUtils.copyProperties(vo, item);
			BeanUtils.copyProperties(vo, in);
			return vo;
		}
		return null;
	}

	@Override
	public StoreOutVO getOutById(String id) {
		StoreItem item = dao().getById(StoreItem.class, id);
		if (item != null) {
			StoreOutVO vo = new StoreOutVO();
			vo.setItemId(item.getId());
			BeanUtils.copyProperties(item, vo);
			vo.setId(StoreItem.randomID());
			return vo;
		}
		return null;
	}

	@Override
	public StoreOutVO getOutVoById(String id) {
		StoreOut out = dao().getById(StoreOut.class, id);
		if (out != null) {
			StoreItem item = dao().getById(StoreItem.class, out.getItemId());
			StoreOutVO vo = new StoreOutVO();
			BeanUtils.copyProperties(item, vo);
			BeanUtils.copyProperties(out, vo);
			return vo;
		}
		return null;
	}

	@Transactional
	@Override
	public StoreOutVO out(StoreOutVO form) {
		StoreOut vo = new StoreOut();
		StoreItem item = dao().getById(StoreItem.class, form.getItemId());
		if (item != null) {
			item.setStatus(StoreItem.STATUS_OUT);
			BeanUtils.copyProperties(form, vo);
			dao().save(vo);
			dao().save(item);
		}
		return form;
	}
}
