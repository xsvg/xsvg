package cc.cnplay.platform.tools.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.service.PluginsService;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping(value = "/ptools/plugins")
public class PluginsController extends AbsController
{
	@Autowired
	private PluginsService pluginsService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统调试/插件管理", component = "platform.tools.view.PluginsPanel", debug = true, sort = 90200)
	public @ResponseBody Json<List<Plugins>> listAll()
	{
		return new Json<List<Plugins>>(pluginsService.findAll());
	}

	@RequestMapping(value = "/enable")
	@ResponseBody
	@RightAnnotation(name = "系统调试/插件管理/启用", button = true, sort = 90201)
	@Description("启用插件")
	public Json<?> enable(String id)
	{
		return pluginsService.updateStatus(id, Status.Normal);
	}

	@RequestMapping(value = "/disable")
	@ResponseBody
	@RightAnnotation(name = "系统调试/插件管理/停用", button = true, debug = true, sort = 90202)
	@Description("停用插件")
	public Json<?> disable(String id)
	{
		return pluginsService.updateStatus(id, Status.Banned);
	}

	@RequestMapping(value = "/remove")
	@ResponseBody
	@RightAnnotation(name = "系统调试/插件管理/删除", button = true, debug = true, sort = 90203)
	@Description("删除插件")
	public Json<?> remove(String id)
	{
		Json<Boolean> json = new Json<Boolean>();
		json.setSuccess(true);
		json.setRows(pluginsService.removeById(id));
		return json;
	}
}
