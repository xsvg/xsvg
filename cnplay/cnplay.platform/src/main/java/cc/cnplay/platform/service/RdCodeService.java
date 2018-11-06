package cc.cnplay.platform.service;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.platform.domain.RdCode;

public interface RdCodeService extends GenericService<RdCode, String>
{

	RdCode getByCode(String code);
}
