package tech.steampunk.kinetic.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Created by Vamshi on 9/18/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
//        Toast.makeText(context, status , Toast.LENGTH_SHORT).show();
    }
}
