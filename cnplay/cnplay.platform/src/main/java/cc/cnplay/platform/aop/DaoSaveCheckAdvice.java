package cc.cnplay.platform.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;
import cc.cnplay.platform.AbsPlatform;

@Aspect
//该注解标示该类为切面类 
@Component
//注入依赖
public final class DaoSaveCheckAdvice extends AbsPlatform
{
	private Logger logger = Logger.getLogger(DaoSaveCheckAdvice.class);

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

	@Memo("保存创建或复核用户信息")
	private void saveCreateOrCheckUser(ProceedingJoinPoint jpoint)
	{
		try
		{
			if (jpoint.getSignature() instanceof MethodSignature)
			{
				MethodSignature ms = (MethodSignature) jpoint.getSignature();
				String method = ms.getMethod().getName();
				if (method.startsWith("save") || method.startsWith("update") || method.startsWith("ins") || method.startsWith("persist") || method.startsWith("merge"))
				{
					if (jpoint.getArgs() == null)
					{
						return;
					}
					for (int i = 0; i < jpoint.getArgs().length; i++)
					{
						Object arg = jpoint.getArgs()[i];
						if (arg == null)
						{
							return;
						}
						if (arg instanceof SuperCheckEntity)
						{
							check((SuperCheckEntity) arg);
						}
					}
				}
			}
		}
		catch (Throwable ex)
		{
			logger.error("保存复核信息异常：" + ex.getMessage(), ex);
		}
	}

	@Around(value = "execution(* cc.cnplay.**.dao.**.*(..))")
	public Object around(ProceedingJoinPoint jpoint) throws Throwable
	{
		saveCreateOrCheckUser(jpoint);
		return jpoint.proceed();
	}
}
