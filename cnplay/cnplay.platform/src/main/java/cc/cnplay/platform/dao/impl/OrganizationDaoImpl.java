package cc.cnplay.platform.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.platform.dao.OrganizationDao;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Status;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

@Repository
public class OrganizationDaoImpl extends GenericDaoImpl<Organization, String> implements OrganizationDao
{

	@Override
	public Organization getByLevelCode(String levelCode)
	{

		return this.getByField("levelCode", levelCode);
	}

	@Override
	public String getLevelCodeById(String id)
	{
		Organization org = getById(Organization.class, id);
		String levelCode = "";
		if (org != null)
		{
			levelCode = org.getLevelCode();
		}
		return levelCode;
	}

	@Override
	public Organization getRoot()
	{
		List<Organization> list = findByParentId("");
		if (list.size() > 0)
		{
//			for (int i = 1; i < list.size(); i++)
//			{
//				Organization org = list.get(i);
//				org.setParentId(list.get(0).getId());
//				this.save(org);
//			}
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<Organization> findByParentId(String parentId)
	{
		if (StringUtils.isEmpty(parentId))
		{
			parentId = "";
		}
		StringBuffer jpql = new StringBuffer();
		jpql.append("select p from " + persistentClass.getSimpleName() + " p");
		jpql.append(" where 1 = 1 ");
		if (!StringUtils.isEmpty(parentId))
		{
			jpql.append(" and (p.parentId = ?1)");
		}
		else
		{
			jpql.append(" and (p.parentId IS NULL OR p.parentId = ?1 OR p.parentId = '0')");
		}
		jpql.append(" and (p.status = ?2)");
		jpql.append(" order by code");
		@SuppressWarnings("unchecked")
		List<Organization> list = (List<Organization>) findList(jpql.toString(), parentId, Status.Normal);
		return list;
	}

	@Override
	public String getAmkCode(Organization org)
	{
		Organization tmp = getManager(org);
		return tmp.getCode();
	}

	@Override
	public Organization getManager(Organization org)
	{
		Organization mgr = org;
		while (mgr != null && mgr.getManager() != null)
		{
			mgr = mgr.getManager();
		}
		return mgr;
	}

	@Override
	public boolean managerExitMe(Organization me, Organization manager)
	{
		Organization mgr = manager;
		while (mgr != null)
		{
			if (me.equals(mgr))
			{
				return true;
			}
			mgr = mgr.getManager();
		}
		return false;
	}

	@Override
	public boolean existCode(Organization org)
	{
		Search search = new Search(Organization.class);
		search.addFilterEqual("code", org.getCode());
		search.addFilterNotEqual("id", org.getId());
		search.addFilterEqual("status", Status.Normal);
		@SuppressWarnings("unchecked")
		List<Organization> lst = search(search);
		return !lst.isEmpty();
	}

	@Override
	public boolean existName(Organization org)
	{
		Search search = new Search(Organization.class);
		search.addFilterEqual("name", org.getName());
		search.addFilterNotEqual("id", org.getId());
		search.addFilterEqual("status", Status.Normal);
		@SuppressWarnings("unchecked")
		List<Organization> lst = search(search);
		return !lst.isEmpty();
	}

	@Override
	public Organization getByCode(String code)
	{
		Search search = new Search(Organization.class);
		search.addFilterEqual("code", code);
		search.addFilterEqual("status", Status.Normal);
		search.setMaxResults(1);
		return (Organization) this.searchUnique(search);
	}

	@Override
	public boolean existChild(String parentId)
	{
		if (StringUtils.isEmpty(parentId))
		{
			parentId = "";
		}
		Search search = new Search(Organization.class);
		if (!StringUtils.isEmpty(parentId))
		{
			search.addFilterEqual("parentId", parentId);
		}
		else
		{
			search.addFilter(Filter.or(Filter.isEmpty("parentId"), Filter.isNull("parentId"), Filter.equal("parentId", "0")));
		}
		return this.count(search) > 0;
	}

	@Override
	public Organization getBySyncId(String syncId)
	{
		Search search = new Search(Organization.class);
		search.addFilterEqual("syncId", syncId);
		search.setMaxResults(1);
		return (Organization) this.searchUnique(search);
	}
}
