package cc.cnplay.platform.domain;

/**
 * 
 * 状态
 * 
 * @author peixere@qq.com
 * 
 * @version 2012-12-03
 * 
 *          0:正常;1:挂起;2:删除
 * 
 */
public enum Status
{
	Normal(0), Banned(1), Delete(2);
	private int value = 0;

	Status(int value)
	{
		this.value = value;
	}

	public int value()
	{
		return this.value;
	}

	public static Status valueOf(int value)
	{
		for (Status v : values())
		{
			if (v.value() == value)
			{
				return v;
			}
		}
		return Normal;
	}
	/*
	public static Status str(String str){
		for (Status v : values())
		{
			if (v.toString().equalsIgnoreCase(str) )
			{
				return v;
			}
		}
		return null;
	}
	*/
	
	//test
	public static void main(String[] args) {
		Status s=Status.valueOf("Banned");
		System.out.println(s.value);		

	}
	
	
}
