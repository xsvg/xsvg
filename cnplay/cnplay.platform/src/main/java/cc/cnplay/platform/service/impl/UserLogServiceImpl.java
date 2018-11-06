package cc.cnplay.platform.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.util.DateUtil;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserLog;
import cc.cnplay.platform.domain.UserOperLog;
import cc.cnplay.platform.service.UserLogService;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Service
@Transactional
public class UserLogServiceImpl extends AbsGenericService<UserLog, String> implements UserLogService
{

	@Override
	public DataGrid<UserLog> findPageLikeName(Date startDate, Date endDate, Organization org, String orgId, String name, int page, int pageSize)
	{
		Search search = new Search(UserLog.class);
		if (StringUtils.isNotBlank(name))
		{
			search.addFilterILike("name", "%" + name + "%");
		}
		if (startDate != null)
			search.addFilterGreaterOrEqual("createTime", startDate);
		if (endDate != null)
			search.addFilterLessOrEqual("createTime", endDate);
		// 性能问题，只搜索当前机构系统日志
//		search.addFilterLike("user.organization.levelCode", org.getLevelCode() + "%");
		if (StringUtils.isNotBlank(orgId))
		{
			search.addFilterEqual("user.organization.id", orgId);
		}
		search.addSortDesc("createTime");
		search.setFirstResult(getFirstResult(page, pageSize));
		search.setMaxResults((pageSize > 0 ? pageSize : 20));
		@SuppressWarnings("unchecked")
		SearchResult<UserLog> result = dao().searchAndCount(search);
		List<UserLog> olList = result.getResult();
		for (UserLog uol : olList)
		{
			User user = uol.getUser();
			if (user != null)
			{
				uol.setUsername(user.getName());
				if (user.getOrganization() != null)
				{
					uol.setOrgId(user.getOrganization().getId());
					uol.setOrgName(user.getOrganization().getName());
				}
			}
		}
		DataGrid<UserLog> dg = new DataGrid<UserLog>((int) result.getTotalCount(), olList, pageSize, page);
		return dg;
	}

	@Override
	public DataGrid<UserOperLog> findUserLogListPageLikeName(String orgId, String name, Date startDate, Date endDate, int page, int pageSize)
	{
		Search search = new Search(UserOperLog.class);
		search.addFilterEqual("userCreate.organization.id", orgId);
		if (StringUtils.isNotBlank(name))
		{
			search.addFilterLike("name", "%" + name + "%");
		}
		if (startDate != null && endDate != null && startDate.getTime() > endDate.getTime())
		{
			Date tmp = startDate;
			startDate = endDate;
			endDate = tmp;
		}
		if (startDate != null)
		{
			search.addFilterGreaterOrEqual("createTime", DateUtil.dateStart(startDate));
		}
		if (endDate != null)
		{
			search.addFilterLessOrEqual("createTime", DateUtil.dateEnd(endDate));
		}
		search.addSortDesc("createTime");
		search.setFirstResult(getFirstResult(page, pageSize));
		search.setMaxResults((pageSize > 0 ? pageSize : 20));
		@SuppressWarnings("unchecked")
		SearchResult<UserOperLog> result = dao().searchAndCount(search);
		DataGrid<UserOperLog> dg = new DataGrid<UserOperLog>((int) result.getTotalCount(), result.getResult(), pageSize, page);
		return dg;
	}

}
