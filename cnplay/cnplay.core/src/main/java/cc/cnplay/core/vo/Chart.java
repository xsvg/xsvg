package cc.cnplay.core.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表对应类
 * 
 * @author <a href="mailto:peixere@qq.com">裴绍国</a>
 * @version 2013-11-05
 * 
 */
public class Chart
{
	/**
	 * 主标题
	 */
	private String title;
	/**
	 * 副标题
	 */
	private String subtitle;

	private String xAxisText;

	private String yAxisText;

	private Map<String, Object> params;

	private List<ChartSerie> series;

	public Chart()
	{
		series = new ArrayList<ChartSerie>();
		params = new HashMap<String, Object>();
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getSubtitle()
	{
		return subtitle;
	}

	public void setSubtitle(String subtitle)
	{
		this.subtitle = subtitle;
	}

	public String getxAxisText()
	{
		return xAxisText;
	}

	public void setxAxisText(String xAxisText)
	{
		this.xAxisText = xAxisText;
	}

	public String getyAxisText()
	{
		return yAxisText;
	}

	public void setyAxisText(String yAxisText)
	{
		this.yAxisText = yAxisText;
	}

	public List<ChartSerie> getSeries()
	{
		return series;
	}

	public void setSeries(List<ChartSerie> series)
	{
		this.series = series;
	}

	public Map<String, Object> getParams()
	{
		return params;
	}

	public void setParams(Map<String, Object> params)
	{
		this.params = params;
	}

}
