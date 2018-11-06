package cc.cnplay.platform.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cc.cnplay.platform.util.CommonUtils;
import cc.cnplay.platform.util.UrlUtils;

public abstract class ApplicationContextFilter implements Filter
{
	protected final Logger logger = Logger.getLogger(getClass());
	protected String acceptJSON = "text/json";

	protected String loginUrl = "/WEB-INF/views/login.jsp";

	protected String url403 = "/WEB-INF/views/403.jsp";

	protected String none = "/static/**;/log*";

	protected String guest = "/*.*;/home/**;/plugins/**";

	protected FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		this.filterConfig = filterConfig;
	}

	@Override
	public void destroy()
	{

	}

	protected boolean isJsonAccept(HttpServletRequest request) throws IOException, ServletException
	{
		String xRequestedWith = request.getHeader("X-Requested-With");
		if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") >= 0)
		{
			return true;
		}
		String accept = request.getHeader("accept");
		return accept != null && accept.indexOf(acceptJSON) >= 0;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException
	{
		String url = UrlUtils.buildUrl((HttpServletRequest) req);
		if (!url.replace(" ", "").equals(url))
		{
			((HttpServletResponse) res).sendRedirect(url.replace(" ", ""));
		}
		else
		{
			doFilter((HttpServletRequest) req, (HttpServletResponse) res, fc);
		}
	}

	protected final String getInitParameter(final FilterConfig filterConfig, final String propertyName, final String defaultValue)
	{
		return CommonUtils.getInitParameter(filterConfig, propertyName, defaultValue);
	}

	protected WebApplicationContext wac()
	{
		return WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
	}

	protected <T> T getBean(Class<T> type)
	{
		return wac().getBean(type);
	}

	protected Object getBean(String name)
	{
		return wac().getBean("");
	}

	protected abstract void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws IOException, ServletException;
}
