package cc.cnplay.platform.system.web.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.CheckLog;
import cc.cnplay.platform.service.CheckLogService;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping(value = "/system/checkLog")
public class CheckLogController extends AbsController
{
	@Autowired
	private CheckLogService checkLogService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统管理/复核记录", component = "platform.system.view.CheckLogPanel", icon = "", sort = 80600)
	public @ResponseBody DataGrid<CheckLog> page(String startDate, String endDate, String orgId)
	{
		DataGrid<CheckLog> dg = new DataGrid<CheckLog>();
		try
		{
			Date checkStartDate = null;
			Date checkEndDate = null;
			if (StringUtils.isNotEmpty(startDate))
			{
				checkStartDate = DateUtil.dateGreater(startDate);
			}
			if (StringUtils.isNotEmpty(endDate))
			{
				checkEndDate = DateUtil.dateLess(endDate);
			}
			dg = checkLogService.findPageByCheckTime(checkStartDate, checkEndDate, this.getSessionUser().getOrganization(), orgId, this.getPage(), this.getPageSize());
		}
		catch (Throwable e)
		{
			logger.error("", e);
			e.printStackTrace();
		}
		return dg;
	}
}
