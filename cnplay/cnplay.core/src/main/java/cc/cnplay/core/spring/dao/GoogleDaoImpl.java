package cc.cnplay.core.spring.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.dao.jpa.GeneralDAOImpl;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;

@Repository
public class GoogleDaoImpl extends GeneralDAOImpl implements GoogleDao
{
//	@PersistenceContext
//	public void setEntityManager(EntityManager entityManager)
//	{
//		super.setEntityManager(entityManager);
//		Session Session = (org.hibernate.Session) entityManager.getDelegate();
//		MetadataUtil metadataUtil = HibernateMetadataUtil.getInstanceForSessionFactory(Session.getSessionFactory());
//		JPASearchProcessor searchProcessor = new JPASearchProcessor(metadataUtil);
//		super.setSearchProcessor(searchProcessor);
//	}

	@Override
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager)
	{
		super.setEntityManager(entityManager);
	}

	@Override
	@Resource
	public void setSearchProcessor(JPASearchProcessor searchProcessor)
	{
		super.setSearchProcessor(searchProcessor);
	}
}