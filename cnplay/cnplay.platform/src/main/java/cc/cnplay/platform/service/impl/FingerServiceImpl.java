package cc.cnplay.platform.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserFinger;
import cc.cnplay.platform.service.FingerService;

import com.googlecode.genericdao.search.Search;

@Service
public class FingerServiceImpl extends AbsGenericService<UserFinger, String> implements FingerService
{

	@Override
	public UserFinger saveForm(UserFinger userFinger)
	{
		UserFinger uf = this.save(userFinger);
		User user = userFinger.getUser();
		List<?> fingerList = findByUserid(user.getId());

		if (fingerList.size() > Constants.FINGER_NUM_MAX)
		{
			for (int k = Constants.FINGER_NUM_MAX; k < fingerList.size(); k++)
			{
				dao().remove(fingerList.get(k));
			}
		}
		return uf;
	}

	@Override
	public List<UserFinger> findByUserid(String userid)
	{
		Search search = new Search(UserFinger.class);
		search.addFilterEqual("user.id", userid);
		search.addSortDesc("createTime");
		@SuppressWarnings("unchecked")
		List<UserFinger> rst = dao().search(search);
		return rst;
	}

	@Override
	public UserFinger getUserFingerByNumAndUser(String fingerNum, String userId)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fingerNum", fingerNum);
		map.put("user.id", userId);
		UserFinger uf = dao().getByMap(UserFinger.class, map);
		return uf;
	}

	@Override
	public void saveFingers(String userId, List<UserFinger> fingerDatas)
	{

		for (UserFinger vo : fingerDatas)
		{
			if (StringUtils.isBlank(vo.getFingerData()))
			{
				continue;
			}
			User user = getById(User.class, userId);
			if (user != null)
			{
				UserFinger userFinger = new UserFinger();
				userFinger.setUser(user);
				userFinger.setFingerDev(vo.getFingerDev());
				userFinger.setFingerData(vo.getFingerData());
				userFinger.setFingerNum(vo.getFingerNum());
				userFinger.setStress(Boolean.valueOf(vo.isStress()));
				this.save(userFinger);
			}
		}

	}

	@Override
	public UserFinger findByFingerNum(String userId, String fingerNum)
	{
		Search search = new Search(UserFinger.class);
		search.addFilterEqual("user.id", userId);
		search.addFilterEqual("fingerNum", fingerNum);

		@SuppressWarnings("unchecked")
		List<UserFinger> rst = dao().search(search);
		if (rst != null && rst.isEmpty() == false)
		{
			return rst.get(0);
		}
		else
		{
			return null;
		}
	}

}
