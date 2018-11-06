package cc.cnplay.platform.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.platform.dao.RoleDao;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.UserRole;

import com.googlecode.genericdao.search.Search;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role, String> implements RoleDao
{
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> findList(boolean isRoot)
	{
		Search search = new Search(Role.class);
		if (!isRoot)
		{
			search.addFilterEqual("subFlag", Boolean.TRUE);
		}
		search.addSortAsc("sort");
		search.addSortDesc("createTime");
		return this.search(search);
	}

	@Override
	public Role getByName(String name)
	{
		Search search = new Search(Role.class);
		search.addFilterEqual("name", name);
		return (Role) searchUnique(search);
	}

	@Override
	public List<String> findRoleIdList(List<String> rightIds)
	{
		Search search = new Search(RoleRight.class);
		search.addField("role.id");
		search.addFilterIn("right.id", rightIds);
		@SuppressWarnings("unchecked")
		List<String> roleIds = this.search(search);
		return roleIds;
	}

	@Override
	public List<UserRole> findByUserId(String userId)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user.id", userId);
		return (List<UserRole>) findByMap(UserRole.class, map);
	}

	@Override
	public List<RoleRight> findRoleRightByRoleId(String roleId)
	{
		List<RoleRight> tmpList = findByField(RoleRight.class, "role.id", roleId);
		return tmpList;
	}
}