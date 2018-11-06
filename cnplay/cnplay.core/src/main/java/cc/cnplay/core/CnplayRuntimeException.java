package cc.cnplay.core;

public class CnplayRuntimeException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code = "9999";

	public CnplayRuntimeException()
	{
		super();
	}

	public CnplayRuntimeException(String message)
	{
		super(message);
	}

	public CnplayRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CnplayRuntimeException(Throwable cause)
	{
		super(cause);
	}

	public CnplayRuntimeException(String code, String message)
	{
		super(message);
		this.setCode(code);
	}

	public CnplayRuntimeException(String code, String message, Throwable cause)
	{
		super(message, cause);
		this.setCode(code);
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

}
