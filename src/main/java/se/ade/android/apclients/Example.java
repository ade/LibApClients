package se.ade.android.apclients;

import android.util.Log;

/**
 * User: ade
 * Date: 2013-08-03 19:13
 */
public class Example {
    public void example() {
        //Warning:
        //Remember to use Activity.runOnUiThread(new Runnable...) when updating iu components

        new WifiApScanner().getClientList(new WifiResultUpdatedListener() {
            @Override
            public void onUpdated(WifiClientScan clientScan, WifiDiscoveredClient clientUpdated) {
                //Incrememtally update UI here if you wish
            }

            @Override
            public void onFinished(WifiClientScan clientScan) {
                //The list is finished at this point
                for(WifiDiscoveredClient client : clientScan.getClients()) {
                    Log.d("LibApClients Example", "Discovered: " + client.getIpAddress());
                }
            }
        });
    }
}
