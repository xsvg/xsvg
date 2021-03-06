package cc.cnplay.store.web.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Attachment;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.AttachmentService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.util.ExcelImportHelp;
import cc.cnplay.platform.web.controller.AbsController;
import cc.cnplay.platform.web.controller.AttachmentController;
import cc.cnplay.store.domain.StoreArea;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.domain.StoreMove;
import cc.cnplay.store.service.StoreAreaService;
import cc.cnplay.store.service.StoreItemService;
import cc.cnplay.store.vo.StoreInVO;
import cc.cnplay.store.vo.StoreOutVO;

@Controller
@RequestMapping(value = "/store")
public class StoreItemController extends AbsController
{

	@Resource
	private StoreItemService storeItemService;

	@Resource
	private StoreAreaService storeAreaService;

	@Resource
	private AttachmentService attachmentService;

	@Resource
	private UserService userService;

	@Ignore
	@RequestMapping(value = "/area/tree")
	@ResponseBody
	public Json<List<StoreArea>> areaLoadTree(String id, String[] areaId)
	{
		String orgId = this.getSessionUser().getOrgId();
		storeAreaService.getRoot(orgId);
		List<StoreArea> list = storeAreaService.findByParentId(orgId, id);
		storeAreaService.checked(areaId, list);
		return new Json<List<StoreArea>>(list);
	}

	@RequestMapping(value = "/out/vo")
	public @ResponseBody Json<StoreOutVO> outVoById(String id)
	{
		StoreOutVO vo = storeItemService.getOutVoById(id);
		if (vo == null)
		{
			vo = new StoreOutVO();
		}
		else
		{
			StoreArea p = storeAreaService.getById(vo.getAreaId());
			if (p != null)
			{
				vo.setAreaName(p.getName());
			}
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getRegisterDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setRegisterDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtStartDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtStartDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtEndDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtEndDate(date);
		}
		catch (Exception e)
		{
		}
		return new Json<StoreOutVO>(vo);
	}

	@RequestMapping(value = "/out/load")
	public @ResponseBody Json<StoreOutVO> outLoadById(String id)
	{
		StoreOutVO vo = storeItemService.getOutByItemId(id);
		if (vo == null)
		{
			vo = new StoreOutVO();
		}
		else
		{
			StoreArea p = storeAreaService.getById(vo.getAreaId());
			if (p != null)
			{
				vo.setAreaName(p.getName());
			}
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getRegisterDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setRegisterDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtStartDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtStartDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtEndDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtEndDate(date);
		}
		catch (Exception e)
		{
		}
		return new Json<StoreOutVO>(vo);
	}

	@RequestMapping(value = "/out/save")
	@RightAnnotation(name = "抵押管理/抵押物查询/出库", button = true, sort = 80101, needCheck = true, resource = "/store/area/tree,/store/out/load")
	@Description("保存机构")
	public @ResponseBody Json<StoreOutVO> outSave(StoreOutVO form)
	{
		Json<StoreOutVO> rst = new Json<StoreOutVO>();
		try
		{
			form.setOrgId(this.getSessionUser().getOrgId());
			form.setOperator(this.getSessionUser().getUsername());
			form = storeItemService.out(form);
			rst.OK(form, "");
		}
		catch (CnplayRuntimeException e)
		{
			logger.error(e);
			rst.NG(e.getMessage());
		}
		catch (Throwable e)
		{
			logger.error(e);
			rst.NG("保存失败，请输入正确的信息");
		}
		return rst;
	}

