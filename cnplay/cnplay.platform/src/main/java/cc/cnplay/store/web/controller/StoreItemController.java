package cc.cnplay.store.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.web.controller.AbsController;
import cc.cnplay.store.domain.StoreArea;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.service.StoreAreaService;
import cc.cnplay.store.service.StoreItemService;
import cc.cnplay.store.vo.StoreInVO;
import cc.cnplay.store.vo.StoreOutVO;

@Controller
@RequestMapping(value = "/store")
public class StoreItemController extends AbsController {

	@Resource
	private StoreItemService storeItemService;

	@Resource
	private StoreAreaService storeAreaService;

	@Ignore
	@RequestMapping(value = "/area/tree")
	@ResponseBody
	public Json<List<StoreArea>> areaLoadTree(String id, String[] areaId) {
		String orgId = this.getSessionUser().getOrgId();
		storeAreaService.getRoot(orgId);
		List<StoreArea> list = storeAreaService.findByParentId(orgId, id);
		storeAreaService.checked(areaId, list);
		return new Json<List<StoreArea>>(list);
	}

	@RequestMapping(value = "/out/load")
	public @ResponseBody Json<StoreOutVO> outLoadById(String id) {
		StoreOutVO vo = storeItemService.getOutById(id);
		if (vo == null) {
			vo = new StoreOutVO();
		} else {
			StoreArea p = storeAreaService.getById(vo.getAreaId());
			if (p != null) {
				vo.setAreaName(p.getName());
			}
		}
		return new Json<StoreOutVO>(vo);
	}

	@RequestMapping(value = "/out/save")
	@RightAnnotation(name = "抵押管理/抵押物查询/出库", button = true, sort = 80101, needCheck = true, resource = "/store/area/tree,/store/out/load")
	@Description("保存机构")
	public @ResponseBody Json<StoreOutVO> outSave(StoreOutVO form) {
		Json<StoreOutVO> rst = new Json<StoreOutVO>();
		try {
			form.setOrgId(this.getSessionUser().getOrgId());
			form.setOperator(this.getSessionUser().getUsername());
			form = storeItemService.out(form);
			rst.OK(form, "");
		} catch (CnplayRuntimeException e) {
			logger.error(e);
			rst.NG(e.getMessage());
		} catch (Throwable e) {
			logger.error(e);
			rst.NG("保存失败，请输入正确的信息");
		}
		return rst;
	}

	@RequestMapping(value = "/out/list")
	@RightAnnotation(name = "抵押管理/出库日志", component = "platform.system.view.StoreOutPanel", resource = "/store/item/*", sort = 80100)
	public @ResponseBody DataGrid<StoreOutVO> outList(String orgId,
			String dywOwner, String startDate, String endDate) {
		User user = this.getSessionUser();
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
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
		if (StringUtils.isNotEmpty(startDate)) {
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate)) {
			endDateTime = DateUtil.dateLess(endDate);
		}
		DataGrid<StoreOutVO> dg = storeItemService.findOutPageLikeName(
				startDateTime, endDateTime, user.getOrgId(), dywOwner,
				this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/in/load")
	public @ResponseBody Json<StoreInVO> inLoadById(String id) {
		StoreInVO vo = storeItemService.getInVoById(id);
		if (vo == null) {
			vo = new StoreInVO();
		} else {
			StoreArea p = storeAreaService.getById(vo.getAreaId());
			if (p != null) {
				vo.setAreaName(p.getName());
			}
		}
		return new Json<StoreInVO>(vo);
	}

	@RequestMapping(value = "/in/save")
	@RightAnnotation(name = "抵押管理/入库管理/入库", button = true, sort = 80101, needCheck = true, resource = "/store/area/tree")
	@Description("保存机构")
	public @ResponseBody Json<StoreInVO> inSave(StoreInVO form) {
		Json<StoreInVO> rst = new Json<StoreInVO>();
		try {
			form.setOrgId(this.getSessionUser().getOrgId());
			form.setOperator(this.getSessionUser().getUsername());
			form = storeItemService.in(form);
			rst.OK(form, "");
		} catch (CnplayRuntimeException e) {
			logger.error(e);
			rst.NG(e.getMessage());
		} catch (Throwable e) {
			logger.error(e);
			rst.NG("保存失败，请输入正确的信息");
		}
		return rst;
	}

	@RequestMapping(value = "/in/list")
	@RightAnnotation(name = "抵押管理/入库管理", component = "platform.system.view.StoreInPanel", resource = "/store/item/*", sort = 80100)
	public @ResponseBody DataGrid<StoreInVO> inLst(String orgId,
			String dywOwner, String startDate, String endDate) {
		User user = this.getSessionUser();
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
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
		if (StringUtils.isNotEmpty(startDate)) {
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate)) {
			endDateTime = DateUtil.dateLess(endDate);
		}
		DataGrid<StoreInVO> dg = storeItemService.findInPageLikeName(
				startDateTime, endDateTime, user.getOrgId(), dywOwner,
				this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/item/list")
	@RightAnnotation(name = "抵押管理/抵押物查询", component = "platform.system.view.StoreItemPanel", resource = "/store/item/*", sort = 80100)
	public @ResponseBody DataGrid<StoreItem> itemList(String orgId,
			String dywOwner, String startDate, String endDate) {
		User user = this.getSessionUser();
		if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
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
		if (StringUtils.isNotEmpty(startDate)) {
			startDateTime = DateUtil.dateGreater(startDate);
		}
		if (StringUtils.isNotEmpty(endDate)) {
			endDateTime = DateUtil.dateLess(endDate);
		}
		DataGrid<StoreItem> dg = storeItemService.findPageLikeName(
				startDateTime, endDateTime, user.getOrgId(), dywOwner,
				this.getPage(), this.getPageSize());
		return dg;
	}
}
