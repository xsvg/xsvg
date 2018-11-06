package cc.cnplay.core.dao;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.OrderBy;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import cc.cnplay.core.util.Converter;
import cc.cnplay.core.vo.DataGrid;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;

/**
 * CRUD
 * 
 * @author <a href="mailto:peixere@qq.com">裴绍国</a>
 */
public class GeneralDaoImpl extends
		com.googlecode.genericdao.dao.jpa.GeneralDAOImpl implements GeneralDao {

	protected final Logger log = Logger.getLogger(getClass());

	protected final String ArgsPrefix = "arg";

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public JdbcTemplate jdbcTemplate() {
		return this.jdbcTemplate;
	}

	// @PersistenceContext
	private EntityManager entityManager;

	protected EntityManager em() {
		return this.entityManager;
	}

	/*
	 * @Override
	 * 
	 * @PersistenceContext public void setEntityManager(EntityManager
	 * entityManager) { this.entityManager = entityManager;
	 * super.setEntityManager(entityManager); Session session =
	 * (org.hibernate.Session) entityManager.getDelegate(); MetadataUtil
	 * metadataUtil =
	 * HibernateMetadataUtil.getInstanceForSessionFactory(session.
	 * getSessionFactory()); JPASearchProcessor searchProcessor = new
	 * JPASearchProcessor(metadataUtil);
	 * super.setSearchProcessor(searchProcessor); }
	 */

	@Override
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
		super.setEntityManager(entityManager);
	}

	@Override
	@Resource
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}

	public Session getSession() {
		return (Session) em().getDelegate();
	}

	public SessionFactory getSessionFactory() {
		return ((HibernateEntityManagerFactory) (em().getEntityManagerFactory()))
				.getSessionFactory();
	}

	@Override
	public void evictAll() {
		em().clear();
		em().getEntityManagerFactory().getCache().evictAll();
		clearLevel2Cache();
	}

	protected void clearLevel2Cache() {
		Cache cache = getSessionFactory().getCache();
		cache.evictEntityRegions();
		cache.evictCollectionRegions();
		cache.evictDefaultQueryRegion();
		cache.evictQueryRegions();
		cache.evictNaturalIdRegions();
	}

	protected void addDefaultSorts(Search search) {
		addSort(search, "sort");
		addSort(search, "createTime");
		Class<?> clazz = search.getSearchClass();
		List<Field> fields = this.findAnnotationFields(clazz, OrderBy.class);
		for (Field f : fields) {
			addSort(search, f.getName());
		}
	}

	protected void addSort(Search search, String name) {
		Class<?> clazz = search.getSearchClass();
		List<Sort> sortList = search.getSorts();
		for (Sort sort : sortList) {
			if (sort.getProperty().equals(name)) {
				return;
			}
		}
		Field field = findField(clazz, name);
		if (field != null && !field.isAnnotationPresent(Transient.class)) {
			OrderBy orderBy = field.getAnnotation(OrderBy.class);
			if (orderBy != null && orderBy.value().equalsIgnoreCase("desc"))
				search.addSortDesc(field.getName());
			else
				search.addSortAsc(field.getName());
		}
	}

	@Override
	public void addSort(Search search, String name, String AscOrDesc) {
		Class<?> clazz = search.getSearchClass();
		List<Sort> sortList = search.getSorts();
		for (Sort sort : sortList) {
			if (sort.getProperty().equals(name)) {
				return;
			}
		}
		Field field = findField(clazz, name);
		if (field != null && !field.isAnnotationPresent(Transient.class)) {
			if (AscOrDesc != null
					&& AscOrDesc.toLowerCase().equalsIgnoreCase("desc"))
				search.addSortDesc(field.getName());
			else
				search.addSortAsc(field.getName());
		}
	}

	@Override
	public boolean exitField(Class<?> clazz, String name) {
		Field field = findField(clazz, name);
		if (field != null && !field.isAnnotationPresent(Transient.class)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDefaultOrderBy(Class<?> clazz, String prefix) {
		String orderby = " order by ";
		Field sort = findField(clazz, "sort");
		if (sort != null && !sort.isAnnotationPresent(Transient.class)) {
			orderby = orderby + prefix + sort.getName();
			OrderBy orderBy = sort.getAnnotation(OrderBy.class);
			if (orderBy != null)
				orderby = orderby + " " + orderBy.value();
			orderby = orderby + ",";
		}
		Field versionNow = findField(clazz, "createTime");
		if (versionNow != null
				&& !versionNow.isAnnotationPresent(Transient.class)) {
			orderby = orderby + prefix + versionNow.getName() + " desc,";
		}
		if (orderby.endsWith(",")) {
			return orderby.substring(0, orderby.length() - 1);
		} else {
			return "";
		}
	}

	@Override
	public long count(Class<?> clazz) {
		Query q = em().createQuery(
				"select count(p) from " + clazz.getSimpleName() + " p");
		return Converter.parseInt("" + q.getSingleResult());
	}

	@Override
	public long count(Class<?> clazz, Map<String, Object> params) {
		if (params == null) {
			return count(clazz);
		}
		String jpql = "select count(p) from " + clazz.getSimpleName()
				+ " p where 1 = 1";
		for (String key : params.keySet()) {
			jpql += " and p." + key + " = :" + key.replace(".", "_");
		}
		Query q = em().createQuery(jpql);
		for (String key : params.keySet()) {
			q.setParameter(key.replace(".", "_"), params.get(key));
		}
		return Converter.parseInt("" + q.getSingleResult());
	}

	@Override
	public long count(String jpql, Object... values) {
		Query q = em().createQuery(jpql);
		for (int i = 0; i < values.length; i++) {
			q.setParameter(i + 1, values[i]);
		}
		return Converter.parseInt("" + q.getSingleResult());
	}

	@Override
	public long countByMap(String jpql, Map<String, Object> params) {
		Query q = em().createQuery(jpql);
		for (String key : params.keySet()) {
			q.setParameter(key, params.get(key));
		}
		return Converter.parseInt("" + q.getSingleResult());
	}

	@Override
	public void saveAll(Collection<?> entities) {
		saveAll(entities, FLUSH_CASE);
	}

	@Override
	public void saveAll(Collection<?> entities, int flushCase) {
		Iterator<?> it = entities.iterator();
		int i = 0;
		while (it.hasNext()) {
			Object entity = it.next();
			em().merge(entity);
			i++;
			if (i % flushCase == 0) {
				em().flush();
				em().clear();
			}
		}
	}

	@Override
	public boolean exist(Class<?> clazz, Serializable id) {
		Object entity = this.getById(clazz, id);
		return entity != null;
	}

	@SuppressWarnings("unchecked")
	protected <T> List<Field> findAnnotationFields(Class<?> clazz,
			Class<T> annotationClass) {
		List<Field> fieldList = new ArrayList<Field>();
		Class<?> superclass = clazz;
		while (!superclass.equals(Object.class)) {
			try {
				Field[] fields = superclass.getDeclaredFields();
				for (Field f : fields) {
					if (f.isAnnotationPresent((Class<? extends Annotation>) annotationClass))
						fieldList.add(f);
				}
			} catch (Exception e) {
				;
			}
			superclass = superclass.getSuperclass();
		}
		return fieldList;
	}

	protected Field findField(Class<?> clazz, String name) {
		Field field = null;
		Class<?> superclass = clazz;
		while (field == null && !superclass.equals(Object.class)) {
			try {
				field = superclass.getDeclaredField(name);
			} catch (Exception e) {
				;
			}
			superclass = superclass.getSuperclass();
		}
		return field;
	}

	@Override
	public <T> List<T> findBySQL(Class<T> clazz, String sql) {
		
		BeanPropertyRowMapper<T> totalRowMapper = new BeanPropertyRowMapper<T>(
				clazz);
		List<T> list = jdbcTemplate().query(sql.toString(), totalRowMapper);
		return list;
//		List<T> eList = null;
//		try {
//			eList = (List<T>) JdbcResultSetUtils.toList(clazz, query(sql));
//		} catch (SQLException ex) {
//			log.error("初始化连接错误", ex);
//			eList = new ArrayList<T>();
//		}
//		return eList;
	}

	@SuppressWarnings("unchecked")
	protected Object[] queryArray(String sql) {
		Query query = this.em().createNativeQuery(sql);
		query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(
				org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> list = query.getResultList();
		Object[] array = new Object[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i).values().iterator().next();
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> query(String sql) {
		Query query = this.em().createNativeQuery(sql);
		query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(
				org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	@Override
	public <T> List<T> findByIds(Class<T> clazz, Serializable[] ids) {
		List<T> list = new ArrayList<T>();
		for (Serializable id : ids) {
			T e = this.getById(clazz, id);
			if (e != null) {
				list.add(e);
			}
		}
		return list;
	}

	@Override
	public <T> DataGrid<T> findPagination(Class<T> clazz, int pageIndex,
			int pageSize) {
		return findPagination(clazz, null, pageIndex, pageSize);
	}

	@Override
	public <T> DataGrid<T> findPagination(Class<T> clazz,
			Map<String, Object> params, int pageIndex, int pageSize) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		long count = count(clazz, params);
		int first = (pageIndex - 1) * pageSize;
		List<T> list = findByMap(clazz, params, pageSize, first > 0 ? first : 0);
		return new DataGrid<T>((int) count, list, pageSize, pageIndex);
	}

	/**
	 * 通过传入jql语句和参数获取一行记录,但结果集为空或结果集大于一行时异常
	 * 
	 * @param jql
	 * @param params
	 * @return
	 */
	@Override
	public Object getUniqueResult(String jql, Map<String, Object> params) {
		Query query = em().createQuery(jql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		return query.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Number> T getScalar(String jql, Map<String, Object> params) {
		Query query = em().createQuery(jql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return (T) query.getSingleResult();
	}

	/**
	 * 通过sql返回数组列表
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	@Override
	public List<?> findBySql(String sql, Map<String, Object> params) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBySql2Map(String sql,
			Map<String, Object> params) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(
				org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	@Override
	public List<?> findBySqlPagination(String sql, Map<String, Object> params,
			int page, int pageSize) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		query.setFirstResult((page - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBySql2MapPagination(String sql,
			Map<String, Object> params, int page, int pageSize) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		query.setFirstResult((page - 1) * pageSize);
		query.setMaxResults(pageSize);
		query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(
				org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByField(Class<T> clazz, String name, Object value) {
		Search search = new Search(clazz);
		if (value == null) {
			value = "";
		}
		search.addFilterEqual(name, value);
		addSort(search, "status", "asc");
		addSort(search, "sort", "asc");
		addSort(search, "createTime", "desc");
		search.setMaxResults(1);
		return (T) this._searchUnique(search);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByMap(Class<T> clazz, Map<String, Object> params) {
		Search search = new Search(clazz);
		if (params != null) {
			for (String key : params.keySet()) {
				search.addFilterEqual(key, params.get(key));
			}
		}
		search.setMaxResults(1);
		return (T) this._searchUnique(search);
	}

	@Override
	public Object[] getUniqueResultBySql(String sql, Map<String, Object> params) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return (Object[]) query.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUniqueMapBySql(String sql,
			Map<String, Object> params) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		query.unwrap(org.hibernate.Query.class).setResultTransformer(
				org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) query.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Number> T getScalarBySql(String sql,
			Map<String, Object> params) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return (T) query.getSingleResult();
	}

	@Override
	public int excuteSql(String sql, Map<String, Object> params) {
		Query query = em().createNativeQuery(sql);
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.executeUpdate();
	}

	@Override
	public Object getSingleResult(String jpql, Object... values) {
		List<?> list = findList(1, jpql, values);
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Object getSingleResultByMap(String jpql, Map<String, Object> params) {
		List<?> list = findListByMap(jpql, params, 1);
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> clazz, int maxResults, int firstResult) {
		String jpql = "select p from " + clazz.getSimpleName() + " p";
		jpql += getDefaultOrderBy(clazz, "p.");
		Query q = em().createQuery(jpql);
		if (maxResults > 0) {
			q.setMaxResults(maxResults);
		}
		if (maxResults > 0 && firstResult >= 0) {
			q.setFirstResult(firstResult);
		}
		return q.getResultList();
	}

	@Override
	public <T> T getLast(Class<T> clazz) {
		List<T> list = this.find(clazz, 1, 0);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> _findByParams(Class<T> clazz,
			Map<String, Object> params, int maxResults, int firstResult) {

		Search search = new Search(clazz);
		if (params != null) {
			for (String key : params.keySet()) {
				search.addFilterEqual(key, params.get(key));
			}
		}
		addDefaultSorts(search);
		if (maxResults > 0) {
			search.setMaxResults(maxResults);
		}
		if (firstResult >= 0) {
			search.setFirstResult(firstResult);
		}
		return this._search(search);
	}

	@Override
	public List<?> findList(String jpql, Object... values) {
		return findList(-1, jpql, values);
	}

	@Override
	public List<?> findList(int maxResults, String jpql, Object... values) {
		Query q = em().createQuery(jpql);
		for (int i = 0; i < values.length; i++) {
			q.setParameter(i + 1, values[i]);
		}
		if (maxResults > 0) {
			q.setMaxResults(maxResults);
		}
		return q.getResultList();
	}

	@Override
	public List<?> findListByMap(String jpql, Map<String, Object> params) {
		return findListByMap(jpql, params, -1);
	}

	@Override
	public List<?> findListByMap(String jpql, Map<String, Object> params,
			int maxResults) {
		return findListByMap(jpql, params, maxResults, -1);
	}

	@Override
	public List<?> findListByMap(String jpql, Map<String, Object> params,
			int maxResults, int firstResult) {
		Query q = em().createQuery(jpql);
		for (String key : params.keySet()) {
			q.setParameter(key, params.get(key));
		}
		if (maxResults > 0) {
			q.setMaxResults(maxResults);
		}
		if (maxResults > 0 && firstResult >= 0) {
			q.setFirstResult(firstResult);
		}
		return q.getResultList();
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public DataGrid<?> findPagination(String jpql, String countJpql,
			int pageIndex, int pageSize, Object... values) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		Long count = count(countJpql, values);
		Query ql = em().createQuery(jpql);
		for (int i = 0; i < values.length; i++) {
			ql.setParameter(i + 1, values[i]);
		}
		int first = (pageIndex - 1) * pageSize;
		ql.setFirstResult((first > 0 ? first : 0));
		ql.setMaxResults((pageSize > 0 ? pageSize : 20));
		return new DataGrid<Object>(count.intValue(), ql.getResultList(),
				pageSize, pageIndex);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public DataGrid<?> findPaginationByMap(String jpql, String countJpql,
			int pageIndex, int pageSize, Map<String, Object> params) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		Long fullListSize = countByMap(countJpql, params);
		Query q = em().createQuery(jpql);
		for (String key : params.keySet()) {
			Object value = params.get(key);
			q.setParameter(key, value);
		}
		int first = (pageIndex - 1) * pageSize;
		q.setFirstResult((first > 0 ? first : 0));
		q.setMaxResults((pageSize > 0 ? pageSize : 20));
		return new DataGrid<Object>(fullListSize.intValue(), q.getResultList(),
				pageSize, pageIndex);
	}

	@Override
	public void execute(String jpql, Object... values) {
		Query q = em().createQuery(jpql);
		for (int i = 0; i < values.length; i++) {
			q.setParameter(i + 1, values[i]);
		}
		q.executeUpdate();
	}

	@Override
	public void removeNative(String table, String where, String value) {
		StringBuffer jpql = new StringBuffer();
		jpql.append("delete from " + table);
		jpql.append(" where " + where + " = :" + where);
		Query q = em().createNativeQuery(jpql.toString());
		q.setParameter(where, value);
		q.executeUpdate();
	}

	@Override
	public void removeAll(Class<?> clazz) {
		String jpql = "delete from " + clazz.getSimpleName();
		em().createQuery(jpql).executeUpdate();
	}

	@Override
	public void removeByField(Class<?> clazz, String where, String value) {
		StringBuffer jpql = new StringBuffer();
		jpql.append("delete from " + clazz.getSimpleName());
		jpql.append(" where " + where + " = :" + where);
		Query q = em().createQuery(jpql.toString());
		q.setParameter(where, value);
		q.executeUpdate();
	}

	@Override
	public <T> List<T> findByMap(Class<T> clazz, Map<String, Object> params) {
		List<T> list = _findByParams(clazz, params, -1, -1);
		return list;
	}

	@Override
	public <T> List<T> findByMap(Class<T> clazz, Map<String, Object> params,
			int maxResults, int firstResult) {
		List<T> list = _findByParams(clazz, params, maxResults, firstResult);
		return list;
	}

	@Override
	public <T> List<T> findByField(Class<T> clazz, String fieldname,
			Object fieldvalue) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(fieldname, fieldvalue);
		return findByMap(clazz, map);
	}

	@Override
	public <T> T getById(Class<T> clazz, Serializable id) {
		try {
			if (id == null) {
				return null;
			} else if (StringUtils.isEmpty(id.toString())) {
				return null;
			}
			return em().find(clazz, id);
		} catch (Exception ex) {
			log.warn(ex.getMessage() + " " + clazz.getName() + " " + id);
			return null;
		}
	}

	@Override
	public int getFirstResult(int page, int pageSize) {
		int first = (page - 1) * pageSize;
		return first > 0 ? first : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] merge(Class<T> type, Object... entities) {
		T[] retList = (T[]) Array.newInstance(type, entities.length);
		for (int i = 0; i < entities.length; i++) {
			retList[i] = (T) merge(entities[i]);
		}
		return retList;
	}

	protected boolean _remove(Object entity) {
		try {
			em().remove(entity);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public <T> T getReference(Class<T> clazz, Serializable id) {
		return em().getReference(clazz, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] getReferences(Class<T> clazz, Serializable... ids) {
		T[] retList = (T[]) Array.newInstance(clazz, ids.length);
		for (int i = 0; i < ids.length; i++) {
			retList[i] = getReference(clazz, ids[i]);
		}
		return retList;
	}

	@Override
	public void persist(Object... entities) {
		em().persist(entities);
	}

	@Override
	public <T> T merge(T entity) {
		return em().merge(entity);
	}

	@Override
	public <T> T save(T entity) {
		return em().merge(entity);
	}

	@Override
	public Object[] save(Object... entities) {
		return _persistOrMerge(Object.class, entities);
	}

	@Override
	public boolean remove(Object entity) {
		return _remove(entity);
	}

	@Override
	public void remove(Object... entities) {
		for (Object entity : entities) {
			_remove(entity);
		}
	}

	@Override
	public boolean removeById(Class<?> clazz, Serializable id) {
		Object value = this.getById(clazz, id);
		if (value != null) {
			em().remove(value);
			return true;
		}
		return false;
	}

	@Override
	protected boolean _removeById(Class<?> clazz, Serializable id) {
		Object value = this.getById(clazz, id);
		if (value != null) {
			em().remove(value);
			return true;
		}
		return false;
	}

	@Override
	public void removeByIds(Class<?> clazz, Serializable... ids) {
		for (Serializable id : ids) {
			_removeById(clazz, id);
		}
	}

	@Override
	public <T> List<T> findAll(Class<T> clazz) {
		return find(clazz, 0, -1);
	}

	@Override
	public <T> List<T> searchFilterEqual(Class<T> clazz, String fieldname,
			Object value) {
		Search search = new Search(clazz);
		search.addFilterEqual(fieldname, value);
		@SuppressWarnings("unchecked")
		List<T> list = search(search);
		return list;
	}

	@Override
	public int countBySql(String sql) {
		return jdbcTemplate().queryForObject(sql, Integer.class);
	}

	// @Override
	// public void refresh(Object... entities)
	// {
	// em().refresh(entities);
	// }
	//
	// @Override
	// public void flush()
	// {
	// em().flush();
	// }
}
