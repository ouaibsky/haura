/*
 * $Id: WakeUpUtil.java,v 1.7 2004/05/17 21:58:59 gon23 Exp $
 */
package org.icroco.haura.wol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to wake up wake-on-lan enabled machines.
 * 
 * @author <a href=
 *         "&#109;&#97;&#105;&#108;&#116;&#111;&#58;&#115;&#46;&#109;&#111;&#108;&#100;&#97;&#110;&#101;&#114;&#64;&#103;&#109;&#120;&#46;&#110;&#101;&#116;"
 *         >Steffen Moldaner</a>
 */
public final class WakeUpUtil
{
	/**
	 * The default broadcast address retreive from first interface.
	 */
	public static final InetAddress	DEFAULT_BCAST;
	/**
	 * The default wakeup port: 9
	 */
	public static final int			DEFAULT_PORT	= 9;
	private static Logger			logger			= Logger.getLogger("org.icroco.haura.wol");
	static
	{
		DEFAULT_BCAST = retrieveBCast();
	}

	private WakeUpUtil()
	{
		super();
	}

	private final static InetAddress retrieveBCast()
	{
		Enumeration<NetworkInterface> interfaces;
		final List<InetAddress> result = new ArrayList<InetAddress>();
		try
		{
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				final NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback())
					continue;    // Don't want to broadcast to the loopback
								// interface
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
				{
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;
					// Use the address
					result.add(broadcast);
				}
				if (result.size() > 1)
				{
					logger.warning("many BCast found, we take the first. " + result);
				}

			}
		} catch (SocketException aException)
		{
			logger.log(Level.SEVERE, "Failed to retreive broadcast address", aException);
		}
		return result.get(0);
	}

	/**
	 * Wakes up the machines with the provided ethernet addresses, using the
	 * default port and host.
	 * 
	 * @param aEthernetAddresses
	 *            the ethernet addresses to wake up
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see #DEFAULT_HOST
	 * @see #DEFAULT_PORT
	 */
	public static void wakeup(EthernetAddress... aEthernetAddresses) throws IOException
	{
		WakeUpUtil.wakeup(DEFAULT_BCAST, DEFAULT_PORT, aEthernetAddresses);
	}

	/**
	 * Wakes up the machines with provided ethernet address. Equal to
	 * <code>WakeUpUtil.wakeup(ethernetAddresses, host, DEFAULT_PORT);</code>
	 * 
	 * @param aEthernetAddresses
	 *            the ethernet addresses to wake up
	 * @param aHost
	 *            the host, the magic sequence will be send to
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see #DEFAULT_PORT
	 */
	public static void wakeup(InetAddress aHost, EthernetAddress... aEthernetAddresses) throws IOException
	{
		WakeUpUtil.wakeup(aHost, DEFAULT_PORT, aEthernetAddresses);
	}

	/**
	 * Wakes up the machines with provided ethernet addresses. The magic
	 * sequences are sent to the given host and port.
	 * 
	 * @param aEthernetAddresses
	 *            the ethernet addresses to wake up
	 * @param aHost
	 *            the host, the magic sequence will be send to
	 * @param aPort
	 *            the port number
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void wakeup(InetAddress aHost, int aPort, EthernetAddress... aEthernetAddresses) throws IOException
	{
		DatagramSocket socket = new DatagramSocket();

		for (int i = 0; i < aEthernetAddresses.length; i++)
		{
			byte[] wakeupFrame = createWakeupFrame(aEthernetAddresses[i]);

			DatagramPacket packet = new DatagramPacket(wakeupFrame, wakeupFrame.length, aHost, aPort);

			socket.send(packet);
		}
	}

	/**
	 * Creates the byte representation of a wakeupframe for the given ethernet
	 * address.
	 * 
	 * @param aEthernetAddress
	 *            the ethernet address
	 * 
	 * @return a byte representation of the wakeupframe
	 */
	protected final static byte[] createWakeupFrame(EthernetAddress aEthernetAddress)
	{
		final byte[] ethernetAddressBytes = aEthernetAddress.toBytes();
		final byte[] wakeupFrame = new byte[6 + 16 * ethernetAddressBytes.length];

		Arrays.fill(wakeupFrame, 0, 6, (byte) 0xFF);

		for (int j = 6; j < wakeupFrame.length; j += ethernetAddressBytes.length)
		{
			System.arraycopy(ethernetAddressBytes, 0, wakeupFrame, j, ethernetAddressBytes.length);
		}

		return wakeupFrame;
	}
}

