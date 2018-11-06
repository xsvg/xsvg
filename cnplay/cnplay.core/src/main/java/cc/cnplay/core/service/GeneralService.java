package cc.cnplay.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cc.cnplay.core.dao.GeneralDao;
import cc.cnplay.core.vo.DataGrid;

/**
 * UniversalDao to CRUD POJOs
 * 
 * @author <a href="mailto:peixere@qq.com">裴绍国</a>
 */

public interface GeneralService
{

	void setGeneralDao(GeneralDao generalDao);

	boolean exist(Class<?> clazz, Serializable id);

	<T> List<T> find(Class<T> clazz, int maxResults, int firstResult);

	<T> List<T> findByMap(Class<T> clazz, Map<String, Object> params);

	<T> List<T> findByField(Class<T> clazz, String fieldname, Object fieldvalue);

	<T> List<T> findAll(Class<T> clazz);

	<T> DataGrid<T> findPagination(Class<T> clazz, int pageIndex, int pageSize);

	<T> DataGrid<T> findPagination(Class<T> clazz, Map<String, Object> params, int pageIndex, int pageSize);

	<T> T getByField(Class<T> clazz, String fieldname, Object fieldvalue);

	<T> T getLast(Class<T> clazz);

	long count(Class<?> clazz);

	void removeAll(Class<?> clazz);

	<T> T save(Class<T> value);

	void saveAll(Collection<?> entitys);

	void saveAll(Collection<?> entities, int flushCase);

	<T> T getById(Class<T> type, Serializable id);

	<T> T getByMap(Class<T> clazz, Map<String, Object> params);

	<T> List<T> findByIds(Class<T> type, Serializable[] ids);

	<T> T getReference(Class<T> type, Serializable id);

	<T> T[] getReferences(Class<T> type, Serializable[] ids);

	void persist(Object... entities);

	<T> T merge(T entity);

	Object[] merge(Object... entities);

	<T> T save(T entity);

	Object[] save(Object... entities);

	boolean remove(Object entity);

	void remove(Object... entities);

	void removeAll(Collection<?> entitys);

	boolean removeById(Class<?> type, Serializable id);

	void removeByIds(Class<?> type, Serializable[] ids);

}