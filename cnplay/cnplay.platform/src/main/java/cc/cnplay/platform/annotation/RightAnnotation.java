package cc.cnplay.platform.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import cc.cnplay.core.annotation.Memo;

/**
 * 注释
 * 
 * @author <mailto:peixere@qq.com>shaoguo pei</mailto>
 * 
 * @version 20130529
 */
@Retention(RUNTIME)
public @interface RightAnnotation
{
	@Memo("功能菜单名称，支持多级用'/'区分上下级关系如：系统管理/角色管理")
	String name();

	@Memo("EXTJS控件名称")
	String component() default "";

	@Memo("排列顺序")
	int sort() default 0;

	@Memo("图标css")
	String iconCls() default "";

	@Memo("图标URL")
	String icon() default "";

	@Memo("是否为按钮")
	boolean button() default false;

	@Memo("调试菜单")
	boolean debug() default false;

	@Memo("删除的功能")
	boolean delete() default false;

	@Memo("功能菜单要引用的资源")
	String resource() default "";

	@Memo("功能菜单是否要复核")
	boolean needCheck() default false;

	Class<?> clazz() default RightAnnotation.class;
}
