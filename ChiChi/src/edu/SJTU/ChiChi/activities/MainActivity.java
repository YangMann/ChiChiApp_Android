package edu.SJTU.ChiChi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.FoodGenerator;
import edu.SJTU.ChiChi.utils.ImageLoader;
import edu.SJTU.ChiChi.views.VerticalTextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    VerticalTextView verticalTextView;
    TextView mWidth;
    ListView mList;
    ImageView mBg;
    View headerView;
    ImageLoader imageloader;
    FoodGenerator foodgenerator = new FoodGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageloader = new ImageLoader(this);
        
        FoodGenerator.Food food = foodgenerator.getFood(0);
        
        String text = "蒜泥白肉粉蒸雞塊\n珍珠丸子紅燒排骨奧爾良雞";
        Typeface Sung = Typeface.createFromAsset(getAssets(), "fonts/ChekiangSung.otf");

//        verticalTextView = (VerticalTextView) findViewById(R.id.verticalTextView1);
//        verticalTextView.setColumnSpacing(2);
//        verticalTextView.setHeight(300);
//        verticalTextView.setVerticalText(text, true);
//        verticalTextView.setTypeface(Sung);

//        CenterImageView backgroundImage = (CenterImageView) findViewById(R.id.background);
//        backgroundImage.setImageResource(R.drawable.bg1);
//
//        mWidth = (TextView) findViewById(R.id.width);
//        mWidth.setText(String.valueOf(findViewById(R.id.linearLayout).getWidth()));
        mList = (ListView) findViewById(R.id.listView);
        mBg = (ImageView) findViewById(R.id.normal_image);
        //设置背景
        imageloader.DisplayImage(food.url, mBg);

        headerView = new View(this);
        headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 700));
        mList.addHeaderView(headerView);
        String[] strings = getResources().getStringArray(R.array.list_content);
        mList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, strings));
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Parallax effect : we apply half the scroll amount to our
                mBg.setTop(headerView.getTop() / 2);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
