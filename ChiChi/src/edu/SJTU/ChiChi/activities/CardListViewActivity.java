package edu.SJTU.ChiChi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.Blur;
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
@SuppressLint({"NewApi", "HandlerLeak"})
public class CardListViewActivity extends Activity {
    // JSON node keys
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

    public static final int MSG_JSON_FETCHED = 0;
    public static final int MSG_JSON_FAILED = 1;
    public static final int MSG_SPLASH_FINISHED = 2;

    private static final int SPLASH_TIME = 0;
    public static final double PARALLAX_RATIO = 2.5;
    public static final int MAX_SHIFT = 500;
    public static final int BLUR_RADIUS = 20;

    private ListView list;
    private ImageView bg;
    private ImageView bg_blurred;
    private Bitmap blurred_img;
    private float blur_alpha;
    CardAdapter adapter0;

    ArrayList<HashMap<String, String>> dishList = new ArrayList<HashMap<String, String>>();

    MainHandler mHandler = new MainHandler();
    FoodGenerator fg = new FoodGenerator();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bg = (ImageView) findViewById(R.id.normal_image);
        bg_blurred = (ImageView) findViewById(R.id.blurred_image);
        list = (ListView) findViewById(R.id.listView);

        list.setCacheColorHint(0);
        list.setSelected(false);


        View footerView = new View(this);
        footerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 0));
        list.addFooterView(footerView);

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

                // Parallax effect : we apply half the scroll amount to our
                // two views
                if (list.getChildAt(0) != null) {
                    if (-list.getChildAt(0).getTop() < MAX_SHIFT) {
                        blur_alpha = (float) -list.getChildAt(0).getTop() / (float) MAX_SHIFT;
                        if (blur_alpha > 1) {
                            blur_alpha = 1;
                        }
                        bg_blurred.setAlpha(blur_alpha);
                        bg.setTop((int) Math.round(list.getChildAt(0).getTop() / PARALLAX_RATIO));
                        bg_blurred.setTop((int) Math.round(list.getChildAt(0).getTop() / PARALLAX_RATIO));
                    }
                }

            }
        });

        new Thread(new FetchJSON()).start();
        new Thread(new DelayRun(SPLASH_TIME, MSG_SPLASH_FINISHED)).start();
    }

    class MainHandler extends Handler {
        boolean json_fetched = false;
        boolean splash_finished = false;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_JSON_FETCHED:
                    json_fetched = true;
                    loadData();
                    break;
                case MSG_JSON_FAILED:
                    break;
                case MSG_SPLASH_FINISHED:
                    splash_finished = true;
                    loadData();
                    break;
            }
        }

        private void loadData() {
            if (!json_fetched || !splash_finished) return;

            FoodGenerator.Food foods[] = null;
            foods = fg.getFoods(0);
            for (int i = 0; i < 1; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_NAME, foods[1].name);
                map.put(KEY_GENRE, foods[1].genre);
                map.put(KEY_BUILDING, foods[1].building);
                map.put(KEY_RESTAURANT, foods[1].restaurant);
                map.put(KEY_PRICE, foods[1].price);
                map.put(KEY_TASTE, foods[1].taste);
                map.put(KEY_DESCRIPTION, foods[1].description);
                map.put(KEY_THUMB_URL, foods[1].url);

                dishList.add(map);
                ImageLoader imageLoader = new ImageLoader(getApplicationContext());
                imageLoader.DisplayImage(foods[1].url, bg);
                blurred_img = Blur.fastBlur(CardListViewActivity.this, imageLoader.getBitmap(foods[1].url), BLUR_RADIUS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bg_blurred.setImageBitmap(blurred_img);
                    }
                });

            }
            adapter0 = new CardAdapter(CardListViewActivity.this, dishList, 0);
            list.setAdapter(adapter0);
        }

    }

    class FetchJSON implements Runnable {

        @Override
        public void run() {
            fg.fetchjson();
            Message msg = new Message();
            if (fg.noerror())
                msg.what = MSG_JSON_FETCHED;
            else
                msg.what = MSG_JSON_FAILED;
            mHandler.sendMessage(msg);
        }
    }

    class DelayRun implements Runnable {
        int delaytime;
        int msgwhat;

        public DelayRun(int dtime, int msg) {
            delaytime = dtime;
            msgwhat = msg;
        }

        public DelayRun(int dtime) {
            this(dtime, -1);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delaytime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (msgwhat != -1) {
                Message msg = new Message();
                msg.what = msgwhat;
                mHandler.sendMessage(msg);
            }
        }
    }

}
