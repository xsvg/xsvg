package cc.cnplay.core.spring;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import cc.cnplay.jdbcconfig.ConfigEncode;

public class LocalJPAEntityManagerFactoryBean extends LocalContainerEntityManagerFactoryBean
{
	protected final Logger logger = Logger.getLogger(this.getClass());

	public void setJpaProperties(Properties jpaProperties)
	{
		logger.info("程序正在启动...");
		ResourceBundle rb = ResourceBundle.getBundle("jdbc", Locale.ROOT);
		String jdbcUrl = rb.getString("jdbc.url").toLowerCase();
		String schema = jpaProperties.getProperty("hibernate.default_schema");
		if (jdbcUrl.startsWith("jdbc:mysql"))
		{
			jpaProperties.remove("hibernate.default_schema");
		}
		else if (jdbcUrl.indexOf("sqlserver") >= 0 || jdbcUrl.indexOf("sybase") >= 0)
		{
			if (StringUtils.isEmpty(schema))
			{
				jpaProperties.remove("hibernate.default_schema");
			}
		}
		else if (StringUtils.isEmpty(schema))
		{
			boolean encode = false;
			String username = rb.getString("jdbc.username");
			if (rb.containsKey("jdbc.encode"))
			{
				String encodeStr = rb.getString("jdbc.encode");
				if (encodeStr == null)
				{
					encodeStr = "false";
				}
				encode = Boolean.parseBoolean(encodeStr);
			}
			if (encode)
			{
				username = ConfigEncode.decryptData(rb.getString("jdbc.username"));
			}
			jpaProperties.put("hibernate.default_schema", username);
		}

		logger.info("jdbc.url=" + rb.getString("jdbc.url"));
		logger.info("jdbc.username=" + rb.getString("jdbc.username"));
		logger.info("jdbc.password=" + rb.getString("jdbc.password"));
		logger.info("hibernate.default_schema=" + jpaProperties.getProperty("hibernate.default_schema"));
		super.setJpaProperties(jpaProperties);
	}
}
