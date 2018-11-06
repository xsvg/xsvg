package cc.cnplay.platform.aop;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.platform.AbsPlatform;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.annotation.OperateLog;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.domain.UserLog;
import cc.cnplay.platform.service.RightService;
import cc.cnplay.platform.service.UserLogService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
// 该注解标示该类为切面类
@Component
// 注入依赖
public class UserLogAdvice extends AbsPlatform {

	@Autowired
	private UserLogService userLogService;

	@Autowired
	private RightService rightService;

	@Around(value = "execution(* cc.cnplay..controller.*.*(..))")
	public Object around(ProceedingJoinPoint jpoint) throws Throwable {
		Object rt = null;
		String proceed = "操作失败";
		User user = getSessionUser();
		try {
			rt = jpoint.proceed();
			proceed = "操作成功";
		} catch (Throwable e) {
			proceed = proceed + " " + e.getMessage();
			throw e;
		} finally {
			try {
				saveLog(jpoint, rt, proceed, user);
			} catch (Throwable ex) {
				logger.warn("保存日志异常：" + ex.getMessage());
			}
		}
		return rt;
	}

	private void saveLog(ProceedingJoinPoint jpoint, Object rt, String proceed,
			User user) {
		final UserLog uol = new UserLog();
		if (user == null) {
			user = getSessionUser();
		}
		OperateLog ol = null;
		Ignore ignore = null;
		if (jpoint.getSignature() instanceof MethodSignature) {
			MethodSignature ms = (MethodSignature) jpoint.getSignature();
			ol = ms.getMethod().getAnnotation(OperateLog.class);
			ignore = ms.getMethod().getAnnotation(Ignore.class);
		}
		if (ignore == null) {
			if (rt != null) {
				uol.setReturnClazz(rt.getClass().getSimpleName());
			}
			uol.setUrl(getURL());
			uol.setUser(user);
			uol.setMethod(jpoint.toString());
			uol.setProceed(proceed.length() < 255 ? proceed : proceed
					.substring(0, 255));
			uol.setRemoteAddr(getRequesRemoteAddr());
			uol.setRemoteUser(getRequesRemoteUser());
			if (ol != null) {
				uol.setMemo(ol.memo());
				uol.setName(ol.name());
				uol.setValue(ol.value());
			} else {
				uol.setName(rightService.getFullMenuName(uol.getUrl()));
			}
			try {
				if (StringUtils.isNotEmpty(uol.getName())) {
					ObjectMapper om = new ObjectMapper();
					if (jpoint.getArgs().length > 0) {
						StringBuffer sb = new StringBuffer();
						for (Object a : jpoint.getArgs()) {
							sb.append((a != null ? a.getClass() : "") + ";");
						}
						uol.setArgsClazz(sb.toString());
						try {
							uol.setArgsJson(om.writeValueAsString(jpoint
									.getArgs()));
							if (StringUtils.isNotEmpty(uol.getArgsJson())
									&& uol.getArgsJson().length() > 4000) {
								uol.setArgsJson(uol.getArgsJson().substring(0,
										4000));
							}
						} catch (Throwable ex) {
							logger.warn(ex.getMessage());
						}
					}
					// if (uol instanceof SuperCheckEntity)
					// {
					// check((SuperCheckEntity) uol);
					// }
					// 日志表单独配置创建时间等公共字段
					User checkUser = this.getCheckUser();
					User loginUser = this.getSessionUser();
					if (loginUser != null) {
						if (StringUtils.isEmpty(uol.getCreateUsername())) {
							uol.setCreateUsername(loginUser.getUsername());
						}
						uol.setUpdateUsername(loginUser.getUsername());
					}
					if (checkUser != null) {
						if (StringUtils.isEmpty(uol.getCreateCheckUsername())) {
							uol.setCreateCheckUsername(checkUser.getUsername());
						}
						uol.setUpdateCheckUsername(checkUser.getUsername());
					}
					uol.setUpdateTime(new Date());

				}
			} catch (Throwable ex) {
				uol.setArgsJson("");
			}
			Constants.executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						userLogService.save(uol);
					} catch (Throwable ex) {
						uol.setArgsJson("");
						userLogService.save(uol);
						logger.warn("保存日志异常：" + ex.getMessage());
					}
				}
			});

//			userLogList.add(uol);
//			if (userLogList.size() > 100) {
//				final List<UserLog> ogList = new ArrayList<UserLog>(userLogList);
//				userLogList.clear();
//				Constants.executorService.execute(new Runnable() {
//					@Override
//					public void run() {
//						for (UserLog ul : ogList) {
//							try {
//								userLogService.save(ul);
//							} catch (Throwable ex) {
//								ul.setArgsJson("");
//								userLogService.save(ul);
//								logger.warn("保存日志异常：" + ex.getMessage());
//							}
//						}
//					}
//				});
//			}
		}

	}
}
