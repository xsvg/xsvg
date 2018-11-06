package cc.cnplay.platform.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.dao.UserDao;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserEmpower;
import cc.cnplay.platform.domain.UserFinger;
import cc.cnplay.platform.domain.UserRight;
import cc.cnplay.platform.domain.UserRole;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User, String> implements UserDao
{

	@Override
	public User getByUsername(String username)
	{
		if (StringUtils.isEmpty(username))
		{
			return null;
		}
		Search search = new Search(User.class);
		search.addFilterEqual("username", username);
		search.addFilterNotEqual("status", Status.Delete);
		search.setMaxResults(1);
		User user = (User) this.searchUnique(search);
		if (user != null && user.getStatus() == Status.Banned && user.getLoginFailNum() > 0 && DateUtil.isPassesHour(user.getLoginTime(), 24))
		{// 自动解锁
			user.setLoginFailNum(0);
			user.setStatus(Status.Normal);
			this.save(user);
		}
		return user;
	}

	@Override
	public DataGrid<User> findList(String userId, String name, String levelCode, String orgId, int pageNum, int pageSize)
	{
		Search search = new Search(User.class);
		// search.addFilterNotEqual("id", userId);
		search.addFilterNotEqual("status", Status.Delete);
		if (StringUtils.isNotBlank(name))
		{
			search.addFilterLike("name", "%" + name + "%");
		}
		search.addFilterLike("organization.levelCode", levelCode + "%");
		if (StringUtils.isNotEmpty(orgId))
		{
			search.addFilterEqual("organization.id", orgId);
		}
		search.setFirstResult(getFirstResult(pageNum, pageSize));
		search.setMaxResults((pageSize > 0 ? pageSize : 20));
		addDefaultSorts(search);
		search.addSort("username", false);
		@SuppressWarnings("unchecked")
		SearchResult<User> result = this.searchAndCount(search);
		DataGrid<User> dg = new DataGrid<User>((int) result.getTotalCount(), result.getResult(), pageSize, pageNum);
		return dg;
	}

	@Override
	public List<User> findList(String userId, String name, String levelCode, String orgId)
	{
		Search search = new Search(User.class);
		// search.addFilterNotEqual("id", userId);
		search.addFilterNotEqual("status", Status.Delete);
		if (StringUtils.isNotBlank(name))
		{
			search.addFilterLike("name", "%" + name + "%");
		}
		search.addFilterLike("organization.levelCode", levelCode + "%");
		if (StringUtils.isNotEmpty(orgId))
		{
			search.addFilterEqual("organization.id", orgId);
		}
		addDefaultSorts(search);
		search.addSort("username", false);
		@SuppressWarnings("unchecked")
		List<User> result = this.search(search);
		return result;
	}

	@Override
	public boolean isRoot(String userid)
	{
		if (hasRight(userid))
		{
			return false;
		}
		User user = this.getById(User.class, userid);
		if (user == null)
		{
			return false;
		}
		if (User.ROOT.equalsIgnoreCase(user.getUsername()) || User.ADMIN.equalsIgnoreCase(user.getUsername()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean hasRight(String userId)
	{
		Search search = new Search(UserRole.class);
		search.addFilterEqual("user.id", userId);
		int count = this.count(search);
		if (count == 0)
		{
			search = new Search(UserRight.class);
			search.addFilterEqual("user.id", userId);
			count = this.count(search);
		}
		return count > 0;
	}

	@Override
	public List<UserEmpower> findToUserEmpowers(String userId)
	{
		return this.searchFilterEqual(UserEmpower.class, "toUser.id", userId);
	}

	@Override
	public List<UserRole> findUserRoles(String userId)
	{
		return this.searchFilterEqual(UserRole.class, "user.id", userId);
	}

	@Override
	public List<UserRight> findUserRights(String userId)
	{
		return this.searchFilterEqual(UserRight.class, "user.id", userId);
	}

	@Override
	public List<UserFinger> findUserFingerByUserid(String userId)
	{
		return this.searchFilterEqual(UserFinger.class, "user.id", userId);
	}

	@Override
	public List<RoleRight> findRoleRightByRoleId(String roleId)
	{
		return this.searchFilterEqual(RoleRight.class, "role.id", roleId);
	}

	@Override
	public List<UserRole> findUserRoles(Role role)
	{
		return this.searchFilterEqual(UserRole.class, "role.id", role.getId());
	}

	@Override
	public void removeNotOrgUserRole(String roleId, String orgId)
	{
		Search search = new Search(UserRole.class);
		search.addFilterEqual("role.id", roleId);
		search.addFilterNotEqual("user.organization.id", orgId);
		@SuppressWarnings("unchecked")
		List<UserRole> list = search(search);
		for (UserRole ur : list)
			this.remove(ur);
	}

	@Override
	public User getByMobile(String mobile)
	{
		if (StringUtils.isEmpty(mobile))
		{
			return null;
		}
		Search search = new Search(this.persistentClass);
		search.addFilterEqual("mobile", mobile);
		search.addFilterEqual("status", Status.Normal);
		search.setMaxResults(1);
		return (User) this.searchUnique(search);
	}

	@Override
	public List<User> findUserByRightId(String rightId, String orgId)
	{
		StringBuffer jpql = new StringBuffer();
		jpql.append("select distinct p.user from " + UserRole.class.getSimpleName() + " p");
		jpql.append("");
		jpql.append(" where p.role.id in(select rr.role.id from " + RoleRight.class.getSimpleName() + " rr");
		jpql.append(" where rr.right.id = ?1)");
		jpql.append(" and (p.user.organization.id = ?2)");
		jpql.append(" and (p.user.status = ?3)");
		@SuppressWarnings("unchecked")
		List<User> list = (List<User>) findList(jpql.toString(), rightId, orgId, Status.Normal);
		return list;
	}

	@Override
	public boolean findUserHasRight(String rightId, String userId)
	{
		StringBuffer jpql = new StringBuffer();
		jpql.append("select distinct p.user from " + UserRole.class.getSimpleName() + " p");
		jpql.append("");
		jpql.append(" where p.role.id in(select rr.role.id from " + RoleRight.class.getSimpleName() + " rr");
		jpql.append(" where rr.right.id = ?1)");
		jpql.append(" and (p.user.id = ?2)");
		jpql.append(" and (p.user.status = ?3)");
		@SuppressWarnings("unchecked")
		List<User> list = (List<User>) findList(jpql.toString(), rightId, userId, Status.Normal);
		return list.size() > 0;
	}
}