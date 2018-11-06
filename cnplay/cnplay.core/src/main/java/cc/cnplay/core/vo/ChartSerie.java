package cc.cnplay.core.vo;

import java.util.ArrayList;
import java.util.List;

public class ChartSerie
{
	private String name;
	private boolean dataLabelEnabled;
	private List<ChartSeriePoint> data;

	public ChartSerie()
	{
		data = new ArrayList<ChartSeriePoint>();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<ChartSeriePoint> getData()
	{
		return data;
	}

	public void setData(List<ChartSeriePoint> data)
	{
		this.data = data;
	}

	public boolean isDataLabelEnabled()
	{
		return dataLabelEnabled;
	}

	public void setDataLabelEnabled(boolean dataLabelEnabled)
	{
		this.dataLabelEnabled = dataLabelEnabled;
	}

}
