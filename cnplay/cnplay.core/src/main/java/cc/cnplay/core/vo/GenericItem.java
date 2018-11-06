package cc.cnplay.core.vo;

public class GenericItem<T>
{
	private T id;
	private String text;

	public GenericItem()
	{
	}

	public GenericItem(T id, String text)
	{
		this.id = id;
		this.text = text;
	}

	public T getId()
	{
		return id;
	}

	public void setId(T id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

}
