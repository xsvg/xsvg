package cc.cnplay.platform.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.domain.Organization;
import cc.cnplay.platform.domain.User;

public interface OrganizationService extends GenericService<Organization, String>
{
	/**
	 * 显示顶级组织及以下
	 * 
	 * @param parentId
	 * @return
	 */
	List<Organization> findByParentId(String parentId);

	List<Organization> findByParentId(User user, String parentId);

	Organization getByLevelCode(String levelCode);

	String removeLogic(String userid, String orgid) throws Exception;

	boolean existUserByOrg(String orgid);

	Organization save(Organization orgForm);

	String getLevelCodeById(String id);

	/**
	 * 取根机构
	 * 
	 * @return
	 */
	Organization getRoot();

	Json<?> loadImportOrg(HttpServletRequest request);

	String saveImportOrg(String json);

	List<Map<String, ?>> findReportData(String orgId);

	boolean isRoot(Organization org);

	void checked(String[] orgId, List<Organization> list);

	String getAmkCode(Organization org);

	Organization getByCode(String code);

	boolean existChild(String id);

	Organization getBySyncId(String syncId);

	Organization saveSync(Organization orgForm);
}
