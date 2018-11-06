package cc.cnplay.platform.dao;

import java.util.Collection;
import java.util.List;

import cc.cnplay.core.dao.GenericDao;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.RightReject;

public interface RightDao extends GenericDao<Right, String>
{

	List<Right> findByParentId(String parentId);

	List<Right> findByName(String name);

	void removeRoleRight(String roleId, String rightId);

	List<RightReject> findRightReject(Collection<String> values);

	boolean findUserMatcherRole(List<String> userIds, List<String> roleIds);

	boolean exitRightReject(String rightId1, String rightId2);

}
