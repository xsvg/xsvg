package cc.cnplay.platform.tools.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.SysConfig;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping(value = "/system/systemConfig")
public class SystemConfigController extends AbsController
{
	@Autowired
	private SystemConfigService systemConfigService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统调试/运行参数", component = "platform.tools.view.SystemConfigPanel", delete = true, debug = true, sort = 90100)
	public @ResponseBody DataGrid<SysConfig> page()
	{
		return systemConfigService.findPagination(this.getPage(), this.getPageSize());
	}

	@RequestMapping(value = "/load")
	public @ResponseBody Json<SysConfig> loadById(String id)
	{
		SysConfig sc = systemConfigService.getById(id);
		if (sc == null)
		{
			sc = new SysConfig();
		}
		return new Json<SysConfig>(sc);
	}

	@RequestMapping(value = "/save")
	@RightAnnotation(name = "系统调试/运行参数/修改", button = true, resource = "/system/systemConfig/load", delete = true, debug = true, sort = 90101)
	@Description("保存系统配置")
	public @ResponseBody Json<Boolean> save(SysConfig sysConfigVO)
	{

		Json<Boolean> json = new Json<Boolean>();
		try
		{
			// SysConfig sc = systemConfigService.getById(sysConfigVO.getId());
			systemConfigService.saveForm(sysConfigVO);
			json.OK(Boolean.TRUE, "");

		}
		catch (Exception e)
		{
			json.NG(e.getMessage());
		}
		return json;
	}

	@RequestMapping(value = "/remove", produces = "application/json")
	@RightAnnotation(name = "系统调试/运行参数/删除", button = true, debug = true, sort = 90102)
	@Description("删除系统配置")
	public @ResponseBody Json<String> remove(String id)
	{
		Json<String> rst = new Json<String>();
		try
		{
			systemConfigService.removeByIds(id.split(separator));
			rst.OK("", "删除成功！");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			rst.NG(e.getMessage());
		}
		return rst;
	}
}
