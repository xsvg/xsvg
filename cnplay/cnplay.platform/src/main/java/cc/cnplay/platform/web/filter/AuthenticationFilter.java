package cc.cnplay.platform.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.Platform;
import cc.cnplay.platform.domain.SysConfigName;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.AuthenticationService;
import cc.cnplay.platform.service.SystemConfigService;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.util.UrlUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthenticationFilter extends ApplicationContextFilter
{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		super.init(filterConfig);
		guest = this.getInitParameter(filterConfig, "guest", guest);
		none = this.getInitParameter(filterConfig, "none", none);
		loginUrl = this.getInitParameter(filterConfig, "loginUrl", loginUrl);
		url403 = this.getInitParameter(filterConfig, "url403", url403);
	}

	@Override
	protected final void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws IOException, ServletException
	{
		try
		{
			if (Constants.Installed == 0)
			{
				forwardHome(request, response, Constants.InstalledMessage);
			}
			else if (Constants.Installed == -1)
			{
				forwardHome(request, response, Constants.InstalledMessage);
			}
			else if (Constants.Installed == 1)
			{
				AuthenticationService authenticationService = wac().getBean(AuthenticationService.class);
				SystemConfigService confogService = wac().getBean(SystemConfigService.class);
				String url = UrlUtils.buildUrl(request);
				if (authenticationService.matcher(url, none))
				{
					fc.doFilter(request, response);
				}
				else
				{
					UserService userService = wac().getBean(UserService.class);
					User user = Platform.getSessionUser(userService,request);
					if (user != null)
					{
						User u = userService.getUserById(user.getId());
						if (u != null)
						{
							u.setDebug(user.isDebug());
							request.getSession().setAttribute(Platform.getUserSessionKey(request), u);
							user = u;
						}
					}
					if (user == null)
					{
						response.setStatus(401);
						if (isJsonAccept(request))
						{
							Json<String> json = new Json<String>(confogService.getByName(SysConfigName.authorization_login_not_tip));
							json.setSuccess(false);
							json.setMsg("用户未登录");
							ObjectMapper om = new ObjectMapper();
							om.writeValue(response.getOutputStream(), json);
						}
						else
						{
							request.getRequestDispatcher(loginUrl).forward(request, response);
						}
					}
					else if (authenticationService.matcher(url, guest))
					{
						fc.doFilter(request, response);
					}
					else if (!authenticationService.check(user, url))
					{
						logger.info("权限验证失败:" + url);
						String message = confogService.getByName(SysConfigName.authorization_check_not_tip);
						powerForward(request, response, message);
					}
					else
					{
						fc.doFilter(request, response);
					}
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

	private void forwardHome(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException
	{
		logger.warn(message);
		request.setAttribute("message", message);
		request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
	}

	/**
	 * 权限验证失败
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @throws IOException
	 * @throws ServletException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 */
	private void powerForward(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException, JsonGenerationException, JsonMappingException
	{
		response.setStatus(403);
		Json<String> json = new Json<String>();
		json.setSuccess(false);
		json.setMsg(message);

		if (isJsonAccept(request))
		{
			ObjectMapper om = new ObjectMapper();
			om.writeValue(response.getOutputStream(), json);
		}
		else
		{
			request.setAttribute("json", json);
			request.getRequestDispatcher(url403).forward(request, response);
		}
	}

}
