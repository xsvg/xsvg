package cc.cnplay.core.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import cc.cnplay.core.annotation.Memo;

@Memo("UDP(组播)")
@ChannelType(ChannelTypeEnum.UDPMulticast)
public class UdpMulticastClient extends ChannelImpl
{
	private static final long serialVersionUID = 1L;
	private final static Logger log = Logger.getLogger(UdpMulticastClient.class.getName());
	private MulticastSocket socket;
	private int sendPort; // 目标端口
	private int receivePort; // 接收端口
	private InetAddress groupAddre;
	InetAddress localAddress;
	boolean isJoin = false;

	public UdpMulticastClient() throws SocketException
	{
		super();
	}

	public UdpMulticastClient(String groupAddress, String localAddress, int port, int localPort) throws UnknownHostException
	{
		super();
		this.sendPort = port;
		this.receivePort = localPort;
		this.groupAddre = InetAddress.getByName(groupAddress);
		this.localAddress = InetAddress.getByName(localAddress);
	}

	public UdpMulticastClient(Parameters parameters)
	{
		setParameters(parameters);
	}

	@Override
	public void setParameters(Parameters parameters)
	{
		try
		{
			this.sendPort = parameters.getPort();
			this.groupAddre = InetAddress.getByName(parameters.getAddress());
			this.receivePort = parameters.getLocalPort();
			this.localAddress = InetAddress.getByName(parameters.getLocalAddres());
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void connect() throws IOException
	{
		try
		{
			if (!connected())
			{
				if (socket == null)
				{
					socket = new MulticastSocket(receivePort);
					NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localAddress);
					if (networkInterface != null)
					{
						log.info("----------------------------->>" + networkInterface.toString());
						if (!networkInterface.getName().equals("lo0"))
						{
							socket.setNetworkInterface(networkInterface);
						}
					}
				}
				this.onState(State.Connecting);
				socket.joinGroup(groupAddre);
				isJoin = true;
				log.info("连接成功[" + this.getId() + "]");
				this.onState(State.Connected);
				socket.setSoTimeout(5000);
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
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, groupAddre, this.sendPort);
		socket.send(dp);
	}

	@Override
	public void close()
	{
		try
		{
			if (socket != null)
			{
				if (isJoin)
				{
					socket.leaveGroup(groupAddre);
					isJoin = false;
				}
				socket.close();
				if (socket.isConnected())
					log.info("closed[" + this.getId() + "]");
			}
		}
		catch (Throwable ex)
		{
			log.warn("clase[" + this.getId() + "]异常：" + ex.getMessage());
		}
		socket = null;
		super.close();
	}

	@Override
	public String getId()
	{
		return groupAddre + ":" + receivePort;
	}

	@Override
	public boolean connected()
	{
		return socket != null;
	}
}
