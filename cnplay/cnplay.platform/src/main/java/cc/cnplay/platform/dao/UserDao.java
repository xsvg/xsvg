package cc.cnplay.platform.dao;

import java.util.List;

import cc.cnplay.core.dao.GenericDao;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserEmpower;
import cc.cnplay.platform.domain.UserFinger;
import cc.cnplay.platform.domain.UserRight;
import cc.cnplay.platform.domain.UserRole;

public interface UserDao extends GenericDao<User, String>
{

	User getByUsername(String username);

	boolean isRoot(String userid);

	List<UserEmpower> findToUserEmpowers(String userId);

	List<UserRole> findUserRoles(String userId);

	List<UserRight> findUserRights(String userId);

	List<UserFinger> findUserFingerByUserid(String userId);

	List<RoleRight> findRoleRightByRoleId(String roleId);

	List<UserRole> findUserRoles(Role role);

	DataGrid<User> findList(String userId, String name, String levelCode, String orgId, int pageNum, int pageSize);

	void removeNotOrgUserRole(String roleId, String orgId);

	User getByMobile(String mobile);

	List<User> findUserByRightId(String rightId, String orgId);

	boolean findUserHasRight(String rightId, String userId);

	List<User> findList(String userId, String name, String levelCode, String orgId);
}