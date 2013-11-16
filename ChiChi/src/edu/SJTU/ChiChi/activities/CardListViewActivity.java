package edu.SJTU.ChiChi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
import java.util.concurrent.atomic.AtomicInteger;

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

    //    MSG 常量
    public static final int MSG_JSON_FETCHED = 0;
    public static final int MSG_JSON_FAILED = 1;
    public static final int MSG_SPLASH_FINISHED = 2;
    public static final int MSG_BLUR_FINISHED = 3;
    public static final int MSG_SENSOR_SHAKE = 10;
    public static final int MSG_REFRESH_PRESSED = 11;

    private static boolean IN_PROGRESS = true;  // “正在处理”标记

    private static final int SPLASH_TIME = 0;  // 闪屏时间
    private static final int SENSOR_THRESHOLD = 32;  // 摇晃间隔
    public static final double PARALLAX_RATIO = 2.5;  // 视差效果比例
    public static final int MAX_SHIFT = 500;  // 背景图片最大位移距离
    public static final int BLUR_RADIUS = 25;  // 模糊半径

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "ChiChi GCM";
    String SENDER_ID = "622100586021";


    private ListView list;  // 最外层ListView
    private ImageView bg;  // 原始背景ImageView
    private ImageView bg_blurred;  // 模糊背景ImageView
    private Bitmap blurred_img;  // 模糊背景Bitmap
    private ImageButton refresh_button;  // 刷新按钮
    private ImageButton refresh_button_shadow;  // 刷新按钮的阴影
    private Animation roundLoading;  // 刷新按钮旋转动画

    private SensorManager sensorManager;
    private Vibrator vibrator;

    private float blur_alpha;
    CardAdapter adapter0;

    ArrayList<HashMap<String, String>> dishList;

    MainHandler mHandler = new MainHandler();  // Handler集中处理MSG请求
    FoodGenerator fg = new FoodGenerator();  // FoodGenerator生成随机食物

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regId;

    public String getJSONFromAssets(String filename) {  // 从assets中获得JSON
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(CardListViewActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object... objects) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(msg);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            //            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG);
            }

        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */

    private void sendRegistrationIdToBackend(String msg) {
        // TODO: Your implementation here.
        Log.i(TAG, msg);
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

        context = getApplicationContext();

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found. ");
        }

        list.setCacheColorHint(0);
        list.setSelected(false);


//        FooterView 是为了在整个List底端添加一段空白
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
//                刷新按钮监听
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
        checkPlayServices();
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
