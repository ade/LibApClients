package se.ade.android.apclients;

import java.util.List;

/**
 * Created by ade on 2013-08-03.
 */
public interface WifiResultUpdatedListener {
    public void onUpdated(WifiClientScan clientScan, WifiDiscoveredClient clientUpdated);
    public void onFinished(WifiClientScan clientScan);
}
