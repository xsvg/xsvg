package cc.cnplay.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import cc.cnplay.core.util.GenericUtil;
import cc.cnplay.core.vo.DataGrid;

/**
 * CRUD
 * 
 * @author <a href="mailto:peixere@qq.com">裴绍国</a>
 * @param <T>
 *            a type variable
 * @param <ID>
 *            the primary key for that type
 */
@SuppressWarnings("unchecked")
public class GenericDaoImpl<T, ID extends Serializable> extends GeneralDaoImpl implements GenericDao<T, ID>
{
	protected Class<T> persistentClass = (Class<T>) GenericUtil.getTypeArguments(GenericDaoImpl.class, this.getClass()).get(0);

	protected String name;

	protected String table;

	public GenericDaoImpl()
	{
		super();
		this.name = persistentClass.getSimpleName();
		this.table = this.persistentClass.getAnnotation(Table.class).name();
	}

	@Override
	public long count()
	{
		return count(this.persistentClass);
	}

	@Override
	public void removeAll()
	{
		String jpql = "delete from " + persistentClass.getSimpleName();
		em().createQuery(jpql).executeUpdate();
	}

	@Override
	public boolean exist(ID id)
	{
		T entity = this.getById(id);
		return entity != null;
	}

	@Override
	public T getByField(String name, Object value)
	{
		return getByField(persistentClass, name, value);
	}

	@Override
	public List<T> find(int maxResults, int firstResult)
	{
		return find(persistentClass, maxResults, firstResult);
	}

	@Override
	public DataGrid<T> findPagination(int pageIndex, int pageSize)
	{
		return this.findPagination(persistentClass, pageIndex, pageSize);
	}

	@Override
	public DataGrid<T> findPagination(Map<String, Object> params, int pageIndex, int pageSize)
	{
		return this.findPagination(persistentClass, params, pageIndex, pageSize);
	}

	@Override
	public T getLast()
	{
		return this.getLast(persistentClass);
	}

	@Override
	public T getById(ID id)
	{
		return this.getById(persistentClass, id);
	}

	@Override
	public List<T> findByIds(ID[] ids)
	{
		return (List<T>) this.findByIds(persistentClass, ids);
	}

	@Override
	public T getReference(ID id)
	{
		return this.getReference(persistentClass, id);
	}

	@Override
	public T[] getReferences(ID[] ids)
	{
		return this.getReferences(persistentClass, ids);
	}

	@Override
	public boolean removeById(ID id)
	{
		return this.removeById(persistentClass, id);
	}

	@Override
	public void removeByIds(ID[] ids)
	{
		this.removeByIds(persistentClass, ids);
	}

	@Override
	public List<T> findAll()
	{
		return this.findAll(persistentClass);
	}

	@Override
	public List<T> findByMap(Map<String, Object> params)
	{
		return this.findByMap(persistentClass, params);
	}

	@Override
	public List<T> findByField(String fieldname, Object fieldvalue)
	{
		return this.findByField(persistentClass, fieldname, fieldvalue);
	}

	@Override
	public T find(ID id)
	{
		return this.getById(id);
	}

	@Override
	public T[] find(ID[] ids)
	{
		return this.find(persistentClass, ids);
	}

}
