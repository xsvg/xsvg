package cc.cnplay.platform.service.impl;

import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.platform.dao.OrgConfDao;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.OrganizationConfig;
import cc.cnplay.platform.service.OrgCfgService;
import cc.cnplay.platform.vo.LoginConf;

@Service
@Transactional
public class OrgCfgServiceImpl extends AbsGenericService<OrganizationConfig, String> implements OrgCfgService
{

	@Resource
	private OrgConfDao orgConfDao;

	@Override
	public OrganizationConfig findByOrg(Organization org, String name)
	{
		return orgConfDao.findByOrg(org, name);
	}

	@Override
	public OrganizationConfig findByOrg2Root(String orgid, String name)
	{
		return orgConfDao.findByOrg2Root(orgid, name);
	}

	@Override
	public Map<String, OrganizationConfig> findMapByOrgId(String orgId)
	{
		return orgConfDao.findMapByOrgId(orgId);
	}

	@Override
	public LoginConf getLoginConf(String orgId)
	{
		return orgConfDao.getLoginConf(orgId);
	}

}
