package org.godotengine.godot;

import android.app.Activity;
import android.util.Pair;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ConnectionController {

    private Activity activity;
    private GoogleSignInOptions signInOptions;
    private GodotCallbacksUtils godotCallbacksUtils;

    public ConnectionController(Activity activity, GoogleSignInOptions signInOptions, GodotCallbacksUtils godotCallbacksUtils) {
        this.activity = activity;
        this.signInOptions = signInOptions;
        this.godotCallbacksUtils = godotCallbacksUtils;
    }

    public  boolean isConnected() {
        return getConnectionInfo().isConnected();
    }

    public ConnectionInfo getConnectionInfo() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        
        String accId = "";
        String token = "";
        if (googleSignInAccount != null && googleSignInAccount.getId() != null) {
            accId = googleSignInAccount.getId();
            token = googleSignInAccount.getIdToken();
        }
        boolean connected = GoogleSignIn.hasPermissions(googleSignInAccount, signInOptions.getScopeArray());
        return new ConnectionInfo(connected, accId, token);
    }

    public void checkIsConnected() {
        ConnectionInfo c = getConnectionInfo();
        godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.PLAYER_CONNECTED, new Object[]{c.isConnected(), c.getAccountId()});
    }
}