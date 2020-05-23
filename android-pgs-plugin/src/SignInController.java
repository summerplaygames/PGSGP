package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignInController {

    public final static int RC_SIGN_IN = 77;

    private Activity activity;
    private GodotCallbacksUtils godotCallbacksUtils;
    private ConnectionController connectionController;
    private boolean showPlayPopups = true;

    public SignInController(Activity activity, GodotCallbacksUtils godotCallbacksUtils, ConnectionController connectionController) {
        this.activity = activity;
        this.godotCallbacksUtils = godotCallbacksUtils;
        this.connectionController = connectionController;
    }

    public void setShowPopups(boolean enablePopUps) {
        this.showPlayPopups = enablePopUps;
    }

    public void signIn(final GoogleSignInClient googleSignInClient) {
        ConnectionInfo connection = connectionController.getConnectionInfo();
        if (connection.isConnected()) {
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{connection.getAccountId(), connection.getToken()});
            enablePopUps();
        } else {
            googleSignInClient
                    .silentSignIn()
                    .addOnCompleteListener(activity, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if (task.isSuccessful()) {
                                GoogleSignInAccount googleSignInAccount = task.getResult();
                                String accId = "";
                                String token = "";
                                if (googleSignInAccount != null && googleSignInAccount.getId() != null) {
                                    accId = googleSignInAccount.getId();
                                    token = googleSignInAccount.getIdToken();
                                }

                                godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{accId, token});
                                enablePopUps();

                            } else {
                                Intent intent = googleSignInClient.getSignInIntent();
                                activity.startActivityForResult(intent, RC_SIGN_IN);
                            }
                        }
                    });
        }
    }

    public void onSignInActivityResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult != null && googleSignInResult.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            String accId = "";
            String token = "";
            if (googleSignInAccount != null && googleSignInAccount.getId() != null) {
                accId = googleSignInAccount.getId();
                token = googleSignInAccount.getIdToken();
            }
            enablePopUps();
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_SUCCESSFUL, new Object[]{accId, token});
        } else {
        	int statusCode = Integer.MIN_VALUE;
        	String statusMessage = "";
        	if (googleSignInResult != null && googleSignInResult.getStatus() != null){
        		statusCode = googleSignInResult.getStatus().getStatusCode();
        		if (googleSignInResult.getStatus().getStatusMessage() != null) {
                    statusMessage = googleSignInResult.getStatus().getStatusMessage();
                }
        	}
            godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGNIN_FAILED, new Object[]{statusCode, statusMessage});
        }
    }
    
    public void signOut(GoogleSignInClient googleSignInClient) {
        googleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGN_OUT_SUCCESS, new Object[]{});
                } else {
                    godotCallbacksUtils.invokeGodotCallback(GodotCallbacksUtils.SIGN_OUT_FAILED, new Object[]{});
                }
            }
        });
    }

    private void enablePopUps() {
        if (showPlayPopups) {
            GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
            if (lastSignedInAccount != null) {
                Games.getGamesClient(activity, lastSignedInAccount).setViewForPopups(activity.findViewById(android.R.id.content));
            }
        }
    }
}
