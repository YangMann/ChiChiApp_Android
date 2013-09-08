package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import edu.SJTU.ChiChi.R;

import java.util.Locale;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private long ms = 0;
    private boolean SPLASH_ACTIVE = true;
    private boolean PAUSED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        String displayLanguage = Locale.getDefault().getDisplayLanguage();
        TextView splashAppName = (TextView) findViewById(R.id.splashAppName);

        if (displayLanguage.equals("English")) {
            Typeface helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-ThEx.otf");
            splashAppName.setTypeface(helvetica);
        } else if (displayLanguage.equals("中文")) {
            Typeface kangxi = Typeface.createFromAsset(getAssets(), "fonts/Kangxi.ttf");
            splashAppName.setTypeface(kangxi);
        }
        if (!isConnected(this)) {
            new AlertDialog.Builder(SplashScreen.this).setTitle(getResources().getString(R.string.disconnected))
                    .setMessage(getResources().getString(R.string.disconnectedMessage))
                    .setPositiveButton(getResources().getString(R.string.confirmButton),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(0);
                                }
                            })
                    .show();
        }


        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, CardListViewActivity.class);
                startActivity(i);

                // close this activity
                if (!isConnected(SplashScreen.this)) {

                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivityForResult(intent, 0);
                    System.exit(0);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ChiChi.isConnected", e.toString());
        }
        return false;
    }

}
