package cc.cnplay.core.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import cc.cnplay.core.annotation.Memo;

@Memo("UDP(广播)")
@ChannelType(ChannelTypeEnum.UDPBroadcast)
public class UdpBroadcast extends ChannelImpl
{
	private static final long serialVersionUID = 1L;
	private DatagramSocket broadcastSocket;
	private InetAddress broadAddress;

	public UdpBroadcast() throws SocketException
	{
		super();
	}

	public UdpBroadcast(String address, int port, int localPort)
	{
		super();
		this.parameters.setLocalPort(localPort);
		this.parameters.setAddress(address);
		this.parameters.setPort(port);
		setParameters(this.parameters);
	}

	public UdpBroadcast(Parameters parameters)
	{
		setParameters(parameters);
	}

	@Override
	public void setParameters(Parameters parameters)
	{
		super.setParameters(parameters);
		this.parameters.setChannelType(ChannelTypeEnum.UDPBroadcast);
	}

	@Override
	public synchronized void connect() throws IOException
	{
		try
		{
			this.broadAddress = InetAddress.getByName(parameters.getAddress());
			if (!connected())
			{
				if (broadcastSocket == null)
				{
					// DatagramSocket dgSocket = new DatagramSocket(parameters.getPort());
					broadcastSocket = new DatagramSocket(parameters.getLocalPort());
				}
				broadcastSocket.setBroadcast(true);
				this.onState(State.Connecting);
				// broadcastSocket.joinGroup(broadAddress);
				log.info("连接成功[" + this.getId() + "]");
				this.onState(State.Connected);
				broadcastSocket.setSoTimeout(1);
				super.connect();
			}
		}
		catch (IOException ex)
		{
			this.close();
			this.onState(State.Fail);
			log.warn("连接失败[" + this.getId() + "]异常：" + ex.getMessage());
			throw ex;
		}
	}

	@Override
	protected int read(byte[] bytes) throws IOException
	{
		if (connected())
		{
			DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
			broadcastSocket.receive(dp);
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
			DatagramPacket dp = new DatagramPacket(bytes, bytes.length, broadAddress, parameters.getPort());
			broadcastSocket.send(dp);
			onMessageListener(bytes, true);
		}
		catch (Exception ex)
		{
			log.warn(Thread.currentThread().getName() + " close[" + this.getId() + "]" + ex.getMessage());
		}
	}

	@Override
	public void close()
	{
		try
		{
			if (broadcastSocket != null)
			{
				// broadcastSocket.leaveGroup(broadAddress);
				broadcastSocket.close();
				if (broadcastSocket.isConnected())
					log.info("closed[" + this.getId() + "]");
			}
		}
		catch (Exception e)
		{
			log.warn(e.getMessage() + " " + this.getId());
		}
		broadcastSocket = null;
		super.close();
	}

	@Override
	public boolean connected()
	{
		return broadcastSocket != null;
	}
}
