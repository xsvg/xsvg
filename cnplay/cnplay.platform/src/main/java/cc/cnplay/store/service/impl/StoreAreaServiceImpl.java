package cc.cnplay.store.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cc.cnplay.core.CnplayRuntimeException;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.core.util.Converter;
import cc.cnplay.platform.dao.AreaDao;
import cc.cnplay.platform.service.PluginsService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.store.dao.StoreAreaDao;
import cc.cnplay.store.domain.StoreArea;
import cc.cnplay.store.service.StoreAreaService;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class StoreAreaServiceImpl extends AbsGenericService<StoreArea, String>
		implements StoreAreaService {

	@Resource
	private StoreAreaDao orgDao;

	@Resource
	private AreaDao areaDao;

	@Resource
	private UserService userService;

	@Resource
	private PluginsService pluginsService;

	@Override
	public List<StoreArea> findByParentId(String orgId, String parentId) {
		return orgDao.findByParentId(orgId, parentId);
	}

	@Override
	public StoreArea getByLevelCode(String orgId, String levelCode) {

		return orgDao.getByLevelCode(orgId, levelCode);
	}

	@Override
	public StoreArea save(StoreArea orgForm) {
		if (StringUtils.isNotBlank(orgForm.getCode())) {
			String regEx = "(^[A-Za-z0-9]+$)";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(orgForm.getCode());
			boolean rs = mat.find();
			if (!rs)
				throw new CnplayRuntimeException("请输入由数字或字母组成的机构编码！");

			if (orgForm.getCode().length() > 12)
				throw new CnplayRuntimeException("机构编码长度最长不能超过12位！");
		}
		if (StringUtils.isNotBlank(orgForm.getMemo())
				&& orgForm.getMemo().length() > 512) {
			throw new CnplayRuntimeException("备注内容不能输入超过512个字符！");
		}
		if (orgDao.existCode(orgForm)) {
			throw new CnplayRuntimeException("该机构编码(" + orgForm.getCode()
					+ ")已存在！");
		}
		if (orgDao.existName(orgForm)) {
			throw new CnplayRuntimeException("该机构名称(" + orgForm.getName()
					+ ")已存在！");
		}
		StoreArea org = dao().getById(StoreArea.class, orgForm.getId());
		if (org != null) {
			if (StringUtils.isNotEmpty(orgForm.getId())
					&& orgForm.getId().equalsIgnoreCase(orgForm.getParentId())) {
				throw new CnplayRuntimeException("上级机构不能是当前用户机构！");
			}
			if (StringUtils.isEmpty(org.getLevelCode())
					|| !orgForm.getParentId().equalsIgnoreCase(
							org.getParentId())) {
				String oldLevelCode = org.getLevelCode();
				org.setLevelCode(getNextLevelCodeByParent(orgForm.getParentId()));
				List<StoreArea> list = findByLeftLikeLevelCode(oldLevelCode);
				for (StoreArea o : list) {
					if (o.getLevelCode() != null)
						o.setLevelCode(o.getLevelCode().replaceFirst(
								oldLevelCode, org.getLevelCode()));
				}
				dao().saveAll(list);
			}
		} else {
			org = new StoreArea();
			org.setLevelCode(getNextLevelCodeByParent(orgForm.getParentId()));
		}
//		if (StringUtils.isBlank(orgForm.getParentId())) {
//			StoreArea root = orgDao.getRoot(orgForm.getOrgId());
//			if (root != null && !root.getId().equals(orgForm.getId())) {
//				throw new CnplayRuntimeException("顶层机构已存在！请指定一个上级机构!");
//			}
//		}
		try {
			orgForm.setLevelCode(org.getLevelCode());
			BeanUtils.copyProperties(org, orgForm);
			dao().save(org);
		} catch (Throwable ex) {
			logger.error("", ex);
			throw new CnplayRuntimeException("保存失败，请输入正确的信息！");
		}
		return org;
	}

	/**
	 * 生成下一个机构层次码 每级5位长，从10000开始， 一级10000 二级1000010000
	 * 三级100001000010000，能从当前层次码确定上级层次码
	 * 
	 * @param parentId
	 * @return
	 */
	private String getNextLevelCodeByParent(String parentId) {
		String nextLevelCode = "";
		Search search = new Search(StoreArea.class);
		if (StringUtils.isEmpty(parentId)) {
			// and (p.parentId IS NULL OR p.parentId = ?1 OR p.parentId = '0')
			search.addFilter(Filter.or(Filter.isEmpty("parentId"),
					Filter.or(Filter.equal("parentId", "0"))));
		} else {
			search.addFilterEqual("parentId", parentId);
		}
		search.addSortDesc("levelCode");
		search.setMaxResults(1);
		StoreArea org = (StoreArea) dao().searchUnique(search);
		StoreArea parent = dao().getById(StoreArea.class, parentId);
		String parentLevelCode = StoreArea.LevelCode;
		if (org == null) {
			if (parent == null) {
				nextLevelCode = StoreArea.LevelCode;
			} else {
				parentLevelCode = parent.getLevelCode();
				nextLevelCode = parentLevelCode + StoreArea.LevelCode;
			}
		} else {
			String lastLevelCode = org.getLevelCode();
			if (StringUtils.isEmpty(lastLevelCode)) {
				parent = dao().getById(StoreArea.class, parentId);
				if (parent == null) {
					lastLevelCode = StoreArea.LevelCode;
				} else {
					parentLevelCode = parent.getLevelCode();
					lastLevelCode = parentLevelCode + StoreArea.LevelCode;
				}
			}
			nextLevelCode = Converter.nextNumber(lastLevelCode);
		}
		while (exitLevelCode(nextLevelCode)) {
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
	private List<StoreArea> findByLeftLikeLevelCode(String levelCode) {
		Search search = new Search(StoreArea.class);
		search.addFilterLike("levelCode", levelCode + "%");
		search.addSortAsc("levelCode");
		return dao().search(search);
	}

	private boolean exitLevelCode(String levelCode) {
		Search search = new Search(StoreArea.class);
		search.addFilterEqual("levelCode", levelCode);
		search.setMaxResults(1);
		StoreArea org = (StoreArea) dao().searchUnique(search);
		return org != null;
	}

	@Override
	public String getLevelCodeById(String id) {
		return this.orgDao.getLevelCodeById(id);
	}

	@Override
	public StoreArea getRoot(String orgId) {
		return this.orgDao.getRoot(orgId);
	}

	@Override
	public boolean isRoot(StoreArea org) {
		return org != null && org.equals(orgDao.getRoot(org.getOrgId()));
	}

	@Override
	public StoreArea getByCode(String orgId, String code) {
		return orgDao.getByCode(orgId, code);
	}

	@Override
	public boolean existChild(String orgId, String id) {
		return orgDao.existChild(orgId, id);
	}

	@Override
	public void checked(String[] areaId, List<StoreArea> list) {
		if (areaId != null) {
			for (StoreArea org : list) {
				if (!org.isChecked()) {
					for (String sid : areaId) {
						if (!org.isChecked())
							org.setChecked(org.getId().equalsIgnoreCase(sid));
					}
				}
			}
		}
	}
}
