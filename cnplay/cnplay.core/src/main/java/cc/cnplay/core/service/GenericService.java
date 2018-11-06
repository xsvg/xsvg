package cc.cnplay.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cc.cnplay.core.vo.DataGrid;

/**
 * GenericDao to CRUD POJOs
 * 
 * @author <a href="mailto:qixere@qq.com">pei shaoguo</a>
 * 
 */
public interface GenericService<T, ID extends Serializable> extends GeneralService
{

	boolean exist(ID id);

	List<T> findByIds(ID[] ids);

	List<T> find(int maxResults, int firstResult);

	List<T> findByMap(Map<String, Object> params);

	List<T> findByField(String fieldname, Object fieldvalue);

	List<T> findAll();

	T getByField(String fieldName, Object fieldValue);

	T getByMap(Map<String, Object> params);

	T getById(ID id);

	T getLast();

	long count();

	boolean removeById(ID id);

	void removeByIds(ID[] ids);

	void removeAll();

	T getReference(ID id);

	T[] getReferences(ID[] ids);

	DataGrid<T> findPagination(int pageIndex, int pageSize);

	DataGrid<T> findPagination(Map<String, Object> params, int pageIndex, int pageSize);
}