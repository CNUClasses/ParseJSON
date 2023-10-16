/**
 *
 */
package com.example.parsejson;

import static android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED;
import static android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class ConnectivityCheck {
    //this class provides basic internet connectivity checks
    //see https://developer.android.com/reference/android/net/NetworkCapabilities
    //for other capabilities

    private Context context;

    ConnectivityCheck(Context context) {
        this.context = context;
    }

    public boolean isNetworkReachable() {
        //can you get to the internet?
        NetworkCapabilities caps = getNetworkInfo();
        return (caps == null)?false:(caps.hasCapability(NET_CAPABILITY_VALIDATED));
    }

    public boolean isUnmeteredReachable() {
        //can you connect to a network that does not charge? (many WiFi's, cellular to a pint)
        NetworkCapabilities caps = getNetworkInfo();
        return (caps == null)?false:(caps.hasCapability(NET_CAPABILITY_NOT_METERED));
    }

    private NetworkCapabilities getNetworkInfo() {
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();

        if (currentNetwork == null)
            return null;    //no network

        return connectivityManager.getNetworkCapabilities(currentNetwork);
    }
}
