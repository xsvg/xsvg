package cc.cnplay.core.util;

/* SM3.java --- SM3密码杂凑算法
 M.L.Y 2012.7.1

 MODIFIED   (YYYY.MM.DD)  Description
 M.L.Y       2012.07.01 - Creation, V1.0

 */

import java.io.FileInputStream;
import java.io.InputStream;

/* 注：Java基本类型的存储空间:
 byte: 8位，short: 16位，int: 32位，long: 64位，float: 32位，double: 64位。
 这六种数字类型都是有符号的。

 Shifts in Java
 In Java, all integer types are signed, and the "<<" and ">>" operators perform
 arithmetic shifts. Java adds the operator ">>>" to perform logical right
 shifts, but because the logical and arithmetic left-shift operations are
 identical, there is no "<<<" operator in Java. These general rules are
 affected in several ways by the default type promotions; for example,
 because the eight-bit type byte is promoted to int in shift-expressions,
 the expression "b >>> 2" effectively performs an arithmetic shift of the
 byte value b instead of a logical shift. Such effects can be mitigated by
 judicious use of casts or bitmasks; for example, "(b & 0xFF) >>> 2"
 effectively results in a logical shift.
 */

public class SM3
{

	/** Number of bytes processed so far. */
	private long total; // 64位

	/** 256-bit interim result. */
	private int state[] = new int[8];

	private int T[] = new int[64]; // 常数

	/** Temporary input buffer. */
	private byte[] buffer = new byte[64];

	// 开关
	private boolean dispDebugInfo = false; // 输出debug信息否
	private boolean hexUpperCase = false; // 输出hash值16进制字母大写否（默认小写）
	private boolean hexInsBlank = false; // 输出hash值16进制每8位加空格否

