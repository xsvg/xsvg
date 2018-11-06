package cc.cnplay.platform.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.domain.CheckLog;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.CheckLogService;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Service
@Transactional
public class CheckLogServiceImpl extends AbsGenericService<CheckLog, String> implements CheckLogService
{

	@Override
	public DataGrid<CheckLog> findPageByCheckTime(Date checkStartDate, Date checkEndDate, Organization org, String orgId, int page, int pageSize)
	{

		Search search = new Search(CheckLog.class);
		if (checkStartDate != null)
			search.addFilterGreaterOrEqual("checkTime", checkStartDate);
		if (checkEndDate != null)
			search.addFilterLessOrEqual("checkTime", checkEndDate);
		search.addFilterLike("organization.levelCode", org.getLevelCode() + "%");
		if (StringUtils.isNotBlank(orgId))
		{
			search.addFilterEqual("organization.id", orgId);
		}
		search.addFilterEqual("status", Status.Normal);
		search.addSortDesc("createTime");
		search.setFirstResult(getFirstResult(page, pageSize));
		search.setMaxResults((pageSize > 0 ? pageSize : 20));
		@SuppressWarnings("unchecked")
		SearchResult<CheckLog> result = dao().searchAndCount(search);
		List<CheckLog> rst = result.getResult();
		for (CheckLog cLog : rst)
		{
			User uCreate = cLog.getCreateUser();
			User uCheck = cLog.getCheckUser();
			String createUserStr = "";
			String createCheckStr = "";
			if (uCreate != null)
			{
				createUserStr = StringUtils.defaultString(uCreate.getName(), "");
			}
			cLog.setCreateUsername(createUserStr);
			if (uCheck != null)
			{
				createCheckStr = StringUtils.defaultString(uCheck.getName(), "");
			}
			cLog.setCreateCheckUsername(createCheckStr);
		}
		DataGrid<CheckLog> dgList = new DataGrid<CheckLog>((int) result.getTotalCount(), rst, pageSize, page);
		return dgList;
	}

}
