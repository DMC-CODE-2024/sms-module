package com.ceir.CEIRPostman.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Service
public class VirtualIpAddressUtil {

    @Value("${virtualIp}")
    String virtualIp;


    private final Logger log = LogManager.getLogger(VirtualIpAddressUtil.class);

    public String getVirtualIp() {
        String vip = "null";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    log.debug("Address::" + inetAddress.getAddress() + " , HostAddress::" + inetAddress.getHostAddress() + ",HostName::" + inetAddress.getHostName());
                    log.debug("Other isSiteLocalAddress Address: " + inetAddress.isSiteLocalAddress()+ "Other isLinkLocalAddress Address: " + inetAddress.isLinkLocalAddress() + "Other isLoopbackAddress Address: " + inetAddress.isLoopbackAddress());
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && !inetAddress.isSiteLocalAddress()) {
                        log.info("Virtual IP Address: " + inetAddress.getHostAddress());
                        vip = inetAddress.getHostAddress();
                    }
                }
            }
            return vip;
        } catch (Exception e) {
            log.error("" + e);
            e.printStackTrace();
        }
        return vip;
    }

    public boolean getFullList() {
        boolean isVipFound = false;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                //         log.info("    " + intf.getName() + " " + intf.getDisplayName());
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements();) {
                    // log.info("   List 2     " + enumIpAddr.nextElement().toString());
                    if (enumIpAddr.nextElement().toString().contains(virtualIp)) {
                        isVipFound = true;
                    }
                }
            }
        } catch (SocketException e) {
            log.error(" (error retrieving network interface list)" + e);
        }
        log.info("is vip found: " + isVipFound);
        return isVipFound;
    }

}
