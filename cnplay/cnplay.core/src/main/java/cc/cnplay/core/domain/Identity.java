package cc.cnplay.core.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Identity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String randomID()
	{
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	@Id()
	@Column(name = "id", unique = true, nullable = false, length = 32)
	private String id = randomID();

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		return (o != null && o.toString().equals(this.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return (id != null ? id.hashCode() : 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		return getClass().getName() + "@id=" + this.id;
	}

}
