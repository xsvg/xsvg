package cc.cnplay.core.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import cc.cnplay.core.Listener;
import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.util.Converter;
import cc.cnplay.core.util.GList;

@Memo("TCP服务端")
@ChannelType(ChannelTypeEnum.TCPServer)
public class TcpServer extends ChannelBase implements Server
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ServerSocket socket;
	private ScheduledExecutorService scheduExec;
	private GList<Terminal> terminalList = new GList<Terminal>();
	public static final ThreadLocal<Channel> terminalPool = new ThreadLocal<Channel>();

	private int maxTerminalSize = 1024;

	public TcpServer()
	{
		this("0.0.0.0", 40000);
	}

	public TcpServer(int port)
	{
		this("0.0.0.0", port);
	}

	public TcpServer(String host, int port)
	{
		super();
		parameters.setAddress(host);
		parameters.setPort(port);
		this.setParameters(parameters);
	}

	@Override
	public void setParameters(Parameters parameters)
	{
		super.setParameters(parameters);
		this.parameters.setChannelType(ChannelTypeEnum.TCPServer);
	}

	@Override
	public boolean connected()
	{
		return socket != null && !socket.isClosed();
	}

	@Override
	public void connect() throws IOException
	{
		try
		{
			if (!connected())
			{
				this.onState(State.Connecting);
				socket = new ServerSocket();
				socket.bind(new InetSocketAddress(parameters.getAddress(), parameters.getPort()));
				// socket.setSoTimeout(parameters.getSoTimeout());
				log.info("启动成功[" + this.getId() + "]SoTimeout=" + socket.getSoTimeout());
				this.onState(State.Connected);
				accept();
			}
		}
		catch (IOException ex)
		{
			this.close();
			this.onState(State.Fail);
			log.warn(Thread.currentThread().getName() + "通道[" + getId() + "]启动异常：" + ex.getClass().getName() + ": " + ex.getMessage());
			throw ex;
		}
	}

	@Override
	protected int receive()
	{
		return -1;
	}

	private void accept()
	{
		if (scheduExec == null || scheduExec.isShutdown())
		{
			scheduExec = Executors.newScheduledThreadPool(2);
		}
		scheduExec.execute(new Runnable()
		{

			@Override
			public void run()
			{
				receiveTerminal();
			}
		});
	}

	private Listener<byte[]> terminalReceiveListener = new Listener<byte[]>()
	{

		@Override
		public boolean onListener(Object sender, byte[] buffer)
		{
			onMessageListener(buffer, false);
			if (receiveListener != null)
			{
				receiveListener.onListener(sender, buffer);
			}
			return true;
		}

	};
	private Listener<State> terminalStateListener = new Listener<State>()
	{

		@Override
		public boolean onListener(Object sender, State e)
		{
			if (stateListeners.size() > 0)
			{
				stateListeners.post(this, state);
			}
			return true;
		}

	};

	private void receiveTerminal()
	{
		if (connected())
		{
			try
			{
				Terminal terminal = new Terminal(socket.accept());
				if (terminalList.size() < maxTerminalSize)
				{
					terminalList.add(terminal);
				}
				else
				{
					terminal.close();
				}
				log.info("terminal count : " + terminalList.size());
			}
			catch (Throwable e)
			{
				log.error(e);
			}
			receiveTerminal();
		}
	}

	@Override
	public void close()
	{
		try
		{
			if (scheduExec != null)
			{
				scheduExec.shutdown();
			}
			scheduExec = null;
			if (socket != null)
			{
				if (!socket.isClosed())
					log.info("closed[" + this.getId() + "]");
				socket.close();
			}
			socket = null;
			for (int i = terminalList.size() - 1; i >= 0; i--)
			{
				Channel chennel = terminalList.get(i);
				chennel.close();
			}
		}
		catch (Throwable ex)
		{
			log.warn(Thread.currentThread().getName() + " close[" + this.getId() + "]", ex);
		}
		super.close();
	}

	@Override
	public void write(byte[] bytes) throws IOException
	{
		for (Channel c : this.terminalList)
		{
			try
			{
				if (c.connected())
					c.write(bytes);
			}
			catch (Throwable e)
			{
				log.error(e.getClass().getName() + ": " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void write(String message) throws IOException
	{
		this.write(Converter.getBytes(message));
	}

	@Override
	protected int read(byte[] bytes) throws Exception
	{
		return -1;
	}

	@Override
	public boolean started()
	{
		return this.connected();
	}

	@Override
	public void start() throws IOException
	{
		this.connect();
	}

	@Override
	public void stop()
	{
		this.close();
	}

	@Override
	public List<Channel> getClientList()
	{
		GList<Channel> tmpList = new GList<Channel>();
		if (terminalList.size() > 0)
			tmpList.AddRange(terminalList.asArray());
		return tmpList;
	}

	class Terminal extends ChannelImpl
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected Socket socket;

		public Terminal(Socket socket)
		{
			terminalPool.set(this);
			this.socket = socket;
			this.parameters.setAddress(socket.getInetAddress().getHostAddress());
			this.parameters.setPort(socket.getPort());
			this.parameters.setLocalPort(socket.getLocalPort());
			this.parameters.setChannelType(ChannelTypeEnum.TCPServer);
			this.setReceiveListener(terminalReceiveListener);
			this.setStateListener(terminalStateListener);
			this.connect();
		}

		@Override
		public void connect()
		{
			try
			{
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				socket.setSoTimeout(1000);
				log.info("连接成功[" + this.getId() + "]SoTimeout=" + socket.getSoTimeout());
				this.onState(State.Connected);
				super.connect();
			}
			catch (Throwable e)
			{
				log.error(e.getClass().getName() + ": " + e.getMessage());
			}
		}

		@Override
		public boolean connected()
		{
			return socket != null && socket.isConnected() && !socket.isClosed();
		}

		@Override
		public void write(byte[] bytes) throws IOException
		{
			try
			{
				super.write(bytes);
			}
			catch (java.net.SocketException ex)
			{
				log.warn(" 通道[" + getId() + "]发送异常：" + ex.getMessage());
				this.close();
				throw new IOException(ex.getMessage(), ex);
			}
			catch (Throwable ex)
			{
				log.warn(Thread.currentThread().getName() + " close[" + this.getId() + "]", ex);
			}
		}

		@Override
		public void close()
		{
			try
			{
				terminalList.remove(this);
				if (null != terminalPool.get())
				{
					terminalPool.remove();
				}
				log.info("terminal count : " + terminalList.size());
				if (socket != null)
				{
					if (socket.isConnected())
						log.info("closed[" + this.getId() + "]");
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				}
				socket = null;
			}
			catch (Throwable ex)
			{
				log.warn(Thread.currentThread().getName() + " close[" + this.getId() + "]", ex);
			}
			super.close();
		}

		@Override
		public void setParameters(Parameters parameters)
		{
			super.setParameters(parameters);
			this.parameters.setChannelType(ChannelTypeEnum.TCPServer);
		}
	}

}
