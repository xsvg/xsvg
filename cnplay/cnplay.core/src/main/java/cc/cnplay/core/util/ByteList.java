package cc.cnplay.core.util;

import cc.cnplay.core.annotation.Memo;

public class ByteList extends GList<Byte>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ByteList()
	{

	}

	public ByteList(byte[] buffer)
	{
		addRange(buffer);
	}

	public synchronized void insertRange(int index, byte[] array)
	{
		if (array == null || array.length == 0)
			return;
		for (byte e : array)
		{
			this.add(e);
		}
		for (int i = (this.size() - array.length - 1); i >= index; i--)
		{
			this.set(i + array.length, this.get(i));
		}
		for (int i = 0; i < array.length; i++)
		{
			this.set(i + index, array[i]);
		}
	}

	public synchronized void addRange(byte[] array)
	{
		if (array == null || array.length == 0)
			return;
		for (int i = 0; i < array.length; i++)
		{
			this.add(array[i]);
		}
	}

	@Memo("转成数据")
	public byte[] ToArray()
	{
		byte[] array = new byte[this.size()];
		for (int i = 0; i < array.length; i++)
		{
			array[i] = this.get(i);
		}
		return array;
	}
}
