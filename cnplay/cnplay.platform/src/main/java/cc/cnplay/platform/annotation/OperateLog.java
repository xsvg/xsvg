package cc.cnplay.platform.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import cc.cnplay.core.annotation.Memo;

@Memo("操作日志注解")
@Retention(RUNTIME)
public @interface OperateLog
{
	String name();

	String value() default "";

	String memo() default "";
}
