package cc.cnplay.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cc.cnplay.core.vo.DataGrid;

/**
 * GenericDao to CRUD POJOs
 * 
 * @author <a href="mailto:pqixere@qq.com">裴绍国</a>
 * @param <T>
 *            a type variable
 * @param <ID>
 *            the primary key for that type
 */
public interface GenericDao<T, ID extends Serializable> extends GeneralDao
{
	boolean exist(ID id);

	List<T> find(int maxResults, int firstResult);

	List<T> findByMap(Map<String, Object> params);

	List<T> findByField(String fieldname, Object fieldvalue);

	T getByField(String fieldname, Object fieldvalue);

	T getLast();

	long count();

	void removeAll();

	DataGrid<T> findPagination(int pageIndex, int pageSize);

	DataGrid<T> findPagination(Map<String, Object> params, int pageIndex, int pageSize);

	T getById(ID id);

	List<T> findByIds(ID[] ids);

	T find(ID id);

	T[] find(ID[] ids);

	T getReference(ID id);

	T[] getReferences(ID[] ids);

	boolean removeById(ID id);

	void removeByIds(ID[] ids);

	List<T> findAll();
}