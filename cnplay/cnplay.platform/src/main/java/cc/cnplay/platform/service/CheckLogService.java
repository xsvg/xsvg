package cc.cnplay.platform.service;

import java.util.Date;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.domain.CheckLog;
import cc.cnplay.platform.domain.Organization;

public interface CheckLogService extends GenericService<CheckLog, String>
{

	public DataGrid<CheckLog> findPageByCheckTime(Date checkStartDate, Date checkEndDate, Organization org, String orgId, int page, int pageSize);

}
