package cc.cnplay.platform.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.TreeCheckedModel;
import cc.cnplay.platform.annotation.RightType;
import cc.cnplay.platform.dao.OrganizationDao;
import cc.cnplay.platform.dao.RoleDao;
import cc.cnplay.platform.dao.UserDao;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.UserRole;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.RoleService;
import cc.cnplay.platform.vo.RightVO;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Service
@Transactional
public class RoleServiceImpl extends AbsGenericService<Role, String> implements RoleService
{
	@Autowired
	private RightService rightService;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private OrganizationDao orgDao;

	@SuppressWarnings("unchecked")
	@Override
	public DataGrid<Role> findPageLikeName(boolean isRoot, String name, int page, int pageSize)
	{
		Search search = new Search(Role.class);
		search.addFilterLike("name", "%" + name + "%");
		if (!isRoot)
		{
			search.addFilterEqual("subFlag", Boolean.TRUE);
		}
		search.addSortAsc("sort");
		search.addSortDesc("createTime");
		search.setFirstResult(getFirstResult(page, pageSize));
		search.setMaxResults((pageSize > 0 ? pageSize : 20));
		SearchResult<Role> result = dao().searchAndCount(search);
		DataGrid<Role> dg = new DataGrid<Role>((int) result.getTotalCount(), result.getResult(), pageSize, page);
		return dg;
	}

