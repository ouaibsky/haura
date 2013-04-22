/**
 * 
 */
package org.icroco.haura;

import java.util.concurrent.ExecutorService;

/**
 * @author christophe
 *
 * @param <T>
 */
public interface IEvent<T> extends Comparable<IEvent<T>>
{
	public IEvent<T> setId(String aId);
	public String getId();
	
	public IEvent<T> setLabel(final String aLabel);
	public String getLabel();
	
	public boolean addListener(final IEventListener<T> aListener);
	public boolean removeListener(final IEventListener<T> aListener);

	public IEvent<T> setExecutor(final ExecutorService aService);
	
}