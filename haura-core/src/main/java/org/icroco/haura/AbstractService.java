/**
 * 
 */
package org.icroco.haura;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author christophe
 * 
 */
@ThreadSafe
public abstract class AbstractService implements IService
{
	protected static final Logger			LOG	= LoggerFactory.getLogger(AbstractService.class);

	protected final ReadWriteLock			lock;
	private final List<IServiceListener>	listeners;
	private volatile STATE				state;
	private volatile String				name;

	/**
	 * 
	 */
	public AbstractService()
	{
		lock = new ReentrantReadWriteLock();
		listeners = new ArrayList<IServiceListener>(2);
		state = STATE.UNINITIALIZED;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		lock.readLock().lock();
		try
		{
			return name;
		} finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * @param aName
	 *            the name to set
	 */
	public final void setName(String aName)
	{
		lock.writeLock().lock();
		try
		{
			name = aName;
		} finally
		{
			lock.writeLock().unlock();
		}
	}

	/*
	 * @param aOldState
	 * @param aState
	 */
	private void fireChange(STATE aOldState, STATE aNewState)
	{
		if (aOldState != aNewState)
		{
			try
			{
				LOG.info(String.format("%1$S State changed, old:%2$14s new:%3$14s", this, aOldState, aNewState));
				lock.readLock().lock();
				for (final IServiceListener listener : listeners)
				{
					listener.onChange(aOldState, aNewState);
				}
			} finally
			{
				lock.readLock().unlock();
			}
		}

	}

	@Override
	public final void start()
	{
		lock.writeLock().lock();
		try
		{
			if (state == STATE.STOPPED)
			{
				initService();
				state = STATE.INITIALIZED;
				fireChange(STATE.UNINITIALIZED, STATE.INITIALIZED);
				startService();
				state = STATE.STARTED;
				fireChange(STATE.STOPPED, STATE.STARTED);
			} else
			{
				if (state == STATE.UNINITIALIZED)
				{
					initService();
					state = STATE.INITIALIZED;
					fireChange(STATE.UNINITIALIZED, STATE.INITIALIZED);
				}
				if (state == STATE.INITIALIZED)
				{
					startService();
					state = STATE.STARTED;
					fireChange(STATE.INITIALIZED, STATE.STARTED);
				}
			}
		} catch (Exception e)
		{
			LOG.error("Failed to start service: {}, currentState: {}", getName(), getState(), e);
		} finally
		{
			lock.writeLock().unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.icroco.domo.api.IService#stop()
	 */
	@Override
	public final void stop()
	{
		lock.writeLock().lock();
		try
		{
			if (state == STATE.STARTED)
			{
				stopService();
				state = STATE.STOPPED;
				fireChange(STATE.STARTED, STATE.STOPPED);
			}
		} catch (Exception e)
		{
			LOG.error("Failed to stop service: {}, currentState: {}", getName(), getState(), e);
		} finally
		{
			lock.writeLock().unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.icroco.domo.api.IService#destroy()
	 */
	@Override
	public final void destroy()
	{
		lock.writeLock().lock();
		try
		{
			if (state == STATE.STARTED)
			{
				stopService();
				state = STATE.STOPPED;
				fireChange(STATE.STARTED, STATE.STOPPED);
			}

			destroyService();
			final STATE oldState = state;
			state = STATE.DESTROYED;
			fireChange(oldState, STATE.DESTROYED);

		} catch (Exception e)
		{
			LOG.error("Failed to destroy service: {}, currentState: {}", getName(), getState(), e);
		} finally
		{
			lock.writeLock().unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Override this to init service. this call is thread safe
	 * 
	 */
	protected abstract void initService() throws Exception;

	/**
	 * Override this to start service. this call is thread safe
	 * 
	 */
	protected abstract void startService() throws Exception;

	/**
	 * Override this to stop service. this call is thread safe
	 * 
	 */
	protected abstract void stopService() throws Exception;

	/**
	 * Override this to stop destroy. this call is thread safe
	 * 
	 */
	protected abstract void destroyService() throws Exception;

	/*
	 * (non-Javadoc)
	 * @see org.icroco.domo.api.IServices#getState()
	 */
	@Override
	public STATE getState()
	{
		lock.readLock().lock();
		try
		{
			return state;
		} finally
		{
			lock.readLock().unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.icroco.domo.api.IServices#addListener(org.icroco.domo.api.
	 * IServiceListener)
	 */
	@Override
	public boolean addListener(IServiceListener aListener)
	{
		if (aListener == null)
			return false;
		lock.writeLock().lock();
		try
		{
			if (listeners.contains(aListener))
				return false;
			listeners.add(aListener);
			return true;
		} finally
		{
			lock.writeLock().unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.icroco.domo.api.IServices#removelistener(org.icroco.domo.api.
	 * IServiceListener)
	 */
	@Override
	public boolean removelistener(IServiceListener aListener)
	{
		if (aListener == null)
			return false;
		lock.writeLock().lock();
		try
		{
			return listeners.remove(aListener);
		} finally
		{
			lock.writeLock().unlock();
		}
	}
}
