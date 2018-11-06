package cc.cnplay.core.comm;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 通道类型注释
 * 
 * @author 裴绍国
 * 
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ChannelType
{
	ChannelTypeEnum value();
}
