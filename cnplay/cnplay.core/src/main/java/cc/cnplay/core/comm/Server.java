package cc.cnplay.core.comm;

import java.io.IOException;
import java.util.List;

import cc.cnplay.core.annotation.Memo;

/**
 * 服务接口
 * 
 * @author <a href="mailto:pqixere@qq.com">裴绍国</a>
 * @version 2013-10-16
 */
@Memo("服务接口")
public interface Server extends Channel
{
	boolean started();

	@Memo("连接通道")
	void start() throws IOException;

	@Memo("关闭")
	void stop();

	@Memo("连接上的终端")
	List<Channel> getClientList();
}