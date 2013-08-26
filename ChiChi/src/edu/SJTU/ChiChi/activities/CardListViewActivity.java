package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.CardAdapter;
import edu.SJTU.ChiChi.utils.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-23
 * Time: 下午4:05
 */
public class CardListViewActivity extends Activity {
    private String URL = "";
    // XML node keys
    public static final String KEY_DISH = "dish"; // parent node
    public static final String KEY_ID = "id";
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
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(URL);
        Document doc = parser.getDomElement(xml);
        NodeList nl = doc.getElementsByTagName(KEY_DISH);
        // looping through all dish nodes <dish>
        for (int i = 0; i < nl.getLength(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
            map.put(KEY_ID, parser.getValue(e, KEY_ID));
            map.put(KEY_NAME, parser.getValue(e, KEY_NAME));
            map.put(KEY_GENRE, parser.getValue(e, KEY_GENRE));
            map.put(KEY_LOCATION, parser.getValue(e, KEY_LOCATION));
            map.put(KEY_PRICE, parser.getValue(e, KEY_PRICE));
            map.put(KEY_TASTE, parser.getValue(e, KEY_TASTE));
            map.put(KEY_DESCRIPTION, parser.getValue(e, KEY_DESCRIPTION));
            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));

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
