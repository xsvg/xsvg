package cc.cnplay.platform.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.cache.OnlineUserCache;
import cc.cnplay.platform.vo.LoginUser;

@Component
public class OnlineUserCacheImpl implements OnlineUserCache {
	private final static Map<String, LoginUser> ONLINE_USER_MAP = Collections.synchronizedMap(new HashMap<String, LoginUser>());
	@Override
	public LoginUser getUser(String userName) {
		synchronized (ONLINE_USER_MAP) {
			return ONLINE_USER_MAP.get(userName);
		}
	}
	
	@Override
	public void putUser(LoginUser user) {
		synchronized (ONLINE_USER_MAP) {
			ONLINE_USER_MAP.put(user.getUsername(), user);
		}
	}

	@Override
	public void putUser(String username,LoginUser user) {
		synchronized (ONLINE_USER_MAP) {
			ONLINE_USER_MAP.put(username, user);
		}
	}
	
	@Override
	public LoginUser removeUser(String userName) {
		synchronized (ONLINE_USER_MAP) {
			return ONLINE_USER_MAP.remove(userName);
		}
	}

	@Override
	public List<LoginUser> getAllUsers() {
		synchronized (ONLINE_USER_MAP) {
			List<LoginUser> users = new ArrayList<LoginUser>();
			Iterator<Map.Entry<String, LoginUser>> it = ONLINE_USER_MAP.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, LoginUser> entry = it.next();
				users.add(entry.getValue());
			}
			return users;
		}
	}

	@Override
	public DataGrid<LoginUser> getUsers(DataGrid<LoginUser> pagination) {
		synchronized (ONLINE_USER_MAP) {
			List<LoginUser> users = new ArrayList<LoginUser>();
			Iterator<Map.Entry<String, LoginUser>> it = ONLINE_USER_MAP.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, LoginUser> entry = it.next();
				users.add(entry.getValue());
			}
			
			List<LoginUser> result = new ArrayList<LoginUser>();
			for(int i = (pagination.getPageNum()-1)*pagination.getPageSize();i<pagination.getPageSize()*pagination.getPageNum();i++){
				if(i<users.size()){
					result.add(users.get(i));
				}
			}
			pagination.setRows(result);
			return pagination;
		}
	}

	

}
