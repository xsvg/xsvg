package cc.cnplay.platform.dao;

import java.util.List;

import cc.cnplay.core.dao.GenericDao;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.UserRole;

public interface RoleDao extends GenericDao<Role, String>
{

	List<Role> findList(boolean isRoot);

	Role getByName(String name);

	List<String> findRoleIdList(List<String> rightIds);

	List<UserRole> findByUserId(String userId);

	List<RoleRight> findRoleRightByRoleId(String roleId);

}
