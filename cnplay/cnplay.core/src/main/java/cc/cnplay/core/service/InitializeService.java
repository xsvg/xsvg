package cc.cnplay.core.service;

import cc.cnplay.core.annotation.Memo;

@Memo("实例化服务")
public interface InitializeService
{
	void init();

	int getSort();

	void destroy();
}