	static byte SM3_padding[] = { (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // 64字节

	// Hex charset
	private static final char[] hex_digits = "0123456789abcdef".toCharArray();
	private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

	public static String intToHexString(int n)
	{

		char[] buf = new char[8];
		for (int i = 7; i >= 0; i--)
		{

			buf[i] = hex_digits[n & 0x0F];
			n >>>= 4;

		}
		return new String(buf);

	}

	public static String intToHexString(int n, boolean upper)
	{

		char[] buf = new char[8];
		for (int i = 7; i >= 0; i--)
		{

			if (upper)
				buf[i] = HEX_DIGITS[n & 0x0F];
			else
				buf[i] = hex_digits[n & 0x0F];
			n >>>= 4;

		}
		return new String(buf);

	}

	public static int unsignedRightShift(int number, int bits)
	{

		if (number >= 0)
			return number >> bits;
		else
			return (number >> bits) + (2 << ~bits);

	}

	public static int unsignedRightShift(int number, long bits)
	{

		return unsignedRightShift(number, (int) bits);

	}

	public static long unsignedRightShift(long number, int bits)
	{

		if (number >= 0)
			return number >> bits;
		else
			return (number >> bits) + (2L << ~bits);

	}

	public static long unsignedRightShift(long number, long bits)
	{

		return unsignedRightShift(number, (int) bits);

	}

	public static int ROL(int x, int n)
	{
// 循环左移
		return (x << n % 32) | (unsignedRightShift(x, (32 - n % 32)));

	}

	public SM3()
	{

	}

	public SM3(boolean dispDebugInfo, boolean hexUpperCase, boolean hexInsBlank)
	{

		this.dispDebugInfo = dispDebugInfo; // 输出debug信息否
		this.hexUpperCase = hexUpperCase; // 输出hash值16进制字母大写否（默认小写）
		this.hexInsBlank = hexInsBlank; // 输出hash值16进制每8位加空格否

	}

	public void SM3_starts()
	{

		total = 0;

		/* 初始值 IV */
		state[0] = 0x7380166f; /* A */
		state[1] = 0x4914b2b9; /* B */
		state[2] = 0x172442d7; /* C */
		state[3] = 0xda8a0600; /* D */
		state[4] = 0xa96f30bc; /* E */
		state[5] = 0x163138aa; /* F */
		state[6] = 0xe38dee4d; /* G */
		state[7] = 0xb0fb0e4e; /* H */

		for (int j = 0; j < 64; j++)
		{

			if (j < 16)
				T[j] = 0x79cc4519;
			else
				T[j] = 0x7a879d8a;

		}

	}

	/* 布尔函数 */
	private int FF(int x, int y, int z, int j)
	{

		return (j < 16 ? (x ^ y ^ z) : ((x & y) | (x & z) | (y & z)));

	}

	private int GG(int x, int y, int z, int j)
	{

		return (j < 16 ? (x ^ y ^ z) : ((x & y) | (~x & z)));

	}

	/* 置换函数 */
	private int P0(int x)
	{

		return (x ^ ROL(x, 9) ^ ROL(x, 17));

	}

	private int P1(int x)
	{

		return (x ^ ROL(x, 15) ^ ROL(x, 23));

	}

	private void SM3_process(byte[] data, int offset)
	/* data: 512 bits 的分组, 算法描述5.3.1中的 B(i) */
	{

		int W[] = new int[68];
		int WB[] = new int[64]; /* WB即算法描述中的W' */
		int A, B, C, D, E, F, G, H, SS1, SS2, TT1, TT2;
		int j;

		if (dispDebugInfo)
		{

			System.out.println("process data:");
			System.out.println(byte2hex(data, offset, 64));

		}

		/* 将消息分组B(i)划分为16个字W[0]--W[15]: */
		for (int i = 0; i < 16; i++)
		{

			W[i] = data[offset++] << 24 | (data[offset++] & 0xFF) << 16 | (data[offset++] & 0xFF) << 8 | (data[offset++] & 0xFF);

		}

		/* 消息扩展: */
		for (j = 16; j < 68; j++)
		{

			W[j] = P1(W[j - 16] ^ W[j - 9] ^ ROL(W[j - 3], 15)) ^ ROL(W[j - 13], 7) ^ W[j - 6];

		}

		if (dispDebugInfo)
		{

			System.out.println("扩展后的消息:\nW0W1...W67:");
			for (int i = 0; i < 68; i++)
			{

				if (i > 0)
				{

					if (i % 8 == 0)
						System.out.println("");
					else
						System.out.print(" ");

				}
				System.out.print(intToHexString(W[i]));

			}
			System.out.println("");

		}

		for (j = 0; j < 64; j++)
		{

			WB[j] = W[j] ^ W[j + 4];

		}

		if (dispDebugInfo)
		{

			System.out.println("W'0W'1...W'63:");
			for (int i = 0; i < 64; i++)
			{

				if (i > 0)
				{

					if (i % 8 == 0)
						System.out.println("");
					else
						System.out.print(" ");

				}
				System.out.print(intToHexString(WB[i]));

			}
			System.out.println("");

		}

		/* 压缩函数开始 */
		A = state[0];
		B = state[1];
		C = state[2];
		D = state[3];
		E = state[4];
		F = state[5];
		G = state[6];
		H = state[7];
		if (dispDebugInfo)
		{

			System.out.print("迭代压缩中间值：\n");
			System.out.print(" j    A        B        C        D");
			System.out.print("        E        F        G        H\n");
			System.out.print("  ");
			System.out.print(" " + intToHexString(A));
			System.out.print(" " + intToHexString(B));
			System.out.print(" " + intToHexString(C));
			System.out.print(" " + intToHexString(D));
			System.out.print(" " + intToHexString(E));
			System.out.print(" " + intToHexString(F));
			System.out.print(" " + intToHexString(G));
			System.out.println(" " + intToHexString(H));

		}

		for (j = 0; j < 64; j++)
		{

			SS1 = ROL(ROL(A, 12) + E + ROL(T[j], j), 7);
			SS2 = SS1 ^ ROL(A, 12);
			TT1 = FF(A, B, C, j) + D + SS2 + WB[j];
			TT2 = GG(E, F, G, j) + H + SS1 + W[j];
			D = C;
			C = ROL(B, 9);
			B = A;
			A = TT1;
			H = G;
			G = ROL(F, 19);
			F = E;
			E = P0(TT2);
			if (dispDebugInfo)
			{

				if (j < 10)
					System.out.print(" " + j);
				else
					System.out.print(j);
				System.out.print(" " + intToHexString(A));
				System.out.print(" " + intToHexString(B));
				System.out.print(" " + intToHexString(C));
				System.out.print(" " + intToHexString(D));
				System.out.print(" " + intToHexString(E));
				System.out.print(" " + intToHexString(F));
				System.out.print(" " + intToHexString(G));
				System.out.println(" " + intToHexString(H));

			}

		}

		state[0] ^= A;
		state[1] ^= B;
		state[2] ^= C;
		state[3] ^= D;
		state[4] ^= E;
		state[5] ^= F;
		state[6] ^= G;
		state[7] ^= H;

	}

	public void SM3_update(byte[] input, int offset, int ilen)
	{

		int fill;
		Long left;

		if (ilen <= 0)
			return;

		left = total & 0x3F; /* mod 64,buffer 剩下未处理的字节数 */
		fill = 64 - left.intValue(); /* 需填入buffer 的字节数 */

		total += ilen;

		if (left > 0 && ilen >= fill)
		{

			// arraycopy(Object src, int srcPos, Object dest, int destPos, int
			// length)
			System.arraycopy(input, offset, buffer, left.intValue(), fill);
			// C: memmove((void *)(buffer + left), (void *)input, fill);
			SM3_process(buffer, 0); /* 处理64字节即512 bits */
			offset += fill;
			ilen -= fill;
			left = 0L;

		}

		while (ilen >= 64) /* 循环处理64字节即512 bits */
		{

			SM3_process(input, offset);
			offset += 64;
			ilen -= 64;

		}

		if (ilen > 0) /* 剩下不到64字节的零头 */
		{

			System.arraycopy(input, offset, buffer, left.intValue(), ilen);
			// C: memmove((void *)(buffer + left), (void *)input, ilen);

		}

	}

	protected byte[] getResult()
	{

		// return new byte[] { (byte) (state[0] >>> 24), (byte) (state[0] >>> 16), (byte) (state[0] >>> 8), (byte) state[0], (byte) (state[1] >>> 24), (byte) (state[1] >>> 16), (byte) (state[1] >>> 8), (byte) state[1], (byte) (state[2] >>> 24), (byte) (state[2] >>> 16), (byte) (state[2] >>> 8), (byte) state[2], (byte) (state[3] >>> 24), (byte) (state[3] >>> 16), (byte) (state[3] >>> 8), (byte) state[3], (byte) (state[4] >>> 24), (byte) (state[4] >>> 16), (byte) (state[4] >>> 8), (byte) state[4], (byte) (state[5] >>> 24), (byte) (state[5] >>> 16), (byte) (state[5] >>> 8), (byte) state[5], (byte) (state[6] >>> 24), (byte) (state[6] >>> 16), (byte) (state[6] >>> 8), (byte) state[6], (byte) (state[7] >>> 24), (byte) (state[7] >>> 16), (byte) (state[7] >>> 8), (byte) state[7] };
		return new byte[] { 
				(byte) (state[0]), (byte) (state[0] >>> 8), (byte) (state[0] >>> 16), (byte) (state[0] >>> 24),
				(byte) (state[1]), (byte) (state[1] >>> 8), (byte) (state[1] >>> 16), (byte) (state[1] >>> 24), 
				(byte) (state[2]), (byte) (state[2] >>> 8), (byte) (state[2] >>> 16), (byte) (state[2] >>> 24), 
				(byte) (state[3]), (byte) (state[3] >>> 8), (byte) (state[3] >>> 16), (byte) (state[3] >>> 24),
				(byte) (state[4]), (byte) (state[4] >>> 8), (byte) (state[4] >>> 16), (byte) (state[4] >>> 24),
				(byte) (state[5]), (byte) (state[5] >>> 8), (byte) (state[5] >>> 16), (byte) (state[5] >>> 24),
				(byte) (state[6]), (byte) (state[6] >>> 8), (byte) (state[6] >>> 16), (byte) (state[6] >>> 24),
				(byte) (state[7]), (byte) (state[7] >>> 8), (byte) (state[7] >>> 16), (byte) (state[7] >>> 24)};
	}

	public byte[] SM3_finish()
	{

		Long last, padn;
		long bits; /* 消息长度bit数 */
		byte[] msglen = new byte[8]; /* 存放消息长度bit数 */

		/* 计算以bit为单位的消息长度，字节数*8即左移3位 */
		bits = total << 3;
		msglen[0] = (byte) (bits >>> 56);
		msglen[1] = (byte) (bits >>> 48);
		msglen[2] = (byte) (bits >>> 40);
		msglen[3] = (byte) (bits >>> 32);
		msglen[4] = (byte) (bits >>> 24);
		msglen[5] = (byte) (bits >>> 16);
		msglen[6] = (byte) (bits >>> 8);
		msglen[7] = (byte) bits;

		last = total & 0x3F; /* mod 64,buffer 剩下未处理的字节数 */
		/* 满足last+padn+8(msglen字节数) 为64的倍数: */
		padn = (last < 56) ? (56 - last) : (120 - last);

		SM3_update(SM3_padding, 0, padn.intValue());
		SM3_update(msglen, 0, 8);

		byte[] result = getResult(); // make a result out of context
		return result;

	}

	public byte[] getStringHash(String input)
	{
		byte[] msg = input.getBytes();
		SM3_starts();
		SM3_update(msg, 0, msg.length);
		return SM3_finish();

	}

	public String encoding(String input)
	{
		byte[] buffer = getStringHash(input);
		return byte2hex(buffer, 0, buffer.length).replace(" ", "");
	}

	public byte[] getFileHash(String filename) throws Exception
	{

		InputStream fis = new FileInputStream(filename);
		byte[] buf = new byte[1024];
		int numRead;

		SM3_starts();
		do
		{

			numRead = fis.read(buf);
			if (numRead > 0)
			{

				SM3_update(buf, 0, numRead);

			}

		}
		while (numRead != -1);
		fis.close();
		return SM3_finish();

	}

	public static String byte2hex(byte[] b, boolean hexUpperCase, boolean hexInsBlank) // 二进制转字符串
	{

		String hs = "";
//   String stmp = "";
		for (int n = 0; n < b.length; n++)
		{

			if (hexInsBlank && n > 0 && n % 4 == 0)
				hs = hs + " ";
			if (hexUpperCase)
				hs = hs + Integer.toString((b[n] & 0xFF) + 0x100, 16).substring(1).toUpperCase();
			else
				hs = hs + Integer.toString((b[n] & 0xFF) + 0x100, 16).substring(1);
//       stmp = (java.lang.Integer.toHexString(b[n] & 0xFF));
//       if (stmp.length() == 1)
//           hs = hs + "0" + stmp;
//       else
//           hs = hs + stmp;

		}
		return hs;

	}

	public static String byte2hex(byte[] b, int offset, int len) // 二进制转字符串
	{

		String hs = "";
//   String stmp = "";
		for (int n = 0; n < len; n++)
		{

			if (n > 0)
			{

				if (n % 32 == 0)
					hs = hs + "\n";
				else if (n % 4 == 0)
					hs = hs + " ";

			}
			hs = hs + Integer.toHexString((b[n] & 0xFF) + 0x100).substring(1);
//       stmp = (java.lang.Integer.toHexString(b[offset + n] & 0xFF));
//       if (stmp.length() == 1)
//           hs = hs + "0" + stmp;
//       else
//           hs = hs + stmp;

		}
		return hs;

	}

	public static void main(String[] args) throws Exception
	{
//66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0
		// SM3 sm3 = new SM3();
		SM3 sm3 = new SM3(true, true, true);
		SM3 sm3a = new SM3(true, false, true);

		System.out.println("--- SM3 test1:");
		byte[] b = sm3.getStringHash("abc");
		String s = byte2hex(b, sm3.hexUpperCase, sm3.hexInsBlank);
		System.out.println("SM3 Hash=" + s);

		System.out.println("--- SM3 test2:");
		b = sm3.getStringHash("abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd");
		s = byte2hex(b, sm3.hexUpperCase, sm3.hexInsBlank);
		System.out.println("SM3 Hash=" + s);

		System.out.println("--- SM3 test3:");
		b = sm3a.getFileHash("D://f1.txt");
		s = byte2hex(b, sm3a.hexUpperCase, sm3a.hexInsBlank);
		System.out.println("SM3 Hash=" + s);

	}

}