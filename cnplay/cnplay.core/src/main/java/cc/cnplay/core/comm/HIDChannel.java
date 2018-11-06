package cc.cnplay.core.comm;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.util.Converter;
import cc.cnplay.core.util.SystemType;

@Memo("USBHID客户端")
@ChannelType(ChannelTypeEnum.HID)
public class HIDChannel extends ChannelImpl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SerialPortChannel.class);
	private HIDManager hm;
	private HIDDevice hd;

	static
	{
		if (SystemType.Android == SystemType.current())
		{
			throw new UnsupportedOperationException("this os Unsupported");
		}
		ClassPathLibraryLoader.loadNativeHIDLibrary();
		log.info("ClassPathLibraryLoader.loadNativeHIDLibrary()");
	}

	public HIDChannel()
	{
		super();
	}

	public HIDChannel(int hidVID, int hidPID)
	{
		super();
		this.parameters.setHidVID(hidVID);
		this.parameters.setHidPID(hidPID);
		log.debug("new HIDChannel()");
	}

	@Override
	public String getId()
	{
		return this.parameters.getId();
	}

	@Override
	public synchronized void connect() throws IOException
	{
		try
		{
			if (!connected())
			{
				if (hm != null)
				{
					hm.release();
				}
				this.onState(State.Connecting);
				hm = HIDManager.getInstance();
				hd = hm.openById(parameters.getHidVID(), parameters.getHidPID(), null);
				hd.enableBlocking();
				log.debug("连接成功[" + this.getId() + "]SoTimeout=" + parameters.getSoTimeout());
				this.onState(State.Connected);
				super.connect();
			}
		}
		catch (IOException ex)
		{
			this.close();
			this.onState(State.Fail);
			log.error(Thread.currentThread().getName() + "通道[" + getId() + "]连接异常：" + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public void close()
	{
		try
		{
			if (hm != null)
			{
				hm.release();
			}
			hm = null;
			if (hd != null)
			{
				hd.close();
				log.info("closed[" + this.getId() + "]");
			}
			hd = null;
		}
		catch (Throwable ex)
		{
			log.warn(Thread.currentThread().getName() + " close[" + this.getId() + "] " + ex.getMessage());
		}
		super.close();
	}

	@Override
	public boolean connected()
	{
		return hm != null && hd != null;
	}

	@Override
	protected int read(byte[] bytes) throws IOException
	{
		return hd.readTimeout(bytes, this.parameters.getSoTimeout());
	}

	@Override
	public void write(byte[] bytes) throws IOException
	{
		hd.write(bytes);
		onMessageListener(bytes, true);
	}

	@Override
	public void write(String message) throws IOException
	{
		this.write(Converter.getBytes(message));
	}

	@Override
	public void setParameters(Parameters parameters)
	{
		super.setParameters(parameters);
		this.parameters.setChannelType(ChannelTypeEnum.HID);
	}
}
