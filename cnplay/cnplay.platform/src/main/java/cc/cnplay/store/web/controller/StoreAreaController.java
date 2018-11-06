package cc.cnplay.store.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.web.controller.AbsController;
import cc.cnplay.store.domain.StoreArea;
import cc.cnplay.store.service.StoreAreaService;

@Controller
@RequestMapping(value = "/store/area")
public class StoreAreaController extends AbsController {
	@Autowired
	private StoreAreaService storeAreaService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "抵押管理/库房区域管理", component = "platform.system.view.StoreAreaPanel", resource = "/system/StoreArea/export*", sort = 80100)
	public @ResponseBody Json<List<StoreArea>> list(String id) {
		User curUser = this.getSessionUser();
		List<StoreArea> list = storeAreaService.findByParentId(
				curUser.getOrgId(), id);
		return new Json<List<StoreArea>>(list);
	}

	@RequestMapping(value = "/load")
	public @ResponseBody Json<StoreArea> loadById(String id) {
		StoreArea org = storeAreaService.getById(id);
		if (org == null) {
			org = new StoreArea();
		} else {
			StoreArea p = storeAreaService.getById(org.getParentId());
			if (p != null) {
				org.setParentName(p.getName());
			}
			org.setChecked(storeAreaService.existChild(org.getOrgId(), id));
		}
		return new Json<StoreArea>(org);
	}

	@RequestMapping(value = "/save")
	@RightAnnotation(name = "抵押管理/库房区域管理/修改", button = true, sort = 80101, resource = "/store/area/*")
	@Description("保存机构")
	public @ResponseBody Json<StoreArea> save(StoreArea form) {
		Json<StoreArea> rst = new Json<StoreArea>();
		try {
			form.setOrgId(this.getSessionUser().getOrgId());
			form.setOrgName(this.getSessionUser().getOrgName());
			form = storeAreaService.save(form);
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

	@Ignore
	@RequestMapping(value = "/loadTree")
	@ResponseBody
	public Json<List<StoreArea>> loadTree(String id, String myId,
			String[] areaId) {
		List<StoreArea> list = storeAreaService.findByParentId(this
				.getSessionUser().getOrgId(), id);
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).getId().equalsIgnoreCase(myId)) {
				list.remove(i);
			}
		}
		storeAreaService.checked(areaId, list);
		return new Json<List<StoreArea>>(list);
	}

	@RequestMapping(value = "/remove", produces = "application/json")
	@RightAnnotation(name = "抵押管理/库房区域管理/删除", button = true, sort = 80102)
	public @ResponseBody Json<String> remove(String id) {
		Json<String> rst = new Json<String>();
		try {
			User curUser = this.getSessionUser();
			StoreArea org = storeAreaService.getById(id);
			if (org != null) {
				if (org.getOrgId().equals(curUser.getOrgId())) {
					storeAreaService.removeById(id);
				}
			}
			rst.OK("", "机构删除成功!");
		} catch (Exception e) {
			logger.error(e);
			rst.NG("删除失败，机构信息被引用！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/findTree")
	public List<StoreArea> findTree() {
		return storeAreaService.findAll();
	}

}
