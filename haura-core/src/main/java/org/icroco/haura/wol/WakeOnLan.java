/**
 * 
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
 * @author christophe
 * 
 */
public class WakeOnLan
{
	private static Logger			logger			= Logger.getLogger("org.icroco.haura.wol");

	public static void main(String[] args) throws IOException
	{
		logger.info("Default Broadcast Address is: " + WakeUpUtil.DEFAULT_BCAST.getCanonicalHostName());

		if (args.length < 1)
		{
			System.out.println("Usage: java WakeOnLan mac-address (broadcast-ip)?");
			System.out.println("Example: java WakeOnLan 00-0D-61-08-22-4A");
			System.out.println("Example: java WakeOnLan 00:0D:61:08:22:4A 192.168.0.255 ");
			System.exit(1);
		}

		String macStr = args[0];
		String bcastStr;
		if (args.length >= 2)
			bcastStr = args[1];

		try
		{
			EthernetAddress eAddr = new EthernetAddress(macStr);
			
			WakeUpUtil.wakeup(eAddr);

			System.out.println("Wake-on-LAN packet sent.");
		} catch (Exception e)
		{
			System.err.println("Failed to send Wake-on-LAN packet: + e");
			System.exit(1);
		}

	}


}
