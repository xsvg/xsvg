package cc.cnplay.platform.system.web.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.vo.RightVO;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping(value = "/system/right")
public class RightController extends AbsController
{
	private final static Logger log = Logger.getLogger(RightController.class);
	@Autowired
	private RightService rightService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统管理/功能管理", component = "platform.system.view.RightPanel", sort = 80400)
	public @ResponseBody Json<List<RightVO>> list(String id)
	{
		List<RightVO> voList = rightService.loadTreeAll();
		return new Json<List<RightVO>>(voList);
//		List<Right> list = rightService.findByParentId(id);
//		for (Right r : list)
//		{
//			r.setLeaf(false);
//			if (RightType.BUTTON.name().equals(r.getType()))
//			{
//				r.setLeaf(true);
//			}
//		}
//		return new Json<List<Right>>(list);
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
	@RightAnnotation(name = "系统管理/功能管理/保存", button = true, sort = 80401, resource = "/system/right/load*;/system/right/reject/*")
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
	//@RightAnnotation(name = "系统管理/功能管理/删除", button = true, sort = 80402)
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

	@RequestMapping("/reject/list")
	@ResponseBody
	public Json<List<RightVO>> listReject(String id)
	{
		List<RightVO> tree = rightService.findRightRejects(id);
		return new Json<List<RightVO>>(tree);
	}

	@RequestMapping("/reject/save")
	@RightAnnotation(name = "系统管理/功能管理/保存互斥", button = true, resource = "/system/right/reject/list", sort = 80403)
	public @ResponseBody Json<Boolean> saveReject(String refId, String rightIds)
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
