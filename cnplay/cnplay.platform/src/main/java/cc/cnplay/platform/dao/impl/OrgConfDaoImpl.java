package cc.cnplay.platform.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.core.util.Converter;
import cc.cnplay.platform.dao.OrgConfDao;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.OrganizationConfig;
import cc.cnplay.platform.vo.LoginConf;

import com.googlecode.genericdao.search.Search;

@Repository
public class OrgConfDaoImpl extends GenericDaoImpl<OrganizationConfig, String> implements OrgConfDao
{

	@Override
	public OrganizationConfig findByOrg(Organization org, String name)
	{
		if (StringUtils.isBlank(name))
		{
			return null;
		}
		Search search = new Search(OrganizationConfig.class);
		search.addFilterEqual("organization.id", org.getId());
		search.addFilterEqual("name", name);
		search.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<OrganizationConfig> lst = this.search(search);
		if (lst.isEmpty())
		{
			return null;
		}
		else
		{
			return lst.get(0);
		}
		// return (OrganizationConfig) _searchUnique(search);
	}

	@Override
	public LoginConf getLoginConf(String orgId)
	{
		LoginConf conf = new LoginConf();

		OrganizationConfig of = this.findByOrg2Root(orgId, LoginConf.LoginByWeb);
		if (of != null)
		{
			int loginByWeb = Converter.parseInt(of.getValue());
			if (loginByWeb == 0)
			{
				loginByWeb = 1;
			}
			conf.setVerifyPwd((loginByWeb & 1) == 1);
			conf.setVerifyFinger((loginByWeb & 2) == 2);
		}
		if (conf.isVerifyFinger())
		{
			of = this.findByOrg2Root(orgId, LoginConf.webFingerType);
			if (of != null)
				conf.setFingerDev(Converter.parseInt(of.getValue()));
		}
		else
		{
			conf.setVerifyPwd(true);
		}
		return conf;
	}

	@Override
	public OrganizationConfig findByOrg2Root(String orgid, String name)
	{
		if (StringUtils.isEmpty(orgid) || StringUtils.isEmpty(name))
		{
			return null;
		}
		OrganizationConfig oc = null;
		String parentId = orgid;
		while (oc == null)
		{
			Organization org = getById(Organization.class, parentId);
			if (org != null)
			{
				oc = findByOrg(org, name);
				parentId = org.getParentId();
			}
			else
			{
				return oc;
			}
		}
		return oc;
	}

	@Override
	public Map<String, OrganizationConfig> findMapByOrgId(String orgId)
	{
		Search search = new Search(OrganizationConfig.class);
		search.addFilterEqual("organization.id", orgId);
		@SuppressWarnings("unchecked")
		List<OrganizationConfig> lst = search(search);// 没有数据时为[]
		Map<String, OrganizationConfig> rst = new HashMap<String, OrganizationConfig>();
		if (lst != null)
		{
			for (OrganizationConfig oc : lst)
			{
				rst.put(oc.getName(), oc);
			}
		}
		return rst;
	}

	@Override
	public List<OrganizationConfig> findByOrg(String orgId, Collection<String> fields)
	{
		Search search = new Search(OrganizationConfig.class);
		search.addFilterEqual("organization.id", orgId);
		search.addFilterIn("name", fields);
		@SuppressWarnings("unchecked")
		List<OrganizationConfig> list = search(search);
		return list;
	}

}
