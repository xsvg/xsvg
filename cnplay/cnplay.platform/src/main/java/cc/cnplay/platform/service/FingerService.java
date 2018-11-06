package cc.cnplay.platform.service;

import java.util.List;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.platform.domain.UserFinger;

public interface FingerService extends GenericService<UserFinger, String>
{

	UserFinger saveForm(UserFinger userFinger);

	List<UserFinger> findByUserid(String userid);

	UserFinger getUserFingerByNumAndUser(String fingerNum, String userId);

	void saveFingers(String userId, List<UserFinger> fingerDatas);

	UserFinger findByFingerNum(String userId, String fingerNum);

}
