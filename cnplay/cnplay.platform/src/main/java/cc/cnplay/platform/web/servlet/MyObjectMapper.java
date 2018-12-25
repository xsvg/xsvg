package cc.cnplay.platform.web.servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyObjectMapper extends ObjectMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyObjectMapper() {
		super();
		enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
	}

}
