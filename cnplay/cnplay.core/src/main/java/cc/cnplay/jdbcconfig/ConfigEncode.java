package cc.cnplay.jdbcconfig;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ConfigEncode
{
	public static String aesKey = "123anoiwef;lk912";

	public static void main(String[] args)
	{
		try
		{
			String str = ConfigEncode.encryptData("123456");
			System.out.println(str);
			String str1 = ConfigEncode.decryptData(str);
			System.out.println(str1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String encryptData(String content)
	{
		if (aesKey == null)
		{
			return content;
		}
		try
		{
			byte[] raw = aesKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
			IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			// 加密前要进行编码,否则js无法解码
			byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
			return parseByte2HexStr(encrypted);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return content;
		}
	}

	public static String decryptData(String content)
	{
		if (aesKey == null)
		{
			return content;
		}
		try
		{
			byte[] raw = aesKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = parseHexStr2Byte(content);// 先用bAES64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return content;
		}
	}

	public static String parseByte2HexStr(byte buf[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++)
		{
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static byte[] parseHexStr2Byte(String hexStr)
	{
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++)
		{
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
}