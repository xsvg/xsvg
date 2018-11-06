package cc.cnplay.core.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public abstract class StringUtil
{
	protected static Logger log = Logger.getLogger(StringUtil.class);
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.err.println(removeSBC("中awefsdfqefsdfdsf039A有sefe<>国交易成功"));

//		System.err.println(matchesNumLetters("a111111111fe", 2, 12));
//		System.err.println(matchesNumLetters("在人", 2, 12));
//		System.err.println(matchesNumLetters("1111aa", 2, 12));
//		System.err.println(matchesNumLetters("1111aaaaaaaaaaaaaaaaaaaaaa", 2, 12));
		String ra = StringUtil.randomNum(12);
//		String[] codes = new String[2];
//		codes[0] = ra.substring(0, 6);
//		codes[1] = ra.substring(6, 12);
//		System.err.println(ra);
		System.err.println(randomNumCheck(ra));
	}

	private static final String FOLDER_SEPARATOR = "/";
	private static final char EXTENSION_SEPARATOR = '.';

	/**
	 * 是否为A-F
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAzF(String str)
	{
		Pattern pattern = Pattern.compile("[A-F]*");
		return pattern.matcher(str).matches();
	}

	public static boolean isNumeric(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static boolean isNullOrEmpty(String value)
	{
		return (value != null) ? (value.trim().length() == 0) : true;
	}

	public static boolean isNotEmpty(String value)
	{
		return !isNullOrEmpty(value);
	}

	public static UUID toUUID(String string)
	{
		if (string.length() == 36)
		{
			return UUID.fromString(string);
		}
		else
		{
			StringBuilder builder = new StringBuilder(36);
			builder.append(string);
			builder.insert(8, '-');
			builder.insert(13, '-');
			builder.insert(18, '-');
			builder.insert(23, '-');
			return UUID.fromString(builder.toString());
		}
	}

	public static String randomUUID32()
	{
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String toUUID32(UUID uid)
	{
		return uid.toString().replace("-", "");
	}

	public static String randomUUID36()
	{
		return UUID.randomUUID().toString();
	}

	public static String random(int length)
	{
		String password = "";
		byte sum = 0;
		for (int i = 0; i < length - 1; i++)
		{
			byte b = (byte) (Math.random() * 1000 % 16);
			password += HEX_DIGITS[b];
			sum = (byte) ((sum + b) % 16);
		}
		password += HEX_DIGITS[sum];
		return password;
	}

	public static String randomNum(int length)
	{
		StringBuffer bf = new StringBuffer();
		while (bf.length() < length)
			bf.append("" + (long) (Math.random() * 1000000000));
		int check = 0;
		StringBuffer num = new StringBuffer();
		for (int i = 0; i < length - 2; i++)
		{
			char c = bf.charAt(i);
			num.append(c);
			check = (check + (c & 0x0f));
		}
		if (check < 10)
		{
			num.append("0");
		}
		num.append(check);
		return num.toString();
	}

	public static boolean randomNumCheck(String randomNum)
	{
		int length = randomNum.length();
		if (length > 2)
		{
			int check = 0;
			for (int i = 0; i < length - 2; i++)
			{
				char c = randomNum.charAt(i);
				check = (check + (c & 0x0f));
			}
			if (check < 10)
			{
				return randomNum.endsWith("0" + check);
			}
			else
			{
				return randomNum.endsWith("" + check);
			}
		}
		return false;
	}

	private static final String PROPERTY_START_TOKEN = "${";
	private static final String PROPERTY_END_TOKEN = "}";

	public static boolean containsString(String in, String str)
	{
		return (((str == null) || (in == null) || (in.length() == 0) || (str.length() == 0)) ? false : (in.indexOf(str) >= 0));
	}

	public static String concat(String str1, String str2, char concatenator)
	{
		if (isNullOrEmpty(str1))
		{
			return str2;
		}
		else if (isNullOrEmpty(str2))
		{
			return str1;
		}
		else
		{
			return (str1 + concatenator + str2);
		}
	}

	public static String trim(String str)
	{
		return trim(str, true, false);
	}

	public static String trim(String str, boolean trimSpaces, boolean trimNonAlpha)
	{
		if ((str == null) || str.equals(""))
		{
			return "";
		}

		int length = str.length();
		StringBuffer sb = new StringBuffer(length);

		for (int i = 0; i < length; i++)
		{
			char c = str.charAt(i);

			if ((trimSpaces && Character.isSpaceChar(c)) || (trimNonAlpha && !Character.isLetterOrDigit(c)))
			{
				continue;
			}

			sb.append(c);
		}

		String key = sb.toString();

		return key.trim();
	}

	public static boolean getBoolean(String value, boolean defaultValue)
	{
		try
		{
			return Boolean.valueOf(value).booleanValue();
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	public static int getInt(String value, int defaultValue)
	{
		try
		{
			return Integer.valueOf(value).intValue();
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	public static float getFloat(String value, float defaultValue)
	{
		try
		{
			return Float.valueOf(value).floatValue();
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	public static double getDouble(String value, double defaultValue)
	{
		try
		{
			return Double.valueOf(value).doubleValue();
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	private static boolean replaceProperty(Properties properties, String value, StringBuffer buffer, int startTokenIndex, String startToken, String propertyEndToken)
	{
		int endTokenIndex = value.indexOf(propertyEndToken);
		if (endTokenIndex < 0)
		{
			return false;
		}

		String propertyName = value.substring(startTokenIndex + startToken.length(), endTokenIndex);
		String propertyValue = properties.getProperty(propertyName);
		if (propertyValue == null)
		{
			propertyValue = "";
		}

		buffer.replace(startTokenIndex, endTokenIndex + 1, propertyValue);
		return true;
	}

	public static String resolveProperties(Properties properties, String value, String startToken, String endToken)
	{
		if (isNullOrEmpty(value) || isNullOrEmpty(startToken) || isNullOrEmpty(endToken))
		{
			return value;
		}

		StringBuffer buffer = new StringBuffer(value);
		while (true)
		{
			int startTokenIndex = value.indexOf(startToken);
			if (startTokenIndex < 0)
			{
				break;
			}

			if (!replaceProperty(properties, value, buffer, startTokenIndex, startToken, endToken))
			{
				break;
			}

			value = buffer.toString();
		}

		return buffer.toString();
	}

	public static String resolveProperties(String value)
	{
		return resolveProperties(System.getProperties(), value);
	}

	public static String resolveProperties(Properties properties, String value)
	{
		return resolveProperties(properties, value, PROPERTY_START_TOKEN, PROPERTY_END_TOKEN);
	}

	public static void resolveProperties(Properties props)
	{
		Enumeration<Object> keys = props.keys();
		if (keys == null)
		{
			return;
		}

		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			String value = props.getProperty(key);
			props.setProperty(key, StringUtil.resolveProperties(props, value));
		}
	}

	public static String[] toArray(String source, String delimiter)
	{
		List<String> results = new ArrayList<String>();
		if (source != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(source, delimiter);
			while (tokenizer.hasMoreTokens())
			{
				String token = tokenizer.nextToken();
				results.add(token.trim());
			}
		}

		return results.toArray(new String[0]);
	}

	public static String getHtml(URL url)
	{
		try
		{
			StringBuffer html = new StringBuffer();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String temp;
			while ((temp = br.readLine()) != null)
			{
				html.append(temp).append("\n");
			}
			br.close();
			isr.close();
			return html.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String HTMLToTEXT(String html)
	{

		html = html.replaceAll("<([^<>]+)>", "");
		html = StringUtil.replace(html, "&nbsp;", " ");
		html = StringUtil.replace(html, "&#160;", " ");
		html = StringUtil.replace(html, "&lt;", "<");
		html = StringUtil.replace(html, "&gt;", ">");
		html = StringUtil.replace(html, "&quot;", "\"");
		html = StringUtil.replace(html, "&amp;", "&");
		return html.replaceAll("<([^<>]+)>", "");
	}

	public static String replaceHtml(String input)
	{
		if (input == null || input.trim().equals(""))
		{
			return "";
		}
		String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "");
		str = str.replaceAll("<[^>]*>", "");
		str = str.replaceAll("[(/>)<]", "");
		return str;
	}

	public static boolean hasLength(CharSequence str)
	{
		return (str != null && str.length() > 0);
	}

	public static boolean hasLength(String str)
	{
		return hasLength((CharSequence) str);
	}

	public static boolean hasText(CharSequence str)
	{
		if (!hasLength(str))
		{
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++)
		{
			if (!Character.isWhitespace(str.charAt(i)))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasText(String str)
	{
		return hasText((CharSequence) str);
	}

	public static boolean containsWhitespace(CharSequence str)
	{
		if (!hasLength(str))
		{
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++)
		{
			if (Character.isWhitespace(str.charAt(i)))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean containsWhitespace(String str)
	{
		return containsWhitespace((CharSequence) str);
	}

	public static String trimWhitespace(String str)
	{
		if (!hasLength(str))
		{
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0)))
		{
			sb.deleteCharAt(0);
		}
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1)))
		{
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String trimAllWhitespace(String str)
	{
		if (!hasLength(str))
		{
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		int index = 0;
		while (sb.length() > index)
		{
			if (Character.isWhitespace(sb.charAt(index)))
			{
				sb.deleteCharAt(index);
			}
			else
			{
				index++;
			}
		}
		return sb.toString();
	}

	/**
	 * Trim leading whitespace from the given String.
	 * 
	 * @param str
	 *            the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimLeadingWhitespace(String str)
	{
		if (!hasLength(str))
		{
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0)))
		{
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim trailing whitespace from the given String.
	 * 
	 * @param str
	 *            the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimTrailingWhitespace(String str)
	{
		if (!hasLength(str))
		{
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1)))
		{
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurences of the supplied leading character from the given String.
	 * 
	 * @param str
	 *            the String to check
	 * @param leadingCharacter
	 *            the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter)
	{
		if (!hasLength(str))
		{
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter)
		{
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurences of the supplied trailing character from the given String.
	 * 
	 * @param str
	 *            the String to check
	 * @param trailingCharacter
	 *            the trailing character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimTrailingCharacter(String str, char trailingCharacter)
	{
		if (!hasLength(str))
		{
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter)
		{
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Test if the given String starts with the specified prefix, ignoring upper/lower case.
	 * 
	 * @param str
	 *            the String to check
	 * @param prefix
	 *            the prefix to look for
	 * @see java.lang.String#startsWith
	 */
	public static boolean startsWithIgnoreCase(String str, String prefix)
	{
		if (str == null || prefix == null)
		{
			return false;
		}
		if (str.startsWith(prefix))
		{
			return true;
		}
		if (str.length() < prefix.length())
		{
			return false;
		}
		String lcStr = str.substring(0, prefix.length());
		return lcStr.equalsIgnoreCase(prefix);
	}

	/**
	 * Test whether the given string matches the given substring at the given index.
	 * 
	 * @param str
	 *            the original string (or StringBuilder)
	 * @param index
	 *            the index in the original string to start matching against
	 * @param substring
	 *            the substring to match at the given index
	 */
	public static boolean substringMatch(CharSequence str, int index, CharSequence substring)
	{
		for (int j = 0; j < substring.length(); j++)
		{
			int i = index + j;
			if (i >= str.length() || str.charAt(i) != substring.charAt(j))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Count the occurrences of the substring in string s.
	 * 
	 * @param str
	 *            string to search in. Return 0 if this is null.
	 * @param sub
	 *            string to search for. Return 0 if this is null.
	 */
	public static int countOccurrencesOf(String str, String sub)
	{
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0)
		{
			return 0;
		}
		int count = 0;
		int pos = 0;
		int idx;
		while ((idx = str.indexOf(sub, pos)) != -1)
		{
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * Replace all occurences of a substring within a string with another string.
	 * 
	 * @param inString
	 *            String to examine
	 * @param oldPattern
	 *            String to replace
	 * @param newPattern
	 *            String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern, String newPattern)
	{
		if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null)
		{
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0)
		{
			sb.append(inString.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sb.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	/**
	 * Delete all occurrences of the given substring.
	 * 
	 * @param inString
	 *            the original String
	 * @param pattern
	 *            the pattern to delete all occurrences of
	 * @return the resulting String
	 */
	public static String delete(String inString, String pattern)
	{
		return replace(inString, pattern, "");
	}

	/**
	 * Delete any character in a given String.
	 * 
	 * @param inString
	 *            the original String
	 * @param charsToDelete
	 *            a set of characters to delete. E.g. "az\n" will delete 'a's, 'z's and new lines.
	 * @return the resulting String
	 */
	public static String deleteAny(String inString, String charsToDelete)
	{
		if (!hasLength(inString) || !hasLength(charsToDelete))
		{
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < inString.length(); i++)
		{
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1)
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	// Convenience methods for working with formatted Strings
	// ---------------------------------------------------------------------
	/**
	 * Quote the given String with single quotes.
	 * 
	 * @param str
	 *            the input String (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"), or <code>null<code> if the input was <code>null</code>
	 */
	public static String quote(String str)
	{
		return (str != null ? "'" + str + "'" : null);
	}

	/**
	 * Turn the given Object into a String with single quotes if it is a String; keeping the Object as-is else.
	 * 
	 * @param obj
	 *            the input Object (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"), or the input object as-is if not a String
	 */
	public static Object quoteIfString(Object obj)
	{
		return (obj instanceof String ? quote((String) obj) : obj);
	}

	/**
	 * Unqualify a string qualified by a '.' dot character. For example, "this.name.is.qualified", returns "qualified".
	 * 
	 * @param qualifiedName
	 *            the qualified name
	 */
	public static String unqualify(String qualifiedName)
	{
		return unqualify(qualifiedName, '.');
	}

	/**
	 * Unqualify a string qualified by a separator character. For example, "this:name:is:qualified" returns "qualified" if using a ':' separator.
	 * 
	 * @param qualifiedName
	 *            the qualified name
	 * @param separator
	 *            the separator
	 */
	public static String unqualify(String qualifiedName, char separator)
	{
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * Capitalize a <code>String</code>, changing the first letter to upper case as per {@link Character#toUpperCase(char)}. No other letters are changed.
	 * 
	 * @param str
	 *            the String to capitalize, may be <code>null</code>
	 * @return the capitalized String, <code>null</code> if null
	 */
	public static String capitalize(String str)
	{
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * Uncapitalize a <code>String</code>, changing the first letter to lower case as per {@link Character#toLowerCase(char)}. No other letters are changed.
	 * 
	 * @param str
	 *            the String to uncapitalize, may be <code>null</code>
	 * @return the uncapitalized String, <code>null</code> if null
	 */
	public static String uncapitalize(String str)
	{
		return changeFirstCharacterCase(str, false);
	}

	private static String changeFirstCharacterCase(String str, boolean capitalize)
	{
		if (str == null || str.length() == 0)
		{
			return str;
		}
		StringBuilder sb = new StringBuilder(str.length());
		if (capitalize)
		{
			sb.append(Character.toUpperCase(str.charAt(0)));
		}
		else
		{
			sb.append(Character.toLowerCase(str.charAt(0)));
		}
		sb.append(str.substring(1));
		return sb.toString();
	}

	/**
	 * Extract the filename from the given path, e.g. "mypath/myfile.txt" -> "myfile.txt".
	 * 
	 * @param path
	 *            the file path (may be <code>null</code>)
	 * @return the extracted filename, or <code>null</code> if none
	 */
	public static String getFilename(String path)
	{
		if (path == null)
		{
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Extract the filename extension from the given path, e.g. "mypath/myfile.txt" -> "txt".
	 * 
	 * @param path
	 *            the file path (may be <code>null</code>)
	 * @return the extracted filename extension, or <code>null</code> if none
	 */
	public static String getFilenameExtension(String path)
	{
		if (path == null)
		{
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(sepIndex + 1) : null);
	}

	/**
	 * Strip the filename extension from the given path, e.g. "mypath/myfile.txt" -> "mypath/myfile".
	 * 
	 * @param path
	 *            the file path (may be <code>null</code>)
	 * @return the path with stripped filename extension, or <code>null</code> if none
	 */
	public static String stripFilenameExtension(String path)
	{
		if (path == null)
		{
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
	}

	/**
	 * Apply the given relative path to the given path, assuming standard Java folder separation (i.e. "/" separators).
	 * 
	 * @param path
	 *            the path to start from (usually a full file path)
	 * @param relativePath
	 *            the relative path to apply (relative to the full file path above)
	 * @return the full file path that results from applying the relative path
	 */
	public static String applyRelativePath(String path, String relativePath)
	{
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (separatorIndex != -1)
		{
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(FOLDER_SEPARATOR))
			{
				newPath += FOLDER_SEPARATOR;
			}
			return newPath + relativePath;
		}
		else
		{
			return relativePath;
		}
	}

	public static Locale parseLocaleString(String localeString)
	{
		String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
		String language = (parts.length > 0 ? parts[0] : "");
		String country = (parts.length > 1 ? parts[1] : "");
		String variant = "";
		if (parts.length >= 2)
		{
			// There is definitely a variant, and it is everything after the country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the variant.
			variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
			if (variant.startsWith("_"))
			{
				variant = trimLeadingCharacter(variant, '_');
			}
		}
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	/**
	 * Determine the RFC 3066 compliant language tag, as used for the HTTP "Accept-Language" header.
	 * 
	 * @param locale
	 *            the Locale to transform to a language tag
	 * @return the RFC 3066 compliant language tag as String
	 */
	public static String toLanguageTag(Locale locale)
	{
		return locale.getLanguage() + (hasText(locale.getCountry()) ? "-" + locale.getCountry() : "");
	}

	public static String[] toStringArray(Collection<String> collection)
	{
		if (collection == null)
		{
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}

	/**
	 * Copy the given Enumeration into a String array. The Enumeration must contain String elements only.
	 * 
	 * @param enumeration
	 *            the Enumeration to copy
	 * @return the String array ( <code>null</code> if the passed-in Enumeration was <code>null</code>)
	 */
	public static String[] toStringArray(Enumeration<String> enumeration)
	{
		if (enumeration == null)
		{
			return null;
		}
		List<String> list = Collections.list(enumeration);
		return list.toArray(new String[list.size()]);
	}

	public static String[] split(String toSplit, String delimiter)
	{
		if (!hasLength(toSplit) || !hasLength(delimiter))
		{
			return null;
		}
		int offset = toSplit.indexOf(delimiter);
		if (offset < 0)
		{
			return null;
		}
		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + delimiter.length());
		return new String[] { beforeDelimiter, afterDelimiter };
	}

	public static String[] tokenizeToStringArray(String str, String delimiters)
	{
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens)
	{

		if (str == null)
		{
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (trimTokens)
			{
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0)
			{
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] delimitedListToStringArray(String str, String delimiter)
	{
		return delimitedListToStringArray(str, delimiter, null);
	}

	public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete)
	{
		if (str == null)
		{
			return new String[0];
		}
		if (delimiter == null)
		{
			return new String[] { str };
		}
		List<String> result = new ArrayList<String>();
		if ("".equals(delimiter))
		{
			for (int i = 0; i < str.length(); i++)
			{
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		}
		else
		{
			int pos = 0;
			int delPos;
			while ((delPos = str.indexOf(delimiter, pos)) != -1)
			{
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length())
			{
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}

	/**
	 * Convert a CSV list into an array of Strings.
	 * 
	 * @param str
	 *            the input String
	 * @return an array of Strings, or the empty array in case of empty input
	 */
	public static String[] commaDelimitedListToStringArray(String str)
	{
		return delimitedListToStringArray(str, ",");
	}

	/**
	 * Convenience method to convert a CSV string list to a set. Note that this will suppress duplicates.
	 * 
	 * @param str
	 *            the input String
	 * @return a Set of String entries in the list
	 */
	public static Set<String> commaDelimitedListToSet(String str)
	{
		Set<String> set = new TreeSet<String>();
		String[] tokens = commaDelimitedListToStringArray(str);
		for (String token : tokens)
		{
			set.add(token);
		}
		return set;
	}

	public static String getMd5(String str)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++)
			{
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			log.info("Md5 32位的加密:" + buf.toString());// 32位的加密
			log.info("Md5 16位的加密:" + buf.toString().substring(8, 24));// 16位的加密
			return buf.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("Md5 " + e.getMessage(), e);
			return "";
		}
	}

	/**
	 * 生成随机数跳步密码
	 * 
	 * @param length
	 *            随机数长度
	 * @return
	 */
	public static String getRandPassword(int length)
	{
		Random random = new Random();
		String password = "";
		for (int i = 0; i < length; i++)
			password += random.nextInt(10) + "";
		return password;
	}

	public static String ToValueString(String[] inputs)
	{
		String idStr = "";
		idStr += " (";
		for (String str : inputs)
		{
			idStr += "'" + str + "',";
		}
		if (idStr.endsWith(","))
			idStr = idStr.substring(0, idStr.length() - 1);
		idStr += ")";
		return idStr;
	}

	public static String encoding(String value, String origCoding, String destCoding)
	{
		try
		{
			if (!StringUtil.isNullOrEmpty(value))
			{
				return new String(value.getBytes(origCoding), destCoding);
			}
			else
			{
				return value;
			}
		}
		catch (UnsupportedEncodingException e)
		{
			return value;
		}
	}

	public static String[] split(String utls, char[] splits)
	{
		if (utls == null)
		{
			utls = "";
		}
		if (splits == null)
		{
			splits = new char[0];
		}
		String none = utls;
		none = none.trim();
		for (char p : splits)
		{
			none = none.replace(p, ';');
		}
		if (none.endsWith(";"))
		{
			none = none.substring(0, none.length() - 1);
		}
		if (none.startsWith(";"))
		{
			none = none.substring(1, none.length());
		}
		String[] resource = none.split(";");
		return resource;
	}

	public static String[] toStringArray(String utls)
	{
		return split(utls, new char[] { '；', '#', ',', '，', '\n' });
	}

	public static String toString(Throwable tr)
	{
		if (tr == null)
		{
			return "";
		}
		Throwable t = tr;
		while (t != null)
		{
			t = t.getCause();
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		return sw.toString();
	}

	public static boolean matchesNumLetters(String value, int minLen, int maxLen)
	{
		String regex = "[0-9A-Za-z]{" + minLen + "," + maxLen + "}";
		return value.matches(regex);
	}

	/**
	 * 删除字符串中的所有全角字符
	 * @param value
	 * @return
	 */
	public static String removeSBC(String value)
	{
		if (value == null)
		{
			return value;
		}
		else
		{
			StringBuffer sb = new StringBuffer(value);
			for (int i = sb.length() - 1; i >= 0; i--)
			{
				try
				{
					byte[] bytes = (new Character(sb.charAt(i)).toString()).getBytes("GBK");
					if (bytes.length != 1)
					{
						sb.deleteCharAt(i);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			return sb.toString();
		}
	}
}
