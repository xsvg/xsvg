package cc.cnplay.platform.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.Listener;
import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.spring.service.IUrlMatcher;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.dao.RightDao;
import cc.cnplay.platform.dao.RoleDao;
import cc.cnplay.platform.dao.UserDao;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.RightReject;
import cc.cnplay.platform.domain.RightType;
import cc.cnplay.platform.domain.RoleRight;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserEmpower;
import cc.cnplay.platform.domain.UserRight;
import cc.cnplay.platform.domain.UserRole;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.vo.RightVO;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class RightServiceImpl extends AbsGenericService<Right, String> implements RightService
{

	@Resource
	private UserDao userDao;
	@Resource
	private RightDao rightDao;
	@Resource
	private RoleDao roleDao;
	@Resource
	private IUrlMatcher urlMatcher;
	@Resource
	private SystemConfigService systemConfigService;

	@Override
	public List<RightVO> findRightWithUser(String userid, String id)
	{
		List<Right> list = this.findByParentId(id);
		// 取用户权限，打勾用
		List<String> userRights = new ArrayList<String>();
		if (StringUtils.isNotBlank(userid))
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("user.id", userid);
			List<UserRight> urLst = dao().findByMap(UserRight.class, map);
			for (UserRight ur : urLst)
			{
				Right r = ur.getRight();
				if (r != null)
				{
					userRights.add(r.getId());
				}
			}
		}

		List<RightVO> rst = new ArrayList<RightVO>();
		for (Right r : list)
		{
			r.setLeaf(false);
			RightVO rvo = new RightVO();
			cc.cnplay.core.util.BeanUtils.copyProperties(rvo, r);
			if (userRights.contains(r.getId()))
			{
				rvo.setChecked(true);
			}
			else
			{
				rvo.setChecked(false);
			}
			rst.add(rvo);
		}
		return rst;
	}

	@Override
	public List<Right> findByParentId(String parentId)
	{
		return findByParentId(parentId, false);
	}

	@Override
	public List<Right> findByParentId(String parentId, boolean debug)
	{
		List<Right> list = rightDao.findByParentId(parentId);
		List<Right> debugList = new ArrayList<Right>();
		for (int i = list.size() - 1; i >= 0; i--)
		{
			Right r = list.get(i);
			if (r.getStatus() != Status.Normal || r.getPlugins().getStatus() != Status.Normal)
			{
				list.remove(i);
			}
			else if (Boolean.valueOf(r.getDebug()))
			{
				debugList.add(r);
				list.remove(i);
			}
		}
		return debug ? debugList : list;
	}

	@Override
	public boolean isLeaf(String id)
	{
		List<Right> list = findByParentId(id);
		if (list.isEmpty())
		{
			return true;
		}
		for (Right r : list)
		{
			RightType type = RightType.valueOf(r.getType());
			switch (type)
			{
			case BUTTON:
			case RESOURCE:
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void checkRight(List<String> rightIds)
	{
		List<Right> list = dao().findAll(Right.class);
		for (Right r : list)
		{
			if (!rightIds.contains(r.getId()))
			{
				logger.warn("删除功能：" + r.getName());
				dao().remove(r);
			}
			else
			{
				if (!r.getLeaf() && isLeaf(r.getId()))
				{
					dao().save(r);
				}
			}
		}
	}

	@Override
	public List<Plugins> findAllPlugins()
	{
		return dao().findAll(Plugins.class);
	}

	@Override
	public Right getBy(String url, String component, String fromClass)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		params.put("component", component);
		params.put("fromClass", fromClass);
		return dao().getByMap(Right.class, params);
	}

	@Override
	public Right getByName(String name)
	{
		Search search = new Search(Right.class);
		search.addFilterEqual("name", name);
		search.setMaxResults(1);
		return (Right) dao().searchUnique(search);
	}

	@Override
	public Right getByFromClassAndText(String fromClass, String text)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("text", text);
		params.put("fromClass", fromClass);
		return dao().getByMap(Right.class, params);
	}

	@Override
	public Right getByPlugins(Plugins plugins, String text)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("plugins.id", plugins.getId());
		params.put("text", text);
		return dao().getByMap(Right.class, params);
	}

	@Override
	public List<Right> findAuthByUserIdAndParentId(String userId, String parentId)
	{
		if (parentId == null)
		{
			parentId = "";
		}
		List<Right> rightList = findAuthByUserId(userId);
		for (int i = rightList.size() - 1; i >= 0; i--)
		{
			Right right = rightList.get(i);
			RightType rightType = RightType.valueOf(right.getType());
			String pid = right.getParentId();
			if (pid == null)
			{
				pid = "";
			}
			if (!parentId.equals(pid) || rightType == RightType.BUTTON || rightType == RightType.RESOURCE)
			{
				rightList.remove(i);
			}
		}
		filterMenuListener(null, rightList);
		return rightList;
	}

	@Memo("获取转授权、角色权限、用户权限的并集")
	@Override
	public List<Right> findAuthByUserId(String userId)
	{
		Map<String, Right> map = new HashMap<String, Right>();
		User user = userDao.getById(userId);
		if (user != null)
		{
			if (userDao.isRoot(user.getId()))
			{
				List<Right> rList = dao().findAll(Right.class);
				for (Right right : rList)
				{
					putMap(map, right);
				}
			}
			else
			{
				@Memo("用户》转授权")
				List<UserEmpower> empowers = userDao.findToUserEmpowers(userId);
				for (UserEmpower ue : empowers)
				{
					if (ue.getStatus() == Status.Normal && ue.getStartTime().getTime() < System.currentTimeMillis() && ue.getEndTime().getTime() > System.currentTimeMillis())
					{
						User fromUser = ue.getFromUser();
						List<UserRole> userRoleList = userDao.findUserRoles(fromUser.getId());// fromUser.getUserRoles();
						for (UserRole userRole : userRoleList)
						{
							List<RoleRight> roleRightList = userDao.findRoleRightByRoleId(userRole.getRole().getId());// userRole.getRole().getRoleRight();
							for (RoleRight rr : roleRightList)
							{
								Right right = rr.getRight();
								putMap(map, right);
							}
						}
						List<UserRight> userRightList = userDao.findUserRights(fromUser.getId());// fromUser.getUserRight();
						for (UserRight ur : userRightList)
						{
							Right right = ur.getRight();
							putMap(map, right);
						}
					}
				}
				@Memo("用户》角色》权限")
				List<UserRole> userRoleList = userDao.findUserRoles(user.getId());// user.getUserRoles();
				for (UserRole userRole : userRoleList)
				{
					List<RoleRight> roleRightList = userDao.findRoleRightByRoleId(userRole.getRole().getId());
					for (RoleRight rr : roleRightList)
					{
						Right right = rr.getRight();
						putMap(map, right);
					}
				}
				@Memo("用户》权限")
				List<UserRight> userRightList = userDao.findUserRights(user.getId());// user.getUserRight();
				for (UserRight ur : userRightList)
				{
					Right right = ur.getRight();
					putMap(map, right);
				}
			}
		}
		return new ArrayList<Right>(map.values());
	}

	private void putMap(Map<String, Right> map, Right right)
	{
		if (!Boolean.valueOf(right.getDebug()))
		{
			if (right.getStatus() == Status.Normal && right.getPlugins().getStatus() == Status.Normal)
			{
				map.put(right.getId(), right);
			}
		}
	}

	@Override
	public List<Right> findAuthByParentId(String parentId)
	{
		List<Right> menuList = findByParentId(parentId);
		return menuList;
	}

	@Override
	public void saveRight(Right right)
	{
		if (right.getId() == null || right.getId().isEmpty())
		{
			return;
		}
		Right tmp = dao().getById(Right.class, right.getId());
		if (tmp == null)
		{
			return;
		}
		tmp.setText(right.getText());
		tmp.setSort(right.getSort());
		tmp.setStatus(right.getStatus());
		if (!StringUtils.isEmpty(right.getIcon()))
		{
			tmp.setIcon(right.getIcon());
		}
		tmp.setNeedCheck(right.getNeedCheck());
		tmp.setUpdateTime(new Date());
		dao().save(tmp);
	}

	@Override
	public Right getByUrl(String url)
	{
		return dao().getByField(Right.class, "url", url);
	}

	@Override
	public String getFullMenuName(String url)
	{
		String rst = "";
		Right right = findByUrl(url);
		if (right != null)
		{
			rst = right.getText() + rst;
			String id = right.getParentId();
			while (StringUtils.isNotBlank(id))
			{
				Right r = dao().getById(Right.class, id);
				if (r != null)
				{
					rst = r.getText() + "-" + rst;
					id = r.getParentId();
				}
				else
				{
					break; // 跳出
				}
			}
		}
		return rst;
	}

	private Right findByUrl(String url)
	{
		Search search = new Search(Right.class);
		search.addFilterEqual("url", url);
		@SuppressWarnings("unchecked")
		List<Right> result = dao().search(search);
		if (result != null && result.size() > 0)
		{
			return result.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RightVO> findRightRejects(String id)
	{
		Search search = new Search(Right.class);
		search.addFilterNotEqual("id", id);
		search.addFilterEqual("debug", Boolean.FALSE.toString());
		search.addSort("sort", false);
		List<Right> rights = dao().search(search);
		search = new Search(RightReject.class);
		search.addFilter(Filter.or(Filter.equal("refRightId", id), Filter.or(Filter.equal("rejectRightId", id))));
		List<RightReject> rejects = dao().search(search);
		List<RightVO> tree = new ArrayList<RightVO>();
		for (Right right : rights)
		{
			String type = right.getType();
			if (!StringUtils.equalsIgnoreCase(type, RightType.RESOURCE.name()))
			{
				RightVO vo = changeRight2VO(right);
				for (RightReject reject : rejects)
				{
					if (right.getId().equals(reject.getRefRightId()) || right.getId().equals(reject.getRejectRightId()))
					{
						vo.setChecked(true);
						break;
					}
				}
				tree.add(vo);
			}
		}

		List<RightVO> result = new ArrayList<RightVO>();

		for (RightVO vo : tree)
		{
			List<RightVO> children = vo.getChildren();
			if (children == null)
			{
				children = new ArrayList<RightVO>();
			}
			String rid = vo.getId();
			for (RightVO vo2 : tree)
			{
				if (rid.equals(vo2.getParentId()))
				{
					children.add(vo2);
				}
			}
			if (children.size() > 0)
			{
				vo.setLeaf(false);
			}
			else
			{
				vo.setLeaf(true);
			}
			vo.setChildren(children);
			if (StringUtils.isEmpty(vo.getParentId()))
			{
				result.add(vo);
			}
		}

		return result;
	}

	private RightVO changeRight2VO(Right right)
	{
		RightVO vo = new RightVO();
		try
		{
			BeanUtils.copyProperties(vo, right);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}

	@Override
	public boolean saveRightReject(String refId, String rejectIds)
	{
		String hql = "delete from RightReject where refRightId=?1 or rejectRightId=?2";
		dao().execute(hql, refId, refId);
		String[] ids = rejectIds.split(",");
		if (ids.length > 0)
		{
			for (String id : ids)
			{
				RightReject r = new RightReject();
				r.setRefRightId(refId);
				r.setRejectRightId(id);
				dao().save(r);
				removeRoleRightReject(refId, id);
				removeUserRightReject(refId, id);
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void removeRoleRightReject(String refId, String rejectId)
	{
		Search search = new Search(RoleRight.class);
		search.addFilterEqual("right.id", refId);
		List<RoleRight> list = dao().search(search);
		for (RoleRight rr : list)
		{
			search = new Search(RoleRight.class);
			search.addFilterEqual("role.id", rr.getRole().getId());
			search.addFilterEqual("right.id", rejectId);
			List<RoleRight> tmpList = dao().search(search);
			for (RoleRight entity : tmpList)
			{
				dao().remove(entity);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void removeUserRightReject(String refId, String rejectId)
	{
		Search search = new Search(UserRight.class);
		search.addFilterEqual("right.id", refId);
		List<UserRight> list = dao().search(search);
		for (UserRight rr : list)
		{
			search = new Search(UserRight.class);
			search.addFilterEqual("user.id", rr.getUser().getId());
			search.addFilterEqual("right.id", rejectId);
			List<UserRight> tmpList = dao().search(search);
			for (UserRight entity : tmpList)
			{
				dao().remove(entity);
			}
		}
	}

	@Override
	public void removeReject(List<Right> rightList)
	{
		Map<String, Right> rightMap = new HashMap<String, Right>();
		for (Right r : rightList)
		{
			rightMap.put(r.getId(), r);
		}
		Search search = new Search(RightReject.class);
		search.addFilterIn("refRightId", rightMap.keySet());
		search.addFilterIn("rejectRightId", rightMap.keySet());
		@SuppressWarnings("unchecked")
		List<RightReject> rrList = dao().search(search);
		for (RightReject rr : rrList)
		{
			Right right2 = rightMap.get(rr.getRejectRightId());
			rightList.remove(right2);
		}
//
//		for (int i = rightList.size() - 1; i >= 0; i--)
//		{
//			for (int j = i - 1; j >= 0; j--)
//			{
//				if (_exitRightReject(rightList.get(i).getId(), rightList.get(j).getId()))
//				{
//					rightList.remove(j);
//				}
//			}
//		}
	}

	@Override
	public boolean exitReject(Right right1, Right right2)
	{
		return rightDao.exitRightReject(right1.getId(), right2.getId());
	}

	@Override
	public String validateRightReject(String[] rightIds)
	{
		StringBuffer sb = new StringBuffer();
		Search search = new Search(Right.class);
		search.addFilterIn("id", Arrays.asList(rightIds));
		@SuppressWarnings("unchecked")
		List<Right> list = dao().search(search);
		Map<String, Right> rightMap = new HashMap<String, Right>();
		for (Right r : list)
		{
			rightMap.put(r.getId(), r);
		}
		search = new Search(RightReject.class);
		search.addFilterIn("refRightId", rightMap.keySet());
		search.addFilterIn("rejectRightId", rightMap.keySet());
		@SuppressWarnings("unchecked")
		List<RightReject> rrList = dao().search(search);
		for (RightReject rr : rrList)
		{
			Right right1 = rightMap.get(rr.getRefRightId());
			Right right2 = rightMap.get(rr.getRejectRightId());
			sb.append("<br/>" + right1.getText() + " 与 " + right2.getText() + " 互斥,不允许授权给同一角色");
		}
//		for (int i = list.size() - 1; i >= 0; i--)
//		{
//			Right ri = list.get(i);
//			for (int j = i - 1; j >= 0; j--)
//			{
//				Right rj = list.get(j);
//				if (exitReject(ri, rj))
//				{
//					sb.append("<br/>[" + ri.getText() + "]和[" + rj.getText() + "]！");
//				}
//			}
//		}
//		if (StringUtils.isNotEmpty(sb.toString()))
//		{
//			return "以下功能不允许同时授权给同一角色" + sb.toString();
//		}
		return sb.toString();
	}

	private String findRightRejectToString(List<RightReject> values)
	{
		StringBuffer sb = new StringBuffer();
		for (RightReject re : values)
		{
			Right right = rightDao.getById(re.getRejectRightId());
			if (right != null)
				sb.append(right.getMemo() + " | ");
		}
		if (sb.length() > 3)
		{
			return sb.substring(0, sb.length() - 3);
		}
		return sb.toString();
	}

	@Override
	public String findRightRejectString(String rightId)
	{
		return findRightRejectToString(rightDao.findRightReject(Arrays.asList(rightId)));
	}

	@Override
	public List<RightVO> loadTreeAll()
	{
		List<Right> list = rightDao.findByParentId(null);
		filterMenuListener(null, list);
		List<RightVO> tree = new ArrayList<RightVO>();
		for (Right r : list)
		{
			if (r.getStatus() == Status.Normal && r.getPlugins().getStatus() == Status.Normal && !Boolean.valueOf(r.getDebug()))
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
				rtc.setDisplayName(findRightRejectToString(rightDao.findRightReject(Arrays.asList(rtc.getId()))));
				callbackLoad(rtc);
				tree.add(rtc);
			}
		}
		return tree;
	}

	private void callbackLoad(RightVO rightvo)
	{
		List<Right> list = rightDao.findByParentId(rightvo.getId());
		filterMenuListener(null, list);
		List<RightVO> tree = new ArrayList<RightVO>();
		rightvo.setChildren(tree);
		for (Right r : list)
		{
			if (r.getStatus() == Status.Normal && r.getPlugins().getStatus() == Status.Normal && !Boolean.valueOf(r.getDebug()))
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
				rtc.setDisplayName(findRightRejectToString(rightDao.findRightReject(Arrays.asList(rtc.getId()))));
				tree.add(rtc);
				callbackLoad(rtc);
			}
		}
	}

	@Override
	public boolean matcher(String url, String resource)
	{
		String[] patterns = StringUtils.split(resource, ";；#,，\n\r");// StringUtil.toStringArray(resource);
		for (String pattern : patterns)
		{
			pattern = pattern.trim();
			if (urlMatcher.pathMatchesUrl(pattern, url))
			{
				return true;
			}
		}
		return false;
	}

	public Listener<List<Right>> filterMenuListener;

	@Override
	public void filterMenuListener(User user, List<Right> menuList)
	{
		try
		{
			String urls = systemConfigService.getByName(Constants.RightFilter, "");
			if (StringUtils.isNotBlank(urls))
			{
				for (int i = menuList.size() - 1; i >= 0; i--)
				{
					Right r = menuList.get(i);
					if (matcher(r.getUrl(), urls))
					{
						menuList.remove(i);
					}
				}
			}
			if (filterMenuListener != null)
				filterMenuListener.onListener(user, menuList);
		}
		catch (Throwable ex)
		{
			logger.debug(ex.getMessage());
		}
	}

	public Listener<List<Right>> getFilterMenuListener()
	{
		return filterMenuListener;
	}

	@Override
	public void setFilterMenuListener(Listener<List<Right>> filterMenuListener)
	{
		this.filterMenuListener = filterMenuListener;
	}

	@Override
	public boolean findUserMatcherRight(User user, List<String> rightIds)
	{
		List<String> roleIds = roleDao.findRoleIdList(rightIds);
		List<UserEmpower> empowers = userDao.findToUserEmpowers(user.getId());
		List<String> userIds = new ArrayList<String>();
		userIds.add(user.getId());
		for (UserEmpower ue : empowers)
		{
			if (ue.getStatus() == Status.Normal && ue.getStartTime().getTime() < System.currentTimeMillis() && ue.getEndTime().getTime() > System.currentTimeMillis())
			{
				User fromUser = ue.getFromUser();
				userIds.add(fromUser.getId());
			}
		}
		return rightDao.findUserMatcherRole(userIds, roleIds);
		// return true;
	}
}
