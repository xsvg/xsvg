package cc.cnplay.platform.system.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.annotation.RightAnnotation;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.AttachmentService;
import cc.cnplay.platform.service.OrganizationService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.web.controller.AbsController;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value = "/system/organization")
public class OrganizationController extends AbsController
{
	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserService userService;

	@Autowired
	private AttachmentService attachmentService;

	@RequestMapping(value = "/list")
	@RightAnnotation(name = "系统管理/机构管理", component = "platform.system.view.OrganizationPanel", resource = "/system/organization/export*", sort = 80100)
	public @ResponseBody Json<List<Organization>> list(String id)
	{
		User curUser = this.getSessionUser();
		List<Organization> list = organizationService.findByParentId(curUser, id);

		return new Json<List<Organization>>(list);
	}

	@RequestMapping(value = "/listAll")
	public @ResponseBody Json<List<Organization>> listAll(String id)
	{
		List<Organization> list = organizationService.findByParentId(id);

		return new Json<List<Organization>>(list);
	}

	@RequestMapping(value = "/loadOrg")
	public @ResponseBody Json<Organization> loadById(String id)
	{
		Organization org = organizationService.getById(id);
		if (org == null)
		{
			org = new Organization();
		}
		else
		{
			Organization p = organizationService.getById(org.getParentId());
			if (p != null)
			{
				org.setParentName(p.getName());
			}
			org.setChecked(organizationService.existChild(id));
		}
		return new Json<Organization>(org);
	}

	@Ignore
	@RequestMapping(value = "/loadOrgTree")
	@ResponseBody
	public Json<List<Organization>> loadOrgTree(String id, String myId, String[] orgId)
	{
		List<Organization> list = organizationService.findByParentId(id);
		for (int i = list.size() - 1; i >= 0; i--)
		{
			if (list.get(i).getId().equalsIgnoreCase(myId))
			{
				list.remove(i);
			}
		}
		organizationService.checked(orgId, list);
		return new Json<List<Organization>>(list);
	}

	@Ignore
	@RequestMapping(value = "/loadUserOrgTree")
	@ResponseBody
	public Json<List<Organization>> loadUserOrgTree(String id, String myId, String[] orgId)
	{
		User loginUser = this.getSessionUser();
		List<Organization> list = organizationService.findByParentId(loginUser, id);
		for (int i = list.size() - 1; i >= 0; i--)
		{
			if (list.get(i).getId().equalsIgnoreCase(myId))
			{
				list.remove(i);
			}
		}
		organizationService.checked(orgId, list);
		return new Json<List<Organization>>(list);
	}

	@RequestMapping(value = "/save")
	@RightAnnotation(name = "系统管理/机构管理/修改", button = true, sort = 80101, resource = "/system/organization/load*")
	@Description("保存机构")
	public @ResponseBody Json<Organization> save(Organization orgForm)
	{
		Json<Organization> rst = new Json<Organization>();
		try
		{
			orgForm = organizationService.save(orgForm);
			rst.OK(orgForm, "");
		}
		catch (CnplayRuntimeException e)
		{
			logger.error(e);
			rst.NG(e.getMessage());
		}
		catch (Throwable e)
		{
			logger.error(e);
			rst.NG("保存失败，请输入正确的信息");
		}
		return rst;
	}

	@RequestMapping(value = "/remove", produces = "application/json")
	@RightAnnotation(name = "系统管理/机构管理/删除", button = true, sort = 80102)
	@Description("删除机构")
	public @ResponseBody Json<String> remove(String id)
	{
		Json<String> rst = new Json<String>();
		try
		{
			User curUser = this.getSessionUser();
			String errMsg = organizationService.removeLogic(curUser.getId(), id);
			if (errMsg != null)
				rst.NG(errMsg);
			else
				rst.OK("", "机构删除成功!");
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("删除失败，机构信息被引用！");
		}
		return rst;
	}

	@ResponseBody
	@RequestMapping(value = "/findOrgTree")
	public List<Organization> findOrgTree()
	{
		return organizationService.findAll();
	}

	/**
	 * 解析导入数据并界面显示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Throwable
	 */

	@RequestMapping(value = "/loadImportOrg", produces = "application/json")
	@RightAnnotation(name = "系统管理/机构管理/机构导入", button = true, sort = 80103, resource = "/system/organization/loadImportOrgCheck")
	@Description("导入机构")
	public @ResponseBody Json<Boolean> loadImportOrg(HttpServletRequest request, HttpServletResponse response) throws Throwable
	{
		return new Json<Boolean>(true);
	}

	@RequestMapping(value = "/loadImportOrgCheck", produces = "application/json")
	public @ResponseBody String loadImportOrgCheck(HttpServletRequest request, HttpServletResponse response) throws Throwable
	{
		@SuppressWarnings("rawtypes")
		Json rst = new Json<String>();
		try
		{
			rst = organizationService.loadImportOrg(request);
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html; charset=utf-8");
			ObjectMapper om = new ObjectMapper();
			om.writeValue(response.getOutputStream(), rst);
		}
		catch (Exception e)
		{
			logger.error(e);
			rst.NG("机构信息解析失败！");
		}
		return null;
	}

	@RequestMapping(value = "/exportOrg", produces = "application/json")
	@Description("导出机构")
	public @ResponseBody Json<String> exportOrg(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			String orgId = this.getSessionUser().getOrganization().getId();
			List<Map<String, ?>> listMap = organizationService.findReportData(orgId);
			if (listMap.size() <= Constants.expReportMax)
			{
				this.expAttachment(request, response, "机构导出信息", listMap, "/static/report/OrgExportInfo.jrxml");
			}
			else
			{
				writeResponse(response, new CnplayRuntimeException("导出数据行数超长！应 <=" + Constants.expReportMax));
			}
		}
		catch (CnplayRuntimeException e)
		{
			logger.error("导出失败", e);
			writeResponse(response, e);
		}
		catch (Throwable e)
		{
			logger.error("导出失败", e);
			writeResponse(response, new CnplayRuntimeException("导出数据失败，请不要一次导出太多数据！"));
			// rst.NG("导出失败，机构信息被引用！");
		}
		return null;
	}
}
