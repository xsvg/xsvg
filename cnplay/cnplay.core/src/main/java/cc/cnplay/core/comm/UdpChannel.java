package cc.cnplay.core.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import cc.cnplay.core.annotation.Memo;

@Memo("UDP")
@ChannelType(ChannelTypeEnum.UDP)
public class UdpChannel extends ChannelImpl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DatagramSocket socket;

	public UdpChannel()
	{
		this("127.0.0.1", 8000, 8001);
	}

	public UdpChannel(String host, int port, int localPort)
	{
		super();
		this.parameters.setLocalPort(localPort);
		this.parameters.setAddress(host);
		this.parameters.setPort(port);
		parameters.setChannelType(ChannelTypeEnum.UDP);
	}

	@Override
	public synchronized void connect() throws IOException
	{
		try
		{
			if (!connected())
			{
				if (socket == null)
					socket = new DatagramSocket(parameters.getLocalPort());
				this.onState(State.Connecting);
				// socket.connect(InetAddress.getByName(host), port);
				socket.connect(new InetSocketAddress(parameters.getAddress(), parameters.getPort()));
				log.info("连接成功[" + this.getId() + "]SoTimeout=" + socket.getSoTimeout());
				this.onState(State.Connected);
				socket.setSoTimeout(parameters.getSoTimeout());
				super.connect();
			}
		}
		catch (IOException ex)
		{
			this.close();
			this.onState(State.Fail);
			log.warn(Thread.currentThread().getName() + "通道[" + getId() + "]连接异常：" + ex.getMessage());
			throw ex;
		}
	}

	@Override
	protected int read(byte[] bytes) throws IOException
	{
		if (connected())
		{
			// String address = this.getParameters().getAddress();
			// int port = this.getParameters().getPort();
			// DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(address), port);
			DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
			socket.receive(dp);
			return dp.getLength();
		}
		else
		{
			return -1;
		}
	}

	@Override
	public void write(byte[] bytes) throws IOException
	{
		try
		{
			DatagramPacket dp = new DatagramPacket(bytes, bytes.length, new InetSocketAddress(parameters.getAddress(), parameters.getPort()));
			socket.send(dp);
			lastTimeMillis = System.currentTimeMillis();
			onMessageListener(bytes, true);
		}
		catch (Throwable ex)
		{
			log.warn(Thread.currentThread().getName() + " close[" + this.getId() + "]" + ex.getMessage());
		}
	}

	@Override
	public boolean connected()
	{
		return socket != null;// && socket.isConnected();
	}

	@Override
	public void close()
	{
		try
		{
			if (socket != null)
			{
				if (socket.isConnected())
					log.info("closed[" + this.getId() + "]");
				socket.close();
			}
			socket = null;
		}
		catch (Throwable ex)
		{
			log.warn(Thread.currentThread().getName() + " close[" + this.getId() + "] " + ex.getMessage());
		}
		super.close();
	}

	@Override
	public void setParameters(Parameters parameters)
	{
		super.setParameters(parameters);
		this.parameters.setChannelType(ChannelTypeEnum.UDP);
	}
}
