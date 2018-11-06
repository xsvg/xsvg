package cc.cnplay.platform.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Platform;
import cc.cnplay.platform.domain.CheckLog;
import cc.cnplay.platform.domain.Status;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.AuthenticationService;
import cc.cnplay.platform.service.CheckLogService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.util.UrlUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CheckFilter extends ApplicationContextFilter
{
	public static final String MULTIPART = "multipart/";

	private static ObjectMapper om = new ObjectMapper();

	public static final boolean isMultipartContent(HttpServletRequest request)
	{
		if (!"post".equals(request.getMethod().toLowerCase()))
		{
			return false;
		}
		String contentType = request.getContentType();
		if (contentType == null)
		{
			return false;
		}
		if (contentType.toLowerCase().startsWith(MULTIPART))
		{
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> cloneParameterMap(HttpServletRequest request)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		if (!isMultipartContent(request))
		{
			params.putAll(request.getParameterMap());
		}
		params.remove("_dc");
		Map<String, Object> cloneMap = new LinkedHashMap<String, Object>();
		List<String> keyList = new ArrayList<String>(params.keySet());
		Collections.sort(keyList, new Comparator<String>()
		{
			public int compare(String key1, String key2)
			{
				return key1.compareTo(key2);
			}
		});
		for (String key : keyList)
		{
			cloneMap.put(key, params.get(key));
		}
		return cloneMap;

	}

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws IOException, ServletException
	{
		try
		{
			if (!isJsonAccept(request))
			{
				fc.doFilter(request, response);
			}
			else
			{
				AuthenticationService authenticationService = wac().getBean(AuthenticationService.class);
				CheckLogService checkLogService = wac().getBean(CheckLogService.class);
				String url = UrlUtils.buildUrl(request);
				if (authenticationService.matcher(url, none) || authenticationService.matcher(url, guest))
				{
					fc.doFilter(request, response);
				}
				else if (authenticationService.isNeedCheck(url))
				{
					Json<CheckLog> json = new Json<CheckLog>();
					User checkUser = (User) request.getSession().getAttribute(Platform.getCheckKey(request));
					if (checkUser == null)
					{
						logger.debug("请复核:" + url);
						json.setSuccess(false);
						json.setNeedCheck(true);
						json.setMsg("请复核");
					}
					else
					{
//						if (authenticationService.check(checkUser, url))
//						{
						request.setAttribute(Platform.getCheckKey(request), checkUser);
						CheckLog checkLog = (CheckLog) request.getSession().getAttribute(Platform.getCheckInfoKey(request));
						request.getSession().removeAttribute(Platform.getCheckInfoKey(request));
						request.getSession().removeAttribute(Platform.getCheckKey(request));
						String checkJson = "";
						if (checkLog != null && checkUser.equals(checkLog.getCheckUser()))
						{
							checkLog.setStatus(Status.Normal);
							checkLogService.save(checkLog);
							checkLog = checkLogService.getById(checkLog.getId());
							checkJson = checkLog.getCheckJson();
						}
						String requestJson = om.writeValueAsString(cloneParameterMap(request));
						if (!checkJson.equals(requestJson))
						{
							json.setSuccess(false);
							json.setNeedCheck(true);
							json.setMsg("复核前后数据不一致！");
							logger.warn(checkJson);
							logger.warn(requestJson);
							logger.warn("复核前后数据不一致：" + url);
						}
						else
						{
							json.setSuccess(true);
							json.setNeedCheck(false);
							json.setMsg("复核完成！");
						}
//						}
//						else
//						{
//							json.setSuccess(false);
//							json.setNeedCheck(true);
//							json.setMsg("您无该功能复核权限");
//						}
					}
					if (json.isNeedCheck())
					{
						UserService userService = wac().getBean(UserService.class);
						CheckLog checkLog = new CheckLog();
						checkLog.setStatus(Status.Delete);
						checkLog.setCreateUser(Platform.getSessionUser(userService,request));
						checkLog.setCheckJson(om.writeValueAsString(cloneParameterMap(request)));
						checkLog.setUrl(url);
						checkLog = checkLogService.save(checkLog);
						json.setRows(checkLog);
						request.getSession().setAttribute(Platform.getCheckInfoKey(request), checkLog);
						om.writeValue(response.getOutputStream(), json);
					}
					else
					{
						fc.doFilter(request, response);
					}
				}
				else
				{
					fc.doFilter(request, response);
				}
			}
		}
		catch (Throwable ex)
		{
			if (ex.getClass().getSimpleName().equals("ClientAbortException"))
			{
				logger.debug(request.getRequestURI());
			}
			else
			{
				logger.warn(request.getRequestURI(), ex);
			}
		}
	}
}
