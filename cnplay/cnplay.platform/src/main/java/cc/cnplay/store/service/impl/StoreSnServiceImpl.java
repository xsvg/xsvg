package cc.cnplay.store.service.impl;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.platform.dao.AreaDao;
import cc.cnplay.store.dao.StoreAreaDao;
import cc.cnplay.store.domain.StoreSn;
import cc.cnplay.store.service.StoreSnService;

@Service
@Transactional
public class StoreSnServiceImpl extends AbsGenericService<StoreSn, String>
		implements StoreSnService {

	@Resource
	private StoreAreaDao orgDao;

	@Resource
	private AreaDao areaDao;


}
