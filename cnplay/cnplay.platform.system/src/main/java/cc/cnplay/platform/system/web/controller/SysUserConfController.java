package cc.cnplay.platform.system.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/system/sysconf")
public class SysUserConfController extends AbsController
{
	@Autowired
	private SystemConfigService systemConfigService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统管理/系统参数", component = "platform.system.view.SysUserConfPanel", sort = 80900)
	public @ResponseBody DataGrid<SysConfig> page()
	{
		return systemConfigService.findUserPage(this.getPage(), this.getPageSize());
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
	@RightAnnotation(name = "系统管理/系统参数/修改参数", button = true, resource = "/system/sysconf/load", sort = 80901)
	public @ResponseBody Json<Boolean> save(SysConfig sysConfigVO)
	{

		Json<Boolean> json = new Json<Boolean>();
		try
		{
			systemConfigService.saveForm(sysConfigVO);
			json.OK(Boolean.TRUE, "");

		}
		catch (Exception e)
		{
			json.NG("删除失败！");
		}
		return json;
	}
}
