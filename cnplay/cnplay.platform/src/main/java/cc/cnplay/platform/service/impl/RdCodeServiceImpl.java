package cc.cnplay.platform.service.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.platform.domain.RdCode;
import cc.cnplay.platform.service.RdCodeService;

import com.googlecode.genericdao.search.Search;

@Service
@Transactional
public class RdCodeServiceImpl extends AbsGenericService<RdCode, String> implements RdCodeService
{

	@Override
	public RdCode getByCode(String code)
	{
		Search search = new Search(RdCode.class);
		search.addFilterEqual("code", code);
		search.addSortDesc("createTime");
		search.setMaxResults(1);
		return (RdCode) dao().searchUnique(search);
	}

	@Override
	public RdCode getLast()
	{
		Search search = new Search(RdCode.class);
		search.addSortDesc("createTime");
		search.setMaxResults(1);
		return (RdCode) dao().searchUnique(search);
	}
}
