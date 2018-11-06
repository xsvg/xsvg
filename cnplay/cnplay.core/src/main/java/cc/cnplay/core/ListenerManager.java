package cc.cnplay.core;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager<T>
{
	private final transient List<Listener<T>> listeners;

	public ListenerManager()
	{
		listeners = new ArrayList<Listener<T>>();
	}

	public synchronized void post(Object sender, T e)
	{
		if (listeners.size() > 0)
		{
			try
			{
				List<Listener<T>> tmps = new ArrayList<Listener<T>>(listeners);
				for (Listener<T> l : tmps)
				{
					try
					{
						l.onListener(sender, e);
					}
					catch (Throwable ex)
					{
						ex.printStackTrace();
					}
				}
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public synchronized void add(Listener<T> l)
	{
		if (!listeners.contains(l) && l != null)
		{
			listeners.add(l);
		}
	}

	public synchronized void remove(Listener<T> l)
	{
		if (listeners.contains(l) && l != null)
		{
			listeners.remove(l);
		}
	}

	public void clear()
	{
		listeners.clear();
	}

	public int size()
	{
		return listeners.size();
	}
}
