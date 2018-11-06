package cc.cnplay.core.spring.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.cnplay.core.service.GeneralServiceImpl;
import cc.cnplay.core.spring.dao.UniversalDao;

/**
 * SuperServiceImpl to CRUD POJOs
 * 
 * @author <a href="mailto:peixere@qq.com">裴绍国</a>
 */
@Service
@Transactional
public class UniversalServiceImpl extends GeneralServiceImpl implements UniversalService
{
	@Autowired
	public void setUniversalDao(UniversalDao dao)
	{
		super.setGeneralDao(dao);
	}
}