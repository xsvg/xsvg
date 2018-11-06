package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperEntity;

@Entity
@Table(name = "p_sys_config_log")
public class SysConfigLog extends SuperEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Memo("配置信息")
	@Column(name = "json_value", length = 1024)
	private String jsonValue;

	@Memo("配置信息")
	@Column(name = "jsonClazz", length = 512)
	private String jsonClazz;

	public String getJsonValue()
	{
		return jsonValue;
	}

	public void setJsonValue(String jsonValue)
	{
		this.jsonValue = jsonValue;
	}

	public String getJsonClazz()
	{
		return jsonClazz;
	}

	public void setJsonClazz(String jsonClazz)
	{
		this.jsonClazz = jsonClazz;
	}

}
