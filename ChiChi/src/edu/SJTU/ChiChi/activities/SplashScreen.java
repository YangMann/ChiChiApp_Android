package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import edu.SJTU.ChiChi.R;

import java.util.Locale;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

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
            Typeface sung = Typeface.createFromAsset(getAssets(), "fonts/ChekiangSung.otf");
            splashAppName.setTypeface(sung);
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
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
