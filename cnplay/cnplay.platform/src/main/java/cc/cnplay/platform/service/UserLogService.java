package cc.cnplay.platform.service;

import java.util.Date;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.UserLog;
import cc.cnplay.platform.domain.UserOperLog;

public interface UserLogService extends GenericService<UserLog, String>
{

	DataGrid<UserLog> findPageLikeName(Date startDate, Date endDate, Organization org, String orgId, String name, int page, int pageSize);

	DataGrid<UserOperLog> findUserLogListPageLikeName(String orgId, String name, Date startDate, Date endDate, int page, int pageSize);

}
