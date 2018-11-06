package cc.cnplay.platform.cache;

import java.util.List;

import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.platform.vo.LoginUser;


public interface OnlineUserCache {
	public LoginUser getUser(String userName);
	
	public void putUser(LoginUser User);
	
	public void putUser(String username,LoginUser User);
	
	public LoginUser removeUser(String userName);
	
	public List<LoginUser> getAllUsers();
	
	public DataGrid<LoginUser> getUsers(DataGrid<LoginUser> pagination);
}
