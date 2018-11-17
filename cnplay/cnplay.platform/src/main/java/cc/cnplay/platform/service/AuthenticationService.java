package cc.cnplay.platform.service;

import java.util.List;

import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.vo.LoginUser;

public interface AuthenticationService //extends InitializeService
{

	/**
	 * 验证用户是否有URL访问权限
	 * 
	 * @param userId
	 * @param url
	 * @return
	 */
	boolean check(User user, String url);

	/**
	 * URL是否匹配表达式
	 * 
	 * @param url
	 * @param patterns
	 *            表达式
	 * @return
	 */
	boolean matcher(String url, String patterns);

	/**
	 * 首页菜单
	 * 
	 * @param parentId
	 * @param userId
	 * @return
	 */
	List<Right> findMenu(String parentId, String userId);

	/**
	 * 用户是否有按钮（URL）的访问权限
	 * 
	 * @param userId
	 * @param url
	 * @return
	 */
	boolean disabled(String userId, String url);

	/**
	 * URL是否要复核
	 * 
	 * @param url
	 * @return
	 */
	boolean isNeedCheck(String url);

	Json<LoginUser> login(LoginUser loginUser);

	void initPlugins();

	/**
	 * 用户是否有生效的转授权
	 * @param userId
	 * @return
	 */
	boolean isEmpower(String userId);

}
