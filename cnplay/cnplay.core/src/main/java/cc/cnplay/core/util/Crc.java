package cc.cnplay.core.util;

import cc.cnplay.core.annotation.Memo;

public class Crc
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		byte[] data = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Crc.create16(data);
		Crc.ICE16(data);
	}

	@Memo("CRC16")
	public static short create16(byte[] bytes)
	{
		return create16(bytes, 0, bytes.length);
	}

	public static short create16(byte[] bytes, int startIndex, int length)
	{
		int crc = 0xffff;
		for (int i = startIndex; i < length; i++)
		{
			for (int b = 128; b != 0; b >>= 1)
			{
				if ((crc & 0x8000) != 0)
				{
					crc <<= 1;
					crc ^= 0x1021;
				}
				else
				{
					crc <<= 1;
				}
				if ((bytes[i] & b) != 0)
				{
					crc ^= 0x1021;
				}
			}
		}
		return (short) crc;
	}

	public static byte[] ICE16(byte[] data)
	{
		int crc;
		int i;
		crc = Integer.parseInt("0000", 16);
		int j = 0;
		int length = data.length;
		while (length-- != 0)
		{
			crc = crc ^ ((int) (data[j]) << 8);
			for (i = 0; i < 8; ++i)
				if ((crc & 0x8000) == 0x8000)
					crc = (crc << 1) ^ 0x1021;
				else
					crc = crc << 1;
			j++;
		}
		byte[] temp = int2Bytes(crc, 2);
		byte[] crcbyte = { temp[0], temp[1] };
		return crcbyte;
	}

	private static byte[] int2Bytes(int value, int len)
	{
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++)
		{
			bytes[i] = (byte) value;
			value = value >> 8;
		}
		return bytes;
	}
}
