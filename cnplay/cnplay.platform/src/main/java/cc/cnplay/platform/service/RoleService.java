package cc.cnplay.platform.service;

import java.util.List;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.TreeCheckedModel;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.vo.RightVO;

public interface RoleService extends GenericService<Role, String>
{

	DataGrid<Role> findPageLikeName(boolean isRoot, String name, int page, int pageSize);

	List<Right> findRightByRoleId(String roleId);

	void saveRoleAndRight(Role role, String[] rightIds);

	List<TreeCheckedModel> loadCheckRight(List<Right> rightList, String roleId);

	/**
	 * 物理删 先删中间表，再删角色
	 * 
	 * @param split
	 * @return
	 */
	String removeByIdsAndRef(String[] split);

	boolean existRoleName(String roleid, String name);

	boolean existUserInRole(String roleid);

	List<RightVO> loadCheckRightTree(List<Right> list, String roleId);

	List<Role> findList(boolean isRoot);
}
