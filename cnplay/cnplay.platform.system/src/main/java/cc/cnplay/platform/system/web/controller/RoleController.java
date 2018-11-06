package cc.cnplay.platform.system.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.domain.Identity;
import cc.cnplay.core.vo.DataGrid;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Right;
import cc.cnplay.platform.domain.Role;
import cc.cnplay.platform.service.OrganizationService;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.RoleService;
import cc.cnplay.platform.vo.RightVO;
import cc.cnplay.platform.web.controller.AbsController;

@Controller
@RequestMapping(value = "/system/role")
public class RoleController extends AbsController
{
	@Autowired
	private RoleService roleService;

	@Autowired
	private RightService rightService;

	@Autowired
	private OrganizationService orgService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统管理/角色管理", component = "platform.system.view.RolePanel", sort = 80200)
	public @ResponseBody DataGrid<Role> page(String name)
	{
		Organization org = this.getSessionUser().getOrganization();
		return roleService.findPageLikeName(orgService.isRoot(org), name, this.getPage(), this.getPageSize());
	}

	@RequestMapping(value = "/load")
	public @ResponseBody Json<Role> loadById(String id)
	{
		Role role = roleService.getById(id);
		if (role == null)
		{
			role = new Role();
		}
		return new Json<Role>(role);
	}

	@RequestMapping(value = "/loadRight")
	public @ResponseBody Json<List<RightVO>> loadRight(String roleId)
	{
		Json<List<RightVO>> result = new Json<List<RightVO>>();
		List<Right> rightList = rightService.findByParentId(null);
		List<RightVO> tcmList = roleService.loadCheckRightTree(rightList, roleId);
		result.setRows(tcmList);
		result.setSuccess(true);
		return result;
	}

	@RequestMapping(value = "/save")
	@RightAnnotation(name = "系统管理/角色管理/新增修改", button = true, sort = 80201, resource = "/system/role/load*")
	@Description("保存角色")
	public @ResponseBody Json<Role> save(Role role, String[] rightIds)
	{
		Json<Role> rst = new Json<Role>();
		String roleid = role.getId();
		try
		{
			if (roleService.existRoleName(roleid, role.getName()))
			{
				rst.NG("该角色名称(" + role.getName() + ")已存在！");
				return rst;
			}
			String validate = rightService.validateRightReject(rightIds);
			if (StringUtils.isNotEmpty(validate))
			{
				rst.NG(validate);
				return rst;
			}
			if (StringUtils.isBlank(role.getId()))
			{
				role.setId(Identity.randomID());
			}
			Role roleOld = roleService.getById(role.getId());
			if (roleOld == null)
			{
				Organization org = this.getSessionUser().getOrganization();
				role.setOrg(org);
			}
			roleService.saveRoleAndRight(role, rightIds);
			rst.OK(role, "保存成功！");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("保存失败，请检查输入项是否正确！");
		}
		return rst;
	}

	@RequestMapping(value = "/remove", produces = "application/json")
	@RightAnnotation(name = "系统管理/角色管理/删除", button = true, sort = 80202)
	@Description("删除角色")
	@ResponseBody
	public Json<String> remove(String id)
	{
		Json<String> rst = new Json<String>();
		try
		{
			roleService.removeByIds(id.split(separator));
			rst.OK("", "角色删除成功!");
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage());
			rst.NG("删除失败");
		}
		return rst;
	}
}
