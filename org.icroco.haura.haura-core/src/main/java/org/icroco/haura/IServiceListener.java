package org.icroco.haura;
/**
 * 
 */


import org.icroco.haura.IService.STATE;

/**
 * @author christophe
 *
 */
public interface IServiceListener
{
	public void onChange(final STATE aOldState, final STATE aNewState);
}
