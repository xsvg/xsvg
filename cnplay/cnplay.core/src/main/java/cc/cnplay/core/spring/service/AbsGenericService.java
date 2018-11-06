package cc.cnplay.core.spring.service;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cc.cnplay.core.service.GenericServiceImpl;
import cc.cnplay.core.spring.dao.UniversalDao;

/**
 * 泛型Service基类
 * 
 * @version 2015-06-19
 */
public abstract class AbsGenericService<T, PK extends Serializable> extends GenericServiceImpl<T, PK>
{
	protected final Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	public void setUniversalDao(UniversalDao dao)
	{
		super.setGeneralDao(dao);
	}
}