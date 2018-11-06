package cc.cnplay.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import cc.cnplay.core.util.GenericUtil;
import cc.cnplay.core.vo.DataGrid;

/**
 * GenericDao to CRUD POJOs
 * 
 * @author <a href="mailto:qixere@qq.com">pei shaoguo</a>
 * @param <T>
 *            a type variable
 * @param <ID>
 *            the primary key for that type
 */
public class GenericServiceImpl<T, ID extends Serializable> extends GeneralServiceImpl implements GenericService<T, ID>
{

	@SuppressWarnings("unchecked")
	protected Class<T> persistentClass = (Class<T>) GenericUtil.getTypeArguments(GenericServiceImpl.class, this.getClass()).get(0);

	@Override
	public boolean exist(ID id)
	{
		return dao().exist(persistentClass, id);
	}

	@Override
	public List<T> find(int maxResults, int firstResult)
	{
		return dao().find(persistentClass, maxResults, firstResult);
	}

	@Override
	public T getByField(String name, Object value)
	{
		return dao().getByField(persistentClass, name, value);
	}

	@Override
	public T getLast()
	{
		return dao().getLast(persistentClass);
	}

	@Override
	public long count()
	{
		return dao().count(persistentClass);
	}

	@Override
	public void removeAll()
	{
		dao().removeAll(persistentClass);

	}

	@Override
	public DataGrid<T> findPagination(int pageIndex, int pageSize)
	{
		return dao().findPagination(persistentClass, pageIndex, pageSize);
	}

	@Override
	public DataGrid<T> findPagination(Map<String, Object> params, int pageIndex, int pageSize)
	{
		return dao().findPagination(persistentClass, params, pageIndex, pageSize);
	}

	@Override
	public T getById(ID id)
	{
		return dao().getById(persistentClass, id);
	}

	@Override
	public T getReference(ID id)
	{
		return dao().getReference(persistentClass, id);
	}

	@Override
	public T[] getReferences(ID[] ids)
	{
		return dao().getReferences(persistentClass, ids);
	}

	@Override
	public List<T> findByIds(ID[] ids)
	{
		return dao().findByIds(persistentClass, ids);
	}

	@Override
	public List<T> findByField(String fieldname, Object fieldvalue)
	{
		return dao().findByField(persistentClass, fieldname, fieldvalue);
	}

	@Override
	public List<T> findByMap(Map<String, Object> params)
	{
		return dao().findByMap(persistentClass, params);
	}

	@Override
	public List<T> findAll()
	{
		return dao().findAll(persistentClass);
	}
	@Transactional
	@Override
	public boolean removeById(ID id)
	{
		return dao().removeById(persistentClass, id);
	}
	@Transactional
	@Override
	public void removeByIds(ID[] ids)
	{
		dao().removeByIds(persistentClass, ids);
	}

	@Override
	public T getByMap(Map<String, Object> params)
	{
		return dao().getByMap(persistentClass, params);
	}

	protected int getFirstResult(int page, int pageSize)
	{
		return dao().getFirstResult(page, pageSize);
	}
}