	@RequestMapping(value = "/out/list")
	@RightAnnotation(name = "抵押管理/出库日志", component = "platform.system.view.StoreOutPanel", resource = "/store/out/vo", sort = 80100)
	public @ResponseBody DataGrid<StoreOutVO> outList(String dywOwner, String startDate, String endDate)
	{
		User user = this.getSessionUser();
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
		{
			// 如果时间参数为空时，默认查询当前一个月数据
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			// 开始时间
			c.setTime(new Date());
			c.add(Calendar.MONTH, -1);
			Date m = c.getTime();
			startDate = format.format(m);
			// 结束时间
			Date d = new Date();
			endDate = format.format(d);
		}
		Date startDateTime = null;
		Date endDateTime = null;
		if (StringUtils.isNotEmpty(startDate))
		{
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate))
		{
			endDateTime = DateUtil.dateLess(endDate);
		}
		DataGrid<StoreOutVO> dg = storeItemService.findOutPageLikeName(startDateTime, endDateTime, user.getOrgId(), dywOwner, this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/in/load")
	public @ResponseBody Json<StoreInVO> inLoadById(String id)
	{
		StoreInVO vo = storeItemService.getInVoById(id);
		if (vo == null)
		{
			vo = new StoreInVO();
			vo.setStoreman(this.getSessionUsername());
		}
		else
		{
			StoreArea p = storeAreaService.getById(vo.getAreaId());
			if (p != null)
			{
				vo.setAreaName(p.getName());
			}
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getRegisterDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setRegisterDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtStartDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtStartDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtEndDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtEndDate(date);
		}
		catch (Exception e)
		{
		}
		return new Json<StoreInVO>(vo);
	}

	@RequestMapping(value = "/in/modify")
	@RightAnnotation(name = "抵押管理/入库管理/修改", button = true, sort = 80101, needCheck = true, resource = "/store/area/tree")
	public @ResponseBody Json<StoreInVO> inModify(StoreInVO form)
	{
		Json<StoreInVO> rst = new Json<StoreInVO>();
		try
		{
			StoreItem item = storeItemService.getInVoByRfid(form.getRfid());
			if (item != null && item.getId().equals(form.getItemId()))
			{
				form.setOrgId(this.getSessionUser().getOrgId());
				form.setOperator(this.getSessionUser().getUsername());
				form = storeItemService.in(form);
				rst.OK(form, "");
			}
			else
			{
				rst.NG("标签号已入库，请使用其它标签号");
			}
		}
		catch (CnplayRuntimeException e)
		{
			logger.error(e);
			rst.NG(e.getMessage());
		}
		catch (Throwable e)
		{
			logger.error(e);
			rst.NG("保存失败，请输入正确的信息");
		}
		return rst;
	}

	@RequestMapping(value = "/in/save")
	@RightAnnotation(name = "抵押管理/入库管理/入库", button = true, sort = 80101, needCheck = true, resource = "/store/area/tree")
	public @ResponseBody Json<StoreInVO> inSave(StoreInVO form)
	{
		Json<StoreInVO> rst = new Json<StoreInVO>();
		try
		{
			if (storeItemService.getInVoByRfid(form.getRfid()) == null)
			{
				form.setOrgId(this.getSessionUser().getOrgId());
				form.setOperator(this.getSessionUser().getUsername());
				form = storeItemService.in(form);
				rst.OK(form, "");
			}
			else
			{
				rst.NG("标签号已入库，请使用其它标签号");
			}
		}
		catch (CnplayRuntimeException e)
		{
			logger.error(e);
			rst.NG(e.getMessage());
		}
		catch (Throwable e)
		{
			logger.error(e);
			rst.NG("保存失败，请输入正确的信息");
		}
		return rst;
	}

	@RequestMapping(value = "/in/tmplist")
	@RightAnnotation(name = "抵押管理/待入库管理", component = "platform.system.view.StoreInTmpPanel", resource = "/store/item/*", sort = 80100)
	public @ResponseBody DataGrid<StoreItem> tmplist()
	{
		User user = this.getSessionUser();
		DataGrid<StoreItem> dg = storeItemService.findInTmpPage(user.getOrgId(), this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/remove")
	public @ResponseBody Json<Boolean> remove(String[] ids)
	{
		Json<Boolean> dg = new Json<Boolean>();
		storeItemService.removeByIds(ids);
		dg.OK(true, "删除");
		return dg;
	}

	@RequestMapping(value = "/in/tmp")
	public @ResponseBody DataGrid<StoreItem> inTmp(String id)
	{
		DataGrid<StoreItem> dg = new DataGrid<StoreItem>();
		User user = this.getSessionUser();
		Attachment att = attachmentService.getAttachment(id);
		String filename = AttachmentController.path + "/" + id + "." + att.getSuffix();
		StringBuilder sb = new StringBuilder();
		try
		{
			List<String[]> itemList = ExcelImportHelp.readExcel(filename);
			List<StoreItem> items = new ArrayList<StoreItem>();
			for (int i = 1; i < itemList.size(); i++)
			{
				String[] strs = itemList.get(i);
				if (StringUtils.isNotEmpty(strs[0]) && StringUtils.isNotEmpty(strs[1]))
				{
					try
					{
						StoreItem item = toItem(i, user, strs);
						if (!storeItemService.existSn(item.getSn()))
						{
							items.add(item);
							sb.append(strs[0] + " 导入成功<br/>");
						}
						else
						{
							sb.append(strs[0] + " 已经存在<br/>");
						}
					}
					catch (Throwable e)
					{
						sb.append(e.getMessage() + " " + strs[0] + " <br/>");
					}
				}
			}
			storeItemService.saveAll(items);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			dg.setSuccess(false);
			dg.setMsg(e.getMessage());
			return dg;
		}
		dg = storeItemService.findInTmpPage(user.getOrgId(), this.getPage(), this.getPageSize());
		dg.setMsg(sb.toString());
		return dg;
	}

	private StoreItem toItem(int rows, User user, String[] strs)
	{
		StoreItem item = new StoreItem();
		item.setSn(strs[0]);
		item.setName(strs[1]);
		item.setDywOwner(strs[2]);
		String pgje = strs[3];
		String jkje = strs[4];
		if (pgje == null)
		{
			pgje = "0";
		}
		if (jkje == null)
		{
			jkje = "0";
		}
		pgje = pgje.replaceAll(" ", "");
		jkje = jkje.replaceAll(" ", "");
		if (StringUtils.isEmpty(pgje))
		{
			pgje = "0";
		}
		if (StringUtils.isEmpty(jkje))
		{
			jkje = "0";
		}
		try
		{
			item.setPgje(new BigDecimal(pgje));
		}
		catch (Throwable ex)
		{
			throw new RuntimeException("第" + (rows + 1) + "行数据评估金额[" + pgje + "]不正确", ex);
		}
		try
		{
			item.setJkje(new BigDecimal(jkje));
		}
		catch (Throwable ex)
		{
			throw new RuntimeException("第" + (rows + 1) + "行数据借款金额[" + jkje + "]不正确", ex);
		}
		item.setRegisterDate(strs[5]);
		// try {
		// String date = DateFormatUtils.format(DateUtils.parseDate(strs[5], "yyyyMMdd"), "yyyy年MM月dd日");
		// item.setRegisterDate(date);
		// } catch (Exception e) {
		// }
		item.setStoreman(strs[6]);
		item.setStatus(StoreItem.STATUS_WIN);
		item.setOrgId(user.getOrgId());
		return item;
	}

	@RequestMapping(value = "/in/tmp/load")
	public @ResponseBody Json<StoreInVO> inTmpLoad(String id)
	{
		StoreItem item = storeItemService.getById(id);
		StoreInVO vo = new StoreInVO();
		BeanUtils.copyProperties(item, vo);
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getRegisterDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setRegisterDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtStartDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtStartDate(date);
		}
		catch (Exception e)
		{
		}
		try
		{
			String date = DateFormatUtils.format(DateUtils.parseDate(vo.getHtEndDate(), "yyyy年MM月dd日"), "yyyyMMdd");
			vo.setHtEndDate(date);
		}
		catch (Exception e)
		{
		}
		vo.setItemId(id);
		vo.setId(StoreItem.randomID());
		vo.setStoreman(getSessionUsername());
		return new Json<StoreInVO>(vo);
	}

	@RequestMapping(value = "/in/list")
	@RightAnnotation(name = "抵押管理/入库管理", component = "platform.system.view.StoreInPanel", resource = "/store/item/*", sort = 80100)
	public @ResponseBody DataGrid<StoreInVO> inLst(String orgId, String dywOwner, String startDate, String endDate)
	{
		User user = this.getSessionUser();
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
		{
			// 如果时间参数为空时，默认查询当前一个月数据
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			// 开始时间
			c.setTime(new Date());
			c.add(Calendar.MONTH, -1);
			Date m = c.getTime();
			startDate = format.format(m);
			// 结束时间
			Date d = new Date();
			endDate = format.format(d);
		}
		Date startDateTime = null;
		Date endDateTime = null;
		if (StringUtils.isNotEmpty(startDate))
		{
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate))
		{
			endDateTime = DateUtil.dateLess(endDate);
		}
		DataGrid<StoreInVO> dg = storeItemService.findInPageLikeName(startDateTime, endDateTime, user.getOrgId(), dywOwner, this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/item/list")
	@RightAnnotation(name = "抵押管理/抵押物查询", component = "platform.system.view.StoreItemPanel", resource = "/store/item/*", sort = 80100)
	public @ResponseBody DataGrid<StoreItem> itemList(String storeman, String dywOwner, String startDate, String endDate) throws Exception
	{
		User user = this.getSessionUser();
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
		{
			// 如果时间参数为空时，默认查询当前一个月数据
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			// 开始时间
			c.setTime(new Date());
			c.add(Calendar.MONTH, -1);
			Date m = c.getTime();
			startDate = format.format(m);
			// 结束时间
			Date d = new Date();
			endDate = format.format(d);
		}
		Date startDateTime = null;
		Date endDateTime = null;
		if (StringUtils.isNotEmpty(startDate))
		{
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate))
		{
			endDateTime = DateUtil.dateLess(endDate);
		}
		DataGrid<StoreItem> dg = storeItemService.findPageLikeName(startDateTime, endDateTime, user.getOrgId(), dywOwner, storeman, this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/item/mylist")
	@RightAnnotation(name = "抵押管理/我的抵押物", component = "platform.system.view.StoreMyItemPanel", resource = "/store/item/*", sort = 80100)
	public @ResponseBody DataGrid<StoreItem> itemMyList(String dywOwner, String startDate, String endDate)
	{
		User user = this.getSessionUser();
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
		{
			// 如果时间参数为空时，默认查询当前一个月数据
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			// 开始时间
			c.setTime(new Date());
			c.add(Calendar.MONTH, -1);
			Date m = c.getTime();
			startDate = format.format(m);
			// 结束时间
			Date d = new Date();
			endDate = format.format(d);
		}
		Date startDateTime = null;
		Date endDateTime = null;
		if (StringUtils.isNotEmpty(startDate))
		{
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate))
		{
			endDateTime = DateUtil.dateLess(endDate);
		}
		DataGrid<StoreItem> dg = storeItemService.findPageLikeName(startDateTime, endDateTime, user.getOrgId(), dywOwner, user.getUsername(), this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/item/movoto")
	@RightAnnotation(name = "抵押管理/我的抵押物/交接", button = true, sort = 80101, needCheck = true, resource = "/store/area/tree")
	@Description("保存机构")
	public @ResponseBody Json<StoreMove> movoto(StoreMove form)
	{
		Json<StoreMove> rst = new Json<StoreMove>();
		User user = this.getSessionUser();
		try
		{
			User moveto = userService.getByUsername(form.getMoveto());
			if (moveto != null)
			{
				form.setOperator(user.getUsername());
				form.setMoveDate(new Date());
				Date date = DateUtils.parseDate(form.getMoveDates(), "yyyy年MM月dd日");
				form.setMoveDate(date);
				if (form.getItemIds() != null && form.getItemIds().length > 0)
				{
					if (storeItemService.moveto(form))
					{
						rst.OK(form, "交接成功");
					}
					else
					{
						rst.NG("交接失败");
					}
				}
				else
				{
					rst.NG("交接失败，请选择交接内容");
				}
			}
			else
			{
				rst.NG("交接失败，接收人不存在！");
			}
		}
		catch (CnplayRuntimeException e)
		{
			logger.error(e);
			rst.NG(e.getMessage());
		}
		catch (Throwable e)
		{
			logger.error(e);
			rst.NG("交接失败，请输入正确的信息");
		}
		return rst;
	}

	@RequestMapping(value = "/item/myexport")
	@RightAnnotation(name = "抵押管理/我的抵押物/导出", button = true, sort = 80101, needCheck = true, resource = "/store/area/tree")
	public void export(String dywOwner, String startDate, String endDate)
	{
		try
		{
			if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
			{
				// 如果时间参数为空时，默认查询当前一个月数据
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Calendar c = Calendar.getInstance();
				// 开始时间
				c.setTime(new Date());
				c.add(Calendar.MONTH, -1);
				Date m = c.getTime();
				startDate = format.format(m);
				// 结束时间
				Date d = new Date();
				endDate = format.format(d);
			}
			Date startDateTime = null;
			Date endDateTime = null;
			if (StringUtils.isNotEmpty(startDate))
			{
				startDateTime = DateUtil.dateGreater(startDate);
			}
			if (StringUtils.isNotEmpty(endDate))
			{
				endDateTime = DateUtil.dateLess(endDate);
			}
			DataGrid<StoreItem> dg = storeItemService.findPageLikeName(startDateTime, endDateTime, this.getSessionUser().getOrgId(), dywOwner, getSessionUsername(), this.getPage(), this.getPageSize());
			HSSFWorkbook wb = storeItemService.export(dg.getRows());
			this.getResponse().setContentType("application/vnd.ms-excel");
			this.getResponse().setHeader("Content-disposition", "attachment;filename=ITEM.xls");
			wb.write(this.getResponse().getOutputStream());
			this.getResponse().getOutputStream().flush();
		}
		catch (Exception e)
		{
			logger.error(e);
		}
	}
}
