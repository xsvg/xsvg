package cc.cnplay.platform.vo;

import java.io.Serializable;

import cc.cnplay.platform.domain.Right;

/**
 * 添加角色时，一次性加载权限使用
 */
public class RightVO extends Right implements Serializable
{
	private static final long serialVersionUID = 1L;

	private boolean expanded;

	private java.util.List<RightVO> children;

	private boolean checked;

	public boolean isExpanded()
	{
		return expanded;
	}

	public void setExpanded(boolean expanded)
	{
		this.expanded = expanded;
	}

	public java.util.List<RightVO> getChildren()
	{
		return children;
	}

	public void setChildren(java.util.List<RightVO> children)
	{
		this.children = children;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

}
