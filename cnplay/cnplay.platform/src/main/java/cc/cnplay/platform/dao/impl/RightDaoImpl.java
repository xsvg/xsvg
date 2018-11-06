package cc.cnplay.platform.dao.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.platform.dao.RightDao;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.RightReject;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.UserRole;

import com.googlecode.genericdao.search.Search;

@Repository
public class RightDaoImpl extends GenericDaoImpl<Right, String> implements RightDao
{

	@Override
	public List<Right> findByParentId(String parentId)
	{
		if (StringUtils.isEmpty(parentId))
		{
			parentId = "";
		}
		StringBuffer jpql = new StringBuffer();
		jpql.append("select p from " + persistentClass.getSimpleName() + " p");
		jpql.append(" where 1 = 1");
		if (!StringUtils.isEmpty(parentId))
		{
			jpql.append(" and p.parentId = ?1");
		}
		else
		{
			jpql.append(" and (p.parentId IS NULL OR p.parentId = ?1 OR p.parentId = '0')");
		}
		jpql.append(" order by sort");
		@SuppressWarnings("unchecked")
		List<Right> list = (List<Right>) findList(jpql.toString(), parentId);
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Right> findByName(String name)
	{
		Search search = new Search(Right.class);
		search.addFilterEqual("name", name);
		return (List<Right>) search(search);
	}

	@Override
	public void removeRoleRight(String roleId, String rightId)
	{
		Search search = new Search(RoleRight.class);
		search.addFilterEqual("role.id", roleId);
		search.addFilterEqual("right.id", rightId);
		@SuppressWarnings("unchecked")
		List<RoleRight> list = this.search(search);
		for (RoleRight rr : list)
		{
			remove(rr);
		}
	}

	@Override
	public List<RightReject> findRightReject(Collection<String> values)
	{
		Search search = new Search(RightReject.class);
		search.addFilterIn("refRightId", values);
		@SuppressWarnings("unchecked")
		List<RightReject> rrList = search(search);
		return rrList;
	}

	@Override
	public boolean findUserMatcherRole(List<String> userIds, List<String> roleIds)
	{
		Search search = new Search(UserRole.class);
		search.addFilterIn("user.id", userIds);
		search.addFilterIn("role.id", roleIds);
		return this.count(search) > 0;
	}

	@Override
	public boolean exitRightReject(String rightId1, String rightId2)
	{
		String hql = "from " + RightReject.class.getSimpleName() + " where (refRightId=?1 and rejectRightId=?2) or(refRightId=?3 and rejectRightId=?4)";
		@SuppressWarnings("unchecked")
		List<RightReject> rejects = (List<RightReject>) findList(hql, rightId1, rightId2, rightId2, rightId1);
		if (rejects.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}