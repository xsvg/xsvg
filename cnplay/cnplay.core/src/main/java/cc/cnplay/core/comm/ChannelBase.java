package cc.cnplay.core.comm;

import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

import cc.cnplay.core.Listener;
import cc.cnplay.core.ListenerManager;
import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.util.Converter;

/**
 * 通道接口基础实现虚类
 * 
 * @author <a href="mailto:pqixere@qq.com">裴绍国</a>
 * @version 2013-10-16
 */
public abstract class ChannelBase implements Channel
{

	private static final long serialVersionUID = 1L;

	protected final Logger log = Logger.getLogger(this.getClass());

	protected final byte[] receiveBuffer = new byte[1024];

	protected Parameters parameters;

	protected final ListenerManager<State> stateListeners = new ListenerManager<State>();

	@Memo("状态监听器")
	protected Listener<State> stateListener;

	@Memo("报文监听器")
	protected Listener<String> messageListener;

	@Memo("报文监听器")
	protected Listener<byte[]> receiveListener;

	protected State state = State.Close;

	protected ChannelBase()
	{
		this.setParameters(new Parameters());
		log.debug("new Channel");
	}

	protected void onState(State state)
	{
		if (this.state != state)
		{
			this.state = state;
			if (stateListeners.size() > 0)
			{
				stateListeners.post(this, state);
			}
		}
	}

	protected void onMessageListener(byte[] buffer, boolean send)
	{
		String hexString = (send ? ">>" : "<<") + Converter.toHexString(buffer);
		if (this.messageListener != null)
		{
			messageListener.onListener(this, hexString);
		}
		else
		{
			log.debug(this.getId());
			log.debug(hexString);
		}
	}

	protected long lastTimeMillis = System.currentTimeMillis();

	private int readByteCount = -1;

	protected int receive()
	{
		readByteCount = -1;
		try
		{
			if (connected() && state == State.Connected)
			{
				readByteCount = read(receiveBuffer);
				if (readByteCount == -1)
				{
					if (checkPeriod > 0 && (System.currentTimeMillis() - lastTimeMillis) > checkPeriod)
					{
						this.write(new byte[] { -1 });
						lastTimeMillis = System.currentTimeMillis();
					}
				}
				else if (readByteCount == 0)
				{
					this.close();
				}
				else if (readByteCount > 0)
				{
					byte[] buffer = new byte[readByteCount];
					System.arraycopy(receiveBuffer, 0, buffer, 0, buffer.length);
					onReceiveListener(buffer);
					lastTimeMillis = System.currentTimeMillis();
				}
			}
		}
		catch (PortUnreachableException ex)
		{
			log.debug(Thread.currentThread().getName() + " 通道[" + getId() + "]接收：PortUnreachableException " + ex.getMessage());
		}
		catch (SocketTimeoutException ex)
		{
			if (checkPeriod > 0 && (System.currentTimeMillis() - lastTimeMillis) > checkPeriod)
			{
				try
				{
					lastTimeMillis = System.currentTimeMillis();
					// log.debug(Thread.currentThread().getName() + " 通道[" + getId() + "]接收超时：" + ex.getClass().getName() + " " + ex.getMessage());
					this.write(new byte[] { -1 });
				}
				catch (Throwable e)
				{
					log.debug(e.getMessage());
				}
			}
		}
		catch (java.net.SocketException ex)
		{
			log.warn(Thread.currentThread().getName() + " 通道[" + getId() + "]接收异常：" + ex.getMessage());
			if (State.Close != this.getState())
			{
				close();
			}
		}
		catch (Throwable ex)
		{
			log.warn(Thread.currentThread().getName() + " 通道[" + getId() + "]接收异常：" + ex.getMessage());
		}
		return readByteCount;
	}

	protected void onReceiveListener(byte[] buffer)
	{
		onMessageListener(buffer, false);
		// if (receiveListener.size() > 0)
		// {
		// receiveListener.post(this, buffer);
		// }
		if (this.receiveListener != null)
		{
			this.receiveListener.onListener(this, buffer);
		}
	}

	protected abstract int read(byte[] bytes) throws Exception;

	@Override
	public Listener<String> getMessageListener()
	{
		return messageListener;
	}

	@Override
	public void setMessageListener(Listener<String> messageListener)
	{
		this.messageListener = messageListener;
	}

	@Override
	public void setStateListener(Listener<State> stateListener)
	{
		removeStateListener(this.stateListener);
		this.stateListener = stateListener;
		addStateListener(this.stateListener);

	}

	@Override
	public void addStateListener(Listener<State> l)
	{
		stateListeners.add(l);
	}

	@Override
	public void removeStateListener(Listener<State> l)
	{
		stateListeners.remove(l);
	}

	@Memo("设置接收数据监听器")
	@Override
	public void setReceiveListener(Listener<byte[]> listener)
	{
		receiveListener = listener;
	}

	@Override
	public Parameters getParameters()
	{
		return parameters;
	}

	@Override
	public void setParameters(Parameters parameters)
	{
		this.parameters = parameters;
	}

	private long checkPeriod = 10000;

	@Memo("获取通道检测周期（毫秒）")
	public long getCheckPeriod()
	{
		return checkPeriod;
	}

	@Memo("设置通道检测周期（毫秒）")
	public void setCheckPeriod(long checkPeriod)
	{
		this.checkPeriod = checkPeriod;
	}

	@Override
	public State getState()
	{
		return state;
	}

	@Override
	public String getId()
	{
		return this.parameters.getId();
	}

	@Override
	public String toString()
	{
		return parameters.toString();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == null)
			return false;
		return this.toString().equals(object.toString());
	}

	@Override
	public void close()
	{
		this.onState(State.Close);
	}
}
