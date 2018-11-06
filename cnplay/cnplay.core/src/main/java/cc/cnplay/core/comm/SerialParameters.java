package cc.cnplay.core.comm;

public class SerialParameters extends Parameters
{
	public static final int DATABITS_5 = 5;
	public static final int DATABITS_6 = 6;
	public static final int DATABITS_7 = 7;
	public static final int DATABITS_8 = 8;
	public static final int STOPBITS_1 = 1;
	public static final int STOPBITS_2 = 2;
	public static final int STOPBITS_1_5 = 3;
	public static final int PARITY_NONE = 0;
	public static final int PARITY_ODD = 1;
	public static final int PARITY_EVEN = 2;
	public static final int PARITY_MARK = 3;
	public static final int PARITY_SPACE = 4;
	public static final int FLOWCONTROL_NONE = 0;
	public static final int FLOWCONTROL_RTSCTS_IN = 1;
	public static final int FLOWCONTROL_RTSCTS_OUT = 2;
	public static final int FLOWCONTROL_XONXOFF_IN = 4;
	public static final int FLOWCONTROL_XONXOFF_OUT = 8;

	public SerialParameters()
	{
		this("COM1", 9600, FLOWCONTROL_NONE, FLOWCONTROL_NONE, DATABITS_8, STOPBITS_1, PARITY_NONE);
	}

	public SerialParameters(String portName, int baudRate)
	{
		this(portName, baudRate, FLOWCONTROL_NONE, FLOWCONTROL_NONE, DATABITS_8, STOPBITS_1, PARITY_NONE);
	}

	public SerialParameters(String portName, int baudRate, int flowControlIn, int flowControlOut, int databits, int stopbits, int parity)
	{
		this.setChannelType(ChannelTypeEnum.SerialPort);
		this.setPortName(portName);
		this.setBaudRate(baudRate);
		this.setFlowControlIn(flowControlIn);
		this.setFlowControlOut(flowControlOut);
		this.setDatabits(databits);
		this.setParity(parity);
		this.setStopbits(stopbits);
	}

	public void setBaudRate(String baudRate)
	{
		this.setBaudRate(Integer.parseInt(baudRate));
	}

	public String getBaudRateString()
	{
		return Integer.toString(this.getBaudRate());
	}

	public void setFlowControlIn(String flowControlIn)
	{
		this.setFlowControlIn(stringToFlow(flowControlIn));
	}

	public String getFlowControlInString()
	{
		return flowToString(this.getFlowControlIn());
	}

	public void setFlowControlOut(String flowControlOut)
	{
		this.setFlowControlOut(stringToFlow(flowControlOut));
	}

	public String getFlowControlOutString()
	{
		return flowToString(this.getFlowControlOut());
	}

	public void setDatabits(String databits)
	{
		if (databits.equals("5"))
		{
			this.setDatabits(DATABITS_5);
		}

		if (databits.equals("6"))
		{
			this.setDatabits(DATABITS_6);
		}

		if (databits.equals("7"))
		{
			this.setDatabits(DATABITS_7);
		}

		if (databits.equals("8"))
		{
			this.setDatabits(DATABITS_8);
		}
	}

	public String getDatabitsString()
	{
		switch (this.getDatabits())
		{

		case DATABITS_5:
			return "5";

		case DATABITS_6:
			return "6";

		case DATABITS_7:
			return "7";

		case DATABITS_8:
			return "8";

		default:
			return "8";
		}
	}

	public void setStopbits(String stopbits)
	{
		if (stopbits.equals("1"))
		{
			this.setStopbits(STOPBITS_1);
		}

		if (stopbits.equals("1.5"))
		{
			this.setStopbits(STOPBITS_1_5);
		}

		if (stopbits.equals("2"))
		{
			this.setStopbits(STOPBITS_2);
		}
	}

	public String getStopbitsString()
	{
		switch (this.getStopbits())
		{

		case STOPBITS_1:
			return "1";

		case STOPBITS_1_5:
			return "1.5";

		case STOPBITS_2:
			return "2";

		default:
			return "1";
		}
	}

	public void setParity(String parity)
	{
		if (parity.equals("None"))
		{
			this.setParity(PARITY_NONE);
		}

		if (parity.equals("Even"))
		{
			this.setParity(PARITY_EVEN);
		}

		if (parity.equals("Odd"))
		{
			this.setParity(PARITY_ODD);
		}
	}

	public String getParityString()
	{
		switch (this.getParity())
		{

		case PARITY_NONE:
			return "None";

		case PARITY_EVEN:
			return "Even";

		case PARITY_ODD:
			return "Odd";

		default:
			return "None";
		}
	}

	private int stringToFlow(String flowControl)
	{
		if (flowControl.equals("None"))
		{
			return FLOWCONTROL_NONE;
		}

		if (flowControl.equals("Xon/Xoff Out"))
		{
			return FLOWCONTROL_XONXOFF_OUT;
		}

		if (flowControl.equals("Xon/Xoff In"))
		{
			return FLOWCONTROL_XONXOFF_IN;
		}

		if (flowControl.equals("RTS/CTS In"))
		{
			return FLOWCONTROL_RTSCTS_IN;
		}

		if (flowControl.equals("RTS/CTS Out"))
		{
			return FLOWCONTROL_RTSCTS_OUT;
		}

		return FLOWCONTROL_NONE;
	}

	private String flowToString(int flowControl)
	{
		switch (flowControl)
		{

		case FLOWCONTROL_NONE:
			return "None";

		case FLOWCONTROL_XONXOFF_OUT:
			return "Xon/Xoff Out";

		case FLOWCONTROL_XONXOFF_IN:
			return "Xon/Xoff In";

		case FLOWCONTROL_RTSCTS_IN:
			return "RTS/CTS In";

		case FLOWCONTROL_RTSCTS_OUT:
			return "RTS/CTS Out";

		default:
			return "None";
		}
	}

}