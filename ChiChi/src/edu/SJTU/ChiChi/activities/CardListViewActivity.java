package edu.SJTU.ChiChi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.CardAdapter;
import edu.SJTU.ChiChi.utils.FoodGenerator;
import edu.SJTU.ChiChi.utils.ImageLoader;
import edu.SJTU.ChiChi.views.TopCenterImageView;

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
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PRICE = "price";
    public static final String KEY_TASTE = "taste";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_THUMB_URL = "thumb_url";

    ListView list;
    TopCenterImageView bg;
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
//        XMLParser parser = new XMLParser();
//        String xml = parser.getXmlFromUrl(URL);
//        Document doc = parser.getDomElement(xml);
//        NodeList nl = doc.getElementsByTagName(KEY_DISH);
        bg = (TopCenterImageView) findViewById(R.id.normal_image);

        FoodGenerator fg = new FoodGenerator();
        FoodGenerator.Food food;
        //如果json读取没错的话再做下一步操作
        if(fg.noerror()){
        	food = fg.getFood(0);
            for (int i = 0; i < 1; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
//                Element e = (Element) nl.item(i);
                // adding each child node to HashMap key => value
//                map.put(KEY_ID, food.getValue(e, KEY_ID));
                map.put(KEY_NAME, food.name);
                map.put(KEY_GENRE, food.genre);
                map.put(KEY_LOCATION, food.building);
                map.put(KEY_PRICE, food.price);
                map.put(KEY_TASTE, food.taste);
                map.put(KEY_DESCRIPTION, food.description);
                map.put(KEY_THUMB_URL, food.url);

                // adding HashList to ArrayList
                dishList.add(map);
                ImageLoader imageLoader = new ImageLoader(this.getApplicationContext());
                imageLoader.DisplayImage(food.url, bg);
            }
        }
        //TODO else... 如果json读取出错的话做点别的。。。
        
        // looping through all dish nodes <dish>
//        for (int i = 0; i < nl.getLength(); i++) {


        list = (ListView) findViewById(R.id.listView);
        adapter0 = new CardAdapter(this, dishList, 0);
        list.setAdapter(adapter0);

        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


            }
        });
    }
}
