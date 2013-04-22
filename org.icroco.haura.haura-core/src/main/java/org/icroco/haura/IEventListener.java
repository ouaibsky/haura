/**
 * 
 */
package org.icroco.haura;

/**
 * @author christophe
 *
 */
public interface IEventListener<T>
{
	public void onChange(IEvent<T> aSensor, final T aOld, final T aNew);
}
