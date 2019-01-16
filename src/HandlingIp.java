
import java.net.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Markus Hölzle & Christian Werder
 * @version 1.0 alpha
 *
 */
public class HandlingIp {

    /**
     *
     * @param ip
     * @return String[]
     */
    public static String[] ipToString(InetAddress ip) {
        String[] ip_s = ip.toString().split("/")[1].split(Pattern.quote("."));
        return ip_s;
    }

    public static String[] getFirstPrivateHostIp(String isClass) {
        String standard_ip = "";

        if ("C".equals(isClass)) {
            standard_ip = "192.168.0.1";
        } else if ("B".equals(isClass)) {
            standard_ip = "172.16.0.1";
        } else if ("A".equals(isClass)) {
            standard_ip = "10.0.0.1";
        }
        return standard_ip.split(Pattern.quote("."));
    }

    public static String[] getFirstPrivateHostIp(String[] mask) {
        String standard_ip = "";
        if ("255".equals(mask[0]) && "255".equals(mask[1]) && "255".equals(mask[2])) {
            standard_ip = "192.168.0.1";
        } else if ("255".equals(mask[0]) && "255".equals(mask[1])) {
            standard_ip = "172.16.0.1";
        } else {
            standard_ip = "10.0.0.1";
        }
        return standard_ip.split(Pattern.quote("."));
    }

    /**
     *
     * @param address
     * @return String
     */
    public static String toBinaryString(String[] address) {
        String[] segments = new String[4];

        for (int i = 0; i < address.length; i++) {
            segments[i] = Integer.toBinaryString(0x100 | Integer.parseInt(address[i])).substring(1);
        }
        return segments[0] + "." + segments[1] + "." + segments[2] + "." + segments[3];
    }

    /**
     *
     * @param ip
     * @return String[]
     * @throws SocketException
     */
    public static short getSubnetBits(InetAddress ip) throws SocketException {
        List<InterfaceAddress> ifaces = NetworkInterface.getByInetAddress(ip).getInterfaceAddresses();
        return ifaces.get(0).getNetworkPrefixLength();
    }

    /**
     *
     * @param bits
     * @return String[]
     */
    public static String[] subBitsToString(short bits) {
        long bit = 0xffffffff ^ (1 << 32 - bits) - 1;
        String mask = String.format("%d.%d.%d.%d", (bit & 0x0000000000ff000000L) >> 24, (bit & 0x0000000000ff0000) >> 16, (bit & 0x0000000000ff00) >> 8, bit & 0xff);
        return mask.split(Pattern.quote("."));
    }

    /**
     *
     * @param mask
     * @return String[]
     */
    public static String[] toStandardSubnetMask(String[] mask) {
        String subnetmask = "";

        if ("255".equals(mask[0]) && "255".equals(mask[1]) && "255".equals(mask[2])) {
            subnetmask = "255.255.255.0";
        } else if ("255".equals(mask[0]) && "255".equals(mask[1])) {
            subnetmask = "255.255.0.0";
        } else {
            subnetmask = "255.0.0.0";
        }
        return subnetmask.split(Pattern.quote("."));
    }

    /**
     *
     * @param bits
     * @return String[]
     */
    public static String[] toStandardSubnetMask(short bits) {
        return toStandardSubnetMask(subBitsToString(bits));
    }

    /**
     *
     * @param isClass
     * @return String[]
     */
    public static String[] getStandardSubnetMaskFromClass(String isClass) {
        String subnetmask = "";

        if ("C".equals(isClass)) {
            subnetmask = "255.255.255.0";
        } else if ("B".equals(isClass)) {
            subnetmask = "255.255.0.0";
        } else if ("A".equals(isClass)) {
            subnetmask = "255.0.0.0";
        } else {
            subnetmask = "255.255.255.255";
        }
        return subnetmask.split(Pattern.quote("."));
    }

    /**
     *
     * @param ip_address
     * @return String
     */
    public static String getClass(String[] ip_address) {
        String h_bits = Integer.toBinaryString(Integer.parseInt(ip_address[0]));

        String isClass = "";

        if ("0".equals(h_bits.substring(0))) {
            isClass = "A";
        } else if ("10".equals(h_bits.substring(0, 2))) {
            isClass = "B";
        } else if ("110".equals(h_bits.substring(0, 3))) {
            isClass = "C";
        } else if ("1110".equals(h_bits.substring(0, 4))) {
            isClass = "D";
        } else {
            isClass = "E";
        }

        return isClass;
    }

    /**
     *
     * @param ip_address
     * @return String
     */
    public static String getClass(InetAddress ip_address) {
        return getClass(ipToString(ip_address));
    }

    /**
     *
     * @param subnetBits
     * @return int
     */
    public static int subnetBitsToSubnets(int subnetBits) {
        return (int) Math.pow(2.0, subnetBits);
    }

    /**
     *
     * @param hostBits
     * @return int
     */
    public static int getMaxHosts(int hostBits) {
        return (int) Math.pow(2.0, hostBits) - 2;
    }

    public static int hostsToMinimumHostBits(int minimalHosts, String isClass) throws Exception {
        int current = 0;
        int hosts = 0;

        while (hosts < minimalHosts) {
            current++;
            hosts = (int) Math.pow(2.0, current) - 2;
        }

        int bits = 0;

        if ("A".equals(isClass)) {
            bits = 24 - current;
        } else if ("B".equals(isClass)) {
            bits = 16 - current;
        } else if ("C".equals(isClass)) {
            bits = 8 - current;
        } else {
            throw new Exception("Class D or E not supportet for subnetting");
        }

        return bits;
    }

    /**
     *
     * @param isClass
     * @param subnetBits
     * @return int
     * @throws Exception
     */
    public static int getHostBits(String isClass, int subnetBits) throws Exception {
        int bits = 0;

        if ("A".equals(isClass)) {
            bits = 24 - subnetBits;
        } else if ("B".equals(isClass)) {
            bits = 16 - subnetBits;
        } else if ("C".equals(isClass)) {
            bits = 8 - subnetBits;
        } else {
            throw new Exception("Class D or E not supportet for subnetting");
        }
        return bits;
    }

    public static int getHostBits(InetAddress ip_address, int subnetBits) throws Exception {
        return getHostBits(getClass(ip_address), subnetBits);
    }

}
