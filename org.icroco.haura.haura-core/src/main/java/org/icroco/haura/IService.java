package org.icroco.haura;


/**
 * 
 */



/**
 * @author christophe
 * 
 */
public interface IService
{
	/**
	 * @author christophe
	 * 
	 */
	public enum STATE
	{
		/**
		 * default state when service is created
		 */
		UNINITIALIZED,
		/**
		 * state once service is initialized. can only move from state
		 * {@value #UNINITIALIZED}.
		 */
		INITIALIZED,
		/**
		 * state once service is initialized. mainly after calling
		 * {@link IService#start()}. Can only move from {@link #INITIALIZED} or
		 * {@link #STOPPED}
		 */
		STARTED,
		/**
		 * state after service is stopped. mainly after calling
		 * {@link IService#stop()}. Can only move from {@link #STARTED}. After
		 * stopping, a service can be restarted by calling
		 * {@link IService#start()}.
		 */
		STOPPED,
		/**
		 * state after calling {@link IService#destroy()}. This service cannot
		 * be restarted. it's a final state.
		 */
		DESTROYED
	}

	/**
	 * start is used to start the service
	 */
	public void start();

	public void stop();

	public void destroy();

	public STATE getState();

	public boolean addListener(final IServiceListener aListener);

	public boolean removelistener(final IServiceListener aListener);

}
