package cc.cnplay.platform.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {

	@Override
	public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		DecimalFormat df = new DecimalFormat("#.00");
		jgen.writeString(df.format(value));
	}

}
