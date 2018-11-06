package cc.cnplay.platform.web.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;

import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.DispatcherServlet;

import cc.cnplay.core.service.InitializeService;
import cc.cnplay.platform.Constants;

public class SpringDispatcherServlet extends DispatcherServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void destroy()
	{
		executeDestroyService();
		super.destroy();
		if (Constants.executorService != null)
		{
			Constants.executorService.shutdown();
		}
	}

	@Override
	protected void initFrameworkServlet() throws ServletException
	{
		Constants.webappAbsolutePath = getServletContext().getRealPath("");
		Constants.extjsPluginsAbsolutePath = Constants.webappAbsolutePath + Constants.EXTJSPLUGINSPATH;
		if (Constants.executorService == null)
		{
			Constants.executorService = Executors.newCachedThreadPool();
		}
		Constants.executorService.execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					executeInitializeService();
					logger.info("initFrameworkServlet success");
					Constants.Installed = 1;
				}
				catch (Throwable e)
				{
					logger.error("initFrameworkServlet final", e);
					Constants.InstalledMessage = "程序启动异常：" + e.getMessage();
					Constants.Installed = -1;
				}
			}

		});
	}

	private void executeInitializeService()
	{
		List<InitializeService> serviceList = getInitializeServiceList();
		for (InitializeService service : serviceList)
		{
			try
			{
				logger.info("插件[" + AopUtils.getTargetClass(service).getName() + "]实例化");
				((InitializeService) service).init();
			}
			catch (Throwable e)
			{
				logger.error("插件[" + AopUtils.getTargetClass(service).getName() + "]实例化异常", e);
			}
		}
	}

	private void executeDestroyService()
	{
		List<InitializeService> serviceList = getInitializeServiceList();
		for (InitializeService service : serviceList)
		{
			try
			{
				logger.info("插件[" + AopUtils.getTargetClass(service).getName() + "]实例摧毁");
				((InitializeService) service).destroy();
			}
			catch (Throwable e)
			{
				logger.error("插件[" + AopUtils.getTargetClass(service).getName() + "]实例摧毁异常", e);
			}
		}
	}

	private List<InitializeService> getInitializeServiceList()
	{
		Map<String, Object> serviceMap = getWebApplicationContext().getBeansWithAnnotation(Service.class);
		serviceMap.putAll(getWebApplicationContext().getBeansWithAnnotation(Component.class));
		if (getWebApplicationContext().getParent() != null)
		{
			serviceMap.putAll(getWebApplicationContext().getParent().getBeansWithAnnotation(Service.class));
			serviceMap.putAll(getWebApplicationContext().getParent().getBeansWithAnnotation(Component.class));
		}
		List<InitializeService> list = new ArrayList<InitializeService>();
		for (Object service : serviceMap.values())
		{
			if (service instanceof InitializeService)
			{
				list.add((InitializeService) service);
			}
		}
		Collections.sort(list, new Comparator<InitializeService>()
		{
			public int compare(InitializeService arg0, InitializeService arg1)
			{
				if (arg0.getSort() == arg1.getSort())
				{
					return arg0.getClass().getName().compareTo(arg1.getClass().getName());
				}
				return arg0.getSort() > arg1.getSort() ? 1 : -1;
			}
		});
		return list;
	}
}
