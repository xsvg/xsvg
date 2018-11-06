package cc.cnplay.platform.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * 功能菜单资源信息表
 * 
 * @author peixere@qq.com
 * 
 * @version 2012-12-03
 * 
 */
@Entity
@Table(name = "p_right")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Right extends SuperCheckEntity implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Column(name = "parent_id", length = 36)
	private String parentId;

	@Memo("菜单名称")
	@Column(name = "text", length = 50)
	private String text;

	@Memo("菜单详细名称")
	@Column(name = "name", length = 100)
	private String name;

	@Memo("排列顺序")
	@Column(name = "sort")
	private Integer sort = 0;

	@Memo("图标css")
	@Column(name = "icon_cls", length = 30)
	private String iconCls;

	@Memo("图标URL")
	@Column(name = "icon", length = 100)
	private String icon;

	@Memo("资源类型")
	@Column(name = "right_type", length = 10)
	private String type = RightType.URL.name();

	@Memo("控件或连接地址")
	@Column(name = "component", length = 100)
	private String component;

	@Memo("控制主URL")
	@Column(name = "url", length = 100)
	private String url;

	@Memo("是否叶子")
	@Column(columnDefinition = "char(1) default 0")
	private Boolean leaf = false;

	@Memo("关联资源")
	@Column(name = "resources")
	private String resource;

	@Memo("状态")
	@Column(name = "status", length = 1)
	private Status status = Status.Normal;

	@Memo("调试菜单")
	@Column(name = "debug", length = 10)
	private String debug;

	@Memo("来源类名")
	@Column(name = "from_class", updatable = false)
	private String fromClass;

	@Memo("是否须复核")
	@Column(name = "need_check", columnDefinition = "char(1) default 0")
	private Boolean needCheck = false;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "plugins_id", referencedColumnName = "id", updatable = false)
	private Plugins plugins;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "right")
	private List<RoleRight> roleRights = new ArrayList<RoleRight>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "right")
	private List<UserRight> userRights = new ArrayList<UserRight>();

	@Transient
	private String statusStr;

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public Integer getSort()
	{
		return sort != null ? sort : 0;
	}

	public void setSort(Integer sort)
	{
		this.sort = sort;
	}

	public String getIconCls()
	{
		return iconCls;
	}

	public void setIconCls(String iconCls)
	{
		this.iconCls = iconCls;
	}

	public String getIcon()
	{
		return icon;
	}

	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getComponent()
	{
		return component;
	}

	public void setComponent(String component)
	{
		this.component = component;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Boolean getLeaf()
	{
		return leaf != null ? leaf : Boolean.FALSE;
	}

	public void setLeaf(Boolean leaf)
	{
		this.leaf = leaf;
	}

	public String getResource()
	{
		return resource;
	}

	public void setResource(String resource)
	{
		this.resource = resource;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public void setStatus(String status)
	{
		this.status = Status.valueOf(status);
	}

	public void setStatus(int status)
	{
		this.status = Status.valueOf(status);
	}

	public String getStatusStr()
	{
		switch (status)
		{
		case Normal:
			statusStr = "正常";
			break;
		case Banned:
			statusStr = "挂起";
			break;
		case Delete:
			statusStr = "删除";
			break;
		default:
			break;
		}
		return statusStr;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDebug()
	{
		return debug;
	}

	public void setDebug(String debug)
	{
		this.debug = debug;
	}

	public String getFromClass()
	{
		return fromClass;
	}

	public void setFromClass(String fromClass)
	{
		this.fromClass = fromClass;
	}

	public Boolean getNeedCheck()
	{
		return needCheck != null ? needCheck : Boolean.FALSE;
	}

	public void setNeedCheck(Boolean needCheck)
	{
		this.needCheck = needCheck;
	}

	public Plugins getPlugins()
	{
		return plugins;
	}

	public void setPlugins(Plugins plugins)
	{
		this.plugins = plugins;
	}
}