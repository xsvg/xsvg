package cc.cnplay.store.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.web.controller.AbsController;
import cc.cnplay.store.domain.StoreItem;
import cc.cnplay.store.service.StoreCheckService;
import cc.cnplay.store.vo.TagVo;

@Controller
@RequestMapping(value = "/home/store")
public class HomeStoreController extends AbsController {

	private final Logger logger = Logger.getLogger(this.getClass());

	public static final Map<String, String> tagMap = new HashMap<String, String>();

	@Resource
	private StoreCheckService storeCheckService;

	@Ignore
	@RequestMapping(value = "/tag", method = RequestMethod.POST)
	public @ResponseBody Json<Boolean> tag(@RequestBody StoreItem item) {
		String username = this.getSessionUsername();
		tagMap.put(username, item.getRfid());
		logger.info(username + "=" + item.getRfid());
		return new Json<Boolean>(true);
	}

	@Ignore
	@RequestMapping(value = "/getTag", method = RequestMethod.POST)
	public @ResponseBody Json<String> getTag() {
		String username = this.getSessionUsername();
		String rfid = tagMap.get(username);
		if (rfid == null) {
			rfid = "";
		}
		tagMap.remove(username);
		logger.info(username + "=" + rfid);
		return new Json<String>(rfid);
	}

	@Ignore
	@RequestMapping(value = "/tagList", method = RequestMethod.POST)
	public @ResponseBody Json<Boolean> tagList(@RequestBody List<TagVo> tagList) {
		for (TagVo vo : tagList) {
			String[] uiis = vo.getTagUii().split("EPC:");
			logger.debug(vo.getTagUii());
			String tid = uiis[0];
			tid = tid.replaceAll("\n", "");
			tid = tid.replaceAll("TID:", "");
			vo.setTid(tid);
			if (uiis.length > 0) {
				vo.setEpc(uiis[1]);
			}
		}
		String username = this.getSessionUsername();
		storeCheckService.put(username, tagList);
		return new Json<Boolean>(true);
	}

	@Ignore
	@RequestMapping(value = "/getTagListSize", method = RequestMethod.POST)
	public @ResponseBody Json<Integer> getTagListSize() {
		String username = this.getSessionUsername();
		List<TagVo> tagList = storeCheckService.get(username);
		Json<Integer> json = new Json<Integer>();
		if (tagList != null && tagList.size() > 0) {
			json.OK(tagList.size(), "盘点数据");
		} else {
			json.NG("请使用PDA盘点扫描上传标签后再进行盘点！");
		}
		return json;
	}
}
