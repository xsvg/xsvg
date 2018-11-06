package cc.cnplay.store.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.store.dao.StoreAreaDao;
import cc.cnplay.store.domain.StoreArea;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

@Repository
public class StoreAreaDaoImpl extends GenericDaoImpl<StoreArea, String>
		implements StoreAreaDao {

	@Override
	public StoreArea getByLevelCode(String orgId, String levelCode) {
		Search search = new Search(StoreArea.class);
		search.addFilterEqual("orgId", orgId);
		search.addFilterEqual("levelCode", levelCode);
		search.setMaxResults(1);
		return (StoreArea) this.searchUnique(search);
	}

	@Override
	public String getLevelCodeById(String id) {
		StoreArea org = getById(StoreArea.class, id);
		String levelCode = "";
		if (org != null) {
			levelCode = org.getLevelCode();
		}
		return levelCode;
	}

	@Override
	public StoreArea getRoot(String orgId) {
		List<StoreArea> list = findByParentId(orgId, "");
		if (list.size() > 0) {
			// for (int i = 1; i < list.size(); i++)
			// {
			// StoreArea org = list.get(i);
			// org.setParentId(list.get(0).getId());
			// this.save(org);
			// }
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<StoreArea> findByParentId(String orgId, String parentId) {
		if (StringUtils.isEmpty(parentId)) {
			parentId = "";
		}
		StringBuffer jpql = new StringBuffer();
		jpql.append("select p from " + persistentClass.getSimpleName() + " p");
		jpql.append(" where 1 = 1 ");
		if (!StringUtils.isEmpty(parentId)) {
			jpql.append(" and (p.parentId = ?1)");
		} else {
			jpql.append(" and (p.parentId IS NULL OR p.parentId = ?1 OR p.parentId = '0')");
		}
		jpql.append(" and p.orgId = ?2");
		jpql.append(" order by code");
		@SuppressWarnings("unchecked")
		List<StoreArea> list = (List<StoreArea>) findList(jpql.toString(),
				parentId, orgId);
		return list;
	}

	@Override
	public boolean existCode(StoreArea org) {
		Search search = new Search(StoreArea.class);
		search.addFilterEqual("orgId", org.getOrgId());
		search.addFilterEqual("code", org.getCode());
		search.addFilterNotEqual("id", org.getId());
		@SuppressWarnings("unchecked")
		List<StoreArea> lst = search(search);
		return !lst.isEmpty();
	}

	@Override
	public boolean existName(StoreArea org) {
		Search search = new Search(StoreArea.class);
		search.addFilterEqual("orgId", org.getOrgId());
		search.addFilterEqual("name", org.getName());
		search.addFilterNotEqual("id", org.getId());
		@SuppressWarnings("unchecked")
		List<StoreArea> lst = search(search);
		return !lst.isEmpty();
	}

	@Override
	public StoreArea getByCode(String orgId, String code) {
		Search search = new Search(StoreArea.class);
		search.addFilterEqual("orgId", orgId);
		search.addFilterEqual("code", code);
		search.addFilterEqual("status", Status.Normal);
		search.setMaxResults(1);
		return (StoreArea) this.searchUnique(search);
	}

	@Override
	public boolean existChild(String orgId, String parentId) {
		if (StringUtils.isEmpty(parentId)) {
			parentId = "";
		}
		Search search = new Search(StoreArea.class);
		search.addFilterEqual("orgId", orgId);
		if (!StringUtils.isEmpty(parentId)) {
			search.addFilterEqual("parentId", parentId);
		} else {
			search.addFilter(Filter.or(Filter.isEmpty("parentId"),
					Filter.isNull("parentId"), Filter.equal("parentId", "0")));
		}
		return this.count(search) > 0;
	}
}
