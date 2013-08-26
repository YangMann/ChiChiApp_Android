package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.CardAdapter;
import edu.SJTU.ChiChi.utils.FoodGenerator;

import java.util.ArrayList;
import java.util.HashMap;

//import edu.SJTU.ChiChi.utils.JSONParser;
//import edu.SJTU.ChiChi.utils.XMLParser;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-23
 * Time: 下午4:05
 */
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
    CardAdapter adapter0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ArrayList<HashMap<String, String>> dishList = new ArrayList<HashMap<String, String>>();
//        XMLParser parser = new XMLParser();
//        String xml = parser.getXmlFromUrl(URL);
//        Document doc = parser.getDomElement(xml);
//        NodeList nl = doc.getElementsByTagName(KEY_DISH);
        FoodGenerator fg = new FoodGenerator();
        FoodGenerator.Food food = fg.getFood(0);
        // looping through all dish nodes <dish>
//        for (int i = 0; i < nl.getLength(); i++) {
        for (int i = 0; i < 1; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
//            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
//            map.put(KEY_ID, food.getValue(e, KEY_ID));
            map.put(KEY_NAME, food.name);
            map.put(KEY_GENRE, food.genre);
            map.put(KEY_LOCATION, food.building);
            map.put(KEY_PRICE, food.price);
            map.put(KEY_TASTE, food.taste);
            map.put(KEY_DESCRIPTION, food.description);
            map.put(KEY_THUMB_URL, food.url);

            // adding HashList to ArrayList
            dishList.add(map);
        }

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
