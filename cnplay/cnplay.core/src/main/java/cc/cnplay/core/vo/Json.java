package cc.cnplay.core.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import cc.cnplay.core.annotation.Memo;

public class Json<T>
{

	private boolean success;

	private boolean needCheck;

	private String msg = "";

	private int code = CodeIng;

	public static final int CodeIng = 0;

	public static final int CodeEnd = 200;

	@JsonInclude(Include.NON_NULL)
	@Memo("数据，rows是为兼容easyUI分布定死的名称，不能改为别的")
	private T rows;

	public Json()
	{
		this(null, false);
	}

	public Json(T data)
	{
		this(data, true);
	}

	public Json(T data, boolean success)
	{
		this(data, success, "");
	}

	public Json(T data, boolean success, String msg)
	{
		this.setSuccess(success);
		this.setRows(data);
		this.setMsg(msg);
	}

	public boolean getSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public void OK(T data, String msg)
	{
		this.setRows(data);
		this.success = true;
		if (msg == null)
		{
			msg = "";
		}
		this.msg = msg;
	}

	public void end(T data, String msg)
	{
		OK(data, msg);
		this.setCode(CodeEnd);
	}

	public void NG(String msg)
	{
		this.setRows(null);
		this.success = false;
		this.msg = msg;
	}

	public T getRows()
	{
		return rows;
	}

	public void setRows(T rows)
	{
		this.rows = rows;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public boolean isNeedCheck()
	{
		return needCheck;
	}

	public void setNeedCheck(boolean needCheck)
	{
		this.needCheck = needCheck;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

}
