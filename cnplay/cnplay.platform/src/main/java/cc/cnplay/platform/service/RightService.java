package cc.cnplay.platform.service;

import java.util.List;

import cc.cnplay.core.Listener;
import cc.cnplay.core.service.GenericService;
import cc.cnplay.platform.domain.Plugins;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.vo.RightVO;

public interface RightService extends GenericService<Right, String>
{

	List<Right> findByParentId(String parentId);

	List<Plugins> findAllPlugins();

	Right getByUrl(String url);

	Right getBy(String url, String component, String fromClass);

	Right getByFromClassAndText(String fromClass, String text);

	Right getByPlugins(Plugins plugins, String text);

	List<Right> findAuthByUserIdAndParentId(String userId, String parentId);

	List<Right> findAuthByUserId(String userId);

	List<Right> findAuthByParentId(String parentId);

	boolean isLeaf(String id);

	void checkRight(List<String> rightIds);

	void saveRight(Right right);

	// 根据URL取全部菜单路径
	String getFullMenuName(String url);

	List<RightVO> findRightRejects(String id);

	boolean saveRightReject(String refId, String rejectIds);

	List<Right> findByParentId(String parentId, boolean debug);

	void removeReject(List<Right> rightList);

	String validateRightReject(String[] rightIds);

	boolean exitReject(Right right1, Right right2);

	Right getByName(String name);

	List<RightVO> loadTreeAll();

	String findRightRejectString(String rightId);

	void setFilterMenuListener(Listener<List<Right>> filterMenuListener);

	void filterMenuListener(User user, List<Right> menuList);

	boolean matcher(String url, String resource);

	boolean findUserMatcherRight(User user, List<String> rightIds);

	List<RightVO> findRightWithUser(String userid, String id);

}
