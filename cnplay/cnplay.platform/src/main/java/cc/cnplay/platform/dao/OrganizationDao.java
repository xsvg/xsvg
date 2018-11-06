package cc.cnplay.platform.dao;

import java.util.List;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.dao.GenericDao;
import cc.cnplay.platform.domain.Organization;

public interface OrganizationDao extends GenericDao<Organization, String>
{
	Organization getByLevelCode(String levelCode);

	String getLevelCodeById(String id);

	/**
	 * 取根据机构
	 * 
	 * @return
	 */
	Organization getRoot();

	List<Organization> findByParentId(String parentId);

	String getAmkCode(Organization org);

	boolean existCode(Organization org);

	boolean existName(Organization org);

	Organization getManager(Organization org);

	@Memo("管理机构中有本机构")
	boolean managerExitMe(Organization me, Organization manager);
	
	Organization getByCode(String code);

	boolean existChild(String parentId);

	Organization getBySyncId(String syncId);
}
