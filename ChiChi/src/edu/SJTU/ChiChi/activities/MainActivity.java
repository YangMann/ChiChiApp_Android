package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.views.CenterImageView;
import edu.SJTU.ChiChi.views.VerticalTextView;

public class MainActivity extends Activity {

    VerticalTextView verticalTextView;
    TextView mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String text = "蒜泥白肉粉蒸雞塊\n珍珠丸子紅燒排骨奧爾良雞";
        Typeface Sung = Typeface.createFromAsset(getAssets(), "fonts/ChekiangSung.otf");

//        verticalTextView = (VerticalTextView) findViewById(R.id.verticalTextView1);
//        verticalTextView.setColumnSpacing(2);
//        verticalTextView.setHeight(300);
//        verticalTextView.setVerticalText(text, true);
//        verticalTextView.setTypeface(Sung);

        CenterImageView backgroundImage = (CenterImageView) findViewById(R.id.background);
        backgroundImage.setImageResource(R.drawable.bg);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
