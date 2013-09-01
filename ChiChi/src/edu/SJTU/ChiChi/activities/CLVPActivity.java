package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.layouts.Pull2RefreshBase;
import edu.SJTU.ChiChi.utils.Blur;
import edu.SJTU.ChiChi.utils.CardAdapter;
import edu.SJTU.ChiChi.utils.FoodGenerator;
import edu.SJTU.ChiChi.utils.ImageLoader;
import edu.SJTU.ChiChi.views.Pull2RefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-9-1
 * Time: 下午3:02
 */
public class CLVPActivity extends Activity implements Pull2RefreshBase.OnRefreshListener<ListView> {

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
    public static final int MSG_BLUR_FINISHED = 3;

    public static final int PARAM_SPLASH_TIME = 0;
    public static final double PARAM_PARALLAX_RATIO = 2.5;
    public static final int PARAM_MAX_SHIFT = 500;
    public static final int PARAM_BLUR_RADIUS = 20;
    public static final int PARAM_SCREEN_COUNT = 6;

    private ViewPager viewPager;
    private ListView[] listViews;
    private Bitmap[] blurred_imgs = new Bitmap[PARAM_SCREEN_COUNT];
    private ImageView[] normalBackgrounds;
    private ImageView[] blurredBackgrounds;
    private CardAdapter[] cardAdapters;

    ArrayList<HashMap<String, String>> dishList = new ArrayList<HashMap<String, String>>();
    MainHandler mainHandler = new MainHandler();
    FoodGenerator foodGenerator = new FoodGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmp);

        viewPager = (ViewPager) findViewById(R.id.main_vp);

        viewPager.setAdapter(new CLVPAdapter());
    }

    @Override
    public void onRefresh(Pull2RefreshBase<ListView> refreshView) {
        new GetDataTask(refreshView).execute();
    }

    private class CLVPAdapter extends PagerAdapter {

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            Context context = container.getContext();

            cardAdapters = new CardAdapter[PARAM_SCREEN_COUNT];
            listViews = new ListView[PARAM_SCREEN_COUNT];
            normalBackgrounds = new ImageView[PARAM_SCREEN_COUNT];
            blurredBackgrounds = new ImageView[PARAM_SCREEN_COUNT];

            Pull2RefreshListView[] pull2RefreshListViews = new Pull2RefreshListView[PARAM_SCREEN_COUNT];

            for (int i = 0; i < PARAM_SCREEN_COUNT; i++) {

                Pull2RefreshListView pull2RefreshListView = (Pull2RefreshListView) LayoutInflater.from(context).inflate(
                        R.layout.list_view_main, container, false);
                normalBackgrounds[i] = (ImageView) pull2RefreshListView.findViewById(R.id.normal_image);
                blurredBackgrounds[i] = (ImageView) pull2RefreshListView.findViewById(R.id.blurred_image);
                pull2RefreshListViews[i] = pull2RefreshListView;

            }

            new Thread(new FetchJSON()).start();
            new Thread(new DelayRun(PARAM_SPLASH_TIME, MSG_SPLASH_FINISHED)).start();

            for (int i = 0; i < PARAM_SCREEN_COUNT; i++) {

                pull2RefreshListViews[i].setAdapter(cardAdapters[i]);
                pull2RefreshListViews[i].setOnRefreshListener(CLVPActivity.this);

            }

            container.addView(pull2RefreshListViews[position],
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return pull2RefreshListViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return PARAM_SCREEN_COUNT;
        }

    }

    private class MainHandler extends Handler {
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
                case MSG_BLUR_FINISHED:
                    showBlur();
            }
        }

        private void loadData() {
            if (!json_fetched || !splash_finished) return;

            FoodGenerator.Food food;
            food = foodGenerator.randomFood();
            for (int i = 0; i < PARAM_SCREEN_COUNT; i++) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_NAME, food.name);
                map.put(KEY_GENRE, food.genre);
                map.put(KEY_BUILDING, food.building);
                map.put(KEY_RESTAURANT, food.restaurant);
                map.put(KEY_PRICE, food.price);
                map.put(KEY_TASTE, food.taste);
                map.put(KEY_DESCRIPTION, food.description);
                map.put(KEY_THUMB_URL, food.url);

                dishList.add(map);
                ImageLoader imageLoader = new ImageLoader(getApplicationContext());
                imageLoader.DisplayImage(food.url, normalBackgrounds[i]);

                new Thread(new BlurInBackground(imageLoader, food, i)).start();
                cardAdapters[i] = new CardAdapter(CLVPActivity.this, dishList, 0);
            }
        }

        private void showBlur() {
            for (int i = 0; i < PARAM_SCREEN_COUNT; i++) {
                blurredBackgrounds[i].setImageBitmap(blurred_imgs[i]);
            }
        }

    }

    class FetchJSON implements Runnable {

        @Override
        public void run() {
            foodGenerator.fetchJSON();
//            foodGenerator.setJSON(getJSONFromAssets("food.json"));  TODO 判断联网
            Message msg = new Message();
            if (foodGenerator.noError())
                msg.what = MSG_JSON_FETCHED;
            else
                msg.what = MSG_JSON_FAILED;
            mainHandler.sendMessage(msg);
        }
    }

    class DelayRun implements Runnable {
        int delayTime;
        int msgWhat;

        public DelayRun(int dTime, int msg) {
            delayTime = dTime;
            msgWhat = msg;
        }

        public DelayRun(int dTime) {
            this(dTime, -1);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (msgWhat != -1) {
                Message msg = new Message();
                msg.what = msgWhat;
                mainHandler.sendMessage(msg);
            }
        }
    }

    private static class GetDataTask extends AsyncTask<Void, Void, Void> {

        Pull2RefreshBase<?> mRefreshedView;

        public GetDataTask(Pull2RefreshBase<?> refreshedView) {
            mRefreshedView = refreshedView;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mRefreshedView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    class BlurInBackground implements Runnable {
        FoodGenerator.Food food;
        ImageLoader imageLoader;
        int index;

        public BlurInBackground(ImageLoader imageloader, FoodGenerator.Food food, int index) {
            this.imageLoader = imageloader;
            this.food = food;
            this.index = index;
        }

        @Override
        public void run() {
            blurred_imgs[index] = Blur.fastBlur(CLVPActivity.this, imageLoader.getBitmap(food.url), PARAM_BLUR_RADIUS);
            Message msg = new Message();
            msg.what = MSG_BLUR_FINISHED;
            mainHandler.sendMessage(msg);
        }
    }

}
