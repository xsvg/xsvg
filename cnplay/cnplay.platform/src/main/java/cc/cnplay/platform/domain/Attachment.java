package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cc.cnplay.core.domain.SuperEntity;

@Entity
@Table(name = "p_attachment")
public class Attachment extends SuperEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "name", length = 255)
	private String name;

	@Column
	private String suffix;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "content_size")
	private long size;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

}
