package se.ade.android.apclients;

import java.util.ArrayList;

/**
 * Created by ade on 2013-08-03.
 */
public class WifiClientScan {
    private ArrayList<WifiDiscoveredClient> clients;
    private WifiResultUpdatedListener listener;

    public WifiClientScan(ArrayList<WifiDiscoveredClient> clients, WifiResultUpdatedListener listener) {
        this.clients = clients;
        this.listener = listener;
    }

    public WifiResultUpdatedListener getListener() {
        return listener;
    }

    public void setListener(WifiResultUpdatedListener listener) {
        this.listener = listener;
    }

    public ArrayList<WifiDiscoveredClient> getClients() {
        return clients;
    }
}
