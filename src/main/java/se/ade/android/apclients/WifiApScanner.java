package se.ade.android.apclients;

/**
 * Created by ade on 2013-08-03.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import libcore.io.ErrnoException;

public class WifiApScanner {

    private enum TcpPollResult {
        CONNECTED,
        REFUSED,
        UNKNOWN_HOST,
        TIMEOUT,
        NO_ROUTE_TO_HOST,
        OTHER_CONNETION_EXCEPTION,
        OTHER_ERRNO_EXCEPTION,
        OTHER
    }

    private ExecutorService threadExecutor = Executors.newCachedThreadPool();

    /**
     * Gets a list of the clients connected to the Hotspot
     * @return ArrayList of {@link WifiDiscoveredClient}
     */
    public void getClientList(final WifiResultUpdatedListener listener) {
        BufferedReader fileReader = null;
        final ArrayList<WifiDiscoveredClient> result = new ArrayList<WifiDiscoveredClient>();
        final WifiClientScan clientScan = new WifiClientScan(result, listener);

        try {
            fileReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] entry = line.split(" +");

                if ((entry != null) && (entry.length >= 4)) {
                    //Make sure entry (hw address) is well-formed.
                    if (entry[3].matches("..:..:..:..:..:..")) {
                        final WifiDiscoveredClient client = new WifiDiscoveredClient(entry[0], entry[3], entry[5]);
                        result.add(client);

                        threadExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    boolean available = false;
                                    TcpPollResult hostAvailableResult = hostAvailable(client.getIpAddress());
                                    Log.d("WifiApScanner", "TCP Poll result for " + client.getIpAddress() + ": " + hostAvailableResult.name());
                                    switch (hostAvailableResult) {
                                        case CONNECTED:
                                        case REFUSED:
                                            available = true;
                                    }

                                    if(available || InetAddress.getByName(client.getIpAddress()).isReachable(3000)) {
                                        client.setState(WifiDiscoveredClient.State.REACHABLE);
                                    } else {
                                        client.setState(WifiDiscoveredClient.State.UNREACHABLE);
                                    }
                                } catch (Exception e) {
                                    client.setState(WifiDiscoveredClient.State.ERROR);
                                }
                                listener.onUpdated(clientScan, client);

                                //Check if entire scan is completed.
                                boolean finished = true;
                                for(WifiDiscoveredClient c : clientScan.getClients()) {
                                    if(c.getState() == WifiDiscoveredClient.State.UPDATING)
                                        finished = false;
                                }

                                if(finished) {
                                    listener.onFinished(clientScan);
                                }
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), e.getMessage());
            }
        }
    }

    public static TcpPollResult hostAvailable(String ip) {
        int port = 80;

        try {
            SocketAddress addr = new InetSocketAddress(ip, port);
            new Socket().connect(addr, 5000);
            return TcpPollResult.CONNECTED;
        } catch (UnknownHostException ex) {
            return TcpPollResult.UNKNOWN_HOST;
        } catch (SocketTimeoutException timeoutException) {
            return TcpPollResult.TIMEOUT;
        } catch (ConnectException e) {
            if(e.getCause() instanceof ErrnoException) {
                ErrnoException errnoException = (ErrnoException)e.getCause();
                switch (errnoException.errno) {
                    case 113:
                        return TcpPollResult.NO_ROUTE_TO_HOST;
                    case 111:
                        return TcpPollResult.REFUSED;
                    default:
                        return TcpPollResult.OTHER_ERRNO_EXCEPTION;
                }
            } else {
                return TcpPollResult.OTHER_CONNETION_EXCEPTION;
            }
        } catch (IOException ex) {
            return TcpPollResult.OTHER;
        }
    }



}