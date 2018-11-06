package cc.cnplay.core.matcher;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UrlMatcherAnt implements UrlMatcher
{
	private boolean requiresLowerCaseUrl = true;
	private PathMatcher pathMatcher = new PathMatcherAnt();

	public UrlMatcherAnt()
	{
		this(true);
	}

	public UrlMatcherAnt(boolean requiresLowerCaseUrl)
	{
		this.requiresLowerCaseUrl = requiresLowerCaseUrl;
	}

	public Object compile(String path)
	{
		if (requiresLowerCaseUrl)
		{
			return path.toLowerCase(Locale.getDefault());
		}

		return path;
	}

	public void setRequiresLowerCaseUrl(boolean requiresLowerCaseUrl)
	{
		this.requiresLowerCaseUrl = requiresLowerCaseUrl;
	}

	public boolean pathMatchesUrl(Object path, String url)
	{
		if ("/**".equals(path) || "**".equals(path))
		{
			return true;
		}
		return pathMatcher.match((String) path, url);
	}

	public String getUniversalMatchPattern()
	{
		return "/**";
	}

	public boolean requiresLowerCaseUrl()
	{
		return requiresLowerCaseUrl;
	}

	public String toString()
	{
		return getClass().getName() + "[requiresLowerCase='" + requiresLowerCaseUrl + "']";
	}

	public static void main(String[] args)
	{
		Map<String, String> resMap = new HashMap<String, String>();
		resMap.put("/admin/*/*/*.jsp", "ADMIN");
		resMap.put("/*/*.html", "ADMIN");
		resMap.put("/**/**.js", "ADMIN");
		resMap.put("/static/**", "ADMIN");
		UrlMatcher matcher = new UrlMatcherAnt();
		for (String res : resMap.keySet())
		{
			if (matcher.pathMatchesUrl(res, "/static/aaa/bb/index.jsp"))
			{
				System.out.println(res);
			}
			if (matcher.pathMatchesUrl(res, "/aa/a/aa.js"))
			{
				System.out.println(res);
			}
			if (matcher.pathMatchesUrl(res, "/aaa.html"))
			{
				System.out.println(res);
			}			
		}
	}
}
