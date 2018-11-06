package cc.cnplay.platform.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.platform.dao.AreaDao;
import cc.cnplay.platform.dao.OrganizationDao;
import cc.cnplay.platform.domain.Area;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.service.AreaService;

import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class AreaServiceImpl extends AbsGenericService<Area, String> implements AreaService
{

	@Resource
	private AreaDao areaDao;
	@Resource
	private OrganizationDao orgDao;

	@Override
	public List<Area> findByParentId(String parentId)
	{
		return areaDao.findByParentId(parentId);
	}

	@Override
	public Area getByLevelCode(String levelCode)
	{

		return areaDao.getByLevelCode(levelCode);
	}

	@Override
	public void removeLogic(String areaId) throws CnplayRuntimeException
	{
		List<Area> list = areaDao.findByParentId(areaId);
		if (list.size() > 0)
		{
			throw new CnplayRuntimeException("该机构存在子机构，不能删除！");
		}
		try
		{
			areaDao.removeById(areaId);
		}
		catch (Throwable ex)
		{
			throw new CnplayRuntimeException("删除失败，些机构被其它功能引用！");
		}
	}

	public boolean existCode(String code)
	{
		Search search = new Search(Area.class);
		search.addFilterEqual("code", code);
		search.addFilterEqual("status", Status.Normal);
		@SuppressWarnings("unchecked")
		List<Area> lst = areaDao.search(search);
		return !lst.isEmpty();
	}

	@Override
	public Area save(Area form)
	{
		if (StringUtils.isNotEmpty(form.getCode()))
		{
			String regEx = "(^[A-Za-z0-9]+$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(form.getCode());
			if (!mat.find())
			{
				throw new CnplayRuntimeException("请输入由数字或字母组成的机构编码！");
			}
		}
		if (StringUtils.isEmpty(form.getCode()) || form.getCode().length() > 12)
		{
			throw new CnplayRuntimeException("机构编码长度最长不能超过12位！");
		}
		if (StringUtils.isNotEmpty(form.getMemo()) && form.getMemo().length() > 512)
		{
			throw new CnplayRuntimeException("备注内容不能输入超过512个字符！");
		}
		if (form.getId().equalsIgnoreCase(form.getParentId()))
		{
			throw new CnplayRuntimeException("上级机构不能是当前机构！");
		}
		if (areaDao.exitCode(form))
		{
			throw new CnplayRuntimeException("该机构编码(" + form.getCode() + ")已存在！");
		}
		if (areaDao.exitName(form))
		{
			throw new CnplayRuntimeException("该机构名称(" + form.getName() + ")已存在！");
		}
		if (StringUtils.isBlank(form.getParentId()))
		{
			if (!isRoot(form))
			{
				throw new CnplayRuntimeException("请指定上级机构!");
			}
		}
		Area org = areaDao.getById(form.getId());
		if (org != null)
		{
			form.setOrgId(org.getOrgId());
			if (StringUtils.isNotEmpty(form.getParentId()) && !form.getParentId().equalsIgnoreCase(org.getParentId()))
			{
				String oldLevelCode = org.getLevelCode();
				org.setLevelCode(areaDao.getNextLevelCodeByParent(form.getParentId()));
				List<Area> list = areaDao.findByStartsWithLevelCode(oldLevelCode);
				for (Area o : list)
				{
					if (o.getLevelCode() != null)
						o.setLevelCode(o.getLevelCode().replaceFirst(oldLevelCode, org.getLevelCode()));
				}
				dao().saveAll(list);
			}
			form.setLevelCode(org.getLevelCode());
		}
		else
		{
			form.setLevelCode(areaDao.getNextLevelCodeByParent(form.getParentId()));
		}
		try
		{
			if (StringUtils.isEmpty(form.getOrgId()))
			{
				form.setOrgId(orgDao.getRoot().getId());
			}
			areaDao.save(form);
		}
		catch (Throwable ex)
		{
			logger.error("", ex);
			throw new CnplayRuntimeException("保存失败，请输入正确的信息！");
		}
		return form;
	}

	@Override
	public String getLevelCodeById(String id)
	{
		return this.areaDao.getLevelCodeById(id);
	}

	@Override
	public Area getRoot()
	{
		return this.areaDao.getRoot();
	}

	@Override
	public boolean isRoot(Area org)
	{
		return org != null && org.equals(areaDao.getRoot());
	}

	@Override
	public boolean existRoot()
	{
		return areaDao.getRoot() != null;
	}

	@Override
	public void checked(String[] areaId, List<Area> list)
	{
		if (areaId != null)
		{
			for (Area org : list)
			{
				if (!org.isChecked())
				{
					for (String sid : areaId)
					{
						if (!org.isChecked())
							org.setChecked(org.getId().equalsIgnoreCase(sid));
					}
				}
			}
		}

	}
}
