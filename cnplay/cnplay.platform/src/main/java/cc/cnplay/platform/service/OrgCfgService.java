//平台取参数用接口
package cc.cnplay.platform.service;

import java.util.Map;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.OrganizationConfig;
import cc.cnplay.platform.vo.LoginConf;

public interface OrgCfgService extends GenericService<OrganizationConfig, String>
{
	/**
	 * 本组织没有参数的场合，本级参数->找上级级参数->找默认参数
	 * 
	 * @param orgid
	 * @param name
	 * @return
	 */
	OrganizationConfig findByOrg2Root(String orgid, String name);

	/**
	 * 仅找指定组织所有参数，
	 * 
	 * @param orgId
	 * @return
	 */
	Map<String, OrganizationConfig> findMapByOrgId(String orgId);

	/**
	 * 找组织的配置项
	 * 
	 * @param org
	 * @param name
	 * @return
	 */
	OrganizationConfig findByOrg(Organization org, String name);

	LoginConf getLoginConf(String orgId);

}
