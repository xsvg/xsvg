package cc.cnplay.platform.tools.web.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.service.AuthenticationService;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.vo.RightVO;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping(value = "/ptools/right")
public class PluginsRightController extends AbsController
{
	private final static Logger log = Logger.getLogger(PluginsRightController.class);
	@Autowired
	private RightService rightService;
	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统调试/功能菜单", component = "platform.tools.view.RightPanel", debug = true, sort = 90300)
	public @ResponseBody Json<List<Right>> list(String id)
	{
		List<Right> list = rightService.findByParentId(id);
		for (Right r : list)
		{
			r.setLeaf(false);
		}
		return new Json<List<Right>>(list);
	}

	@RequestMapping(value = "/load")
	public @ResponseBody Json<Right> load(String id)
	{
		Right e = rightService.getById(id);
		if (e == null)
		{
			e = new Right();
		}
		return new Json<Right>(e);
	}

	@RequestMapping(value = "/save")
	@RightAnnotation(name = "系统调试/功能菜单/保存功能菜单", button = true, debug = true, resource = "/ptools/right/load*", sort = 90301)
	public @ResponseBody Json<Right> save(Right right)
	{
		Json<Right> result = new Json<Right>();
		try
		{
			rightService.saveRight(right);
			result.setSuccess(true);
		}
		catch (Exception e)
		{
			log.error("菜单保存失败", e);
			result.setMsg("数据保存失败,服务器可能已失去数据库连接");
		}
		return result;
	}

	@RequestMapping(value = "/remove")
	@RightAnnotation(name = "系统调试/功能菜单/删除功能菜单", debug = true, button = true, sort = 90302)
	public @ResponseBody Json<Boolean> remove(String id)
	{
		try
		{
			String[] ids = id.split(separator);
			for (String rid : ids)
			{
				List<Right> rightList = rightService.findByParentId(rid);
				if (rightList.size() > 0)
				{
					return new Json<Boolean>(false, true, "删除失败，请把子功能先删除！");
				}
			}
			rightService.removeByIds(id.split(separator));
			return new Json<Boolean>(true);
		}
		catch (Exception e)
		{
			log.error(e);
			return new Json<Boolean>(false, true, "删除失败，请把子功能先删除！");
		}
	}

	@RequestMapping(value = "/reload")
	@RightAnnotation(name = "系统调试/功能菜单/重置功能菜单", button = true, debug = true, sort = 90303)
	public @ResponseBody Json<String> reload(String id)
	{
		try
		{
			rightService.removeAll();
			rightService.removeAll(Plugins.class);
			authenticationService.initPlugins();
			return new Json<String>("重置功能菜单成功");
		}
		catch (java.lang.Throwable ex)
		{
			logger.error("插件初始化异常", ex);
			return new Json<String>("重置功能菜单失败");
		}

	}

	@RequestMapping("/reject/list")
	@ResponseBody
	public Json<List<RightVO>> listReject(String id)
	{
		List<RightVO> tree = rightService.findRightRejects(id);
		return new Json<List<RightVO>>(tree);
	}

	@RequestMapping("/reject/save")
	@ResponseBody
	public Json<Boolean> saveReject(String refId, String rightIds)
	{
		try
		{
			rightService.saveRightReject(refId, rightIds);
			return new Json<Boolean>(true);
		}
		catch (Exception e)
		{
			log.error(e);
			return new Json<Boolean>(false, true, "保存失败,原因:" + e.getMessage());
		}
	}
}
