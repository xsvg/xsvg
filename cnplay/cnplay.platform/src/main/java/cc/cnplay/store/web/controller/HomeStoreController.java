package cc.cnplay.store.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cc.cnplay.store.vo.TagVo;

@Controller
@RequestMapping(value = "/home/store")
public class HomeStoreController extends AbsController {

	private final Logger logger = Logger.getLogger(this.getClass());

	private static final Map<String, String> tagMap = new HashMap<String, String>();

	@Ignore
	@RequestMapping(value = "/tag", method = RequestMethod.POST)
	public @ResponseBody Json<Boolean> tag(@RequestBody StoreItem item) {
		String username = this.getSessionUsername();
		tagMap.put(username, item.getRfid());
		logger.info(username + "=" + item.getRfid());
		return new Json<Boolean>(true);
	}

	@Ignore
	@RequestMapping(value = "/tag", method = RequestMethod.GET)
	public @ResponseBody Json<String> tag() {
		String username = this.getSessionUsername();
		logger.info(username + "=" + tagMap.get(username));
		return new Json<String>(tagMap.get(username));
	}

	@Ignore
	@RequestMapping(value = "/tagList", method = RequestMethod.POST)
	public @ResponseBody Json<Boolean> tagList(@RequestBody List<TagVo> json) {
		for (TagVo vo : json) {
			String[] uiis = vo.getTagUii().split("EPC:");
			String tid = uiis[0];
			tid = tid.replaceAll("\n", "");
			tid = tid.replaceAll("TID:", "");
			vo.setTid(tid);
			if(uiis.length>0) {
				vo.setEpc(uiis[1]);
			}
		}
		logger.info(json);
		return new Json<Boolean>(true);
	}

	@Ignore
	@RequestMapping(value = "/tagList", method = RequestMethod.GET)
	public @ResponseBody Json<Boolean> tagList() {
		String username = this.getSessionUsername();

		return new Json<Boolean>(true);
	}
}
