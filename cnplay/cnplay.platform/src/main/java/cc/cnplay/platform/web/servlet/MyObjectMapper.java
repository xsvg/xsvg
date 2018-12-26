package cc.cnplay.platform.web.servlet;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MyObjectMapper extends ObjectMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyObjectMapper() {
		super();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(BigDecimal.class, new BigDecimalJsonSerializer());
		registerModule(simpleModule);
		enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
	}

}
