package cc.cnplay.platform.aop;

import java.io.Serializable;

public class SystemLog implements Serializable
{
	/**
	 * 系统日志输出JSON对象
	 */
	private static final long serialVersionUID = 1L;
	private String method;
	private Class<?>[] argsType;
	private Object[] args;
	private Object returnValue;
	private String url;
	private String username;
	private String errorMsg;

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public Class<?>[] getArgsType()
	{
		return argsType;
	}

	public void setArgsType(Class<?>[] argsType)
	{
		this.argsType = argsType;
	}

	public Object[] getArgs()
	{
		return args;
	}

	public void setArgs(Object[] args)
	{
		this.args = args;
	}

	public Object getReturnValue()
	{
		return returnValue;
	}

	public void setReturnValue(Object returnValue)
	{
		this.returnValue = returnValue;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}

}
