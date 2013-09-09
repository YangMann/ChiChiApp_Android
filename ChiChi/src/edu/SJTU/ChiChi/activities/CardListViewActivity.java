package edu.SJTU.ChiChi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.Blur;
import edu.SJTU.ChiChi.utils.CardAdapter;
import edu.SJTU.ChiChi.utils.FoodGenerator;
import edu.SJTU.ChiChi.utils.ImageLoader;
import org.apache.http.util.EncodingUtils;

import java.io.IOException;
import java.io.InputStream;
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
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_PRICE = "price";
    public static final String KEY_TASTE = "taste";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_THUMB_URL = "thumb_url";
    public static final String KEY_PHOTOGRAPHER = "photographer";

    public static final int MSG_JSON_FETCHED = 0;
    public static final int MSG_JSON_FAILED = 1;
    public static final int MSG_SPLASH_FINISHED = 2;
    public static final int MSG_BLUR_FINISHED = 3;
    public static final int MSG_SENSOR_SHAKE = 10;
    public static final int MSG_REFRESH_PRESSED = 11;

    private static boolean IN_PROGRESS = true;

    private static final int SPLASH_TIME = 0;
    private static final int SENSOR_THRESHOLD = 32;
    public static final double PARALLAX_RATIO = 2.5;
    public static final int MAX_SHIFT = 500;
    public static final int BLUR_RADIUS = 25;


    private ListView list;
    private ImageView bg;
    private ImageView bg_blurred;
    private Bitmap blurred_img;
    private ImageButton refresh_button;
    private ImageButton refresh_button_shadow;
    private Animation roundLoading;

    private SensorManager sensorManager;
    private Vibrator vibrator;

    private float blur_alpha;
    CardAdapter adapter0;

    ArrayList<HashMap<String, String>> dishList;

    MainHandler mHandler = new MainHandler();
    FoodGenerator fg = new FoodGenerator();

    public String getJSONFromAssets(String filename) {
        String result = "";
        try {
            InputStream in = getResources().getAssets().open(filename);
            int length = in.available();
            byte[] buffer = new byte[length];
//            Log.v("buffer", String.valueOf(buffer.length));
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (IOException e) {
//            Log.v("ioe", e.getMessage());
            e.printStackTrace();
        }
//        Log.v("result", result);

        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_main);

        bg = (ImageView) findViewById(R.id.normal_image);
        bg_blurred = (ImageView) findViewById(R.id.blurred_image);
        list = (ListView) findViewById(R.id.listView);
        refresh_button = (ImageButton) findViewById(R.id.refreshButton);
        refresh_button_shadow = (ImageButton) findViewById(R.id.refreshButtonShadow);
        roundLoading = AnimationUtils.loadAnimation(CardListViewActivity.this, R.anim.round_loading);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

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

        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_button.startAnimation(roundLoading);
                refresh_button_shadow.startAnimation(roundLoading);
//                Log.e("button", "button clicked!");
                Message msg = new Message();
                msg.what = MSG_REFRESH_PRESSED;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(
                    sensorEventListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            if (Math.abs(x) > SENSOR_THRESHOLD || Math.abs(y) > SENSOR_THRESHOLD || Math.abs(z) > SENSOR_THRESHOLD) {
                if (!IN_PROGRESS) {
                    refresh_button.startAnimation(roundLoading);
                    refresh_button_shadow.startAnimation(roundLoading);
                    vibrator.vibrate(200);
                    Message msg = new Message();
                    msg.what = MSG_SENSOR_SHAKE;
                    mHandler.sendMessage(msg);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

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
                    setAdapter();
                    break;
                case MSG_JSON_FAILED:
                    break;
                case MSG_SPLASH_FINISHED:
                    splash_finished = true;
                    loadData();
                    setAdapter();
                    break;
                case MSG_BLUR_FINISHED:
                    showBlur();
                    refresh_button.clearAnimation();
                    refresh_button_shadow.clearAnimation();
                    IN_PROGRESS = false;
                    break;
                case MSG_SENSOR_SHAKE:
//                    Log.e("sensor", "shake!");
                    if (!IN_PROGRESS) {
                        IN_PROGRESS = true;
                        mHandler.loadData();
                        adapter0.refreshData(dishList);
                    }
                    break;
                case MSG_REFRESH_PRESSED:
                    if (!IN_PROGRESS) {
                        mHandler.loadData();
                        adapter0.refreshData(dishList);
                    }
            }
        }

        private void loadData() {
            dishList = new ArrayList<HashMap<String, String>>();

            if (!json_fetched || !splash_finished) return;

            FoodGenerator.Food food;
            food = fg.randomFood();
            for (int i = 0; i < 1; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_NAME, food.name);
                map.put(KEY_GENRE, food.genre);
                map.put(KEY_BUILDING, food.building);
                map.put(KEY_LAT, food.lat);
                map.put(KEY_LNG, food.lng);
                map.put(KEY_RESTAURANT, food.restaurant);
                map.put(KEY_PRICE, food.price);
                map.put(KEY_TASTE, food.taste);
                map.put(KEY_DESCRIPTION, food.description);
                map.put(KEY_THUMB_URL, food.url);
                map.put(KEY_PHOTOGRAPHER, food.photographer);

                dishList.add(map);
                ImageLoader imageLoader = new ImageLoader(getApplicationContext());
                imageLoader.DisplayImage(food.url, bg);
                new Thread(new BlurInBackground(imageLoader, food)).start();
            }
//            adapter0 = new CardAdapter(CardListViewActivity.this, dishList, 0);
//            list.setAdapter(adapter0);
        }

        private void setAdapter() {
            adapter0 = new CardAdapter(CardListViewActivity.this, dishList, 0);
            list.setAdapter(adapter0);
            /*refresh_button = (ImageButton) findViewById(R.id.refreshButton);
            if (refresh_button != null) {
                refresh_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("button", "Button clicked!");
                        mHandler.loadData();
                        adapter0.refreshData(dishList);
                    }
                });
            }*/
        }

        private void showBlur() {
            bg_blurred.setImageBitmap(blurred_img);
        }

    }

    class FetchJSON implements Runnable {

        @Override
        public void run() {
//            fg.fetchJSON();
            fg.setJSON(getJSONFromAssets("JSON/food.json"));  // TODO 判断联网
            Message msg = new Message();
            if (fg.noError())
                msg.what = MSG_JSON_FETCHED;
            else
                msg.what = MSG_JSON_FAILED;
            mHandler.sendMessage(msg);
        }
    }

    class DelayRun implements Runnable {
        int delayTime;
        int msgWhat;

        public DelayRun(int dtime, int msg) {
            delayTime = dtime;
            msgWhat = msg;
        }

        public DelayRun(int dtime) {
            this(dtime, -1);
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
                mHandler.sendMessage(msg);
            }
        }
    }

    class BlurInBackground implements Runnable {
        FoodGenerator.Food food;
        ImageLoader imageLoader;

        public BlurInBackground(ImageLoader imageloader, FoodGenerator.Food food) {
            this.imageLoader = imageloader;
            this.food = food;
        }

        @Override
        public void run() {
            blurred_img = Blur.fastBlur(CardListViewActivity.this, imageLoader.getBitmap(food.url), BLUR_RADIUS);
            Message msg = new Message();
            msg.what = MSG_BLUR_FINISHED;
            mHandler.sendMessage(msg);
        }
    }

}
