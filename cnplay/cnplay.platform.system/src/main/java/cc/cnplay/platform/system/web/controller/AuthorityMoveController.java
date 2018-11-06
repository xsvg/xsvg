package cc.cnplay.platform.system.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserEmpower;
import cc.cnplay.platform.service.UserEmpowerService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping("/manage/authority")
public class AuthorityMoveController extends AbsController
{
	@Autowired
	private UserService userService;

	@Autowired
	private UserEmpowerService authorityMoveService;

	@ResponseBody
	@RequestMapping(value = "/list", produces = "application/json")
	@RightAnnotation(name = "系统管理/授权管理", component = "platform.system.view.AuthorityMoveList", icon = "", sort = 80500)
	public DataGrid<UserEmpower> list(String fromDate, String toDate)
	{
		DataGrid<UserEmpower> rst = null;
		try
		{
			authorityMoveService.check();
			rst = authorityMoveService.findByCondition(fromDate, toDate, this.getPage(), this.getPageSize(), this.getSessionUser());
		}
		catch (Exception e)
		{
			logger.equals(e.getMessage());
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/loadById")
	public Json<UserEmpower> load(String id)
	{
		Json<UserEmpower> rst = new Json<UserEmpower>();
		try
		{
			UserEmpower ue = authorityMoveService.getById(id);
			if (ue.getStatus() == Status.Normal)
			{
				if (ue.getEndTime().getTime() < System.currentTimeMillis())
				{
					ue.setStatus(Status.Banned);
					authorityMoveService.save(ue);
					rst.NG("授权已撤销或已经过期不能再进行修改");
				}
				else
				{
					rst.OK(ue, "");
				}
			}
			else
			{
				rst.NG("授权已撤销或已经过期不能再进行修改");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			rst.NG(e.getMessage());
		}
		return rst;

	}

	@ResponseBody
	@RequestMapping(value = "/cancel")
	@RightAnnotation(name = "系统管理/授权管理/撤消授权", button = true)
	public Json<String> cancel(String id)
	{
		authorityMoveService.cancel(id);
		return new Json<String>("");
	}

	@ResponseBody
	@RequestMapping(value = "/findUserByName")
	public Json<List<User>> findUserByName(String username)
	{
		Json<List<User>> rst = new Json<List<User>>();
		List<User> usrLst;
		try
		{
			User lu = this.getSessionUser();
			usrLst = authorityMoveService.findUserByName(username, lu);
			rst.OK(usrLst, "");
		}
		catch (Exception e)
		{
			logger.equals(e.getMessage());
			rst.NG("操作失败，请重试！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/save")
	@RightAnnotation(name = "系统管理/授权管理/新增或修改", button = true, resource = "/manage/authority/loadById;/manage/authority/findUserByName")
	public Json<String> save(UserEmpower form)
	{
		Json<String> rst = new Json<String>();
		try
		{
			User lu = this.getSessionUser();
			authorityMoveService.saveForm(form, lu);
			rst.OK("true", "");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			rst.NG(e.getMessage());
		}
		return rst;
	}

}
