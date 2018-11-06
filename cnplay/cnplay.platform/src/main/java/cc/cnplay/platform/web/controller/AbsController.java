package cc.cnplay.platform.web.controller;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.util.Converter;
import cc.cnplay.core.util.IReportExporter;
import cc.cnplay.platform.AbsPlatform;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.domain.User;

public abstract class AbsController extends AbsPlatform
{

	@Memo("id分隔符")
	protected final String separator = ",";

	protected String getParameter(String name)
	{
		if (getRequest() == null)
		{
			return null;
		}
		return getRequest().getParameter(name);
	}

	@Memo("兼容easyUI和extjs的分页")
	protected int getPageSize()
	{
		int pageSize = Converter.parseInt(getParameter("rows"));
		if (pageSize == 0)
		{
			pageSize = Converter.parseInt(getParameter("limit"));
		}
		if (pageSize == 0)
		{
			pageSize = 20;
		}
		return pageSize;
	}

	@Memo("兼容easyUI和extjs的分页")
	protected int getPage()
	{
		int page = Converter.parseInt(getParameter("page"));
		if (page == 0)
		{
			page = 1;
		}
		return page;
	}

	protected String getParameter(String name, String defaultValue)
	{
		String value = getRequest().getParameter(name);
		if (StringUtils.isNotEmpty(value))
		{
			return value;
		}
		else
		{
			return defaultValue;
		}
	}

	protected HttpServletResponse getResponse()
	{
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}

	protected String getSessionId()
	{
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getSessionId();
	}

	protected String getContextPath()
	{
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getContextPath();
	}

	@Memo("判断用户是否登录")
	protected boolean isLogin()
	{
		User user = this.getSessionUser();
		return user == null ? false : true;
	}

	@Memo("当前登录用户ID")
	protected String getSessionUserId()
	{
		return getSessionUser().getId();
	}

	@Memo("当前登录用户帐号")
	protected String getSessionUsername()
	{
		return getSessionUser().getUsername();
	}

	protected void expAttachment(HttpServletRequest request, HttpServletResponse response, String name, List<Map<String, ?>> listMap, String jrxml) throws Exception
	{
		JRDataSource dataSource = new JRMapCollectionDataSource(listMap);
		// 加载模版
		String url = request.getSession().getServletContext().getRealPath(jrxml);
		// 参数
		Map<String, Object> parameters = new HashMap<String, Object>();
		IReportExporter exporter = new IReportExporter(url, parameters, dataSource);
		String format = Constants.expReportFmt;
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;fileName=" + new String((name + Constants.expReportFmt).getBytes("gbk"), "iso-8859-1"));		
		OutputStream os = response.getOutputStream();
		if (!format.startsWith("."))
		{
			format = "." + format;
		}
		format = format.toLowerCase();
		if (format.equalsIgnoreCase(".pdf"))
		{
			exporter.expPDF(os);
		}
		else if (format.startsWith(".xls"))
		{
			exporter.expXls(os);
		}
//		else if (format.startsWith(".htm"))
//		{
//			exporter.expHtml(os);
//		}
		else if (format.startsWith(".doc"))
		{
			exporter.expDocx(os);
		}
		else
		{
			logger.error("不支持的格式" + Constants.expReportFmt);
		}
	}

	protected void writeResponse(HttpServletResponse response, Throwable ex)
	{
		try
		{
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html; charset=utf-8");
			PrintWriter pw = new PrintWriter(response.getOutputStream());
			pw.write(ex.getMessage());
			pw.flush();
		}
		catch (Throwable e)
		{
			logger.error("写入失败", e);
		}

	}
}
