package cc.cnplay.core;

import java.util.EventListener;

public interface Listener<T> extends EventListener
{
	public abstract boolean onListener(Object sender, T e);
}
