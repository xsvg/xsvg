package cc.cnplay.core.vo;

import java.util.List;

public class TreeChildrenModel extends TreeCheckedModel
{
	private List<TreeChildrenModel> children;

	public List<TreeChildrenModel> getChildren()
	{
		return children;
	}

	public void setChildren(List<TreeChildrenModel> children)
	{
		this.children = children;
	}

}
