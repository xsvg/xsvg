package cc.cnplay.core.comm;

import java.io.IOException;

import cc.cnplay.core.Listener;
import cc.cnplay.core.annotation.Memo;

/**
 * 通道接口
 * 
 * @author <a href="mailto:pqixere@qq.com">裴绍国</a>
 * @version 2013-10-16
 */
@Memo("通道接口")
public interface Channel extends java.io.Serializable
{

	@Memo("获取通道参数")
	Parameters getParameters();

	@Memo("设置通道参数")
	void setParameters(Parameters parameters);

	@Memo("通道Id")
	String getId();

	boolean connected();

	@Memo("连接通道")
	void connect() throws IOException;

	@Memo("设置读数据监听器")
	void setReceiveListener(Listener<byte[]> listener);

	@Memo("写数据到通道")
	void write(byte[] bytes) throws IOException;

	@Memo("写数据到通道")
	void write(String bytes) throws IOException;

	@Memo("关闭")
	void close();

	@Memo("设置通道事件")
	void setStateListener(Listener<State> stateListener);

	@Memo("+通道事件")
	void addStateListener(Listener<State> stateListener);

	@Memo("-通道事件")
	void removeStateListener(Listener<State> stateListener);

	@Memo("获取报文监听器")
	Listener<String> getMessageListener();

	@Memo("设置报文监听器")
	void setMessageListener(Listener<String> messageListener);

	State getState();

	@Memo("获取通道检测周期（毫秒）")
	long getCheckPeriod();

	@Memo("设置通道检测周期（毫秒）")
	void setCheckPeriod(long checkPeriod);
}