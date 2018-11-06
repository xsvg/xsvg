package cc.cnplay.core.spring.service;

import org.springframework.stereotype.Service;

import cc.cnplay.core.util.PasswordEncoderMessageDigest;

/**
 * PasswordEncoderImpl
 * 
 * @author <a href="mailto:peixere@qq.com">裴绍国</a>
 */
@Service
public class PasswordEncoderServiceImpl extends PasswordEncoderMessageDigest implements PasswordEncoderService
{
	public PasswordEncoderServiceImpl()
	{
		super("SM3");
		// super();
	}
}