package cc.cnplay.platform.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.cache.OnlineUserCache;
import cc.cnplay.platform.dao.RoleDao;
import cc.cnplay.platform.dao.UserDao;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserEmpower;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.service.UserEmpowerService;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Service
public class UserEmpowerServiceImpl extends AbsGenericService<UserEmpower, String> implements UserEmpowerService
{

	@Resource
	private RoleDao roleDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private OnlineUserCache onlineCache;

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private RightService rightService;

	@Override
	public void cancel(String ids)
	{
		String[] arrId = ids.split(",");
		for (String str : arrId)
		{
			UserEmpower entity = dao().getById(UserEmpower.class, str);
			if (entity.getStatus() == Status.Normal)
			{
				entity.setStatus(Status.Delete);
				dao().save(entity);
			}
		}

	}

	@Override
	public void check()
	{
		List<UserEmpower> ueList = dao().findAll(UserEmpower.class);
		for (UserEmpower ue : ueList)
		{
			if (ue.getStatus() == Status.Normal && ue.getEndTime().getTime() < System.currentTimeMillis())
			{
				ue.setStatus(Status.Banned);
				dao().save(ue);
			}
		}

	}

	@Override
	public DataGrid<UserEmpower> findByCondition(String fromDate, String toDate, int page, int pageSize, User user)
	{
		Search search = new Search(UserEmpower.class);
		if (StringUtils.isNotBlank(fromDate))
		{
			search.addFilterGreaterOrEqual("startTime", DateUtil.dateGreater(fromDate));
		}
		if (StringUtils.isNotBlank(toDate))
		{
			search.addFilterLessThan("endTime", DateUtil.dateLess(toDate));
		}
		search.addFilter(Filter.or(Filter.equal("fromUser.id", user.getId()), Filter.or(Filter.equal("toUser.id", user.getId()))));
		search.addSortDesc("createTime");
		search.setFirstResult(getFirstResult(page, pageSize));
		search.setMaxResults((pageSize > 0 ? pageSize : 20));
		@SuppressWarnings("unchecked")
		SearchResult<UserEmpower> sr = dao().searchAndCount(search);
		List<UserEmpower> result = sr.getResult();
		for (UserEmpower ue : result)
		{
			User fromUser = ue.getFromUser();
			if (fromUser != null)
			{
				ue.setFromUserName(fromUser.getName());
			}
			User toUser = ue.getToUser();
			if (toUser != null)
			{
				ue.setToUserName(toUser.getName());
			}
		}
		DataGrid<UserEmpower> dg = new DataGrid<UserEmpower>((int) sr.getTotalCount(), result, pageSize, page);
		return dg;
	}

	@Override
	public List<User> findUserByName(String username, User loginUser) throws Exception
	{
		if (loginUser == null)
		{
			throw new CnplayRuntimeException("session可能超时，请重新登录");
		}
		Organization org = loginUser.getOrganization();
		if (org == null)
		{
			throw new CnplayRuntimeException("当前用户没有指定组织");
		}
		Search search = new Search(User.class);
		// 要排除自己，不能给自己转授权
		if (StringUtils.isNotBlank(username))
		{
			search.addFilterLike("name", "%" + username + "%");
		}
		search.addFilterEqual("organization.id", org.getId());
		search.addFilterNotEqual("id", loginUser.getId());

		search.addSortAsc("name");

		@SuppressWarnings("unchecked")
		List<User> userLst = dao().search(search);
		for (User u : userLst)
		{
			String orgStr = "";
			if (org != null)
			{
				orgStr = StringUtils.defaultString(org.getName(), "");
			}
			u.setOrgName(orgStr);
		}
		return userLst;
	}

	/*
	 * 1.别人转给我，我不能再转给别人 2.转给一个人，再不能转第二个人
	 */
	@Override
	public void saveForm(UserEmpower form, User loginUser) throws Exception
	{
		User fromUser = dao().getById(User.class, loginUser.getId());
		User toUser = dao().getById(User.class, form.getToUserId());
		Date startDate = DateUtil.parsetDate(form.getStartTimeStr());
		Date endDate = DateUtil.parsetDate(form.getEndTimeStr());

		if (findExistToMe(loginUser.getId(), form.getId()))
		{
			throw new CnplayRuntimeException("已经是被授权，不能再转给其它人");
		}
		if (findExistFromMe(loginUser.getId(), form.getId()))
		{
			throw new CnplayRuntimeException("已经存在一个授权人，不能重复授权");
		}

		UserEmpower ue = new UserEmpower();
		if (StringUtils.isNotEmpty(form.getId()))
		{
			ue = dao().getById(UserEmpower.class, form.getId());
			ue.setUpdateTime(new Date());
			ue.setUpdateUsername(loginUser.getName() + loginUser.getUsername());
		}
		else
		{
			ue.setCreateTime(new Date());
			ue.setCreateUsername(loginUser.getName() + loginUser.getUsername());
		}
		ue.setFromUser(fromUser);
		ue.setToUser(toUser);
		ue.setStartTime(startDate);
		ue.setEndTime(endDate);
		ue.setStatus(Status.Normal);
		dao().save(ue);
	}

	private boolean findExistToMe(String userid, String id)
	{
		Search search = new Search(UserEmpower.class);
		search.addFilterEqual("toUser.id", userid);
		search.addFilterGreaterThan("endTime", new Date());
		search.addFilterEqual("status", Status.Normal);
		if (StringUtils.isNoneEmpty(id))
		{
			search.addFilterNotEqual("id", id);
		}
		@SuppressWarnings("unchecked")
		List<UserEmpower> result = dao().search(search);
		if (result.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	private boolean findExistFromMe(String userid, String id)
	{
		Search search = new Search(UserEmpower.class);
		search.addFilterEqual("fromUser.id", userid);
		search.addFilterGreaterThan("endTime", new Date());
		search.addFilterEqual("status", Status.Normal);
		if (StringUtils.isNoneEmpty(id))
		{
			search.addFilterNotEqual("id", id);
		}
		@SuppressWarnings("unchecked")
		List<UserEmpower> result = dao().search(search);
		if (result.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	@Override
	public boolean isEmpower(String userId)
	{
		Search search = new Search(UserEmpower.class);
		search.addFilterEqual("fromUser.id", userId);
		search.addFilterGreaterThan("endTime", new Date());
		search.addFilterEqual("status", Status.Normal);
		@SuppressWarnings("unchecked")
		List<UserEmpower> result = dao().search(search);
		if (result.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void cancelByUser(String userId)
	{
		Search search = new Search(UserEmpower.class);
		search.addFilterEqual("fromUser.id", userId);
		search.addFilterGreaterThan("endTime", new Date());
		search.addFilterEqual("status", Status.Normal);
		@SuppressWarnings("unchecked")
		List<UserEmpower> result = dao().search(search);
		for (UserEmpower ue : result)
		{
			ue.setStatus(Status.Delete);
			dao().save(ue);
		}

	}

}
