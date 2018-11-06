package cc.cnplay.platform;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.UserService;
import cc.cnplay.platform.util.UrlUtils;

public abstract class AbsPlatform
{
	protected static final Logger logger = Logger.getLogger(AbsPlatform.class);

	protected HttpServletRequest getRequest()
	{
		if (RequestContextHolder.getRequestAttributes() == null)
			return null;
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	protected String getRequesRemoteAddr()
	{
		HttpServletRequest request = getRequest();
		if (request != null)
		{
			return request.getRemoteAddr();
		}
		return "";
	}

	protected String getRequesRemoteUser()
	{
		HttpServletRequest request = getRequest();
		if (request != null)
		{
			return request.getRemoteUser();
		}
		return "";
	}

	@SuppressWarnings("rawtypes")
	protected Map<?, ?> getParameterMap()
	{
		if (getRequest() == null)
		{
			return new HashMap();
		}
		return getRequest().getParameterMap();
	}

	protected String getSessionId()
	{
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getSessionId();
	}

	protected HttpSession getSession()
	{
		if (getRequest() == null)
		{
			return null;
		}
		return getRequest().getSession();
	}

	protected String getURL()
	{
		if (getRequest() == null)
		{
			return null;
		}
		return UrlUtils.buildUrl(getRequest());
	}

	@Autowired
	private UserService userService;

	@Memo("当前登录用户")
	protected User getSessionUser()
	{
		return Platform.getSessionUser(userService,getRequest());
	}

	protected void removeUserSession()
	{
		HttpSession session = getSession();
		if (session != null)
		{
			session.removeAttribute(Platform.getUserSessionKey(this.getRequest()));
		}
	}

	@Memo("设置登录用户SESSION")
	protected void setUserSession(User user)
	{
		HttpSession session = getSession();
		if (session == null)
		{
			return;
		}
		this.getSession().setAttribute(Platform.getUserSessionKey(this.getRequest()), user);
	}

	@Memo("当前复核用户")
	protected User getCheckUser()
	{
		if (getRequest() == null)
		{
			return null;
		}
		User checkUser = (User) getRequest().getAttribute(Platform.getCheckKey(getRequest()));
		return checkUser;
	}

	@Memo("当前确认用户")
	protected User getConfirmUser()
	{
		if (getRequest() == null)
		{
			return null;
		}
		return Platform.getConfirmUser(getRequest());
	}

	protected void check(SuperCheckEntity e)
	{
		User checkUser = this.getCheckUser();
		User loginUser = this.getSessionUser();
		if (loginUser != null)
		{
			if (StringUtils.isEmpty(e.getCreateUsername()))
			{
				e.setCreateUsername(loginUser.getUsername());
			}
			e.setUpdateUsername(loginUser.getUsername());
		}
		if (checkUser != null)
		{
			if (StringUtils.isEmpty(e.getCreateCheckUsername()))
			{
				e.setCreateCheckUsername(checkUser.getUsername());
			}
			e.setUpdateCheckUsername(checkUser.getUsername());
		}
		e.setUpdateTime(new Date());
	}
}
