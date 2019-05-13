package cc.cnplay.store.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.dao.OrganizationDao;
import cc.cnplay.store.domain.StoreIn;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.domain.StoreMove;
import cc.cnplay.store.domain.StoreOut;
import cc.cnplay.store.service.StoreItemService;
import cc.cnplay.store.vo.StoreInVO;
import cc.cnplay.store.vo.StoreOutVO;

@Service
public class StoreItemServiceImpl extends AbsGenericService<StoreItem, String> implements StoreItemService {

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
	public DataGrid<StoreItem> findPageLikeName(Date startDate, Date endDate, String orgId, String dywOwner,
			String storeman, int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_item ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE store_item.status = " + StoreItem.STATUS_IN);
		sb.append(" and store_area.org_id in (" + lowerOrgIdSql(orgId) + ")");
		if (StringUtils.isNoneEmpty(dywOwner)) {
			sb.append(" and store_item.dyw_owner LIKE '%" + dywOwner + "%'");
		}
		if (StringUtils.isNoneEmpty(storeman)) {
			sb.append(" and store_item.storeman = '" + storeman + "'");
		}
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate, "yyyy-MM-dd 00:00:00");
			sb.append(" and store_item.create_time >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils.format(endDate, "yyyy-MM-dd 23:59:59");
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
		List<StoreItem> list = dao().findBySQL(StoreItem.class, sqllist.toString());
		DataGrid<StoreItem> dg = new DataGrid<StoreItem>((int) total, list, pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<StoreItem> findPageByStoreman(String storeman, int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_item ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE store_item.status = " + StoreItem.STATUS_IN);
		sb.append(" and store_item.storeman = '" + storeman + "'");
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
		List<StoreItem> list = dao().findBySQL(StoreItem.class, sqllist.toString());
		DataGrid<StoreItem> dg = new DataGrid<StoreItem>((int) total, list, pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<StoreItem> findInTmpPage(String orgId, int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_item t");
		sb.append(" WHERE 1 = 1");
		sb.append(" and t.status = " + StoreItem.STATUS_WIN + "");
		sb.append(" and t.org_id = '" + orgId + "'");
		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" t.* ");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(t.id) total");
		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  t.create_time DESC");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreItem> list = dao().findBySQL(StoreItem.class, sqllist.toString());
		DataGrid<StoreItem> dg = new DataGrid<StoreItem>((int) total, list, pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<StoreInVO> findInPageLikeName(Date startDate, Date endDate, String orgId, String dywOwner, int page,
			int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_in ");
		sb.append(" INNER JOIN store_item ON store_item.id = store_in.item_id ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE 1 = 1");
		sb.append(" and store_area.org_id in (" + lowerOrgIdSql(orgId) + ")");
		if (StringUtils.isNoneEmpty(dywOwner)) {
			sb.append(" and store_item.dyw_owner LIKE '%" + dywOwner + "%'");
		}
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate, "yyyy-MM-dd 00:00:00");
			sb.append(" and store_in.create_time >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils.format(endDate, "yyyy-MM-dd 23:59:59");
			sb.append(" and store_in.create_time <= '" + date + "'");
		}

		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" store_item.*,");
		sqllist.append(" store_in.*,");
		sqllist.append(" store_area.`name` as areaName");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(store_in.id) total");
		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  store_in.create_time DESC");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreInVO> list = dao().findBySQL(StoreInVO.class, sqllist.toString());
		DataGrid<StoreInVO> dg = new DataGrid<StoreInVO>((int) total, list, pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<StoreOutVO> findOutPageLikeName(Date startDate, Date endDate, String orgId, String dywOwner,
			int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" FROM store_out ");
		sb.append(" INNER JOIN store_item ON store_item.id = store_out.item_id ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE 1 = 1");
		sb.append(" and store_area.org_id in (" + lowerOrgIdSql(orgId) + ")");
		if (StringUtils.isNoneEmpty(dywOwner)) {
			sb.append(" and store_item.dyw_owner LIKE '%" + dywOwner + "%'");
		}
		if (startDate != null) {
			String date = DateFormatUtils.format(startDate, "yyyy-MM-dd 00:00:00");
			sb.append(" and store_out.create_time >= '" + date + "'");
		}
		if (endDate != null) {
			String date = DateFormatUtils.format(endDate, "yyyy-MM-dd 23:59:59");
			sb.append(" and store_out.create_time <= '" + date + "'");
		}

		StringBuffer sqllist = new StringBuffer();
		sqllist.append("SELECT");
		sqllist.append(" store_item.*,");
		sqllist.append(" store_out.*,");
		sqllist.append(" store_area.`name` as areaName");
		sqllist.append(sb.toString());

		sb.insert(0, "SELECT count(store_out.id) total");

		int total = dao().countBySql(sb.toString());
		int firstResult = getFirstResult(page, pageSize);
		int end = firstResult + pageSize;
		sqllist.append(" ORDER BY  store_out.create_time DESC");
		sqllist.append(" LIMIT " + firstResult + "," + end);
		List<StoreOutVO> list = dao().findBySQL(StoreOutVO.class, sqllist.toString());
		DataGrid<StoreOutVO> dg = new DataGrid<StoreOutVO>((int) total, list, pageSize, page);
		return dg;

	}

	@Transactional
	@Override
	public StoreInVO in(StoreInVO form) {
		StoreIn in = new StoreIn();
		StoreItem item = new StoreItem();
		BeanUtils.copyProperties(form, in);
		BeanUtils.copyProperties(form, item);
		if (!StringUtils.isEmpty(form.getItemId())) {
			item.setId(form.getItemId());
		} else {
			item.setId(StoreItem.randomID());
		}
		in.setItemId(item.getId());
		item.setStatus(StoreItem.STATUS_IN);
		dao().save(in);
		dao().save(item);
		return form;
	}

	@Transactional
	@Override
	public StoreInVO inModify(StoreInVO form) {
		if (!StringUtils.isEmpty(form.getItemId())) {
			StoreItem item = dao().getById(StoreItem.class, form.getItemId());
			if (item != null) {
				BeanUtils.copyProperties(form, item);
				item.setId(form.getItemId());
				item.setStatus(StoreItem.STATUS_IN);
				dao().save(item);
			}
		}
		return form;
	}

	@Override
	public StoreInVO getInVoById(String id) {
		StoreIn in = dao().getById(StoreIn.class, id);
		if (in != null) {
			StoreItem item = dao().getById(StoreItem.class, in.getItemId());
			StoreInVO vo = new StoreInVO();
			BeanUtils.copyProperties(item, vo);
			BeanUtils.copyProperties(in, vo);
			return vo;
		}
		return null;
	}

	@Override
	public StoreItem getInVoByRfid(String rfid) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM store_item t");
		sb.append(" WHERE 1 = 1");
		sb.append(" and t.rfid = '" + rfid + "'");
		sb.append(" and t.status = 1 LIMIT 1");
		List<StoreItem> list = dao().findBySQL(StoreItem.class, sb.toString());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public StoreOutVO getOutByItemId(String id) {
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

	@Override
	public List<StoreItem> findByDywOwner(String orgId, String dywOwner, String dywOwnerId) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT");
		sb.append(" store_item.*,");
		sb.append(" store_area.`name` as areaName");
		sb.append(" FROM store_item ");
		sb.append(" INNER JOIN store_area ON store_area.id = store_item.area_id ");
		sb.append(" WHERE store_item.status = " + StoreItem.STATUS_IN);
		sb.append(" and store_area.org_id = '" + orgId + "'");
		if (StringUtils.isEmpty(dywOwner) && StringUtils.isEmpty(dywOwnerId)) {
			return new ArrayList<StoreItem>();
		} else {
			sb.append(" and (store_item.dyw_owner = '" + dywOwner + "'");
			sb.append(" or store_item.dyw_owner_id = '" + dywOwnerId + "')");
		}
		sb.append(" ORDER BY  store_item.create_time DESC");
		List<StoreItem> list = dao().findBySQL(StoreItem.class, sb.toString());
		return list;
	}

	@Transactional
	@Override
	public boolean moveto(StoreMove form) {
		List<StoreItem> itemList = this.dao().findByIds(StoreItem.class, form.getItemIds());
		for (StoreItem item : itemList) {
			if (!item.getStoreman().equals(form.getOperator())) {
				throw new RuntimeException("请不要转交他人保管的物品");
			}
			StoreMove move = new StoreMove();
			BeanUtils.copyProperties(form, move);
			move.setId(StoreMove.randomID());
			move.setItemId(item.getId());
			item.setStoreman(form.getMoveto());
			dao().save(move);
			dao().save(item);
		}
		return true;
	}

	@Override
	public HSSFWorkbook export(List<StoreItem> list) throws Exception {
		String[] excelHeader = { "物品编号", "物品名称", "担保物品所有人", "评估价值", "抵质押金额", "表外登记日期", "保管人" };
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("抵押物交接导出");
		sheet.setColumnWidth(0, 9000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);

		HSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		style.setWrapText(true);

		HSSFRow row = sheet.createRow((int) 0);
		for (int i = 0; i < excelHeader.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(excelHeader[i]);
			cell.setCellStyle(style);
		}

		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(i + 1);
			StoreItem item = list.get(i);

			HSSFCell cell = null;
			cell = row.createCell(0);
			cell.setCellValue(item.getSn());
			cell.setCellStyle(style);

			cell = row.createCell(1);
			cell.setCellValue(item.getName());
			cell.setCellStyle(style);

			cell = row.createCell(2);
			cell.setCellValue(item.getDywOwner());
			cell.setCellStyle(style);

			cell = row.createCell(3);
			if (item.getPgje() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(item.getPgje() + "");
			}
			cell.setCellStyle(style);

			cell = row.createCell(4);
			if (item.getJkje() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(item.getJkje() + "");
			}
			cell.setCellStyle(style);

			cell = row.createCell(5);
			cell.setCellValue(item.getRegisterDate());
			cell.setCellStyle(style);

			cell = row.createCell(6);
			cell.setCellValue(item.getStoreman());
			cell.setCellStyle(style);

		}
		return wb;

	}

	@Override
	public boolean existSn(String sn)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM store_item t");
		sb.append(" WHERE 1 = 1");
		sb.append(" and t.sn = '" + sn + "'");
		sb.append(" and t.status > 2 LIMIT 1");
		List<StoreItem> list = dao().findBySQL(StoreItem.class, sb.toString());
		return list.size() > 0;
	}
	
}
