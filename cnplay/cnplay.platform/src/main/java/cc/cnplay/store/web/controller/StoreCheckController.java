package cc.cnplay.store.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.web.controller.AbsController;
import cc.cnplay.store.domain.StoreCheck;
import cc.cnplay.store.domain.StoreCheckItem;
import cc.cnplay.store.service.StoreCheckService;

@Controller
@RequestMapping(value = "/storeCheck")
public class StoreCheckController extends AbsController {

	@Resource
	private StoreCheckService storeCheckService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "抵押管理/盘库记录", component = "platform.system.view.StoreCheckPanel", resource = "/storeCheck/*", sort = 80100)
	public @ResponseBody DataGrid<StoreCheck> list(String operator,
			String startDate, String endDate) {
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
		DataGrid<StoreCheck> dg = storeCheckService.findPage(startDateTime,
				endDateTime, user.getOrgId(), operator, this.getPage(),
				this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/load")
	public @ResponseBody Json<StoreCheck> load(String id) {
		StoreCheck vo = storeCheckService.getById(id);
		return new Json<StoreCheck>(vo);
	}

	@RequestMapping(value = "/item")
	public @ResponseBody DataGrid<StoreCheckItem> itemPage(String id) {
		DataGrid<StoreCheckItem> dg = storeCheckService.temPagee(id,
				this.getPage(), this.getPageSize());
		return dg;
	}

	@RequestMapping(value = "/analyse")
	public @ResponseBody Json<StoreCheck> analyse(
			@RequestBody List<String> rfidList) {
		User user = this.getSessionUser();
		StoreCheck vo = storeCheckService.check(user.getOrgId(),
				user.getUsername(), rfidList);
		return new Json<StoreCheck>(vo);
	}

	@RequestMapping(value = "/save")
	@RightAnnotation(name = "抵押管理/盘库记录/保存", button = true, sort = 80101, needCheck = true, resource = "/storeCheck/*")
	public @ResponseBody Json<StoreCheck> save(StoreCheck form) {
		User user = this.getSessionUser();
		form.setOrgId(user.getOrgId());
		form.setOrgName(user.getOrgName());
		form.setOperator(user.getUsername());
		if (form.getItemList() != null && form.getItemList().size() > 0) {
			StoreCheck vo = storeCheckService.saveCheckItem(form);
			return new Json<StoreCheck>(vo);
		}
		return new Json<StoreCheck>(form);
	}
}
