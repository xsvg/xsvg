package cc.cnplay.core.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.genericdao.search.Search;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.vo.DataGrid;

public interface GeneralDao extends com.googlecode.genericdao.dao.jpa.GeneralDAO
{

	@Memo("批量添加或更新时释放清理的记录数")
	static final int FLUSH_CASE = 50;

	@Memo("执行一条sql语句返回影响的行数")
	int excuteSql(String sql, Map<String, Object> params);

	@Memo("执行一条jpql语句返回影响的行数")
	void execute(String jpql, Object... values);

	boolean exist(Class<?> clazz, Serializable id);

	<T> List<T> find(Class<T> clazz, int maxResults, int firstResult);

	<T> List<T> findBySQL(Class<T> clazz, String sql);

	<T> List<T> findByMap(Class<T> clazz, Map<String, Object> params);

	<T> List<T> findByMap(Class<T> clazz, Map<String, Object> params, int maxResults, int firstResult);

	<T> List<T> findByField(Class<T> clazz, String fieldname, Object fieldvalue);

	<T> List<T> findByIds(Class<T> clazz, Serializable[] ids);

	List<?> findList(String jpql, Object... values);

	List<?> findList(int maxResults, String jpql, Object... values);

	List<?> findListByMap(String jpql, Map<String, Object> params);

	List<?> findListByMap(String jpql, Map<String, Object> params, int maxResults);

	List<?> findListByMap(String jpql, Map<String, Object> params, int maxResults, int firstResult);

	DataGrid<?> findPagination(String jpql, String countJpql, int pageIndex, int pageSize, Object... values);

	DataGrid<?> findPaginationByMap(String jpql, String countJpql, int pageIndex, int pageSize, Map<String, Object> params);

	<T> DataGrid<T> findPagination(Class<T> clazz, int pageIndex, int pageSize);

	<T> DataGrid<T> findPagination(Class<T> clazz, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 返回数组列表
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List findBySql(String sql, Map<String, Object> params);

	/**
	 * 返回map列表,传入sql查询列必须指定别名,如"select t.usid usid,t.name name from t_user t" 返回map的key值均为大写，如要改变key值的大小写则在sql中命名列别名时以双引号包裹,如String sql="select t.usid \"usid\",t.name \"name\" from t_user t"
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> findBySql2Map(String sql, Map<String, Object> params);

	/**
	 * 返回数组列表
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List findBySqlPagination(String sql, Map<String, Object> params, int page, int pageSize);

	/**
	 * 返回map列表,传入sql查询列必须指定别名,如"select t.usid usid,t.name name from t_user t" 返回map的key值均为大写，如要改变key值的大小写则在sql中命名列别名时以双引号包裹,如String sql="select t.usid \"usid\",t.name \"name\" from t_user t"
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> findBySql2MapPagination(String sql, Map<String, Object> params, int page, int pageSize);

	<T> T getByField(Class<T> clazz, String fieldName, Object fieldValue);

	<T> T getByMap(Class<T> clazz, Map<String, Object> params);

	<T> T getLast(Class<T> clazz);

	long count(Class<?> clazz);

	long count(Class<?> clazz, Map<String, Object> params);

	long countByMap(String jpql, Map<String, Object> params);

	long count(String jpql, Object... values);

	String getDefaultOrderBy(Class<?> clazz, String prefix);

	Object getUniqueResult(String jql, Map<String, Object> params);

	<T extends Number> T getScalar(String jql, Map<String, Object> params);

	/**
	 * 返回一个数组,如查询结果为空或行数大于1异常
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	Object[] getUniqueResultBySql(String sql, Map<String, Object> params);

	/**
	 * 返回一个map,传入sql查询列必须指定别名,如String sql="select t.usid usid,t.name name from t_user t", 返回map的key值均为大写，如要改变key值的大小写则在sql中命名列别名时以双引号包裹,如String sql="select t.usid \"usid\",t.name \"name\" from t_user t"
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	Map<String, Object> getUniqueMapBySql(String sql, Map<String, Object> params);

	/**
	 * 返回一个数值类型的值,用户count,sum等统计语句
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	<T extends Number> T getScalarBySql(String sql, Map<String, Object> params);

	Object getSingleResult(String jpql, Object... values);

	Object getSingleResultByMap(String jpql, Map<String, Object> params);

	void removeAll(Class<?> clazz);

	void removeNative(String table, String where, String value);

	void removeByField(Class<?> clazz, String fieldName, String fieldValue);

	void saveAll(Collection<?> entitys);

	void saveAll(Collection<?> entities, int flushCase);

	public <T> T getById(Class<T> type, Serializable id);

	public <T> T[] merge(Class<T> type, Object... entities);

	int getFirstResult(int page, int pageSize);

	void addSort(Search search, String name, String AscOrDesc);

	boolean exitField(Class<?> clazz, String name);

	<T> List<T> searchFilterEqual(Class<T> clazz, String fieldname, Object value);

	void evictAll();

	JdbcTemplate jdbcTemplate();

	int countBySql(String sql);

}
