package cc.cnplay.platform.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.util.Converter;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.dao.AreaDao;
import cc.cnplay.platform.dao.OrganizationDao;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.OrganizationService;
import cc.cnplay.platform.service.PluginsService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.util.ExcelImportHelp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class OrganizationServiceImpl extends AbsGenericService<Organization, String> implements OrganizationService
{

	@Resource
	private OrganizationDao orgDao;

	@Resource
	private AreaDao areaDao;

	@Resource
	private UserService userService;

	@Resource
	private PluginsService pluginsService;

	@Override
	public List<Organization> findByParentId(String parentId)
	{
		return orgDao.findByParentId(parentId);
	}

	@Override
	public List<Organization> findByParentId(User user, String parentId)
	{
		List<Organization> list = new ArrayList<Organization>();
		User tmpUser = userService.getById(User.class, user.getId());
		if (StringUtils.isBlank(parentId))
		{
			if (StringUtils.isBlank(tmpUser.getOrganization().getParentId()))
			{
				list = orgDao.findByParentId("");
			}
			else
			{
				list.add(tmpUser.getOrganization());
			}
		}
		else
		{
			list = orgDao.findByParentId(parentId);
		}
		return list;
	}

	@Override
	public void checked(String[] orgId, List<Organization> list)
	{
		if (orgId != null)
		{
			for (Organization org : list)
			{
				if (!org.isChecked())
				{
					for (String sid : orgId)
					{
						if (!org.isChecked())
							org.setChecked(org.getId().equalsIgnoreCase(sid));
					}
				}
			}
		}
	}

	@Override
	public Organization getByLevelCode(String levelCode)
	{

		return orgDao.getByLevelCode(levelCode);
	}

	@Override
	public String removeLogic(String userid, String orgid) throws Exception
	{
		String errMsg = null;
		List<Organization> listOrgChild = orgDao.findByParentId(orgid);
		if (listOrgChild.size() > 0)
		{
			return errMsg = "该机构存在子机构，不能删除！";
		}
		// 存在用户，不能删除
		if (existUserByOrg(orgid))
		{
			return errMsg = "机构下存在用户，不能删除！";
		}
		try
		{
			Organization org = dao().getById(Organization.class, orgid);
			// org.setStatus(Status.Delete);
			// dao().save(org);
			dao().remove(org);
		}
		catch (Exception ex)
		{
			errMsg = "删除失败，些机构被其它功能引用！";
		}
		return errMsg;
	}

	@Override
	public boolean existUserByOrg(String orgid)
	{
		Search search = new Search(User.class);
		search.addFilterNotEqual("status", Status.Delete);
		search.addFilterEqual("organization.id", orgid);

		@SuppressWarnings("unchecked")
		List<User> userLst = dao().search(search);
		return !userLst.isEmpty();
	}

	@Override
	public Organization saveSync(Organization orgForm)
	{
		Organization org = dao().getById(Organization.class, orgForm.getId());
		if (org != null)
		{
			if (StringUtils.isNotEmpty(orgForm.getId()) && orgForm.getId().equalsIgnoreCase(orgForm.getParentId()))
			{
				throw new CnplayRuntimeException("上级机构不能是当前用户机构！");
			}
			if (StringUtils.isEmpty(org.getLevelCode()) || !orgForm.getParentId().equalsIgnoreCase(org.getParentId()))
			{
				String oldLevelCode = org.getLevelCode();
				org.setLevelCode(getNextLevelCodeByParent(orgForm.getParentId()));
				List<Organization> list = findByLeftLikeLevelCode(oldLevelCode);
				for (Organization o : list)
				{
					if (o.getLevelCode() != null)
						o.setLevelCode(o.getLevelCode().replaceFirst(oldLevelCode, org.getLevelCode()));
				}
				dao().saveAll(list);
			}
		}
		else
		{
			org = new Organization();
			org.setLevelCode(getNextLevelCodeByParent(orgForm.getParentId()));
		}
		if (StringUtils.isBlank(orgForm.getParentId()))
		{
			Organization root = orgDao.getRoot();
			if (root != null && !root.getId().equals(orgForm.getId()))
			{
				throw new CnplayRuntimeException("顶层机构已存在！请指定一个上级机构!");
			}
		}
		Organization manage = orgDao.getById(orgForm.getManagerId());
		if (orgDao.managerExitMe(org, manage))
		{
			throw new CnplayRuntimeException("管理机构的管理机构不能是本机构！");
		}
		try
		{
			orgForm.setLevelCode(org.getLevelCode());
			BeanUtils.copyProperties(org, orgForm);
			if (manage != null && manage.getId().equalsIgnoreCase(org.getId()))
			{
				org.setManager(null);
			}
			else
			{
				org.setManager(manage);
			}
			dao().save(org);
		}
		catch (Throwable ex)
		{
			logger.error("", ex);
			throw new CnplayRuntimeException("保存失败，请输入正确的信息！");
		}
		return org;
	}

	@Override
	public Organization save(Organization orgForm)
	{
		if (StringUtils.isNotBlank(orgForm.getCode()))
		{
			String regEx = "(^[A-Za-z0-9]+$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(orgForm.getCode());
			boolean rs = mat.find();
			if (!rs)
				throw new CnplayRuntimeException("请输入由数字或字母组成的机构编码！");

			if (orgForm.getCode().length() > 12)
				throw new CnplayRuntimeException("机构编码长度最长不能超过12位！");
		}
		if (StringUtils.isNotBlank(orgForm.getMemo()) && orgForm.getMemo().length() > 512)
		{
			throw new CnplayRuntimeException("备注内容不能输入超过512个字符！");
		}
		if (orgDao.existCode(orgForm))
		{
			throw new CnplayRuntimeException("该机构编码(" + orgForm.getCode() + ")已存在！");
		}
		if (orgDao.existName(orgForm))
		{
			throw new CnplayRuntimeException("该机构名称(" + orgForm.getName() + ")已存在！");
		}
		Organization org = dao().getById(Organization.class, orgForm.getId());
		if (org != null)
		{
			if (StringUtils.isNotEmpty(orgForm.getId()) && orgForm.getId().equalsIgnoreCase(orgForm.getParentId()))
			{
				throw new CnplayRuntimeException("上级机构不能是当前用户机构！");
			}
			if (StringUtils.isEmpty(org.getLevelCode()) || !orgForm.getParentId().equalsIgnoreCase(org.getParentId()))
			{
				String oldLevelCode = org.getLevelCode();
				org.setLevelCode(getNextLevelCodeByParent(orgForm.getParentId()));
				List<Organization> list = findByLeftLikeLevelCode(oldLevelCode);
				for (Organization o : list)
				{
					if (o.getLevelCode() != null)
						o.setLevelCode(o.getLevelCode().replaceFirst(oldLevelCode, org.getLevelCode()));
				}
				dao().saveAll(list);
			}
		}
		else
		{
			org = new Organization();
			org.setLevelCode(getNextLevelCodeByParent(orgForm.getParentId()));
		}
		if (StringUtils.isBlank(orgForm.getParentId()))
		{
			Organization root = orgDao.getRoot();
			if (root != null && !root.getId().equals(orgForm.getId()))
			{
				throw new CnplayRuntimeException("顶层机构已存在！请指定一个上级机构!");
			}
		}
		Organization manage = orgDao.getById(orgForm.getManagerId());
		if (orgDao.managerExitMe(org, manage))
		{
			throw new CnplayRuntimeException("管理机构的管理机构不能是本机构！");
		}
		try
		{
			orgForm.setLevelCode(org.getLevelCode());
			orgForm.setSyncId(org.getSyncId());
			BeanUtils.copyProperties(org, orgForm);
			if (manage != null && manage.getId().equalsIgnoreCase(org.getId()))
			{
				org.setManager(null);
			}
			else
			{
				org.setManager(manage);
			}
			dao().save(org);
		}
		catch (Throwable ex)
		{
			logger.error("", ex);
			throw new CnplayRuntimeException("保存失败，请输入正确的信息！");
		}
		return org;
	}

	/**
	 * 生成下一个机构层次码 每级5位长，从10000开始， 一级10000 二级1000010000 三级100001000010000，能从当前层次码确定上级层次码
	 * @param parentId
	 * @return
	 */
	private String getNextLevelCodeByParent(String parentId)
	{
		String nextLevelCode = "";
		Search search = new Search(Organization.class);
		if (StringUtils.isEmpty(parentId))
		{
			// and (p.parentId IS NULL OR p.parentId = ?1 OR p.parentId = '0')
			search.addFilter(Filter.or(Filter.isEmpty("parentId"), Filter.or(Filter.equal("parentId", "0"))));
		}
		else
		{
			search.addFilterEqual("parentId", parentId);
		}
		search.addSortDesc("levelCode");
		search.setMaxResults(1);
		Organization org = (Organization) dao().searchUnique(search);
		Organization parent = dao().getById(Organization.class, parentId);
		String parentLevelCode = Organization.LevelCode;
		if (org == null)
		{
			if (parent == null)
			{
				nextLevelCode = Organization.LevelCode;
			}
			else
			{
				parentLevelCode = parent.getLevelCode();
				nextLevelCode = parentLevelCode + Organization.LevelCode;
			}
		}
		else
		{
			String lastLevelCode = org.getLevelCode();
			if (StringUtils.isEmpty(lastLevelCode))
			{
				parent = dao().getById(Organization.class, parentId);
				if (parent == null)
				{
					lastLevelCode = Organization.LevelCode;
				}
				else
				{
					parentLevelCode = parent.getLevelCode();
					lastLevelCode = parentLevelCode + Organization.LevelCode;
				}
			}
			nextLevelCode = Converter.nextNumber(lastLevelCode);
		}
		while (exitLevelCode(nextLevelCode))
		{
			nextLevelCode = Converter.nextNumber(nextLevelCode);
		}
		return nextLevelCode;
	}

	/**
	 * 查所有以levelCode开关的组织机构
	 * 
	 * @param levelCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Organization> findByLeftLikeLevelCode(String levelCode)
	{
		Search search = new Search(Organization.class);
		search.addFilterLike("levelCode", levelCode + "%");
		search.addSortAsc("levelCode");
		return dao().search(search);
	}

	private boolean exitLevelCode(String levelCode)
	{
		Search search = new Search(Organization.class);
		search.addFilterEqual("levelCode", levelCode);
		search.setMaxResults(1);
		Organization org = (Organization) dao().searchUnique(search);
		return org != null;
	}

	@Override
	public String getLevelCodeById(String id)
	{
		return this.orgDao.getLevelCodeById(id);
	}

	@Override
	public Organization getRoot()
	{
		return this.orgDao.getRoot();
	}

	/**
	 * 解析显示不能导入的机构数据，可以成功保存的直接保存数据库
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Json<List<Organization>> loadImportOrg(HttpServletRequest request)
	{
		Json<List<Organization>> json = new Json();
		List<Organization> sucList = new ArrayList<Organization>();
		json.setRows(sucList);
		List<Organization> errorList = new ArrayList<Organization>();
		List<String> strList = new ArrayList<String>();
		String errorMsg = "";
		try
		{
			// 创建一个通用的多部分解析器.
			CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			// 设置编码
			commonsMultipartResolver.setDefaultEncoding("utf-8");
			// 判断 request 是否有文件上传,即多部分请求
			if (commonsMultipartResolver.isMultipart(request))
			{
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<MultipartFile> iterator = multiRequest.getFileMap().values().iterator();
				try
				{
					// 读取并解析导入文件
					while (iterator.hasNext())
					{
						MultipartFile item = iterator.next();
						// 解析Excel文件
						strList = ExcelImportHelp.getTargetList(item);
					}
				}
				catch (Exception e)
				{
					logger.error("导入过程出现错误", e);
					json.setSuccess(true);
					json.setMsg("非法的导入文件或格式错误");
					return json;
				}
			}
			if (strList.size() == 0)
			{
				json.setRows(errorList);
				json.setMsg("未找到机构信息，请按格式进行填写！");
				json.setSuccess(true);
				return json;
			}
			for (String str : strList)
			{
				Organization org = new Organization();
				if (str == null || str.equals(""))
				{
					continue;
				}
				String[] strArray = str.split(",");

				if (strArray.length >= 2)
				{
					if (strArray[1].equals("机构信息表") || strArray[1].equals("机构编号"))
					{
						continue;
					}
					org.setCode(strArray[1].trim());// 机构编号
				}
				if (strArray.length >= 3)
					org.setName(strArray[2].trim());// 机构名称
				if (strArray.length >= 4)
					org.setParentName(strArray[3].trim());// 上级机构
				if (strArray.length >= 5)
					org.setMemo(strArray[4].trim());// 备注
				// 校验机构信息
				errorMsg = checkOrgInfo(org, strList);
				if (StringUtils.isBlank(errorMsg))
				{
					// 正确数据直接保存
					sucList.add(org);
				}
				else
				{
					// 错误数据 界面显示错误的消息 暂时征用CreateUsername 显示errorMsg
					org.setCreateUsername(errorMsg);
					errorList.add(org);
				}
			}
			// 保存正确的数据
			for (Organization log : sucList)
			{
				Organization logSave = dao().find(Organization.class, log.getId());
				if (logSave == null)
				{
					saveOrgDataList(log, sucList);
				}
			}
			// 回传界面失败的数据
			if (errorList.size() > 0)
			{
				json.setRows(errorList);
				json.setMsg("机构信息导入失败，请查看详细错误信息！");
				json.setSuccess(true);
			}
			else
			{
				json.setRows(errorList);
				json.setMsg("机构信息导入成功！");
				json.setSuccess(true);
			}

		}
		catch (Exception ex)
		{
			logger.error("解析过程出现错误", ex);
			json.setMsg("机构信息导入失败，请确认！");
			json.setSuccess(true);
			return json;
		}
		return json;
	}

	private String checkOrgInfo(Organization org, List<String> strList)
	{
		String errorMsg = "";
		boolean parOrgflag = false;// 无上级机构
		boolean codeflag = false;// 无重复机构编号
		boolean nameflag = false;// 无重复机构名称
		if (StringUtils.isNotBlank(org.getCode()))
		{
			String regEx = "(^[A-Za-z0-9]+$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(org.getCode());
			boolean rs = mat.find();
			if (!rs)
				errorMsg = errorMsg + "机构编码不符合规范（由数字或字母组成!";
			if (org.getCode().length() > 12)
				errorMsg = errorMsg + "机构编码长度最长不能超过12位！";
		}
		else
		{
			errorMsg = errorMsg + "机构编号不能为空！";
		}
		if (!StringUtils.isNotBlank(org.getName()))
			errorMsg = errorMsg + "机构名称不能为空！";
		if (!StringUtils.isNotBlank(org.getParentName()))
			errorMsg = errorMsg + "上级机构编号不能为空！";
		if (StringUtils.isNotBlank(org.getMemo()) && org.getMemo().length() > 512)
			errorMsg = errorMsg + "备注内容不能输入超过512个字符！";

		// 校验解析list数据中是否有重复的数据
		for (String str : strList)
		{
			if (str == null || str.equals(""))
			{
				continue;
			}
			String[] strArray = str.split(",");
			if (strArray[1].equals("机构信息表") || strArray[1].equals("机构编号"))
			{
				continue;
			}
			if (strArray[1].equals(org.getParentName()))
			{
				parOrgflag = true;
			}
			if (strArray[1].equals(org.getCode()))
			{
				codeflag = true;
			}
			if (strArray[1].equals(org.getName()))
			{
				nameflag = true;
			}
		}
		if (codeflag && orgDao.existCode(org))
		{
			errorMsg = errorMsg + "该机构编码(" + org.getCode() + ")已存在！";
		}
		if (nameflag && orgDao.existName(org))
		{
			errorMsg = errorMsg + "该机构名称(" + org.getName() + ")已存在！";
		}
		// 上级机构校验
		List<Organization> existParOrg = dao().findByField(Organization.class, "code", org.getParentName());
		if (!parOrgflag && existParOrg.size() <= 0)
		{
			errorMsg = errorMsg + "上级机构不存在，上级机构名称：" + org.getParentName() + "本机机构名称：" + org.getName();
		}
		return errorMsg;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String saveImportOrg(String json)
	{
		String errorMsg = "";
		try
		{
			List<Organization> orgList = new ArrayList<Organization>();
			// 解析JSON数据--list列表
			ObjectMapper om = new ObjectMapper();
			@SuppressWarnings("unchecked")
			List<LinkedHashMap<String, Object>> list = om.readValue(json, List.class);
			for (LinkedHashMap<String, Object> map : list)
			{
				Iterator it = map.entrySet().iterator();
				Organization org = new Organization();
				while (it.hasNext())
				{
					Map.Entry entry = (Map.Entry) it.next();
					// 机构ID
					if (entry.getKey().equals("id"))
					{
						org.setId(entry.getValue().toString());
					}
					// 机构编号
					if (entry.getKey().equals("code"))
					{
						org.setCode(entry.getValue().toString());
					}
					// 机构名称
					if (entry.getKey().equals("name"))
					{
						org.setName(entry.getValue().toString());
					}
					// 上级机构名称
					if (entry.getKey().equals("parentName"))
					{
						org.setParentName(entry.getValue().toString());
					}
					if (entry.getKey().equals("memo"))
					{
						org.setMemo(entry.getValue().toString());
					}
				}
				orgList.add(org);
			}
			// 校验上级并保存数据
			for (Organization log : orgList)
			{
				Organization logSave = dao().find(Organization.class, log.getId());
				if (logSave == null)
				{
					saveOrgDataList(log, orgList);
				}
			}
			// 排序 编号从小到大
			/*
			 * Collections.sort(orgList, new Comparator<Organization>() { public int compare(Organization arg0, Organization arg1) { return arg0.getCode().compareTo(arg1.getCode()); } });
			 */
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		return errorMsg;
	}

	// 取上级ID 数据库没有list找上级机构并保存
	public void saveOrgDataList(Organization org, List<Organization> orgList)
	{
		if (org != null)
		{
			List<Organization> parentOrgList = dao().findByField(Organization.class, "code", org.getParentName());
			if (parentOrgList.size() > 0)
			{
				// 上级机构存在，直接保存
				org.setParentId(parentOrgList.get(0).getId());
				save(org);
				// orgList.remove(org);
			}
			else
			{
				// 上级机构不存在，list中查找 id赋值
				for (Organization og : orgList)
				{
					if (og.getCode().equals(org.getParentName()))
					{
						saveOrgDataList(og, orgList);
						org.setParentId(og.getId());
						save(org);
						continue;
					}
				}
			}
		}
	}

	@Override
	public List<Map<String, ?>> findReportData(String orgId)
	{
		Search search = new Search(Organization.class);
		search.addFilterLike("levelCode", orgDao.getLevelCodeById(orgId) + "%");
		search.addSortAsc("levelCode");
		@SuppressWarnings("unchecked")
		List<Organization> list = dao().search(search);

		List<String> ids = new ArrayList<String>();
		for (Organization org : list)
		{
			if (StringUtils.isNoneBlank(org.getParentId()))
			{
				ids.add(org.getId());
			}
		}
		search = new Search(Organization.class);
		search.addFilterIn("id", ids);
		search.addSortAsc("levelCode");
		@SuppressWarnings("unchecked")
		List<Organization> plist = dao().search(search);
		List<Map<String, ?>> listMap = new ArrayList<Map<String, ?>>();
		for (Organization org : list)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("code", org.getCode());
			m.put("name", org.getName());
			m.put("parentCode", "");
			m.put("parentName", "");
			for (Organization p : plist)
			{
				if (p.getId().equals(org.getParentId()))
				{
					m.put("parentCode", p.getCode());
					m.put("parentName", p.getName());
				}
			}
			m.put("memo", org.getMemo());
			listMap.add(m);
		}
		return listMap;
	}

	@Override
	public boolean isRoot(Organization org)
	{
		return org != null && org.equals(orgDao.getRoot());
	}

	@Override
	public String getAmkCode(Organization org)
	{
		return orgDao.getAmkCode(org);
	}

	@Override
	public Organization getByCode(String code)
	{
		return orgDao.getByCode(code);
	}

	@Override
	public Organization getBySyncId(String syncId)
	{
		return orgDao.getBySyncId(syncId);
	}

	@Override
	public boolean existChild(String id)
	{
		return orgDao.existChild(id);
	}
}
