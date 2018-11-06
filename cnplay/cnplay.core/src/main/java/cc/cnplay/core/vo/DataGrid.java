package cc.cnplay.core.vo;

import java.util.ArrayList;
import java.util.List;

import cc.cnplay.core.annotation.Memo;

/**
 * 
 * 分页类(从第一页开始)
 * 
 * @author <a href="mailto:pqixere@qq.com">裴绍国</a>
 * @version 2013-11-17
 */
@Memo("分页类(从第一页开始)")
public class DataGrid<T> extends Json<Object>
{

	@Memo("页号,从1开始")
	private int pageNum = 1;
	@Memo("每页行数")
	private int pageSize = 25;
	@Memo("总行数")
	private int total;
	@Memo("总页数")
	private int size;
	@Memo("是否有下一页")
	private boolean next;
	@Memo("是否有上一页")
	private boolean prev;
	private int start;
	private int limit;

	@Memo("分页")
	public DataGrid()
	{
		this.setSuccess(true);
		this.setRows(new ArrayList<T>());
	}

	/**
	 * 分页实例化
	 * 
	 * @param total
	 *            总行数
	 * @param list
	 *            当前页数据
	 * @param pageSize
	 *            每页行数
	 * @param pageNum
	 *            当前页码
	 */
	public DataGrid(int total, List<T> list, int pageSize, int pageNum)
	{
		this.setSuccess(true);
		this.total = total;
		this.pageSize = pageSize;
		setRows(list);
		this.pageNum = pageNum;
		init();
	}

	/**
	 * 分页实例化
	 * 
	 * @param list
	 *            当前页数据
	 * @param total
	 *            总行数
	 * @param start
	 *            开始行 从0开始
	 * @param limit
	 *            结束行
	 */
	public DataGrid(List<T> list, int total, int start, int limit)
	{
		this.setSuccess(true);
		this.total = total;
		this.pageSize = (limit - start) + 1;
		setRows(list);
		this.pageNum = (limit < total) ? limit / pageSize : (total / limit);
		this.pageNum = this.pageNum + 1;
		init();
	}

	private void init()
	{
		getSize();
		getPrev();
		getNext();
		this.start = (pageNum - 1) * pageSize + 1;
		this.limit = pageNum * pageSize;
	}

	private int size()
	{
		int totalSize = total / pageSize;
		return total % pageSize > 0 ? totalSize + 1 : totalSize;
	}

	public boolean getNext()
	{
		next = pageNum < size();
		return next;
	}

	public int getSize()
	{
		size = size();
		return size;
	}

	public boolean getPrev()
	{
		prev = pageNum > 1;
		return prev;
	}

	public int getStart()
	{
		return start;
	}

	public int getLimit()
	{
		return limit;
	}

	public int getPageNum()
	{
		return pageNum;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public int getTotal()
	{
		return total;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getRows()
	{
		return (List<T>) super.getRows();
	}
}
