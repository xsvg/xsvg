package cc.cnplay.platform.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cc.cnplay.core.Listener;
import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserFinger;
import cc.cnplay.platform.domain.UserOperLog;
import cc.cnplay.platform.vo.LoginUser;
import cc.cnplay.platform.vo.SettingPassword;

public interface UserService extends GenericService<User, String>
{

	void init();

	User getNormalByUsername(String username);

	Json<LoginUser> login(LoginUser login);

	Json<Boolean> settingPassword(SettingPassword setting);	

	void updatePwd(String ids, UserOperLog uol);

	void updateStatus(String ids, String status, UserOperLog uol) throws CnplayRuntimeException;

	User getUserById(String id);

	DataGrid<User> findByName(User user, String name, String orgid, int pageNum, int pageSize);

	User saveUser(User userVO, UserOperLog uol);

	List<Role> findRoleWithUser(String userid, String loginUserid);

	void updateRoleRight(String userid, String[] roleids, String[] rightids, UserOperLog uol) throws CnplayRuntimeException;

	/**
	 * 查找用户指纹，参数：用户、指纹设备类型
	 * 
	 * @param user
	 * @param deviceDev
	 * @return
	 */
	@Memo("查找用户指纹，参数：用户和指纹设备类型")
	List<UserFinger> findFinger(User user, String deviceDev);

	/**
	 * 指纹校验，参数：用户、指纹设备类型、指纹
	 * 
	 * @param user
	 * @param fingerDev
	 * @param fingerData
	 * @return
	 */
	@Memo("指纹校验，参数：用户、指纹设备类型、指纹")
	int checkFinger(User user, String fingerDev, String fingerData);

	boolean checkUserFinger(User user, String fingerDev, String fingerData);

	String findRoleNamesByUser(String userId);

	User getByOrgAndFingerData(final String orgId, final String fingerDev, final String fingerData);

	List<User> findList(String name, String orgId);

	String validateRightReject(String[] roleIds);

	Role defaultRole();

	Json<?> loadImportUser(HttpServletRequest request, String roleConfig, UserOperLog uol);

	String saveImportUser(String json, UserOperLog uol);

	List<Map<String, ?>> findReportData(Organization org, String orgId, String name);

	boolean exitRight(String userId, String component);

	boolean isRootOrg(String userId);

	User getByMobile(String mobile);

	User getByUsername(String username);

	boolean isDefaultPwd(User user);

	boolean isNeedModifyPwd(User user);

	void setRemoveUserListener(Listener<User> removeUserListener);

	boolean isRoot(String id);
}
