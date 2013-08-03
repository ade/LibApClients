package se.ade.android.apclients;

/**
 * Created by ade on 2013-08-03.
 */
public class WifiDiscoveredClient {
    public enum State {
        REACHABLE,
        UNREACHABLE,
        ERROR,
        UPDATING
    }

    private String ipAddress;
    private String macAddress;
    private String inetDevice;
    private State state = State.UPDATING;

    public WifiDiscoveredClient(String ipAddress, String hWAddr, String inetDevice) {
        super();
        this.ipAddress = ipAddress;
        this.macAddress = hWAddr;
        this.inetDevice = inetDevice;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String hWAddr) {
        macAddress = hWAddr;
    }

    public String getInetDevice() {
        return inetDevice;
    }

    public void setInetDevice(String inetDevice) {
        this.inetDevice = inetDevice;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}