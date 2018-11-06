package cc.cnplay.platform.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Common utilities so that we don't need to include log4j
 * 
 * @author peixere@qq.com
 * @version $Revision: 11729 $ $Date: 2007-09-26 14:22:30 -0400 (Tue, 26 Sep
 *          2007) $
 * @since 3.0
 */
public final class CommonUtils
{

	private static final Logger log = Logger.getLogger(CommonUtils.class);

	public static String formatForUtcTime(final Date date)
	{
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	public static String constructRedirectUrl(final String serverLoginUrl, final String serviceParameter, final String serviceUrl)
	{
		try
		{
			return serverLoginUrl + (serverLoginUrl.indexOf("?") != -1 ? "&" : "?") + serviceParameter + "=" + URLEncoder.encode(serviceUrl, "UTF-8");
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String getServerName(HttpServletRequest r)
	{

		String scheme = r.getScheme().toLowerCase();
		String serverName = r.getServerName();
		int serverPort = r.getServerPort();
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);
		if ("http".equals(scheme))
		{
			if (serverPort != 80)
			{
				url.append(":").append(serverPort);
			}
		}
		else if ("https".equals(scheme))
		{
			if (serverPort != 443)
			{
				url.append(":").append(serverPort);
			}
		}
		return url.toString();
	}

	public static String constructUrl(final HttpServletRequest request, final HttpServletResponse response, final String service, final String serverName, final String ticketParameterName, final boolean encode)
	{
		if (StringUtils.isNotBlank(service)) { return encode ? response.encodeURL(service) : service; }
		final StringBuilder buffer = new StringBuilder();
		if (StringUtils.isNotBlank(serverName))
		{
			if (!serverName.startsWith("https://") && !serverName.startsWith("http://"))
			{
				buffer.append(request.isSecure() ? "https://" : "http://");
			}

			buffer.append(serverName);
		}
		else
		{
			buffer.append(getServerName(request));
		}
		buffer.append(request.getRequestURI());

		if (StringUtils.isNotBlank(request.getQueryString()))
		{
			final int location = request.getQueryString().indexOf(ticketParameterName + "=");

			if (location == 0)
			{
				final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
				return returnValue;
			}

			buffer.append("?");

			if (location == -1)
			{
				buffer.append(request.getQueryString());
			}
			else if (location > 0)
			{
				final int actualLocation = request.getQueryString().indexOf("&" + ticketParameterName + "=");

				if (actualLocation == -1)
				{
					buffer.append(request.getQueryString());
				}
				else if (actualLocation > 0)
				{
					buffer.append(request.getQueryString().substring(0, actualLocation));
				}
			}
		}

		final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
		return returnValue;
	}

	public static String safeGetParameter(final HttpServletRequest request, final String parameter)
	{
		if ("POST".equals(request.getMethod()) && "logoutRequest".equals(parameter))
		{
			log.debug("safeGetParameter called on a POST HttpServletRequest for LogoutRequest.  Cannot complete check safely.  Reverting to standard behavior for this Parameter");
			return request.getParameter(parameter);
		}
		return request.getQueryString() == null || request.getQueryString().indexOf(parameter) == -1 ? null : request.getParameter(parameter);
	}

	public static String getResponseFromServer(final URL constructedUrl, final HostnameVerifier hostnameVerifier, final String encoding, final String sessionId)
	{
		URLConnection conn = null;
		try
		{
			conn = constructedUrl.openConnection();
			if (conn instanceof HttpsURLConnection)
			{
				((HttpsURLConnection) conn).setHostnameVerifier(hostnameVerifier);
				((HttpsURLConnection) conn).setRequestProperty("Cookie", sessionId);
			}
			final BufferedReader in;

			if (StringUtils.isEmpty(encoding))
			{
				in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}
			else
			{
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
			}

			String line;
			final StringBuilder stringBuffer = new StringBuilder(255);

			while ((line = in.readLine()) != null)
			{
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			return stringBuffer.toString();
		}
		catch (final Exception e)
		{
			log.error(e.getMessage() + "  " + constructedUrl, e);
			throw new RuntimeException(e);
		}
		finally
		{
			if (conn != null && conn instanceof HttpURLConnection)
			{
				((HttpURLConnection) conn).disconnect();
			}
		}

	}

	public static void sendRedirect(final HttpServletResponse response, final String url)
	{
		try
		{
			response.sendRedirect(url);
		}
		catch (final Exception e)
		{
			log.warn(e.getMessage(), e);
		}

	}

	public static String getServerUrlPrefix(HttpServletRequest r, String casServerUrlPrefix)
	{
		if (!casServerUrlPrefix.startsWith("https://") && !casServerUrlPrefix.startsWith("http://"))
		{
			casServerUrlPrefix = getServerName(r) + casServerUrlPrefix;
		}
		return casServerUrlPrefix;
	}

	public final static String getInitParameter(final FilterConfig filterConfig, final String propertyName, final String defaultValue)
	{
		String value = filterConfig.getInitParameter(propertyName);
		if (StringUtils.isNotEmpty(value))
		{
			log.info("Property [" + propertyName + "] loaded from FilterConfig.getInitParameter with value [" + value + "]");
			return value;
		}
		return CommonUtils.getInitParameter(filterConfig.getServletContext(), propertyName, defaultValue);
	}

	public static String getInitParameter(final ServletContext servletContext, final String propertyName, final String defaultValue)
	{
		String value = servletContext.getInitParameter(propertyName);
		if (StringUtils.isNotBlank(value))
		{
			log.info("Property [" + propertyName + "] loaded from ServletContext.getInitParameter with value [" + value + "]");
			return value;
		}
		if (StringUtils.isEmpty(value))
		{
			value = defaultValue;
			log.info("Property [" + propertyName + "] not found.  Using default value [" + defaultValue + "]");
		}
		return defaultValue;
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens)
	{

		if (str == null) { return null; }
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

	public static String[] toStringArray(Collection<String> collection)
	{
		if (collection == null) { return null; }
		return collection.toArray(new String[collection.size()]);
	}

	public static boolean hasLength(CharSequence str)
	{
		return (str != null && str.length() > 0);
	}

	public static int countOccurrencesOf(String str, String sub)
	{
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) { return 0; }
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

	public static void printParameters(HttpServletRequest request)
	{
		@SuppressWarnings("unchecked")
		Map<String, String[]> params = request.getParameterMap();
		StringBuffer sb = new StringBuffer();
		for (String key : params.keySet())
		{
			sb.append("\n" + key + "=");
			String[] values = params.get(key);
			for (String value : values)
			{
				sb.append(value + ",");
			}
		}
		log.debug(sb);
	}

	public static String formatXML(String inputXML)
	{
		SAXReader reader = new SAXReader();
		XMLWriter writer = null;
		try
		{
			Document document = (Document) reader.read(new StringReader(inputXML));
			StringWriter stringWriter = new StringWriter();
			OutputFormat format = new OutputFormat(" ", true);
			writer = new XMLWriter(stringWriter, format);
			writer.write(document);
			writer.flush();
			inputXML = stringWriter.getBuffer().toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage());
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return inputXML;
	}
	
	public static String loginType(int k){
		String rst="";
		int ir =k & 1;
		if(ir==1){
			rst+="+用户ID";
		}
		ir=k&2;
		if(ir==2){
			rst+="+密码";
		}
		ir=k&4;
		if(ir==4){
			rst+="+指纹";
		}
		if(rst.length()>1){
			rst=rst.substring(1);
		}
		return rst;
		
		
	}
}
