package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.views.VerticalTextView;

public class MainActivity extends Activity {

    VerticalTextView verticalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String text = "叶楠张阳何逸群十六\n叶楠张阳何逸群十六叶楠张阳何逸群十六";

        verticalTextView = (VerticalTextView) findViewById(R.id.verticalTextView1);
        verticalTextView.setColumnSpacing(2);
        verticalTextView.setHeight(300);
        verticalTextView.setVerticalText(text, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
