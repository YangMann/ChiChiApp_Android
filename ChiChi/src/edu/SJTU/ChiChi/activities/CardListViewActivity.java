package edu.SJTU.ChiChi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.utils.CardAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-23
 * Time: 下午4:05
 */
public class CardListViewActivity extends Activity {
    private String URL = "";
    // XML node keys
    static final String KEY_NAME = "name";
    static final String KEY_ID = "id";
    static final String KEY_GENRE = "genre";
    static final String KEY_LOCATION = "location";
    static final String KEY_PRICE = "price";
    static final String KEY_TASTE = "taste";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_THUMB_URL = "thumb_url";

    ListView list;
    CardAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
