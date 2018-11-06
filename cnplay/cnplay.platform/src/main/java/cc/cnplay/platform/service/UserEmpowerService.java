package cc.cnplay.platform.service;

import java.util.List;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserEmpower;

public interface UserEmpowerService extends GenericService<UserEmpower, String>
{

	void cancel(String ids);

	DataGrid<UserEmpower> findByCondition(String fromDate, String toDate, int page, int pageSize, User user);

	List<User> findUserByName(String username, User loginUser) throws Exception;

	void saveForm(UserEmpower form, User loginUser) throws Exception;

	boolean isEmpower(String userId);

	void check();

	void cancelByUser(String userId);

}
