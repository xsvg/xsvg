package cc.cnplay.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import cc.cnplay.core.dao.GeneralDao;
import cc.cnplay.core.vo.DataGrid;

/**
 * dao() to CRUD POJOs
 * 
 * @author <a href="mailto:peixere@qq.com">裴绍国</a>
 */
public class GeneralServiceImpl implements GeneralService
{
	protected GeneralDao generalDao;

	protected GeneralDao dao()
	{
		return generalDao;
	}

	@Override
	public void setGeneralDao(GeneralDao generalDao)
	{
		this.generalDao = generalDao;
	}

	@Override
	public boolean exist(Class<?> clazz, Serializable id)
	{
		return dao().exist(clazz, id);
	}

	@Override
	public <T> List<T> find(Class<T> clazz, int maxResults, int firstResult)
	{
		return dao().find(clazz, maxResults, firstResult);
	}

	@Override
	public <T> List<T> findByMap(Class<T> clazz, Map<String, Object> params)
	{
		return dao().findByMap(clazz, params);
	}

	@Override
	public <T> List<T> findByField(Class<T> clazz, String fieldname, Object fieldvalue)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(fieldname, fieldvalue);
		return dao().findByMap(clazz, map);
	}

	@Override
	public <T> List<T> findAll(Class<T> clazz)
	{
		return dao().findAll(clazz);
	}

	@Override
	public <T> List<T> findByIds(Class<T> clazz, Serializable[] ids)
	{
		return dao().findByIds(clazz, ids);
	}

	@Override
	public <T> DataGrid<T> findPagination(Class<T> clazz, int pageIndex, int pageSize)
	{
		return dao().findPagination(clazz, pageIndex, pageSize);
	}

	@Override
	public <T> DataGrid<T> findPagination(Class<T> clazz, Map<String, Object> params, int pageIndex, int pageSize)
	{
		return dao().findPagination(clazz, params, pageIndex, pageSize);
	}

	@Override
	public <T> T getByField(Class<T> clazz, String name, Object value)
	{
		return dao().getByField(clazz, name, value);
	}

	@Override
	public <T> T getById(Class<T> clazz, Serializable id)
	{
		return dao().getById(clazz, id);
	}

	@Override
	public <T> T getLast(Class<T> clazz)
	{
		return dao().getLast(clazz);
	}

	@Override
	public long count(Class<?> clazz)
	{
		return dao().count(clazz);
	}

	@Override
	public <T> T getReference(Class<T> type, Serializable id)
	{
		return dao().getReference(type, id);
	}

	@Override
	public <T> T[] getReferences(Class<T> type, Serializable[] ids)
	{
		return dao().getReferences(type, ids);
	}
	@Transactional
	@Override
	public boolean remove(Object value)
	{
		return dao().remove(value);
	}
	@Transactional
	@Override
	public void remove(Object... entities)
	{
		dao().remove(entities);
	}
	@Transactional
	@Override
	public boolean removeById(Class<?> clazz, Serializable id)
	{
		return dao().removeById(clazz, id);
	}
	@Transactional
	@Override
	public void removeByIds(Class<?> clazz, Serializable[] ids)
	{
		dao().removeByIds(clazz, ids);
	}
	@Transactional
	@Override
	public void removeAll(Class<?> clazz)
	{
		dao().removeAll(clazz);
	}
	@Transactional
	@Override
	public void removeAll(Collection<?> entitys)
	{
		dao().remove(entitys.toArray());
	}
	@Transactional
	@Override
	public <T> T merge(T entity)
	{
		return dao().merge(entity);
	}
	@Transactional
	@Override
	public Object[] merge(Object... entities)
	{
		return dao().merge(entities);
	}
	@Transactional
	@Override
	public void persist(Object... entities)
	{
		dao().persist(entities);
	}
	@Transactional
	@Override
	public <T> T save(T entity)
	{
		return dao().save(entity);
	}
	@Transactional
	@SuppressWarnings("unchecked")
	@Override
	public <T> T save(Class<T> value)
	{
		return (T) dao().save(value);
	}
	@Transactional
	@Override
	public Object[] save(Object... entities)
	{
		return dao().save(entities);
	}
	@Transactional
	@Override
	public void saveAll(Collection<?> entitys)
	{
		dao().saveAll(entitys);
	}
	@Transactional
	@Override
	public void saveAll(Collection<?> entities, int flushCase)
	{
		dao().saveAll(entities, flushCase);
	}

	@Override
	public <T> T getByMap(Class<T> clazz, Map<String, Object> params)
	{
		return dao().getByMap(clazz, params);
	}

}