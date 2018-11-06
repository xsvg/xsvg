package cc.cnplay.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoderMessageDigest implements PasswordEncoder
{
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private String encodingAlgorithm;

	private String characterEncoding;

	public PasswordEncoderMessageDigest()
	{
		this("MD5");
	}

	public PasswordEncoderMessageDigest(final String encodingAlgorithm)
	{
		this.encodingAlgorithm = encodingAlgorithm;
	}

	@Override
	public String encode(final String password)
	{
		if (password == null)
		{
			return null;
		}
		try
		{
			if (encodingAlgorithm == null || encodingAlgorithm.trim().length() == 0 || encodingAlgorithm.equalsIgnoreCase("none"))
			{
				return password;
			}
			if (encodingAlgorithm.equalsIgnoreCase("SM3"))
			{
				SM3 sm3 = new SM3();
				return Converter.toHexString(sm3.getStringHash(password), "");
				// return getFormattedText(sm3.getStringHash(password));
			}
			MessageDigest messageDigest = MessageDigest.getInstance(this.encodingAlgorithm);

			if (this.characterEncoding != null && this.characterEncoding.length() > 0)
			{
				messageDigest.update(password.getBytes(this.characterEncoding));
			}
			else
			{
				messageDigest.update(password.getBytes());
			}

			final byte[] digest = messageDigest.digest();

			return getFormattedText(digest);
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new SecurityException(e);
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Takes the raw bytes from the digest and formats them correct.
	 * 
	 * @param bytes
	 *            the raw bytes from the digest.
	 * @return the formatted bytes.
	 */
	private String getFormattedText(final byte[] bytes)
	{
		final StringBuilder buf = new StringBuilder(bytes.length * 2);

		for (int j = 0; j < bytes.length; j++)
		{
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	public String getEncodingAlgorithm()
	{
		return encodingAlgorithm;
	}

	public String getCharacterEncoding()
	{
		return characterEncoding;
	}

	@Override
	public void setCharacterEncoding(final String characterEncoding)
	{
		this.characterEncoding = characterEncoding;
	}

	@Override
	public void setEncodingAlgorithm(String encodingAlgorithm)
	{
		this.encodingAlgorithm = encodingAlgorithm;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		PasswordEncoderMessageDigest pe = new PasswordEncoderMessageDigest("SM3");
		System.err.println(pe.encode("abc"));

	}

}
