package edu.SJTU.ChiChi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.CardAdapter;
import edu.SJTU.ChiChi.utils.FoodGenerator;
import edu.SJTU.ChiChi.utils.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-23
 * Time: 下午4:05
 */
@SuppressLint("NewApi")
public class CardListViewActivity extends Activity {
    //    private String URL = "";
    // XML node keys
//    public static final String KEY_DISH = "dish"; // parent node
//    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_GENRE = "genre";
    public static final String KEY_BUILDING = "building";
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_PRICE = "price";
    public static final String KEY_TASTE = "taste";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_THUMB_URL = "thumb_url";

    private ListView list;
    private ImageView bg;
    private ImageView bg_blurred;
    CardAdapter adapter0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ArrayList<HashMap<String, String>> dishList = new ArrayList<HashMap<String, String>>();
        bg = (ImageView) findViewById(R.id.normal_image);
        bg_blurred = (ImageView) findViewById(R.id.blurred_image);
        list = (ListView) findViewById(R.id.listView);

        list.setCacheColorHint(0);
        list.setSelected(false);

        FoodGenerator fg = new FoodGenerator();
        //如果json读取没错的话再做下一步操作
        if (fg.noerror()) {
            FoodGenerator.Food foods[] = null;
            foods = fg.getFoods(0);
            for (int i = 0; i < 1; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
//                Element e = (Element) nl.item(i);
                // adding each child node to HashMap key => value
//                map.put(KEY_ID, foods[1].getValue(e, KEY_ID));
                map.put(KEY_NAME, foods[1].name);
                map.put(KEY_GENRE, foods[1].genre);
                map.put(KEY_BUILDING, foods[1].building);
                map.put(KEY_RESTAURANT, foods[1].restaurant);
                map.put(KEY_PRICE, foods[1].price);
                map.put(KEY_TASTE, foods[1].taste);
                map.put(KEY_DESCRIPTION, foods[1].description);
                map.put(KEY_THUMB_URL, foods[1].url);

                // adding HashList to ArrayList
                dishList.add(map);
                ImageLoader imageLoader = new ImageLoader(this.getApplicationContext());
                imageLoader.DisplayImage(foods[1].url, bg);

            }
        }
        //TODO else... 如果json读取出错的话做点别的。。。

        // looping through all dish nodes <dish>
//        for (int i = 0; i < nl.getLength(); i++) {


        View footerView = new View(this);
        footerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 0));
        list.addFooterView(footerView);

        adapter0 = new CardAdapter(this, dishList, 0);
        list.setAdapter(adapter0);


        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


            }
        });

        // Scroll listener for list
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            /**
             * Listen to the list scroll.
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Calculate the ratio between the scroll amount and the list
                // header weight to determinate the top picture alpha
//                alpha = (float) -headerView.getTop() / (float) TOP_HEIGHT;
//                // Apply a ceil
//                if (alpha > 1) {
//                    alpha = 1;
//                }
//
//                // Apply on the ImageView if needed
//                if (mSwitch.isChecked()) {
//                    mBlurredImage.setAlpha(alpha);
//                }totalItemCount

                // Parallax effect : we apply half the scroll amount to our
                // three views
            	if(view.getChildAt(0)!=null){
                	Log.v("chichidebug", "top: "+String.valueOf(view.getChildAt(0).getTop()));
            	}
            	Log.v("chichidebug", "getFirstVisiblePosition: "+String.valueOf(view.getFirstVisiblePosition()));
            	Log.v("chichidebug", "firstVisibleItem: "+String.valueOf(firstVisibleItem));
            	Log.v("chichidebug", "visibleItemCount: "+String.valueOf(visibleItemCount));
            	Log.v("chichidebug", "totalItemCount: "+String.valueOf(totalItemCount));
                bg.setTop(list.getTop() / 2);
                bg_blurred.setTop(list.getTop() / 2);

            }
            
        });
    }

}