	@Override
	public List<TreeCheckedModel> loadCheckRight(List<Right> rightList, String roleId)
	{
		List<Right> roleRightList = findRightByRoleId(roleId);
		List<TreeCheckedModel> tcmList = new ArrayList<TreeCheckedModel>();
		for (Right right : rightList)
		{
			TreeCheckedModel tcm = new TreeCheckedModel();
			try
			{
				BeanUtils.copyProperties(tcm, right);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
			tcm.setLeaf(false);
			for (Right r : roleRightList)
			{
				if (right.equals(r))
				{
					tcm.setChecked(true);
					break;
				}
			}
			tcmList.add(tcm);
		}
		return tcmList;
	}

	@Override
	public List<Right> findRightByRoleId(String roleId)
	{
		List<RoleRight> tmpList = roleDao.findRoleRightByRoleId(roleId);
		List<Right> rightList = new ArrayList<Right>();
		for (RoleRight rr : tmpList)
		{
			rightList.add(rr.getRight());
		}
		return rightList;
	}

	@Override
	public void saveRoleAndRight(Role role, String[] rightIds)
	{
//		if (!role.getSubFlag())
//		{
//			Organization root = orgDao.getRoot();
//			if (root != null)
//			{
//				userDao.removeNotOrgUserRole(role.getId(), root.getId());
//			}
//		}
		dao().save(role);
		List<RoleRight> rrList = roleDao.findRoleRightByRoleId(role.getId());
		for (int i = rrList.size() - 1; i >= 0; i--)
		{
			RoleRight rr = rrList.get(i);
			boolean selected = false;
			for (String id : rightIds)
			{
				if (id.equals(rr.getRight().getId()))
				{
					selected = true;
					// rrList.remove(i);
					break;
				}
			}
			if (!selected)
			{
				dao().remove(rr);
			}
		}
		for (String id : rightIds)
		{
			boolean selected = false;
			for (RoleRight rr : rrList)
			{
				if (rr.getRight().getId().equals(id))
				{
					selected = true;
					break;
				}
			}
			if (!selected)
			{
				Right right = dao().getById(Right.class, id);
				if (right != null)
				{
					RoleRight rr = new RoleRight();
					rr.setRight(right);
					rr.setRole(role);
					dao().save(rr);
				}
			}
		}
	}

	@Override
	public String removeByIdsAndRef(String[] split)
	{
		String errMsg = null;
		for (String roleid : split)
		{
			Role r = this.getById(roleid);
			dao().remove(r);
//			if (existUserInRole(roleid))
//			{
//				return errMsg = "角色(" + r.getName() + ")已绑定用户，不能删除！";
//			}
//			else
//			{
//				dao().remove(r);
//			}
		}
		return errMsg;
	}

	/**
	 * <p> description:系统中是否存在相同角色名称 </p>
	 * 
	 * @author: hqh
	 * @param name
	 * @return boolean
	 */
	public boolean existRoleName(String roleid, String name)
	{
		Search search = new Search(Role.class);
		search.addFilterNotEqual("id", roleid);
		search.addFilterEqual("name", name);

		@SuppressWarnings("unchecked")
		List<Organization> lst = dao().search(search);
		return !lst.isEmpty();
	}

	/**
	 * <p> description:角色下是否存在未逻辑删除的用户 </p>
	 * 
	 * @author: hqh
	 * @param name
	 * @return boolean
	 */
	public boolean existUserInRole(String roleid)
	{
		Search search = new Search(UserRole.class);
		search.addFilterEqual("role.id", roleid);
		return dao().count(search) > 0;
	}

	/**
	 * <p> description:根据当前角色ID和顶级权限（父ID为null）获取所有子权限 </p>
	 * 
	 * @author: hqh
	 * @param list
	 * @param roleId
	 * @return List<RightTreeCheckedVO>
	 */
	@Override
	public List<RightVO> loadCheckRightTree(List<Right> list, String roleId)
	{
		List<Right> roleRightList = findRightByRoleId(roleId);
		List<RightVO> retList = loadCheckedTree(list, roleRightList);
		return retList;
	}

	/**
	 * <p> description:根据顶级权限和当前角色获取所有子权限 </p>
	 * 
	 * @author: hqh
	 * @param list
	 * @param roleRightList
	 * @return List<RightTreeCheckedVO>
	 */
	private List<RightVO> loadCheckedTree(List<Right> list, List<Right> roleRightList)
	{
		rightService.filterMenuListener(null, list);
		List<RightVO> tree = new ArrayList<RightVO>();
		for (Right r : list)
		{
			RightVO rv = new RightVO();
			try
			{
				BeanUtils.copyProperties(rv, r);
			}
			catch (Throwable e)
			{
				logger.error("", e);
			}
			rv.setLeaf(false);
			RightType type = RightType.valueOf(rv.getType());
			if (RightType.BUTTON.equals(type) || RightType.RESOURCE.equals(type))
			{
				rv.setLeaf(true);
			}
			setTreeChecked(rv, roleRightList);
			tree.add(rv);
			loadCheckedTreeCallback(rv, roleRightList);
		}
		return tree;
	}

	/**
	 * <p> description:根据当前层级的权限ID回调得到所有子权限 </p>
	 * 
	 * @author: hqh
	 * @param rightvo
	 * @param roleRightList
	 * @return void
	 */
	private void loadCheckedTreeCallback(RightVO rightvo, List<Right> roleRightList)
	{
		List<Right> list = rightService.findByParentId(rightvo.getId());
		rightService.filterMenuListener(null, list);
		List<RightVO> tree = new ArrayList<RightVO>();
		rightvo.setChildren(tree);
		for (Right r : list)
		{
			RightVO rtc = new RightVO();
			try
			{
				BeanUtils.copyProperties(rtc, r);
			}
			catch (Throwable e)
			{
				logger.error("", e);
			}
			rtc.setLeaf(false);
			RightType type = RightType.valueOf(rtc.getType());
			if (RightType.BUTTON.equals(type) || RightType.RESOURCE.equals(type))
			{
				rtc.setLeaf(true);
			}
			setTreeChecked(rtc, roleRightList);
			tree.add(rtc);
			loadCheckedTreeCallback(rtc, roleRightList);
		}
	}

	/**
	 * <p> description:根据当前权限VO实体和当前角色所拥有的权限设置勾选情况 </p>
	 * 
	 * @author: hqh
	 * @param rtc
	 * @param roleRightList
	 */
	private void setTreeChecked(RightVO rtc, List<Right> roleRightList)
	{
		if (roleRightList != null && roleRightList.size() > 0)
		{
			for (Right r : roleRightList)
			{
				if (rtc.getId().equalsIgnoreCase(r.getId()))
				{
					rtc.setChecked(true);
					break;
				}
			}
		}
	}

	@Override
	public List<Role> findList(boolean isRoot)
	{
		return roleDao.findList(isRoot);
	}

}
