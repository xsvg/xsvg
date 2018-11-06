package cc.cnplay.platform.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;

import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.platform.AbsPlatform;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@Aspect
//该注解标示该类为切面类 
//@Component
//注入依赖
public final class SystemLogAdvice extends AbsPlatform
{
	private final static Logger log = Logger.getLogger(SystemLogAdvice.class.getName());
	private final static ObjectMapper om = new ObjectMapper();

	// before通知方法
	public void before()
	{

	}

	// after通知方法
	public void after()
	{

	}

	// afterReturn通知方法
	public void afterReturn()
	{

	}

	// afterThrowing通知方法
	public void afterThrowing()
	{

	}

	@Around(value = "execution(* cc.cnplay.**.service.**.*(..))")
	public Object around(ProceedingJoinPoint jpoint) throws Throwable
	{
		SystemLog systemLog = new SystemLog();
		try
		{
			systemLog.setReturnValue(jpoint.proceed());
			return systemLog.getReturnValue();
		}
		catch (Throwable e)
		{
			systemLog.setErrorMsg(e.getMessage());
			throw e;
		}
		finally
		{
			try
			{
				Ignore ignore = null;
				if (jpoint.getSignature() instanceof MethodSignature)
				{
					MethodSignature ms = (MethodSignature) jpoint.getSignature();
					ignore = ms.getMethod().getAnnotation(Ignore.class);
				}
				if (ignore == null)
				{

					systemLog.setMethod(jpoint.toString());
					systemLog.setArgs(jpoint.getArgs());
					Class<?>[] clazzs = new Class<?>[jpoint.getArgs().length];
					for (int i = 0; i < jpoint.getArgs().length; i++)
					{
						Object arg = jpoint.getArgs()[i];
						clazzs[i] = arg != null ? AopUtils.getTargetClass(arg) : null;
					}
					systemLog.setArgsType(clazzs);
					systemLog.setUrl(getURL());
					String userName = "";
					if (getSessionUser() != null)
					{
						userName = getSessionUser().getUsername();
					}
					systemLog.setUsername(userName);
					log.info(om.writeValueAsString(systemLog));
				}
			}
			catch (JsonMappingException mx)
			{
				logger.warn("保存日志异常：" + mx.getMessage());
			}
			catch (Throwable ex)
			{
				logger.error(ex.getMessage(), ex);
			}
		}
	}
}
