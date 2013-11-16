package edu.SJTU.ChiChi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.activities.CardListViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-23
 * Time: 下午3:43
 * <p/>
 * Modified by 游杰 2013-11-13 21:55
 * 添加了对map fragment的查找，并对应hash表对map 进行了调整
 */
@SuppressLint("NewApi")
public class CardAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> data;
    private int cardType;
    private static LayoutInflater inflater = null;
    private Typeface Sung;
    private Typeface Kangxi;
    private Typeface Segoe;
    private Typeface Helvetica;
    private Typeface HelveticaU;
    private Activity activity;

    public ImageLoader imageLoader;


    public GoogleMap frag_map; /// 这两个作为public便于在外面访问
    public TextView mapHeader;


    private static final float ZOOM = 16.5f;
    private static final float BEARING = 350;
    private static final float TILT = 0;

    public CardAdapter(Activity a, ArrayList<HashMap<String, String>> d, int c) {
        activity = a;
        data = d;
        cardType = c;
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(a.getApplicationContext());
//        Sung = Typeface.createFromAsset(a.getAssets(), "fonts/ChekiangSung.otf");
        Kangxi = Typeface.createFromAsset(a.getAssets(), "fonts/Kangxi.ttf");
        Segoe = Typeface.createFromAsset(a.getAssets(), "fonts/segoeuil.ttf");
        Helvetica = Typeface.createFromAsset(a.getAssets(), "fonts/HelveticaNeueLTPro-Lt.otf");
        HelveticaU = Typeface.createFromAsset(a.getAssets(), "fonts/HelveticaNeueLTPro-UltLtEx.otf");
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            switch (cardType) {
                case 0: {
                    vi = inflater.inflate(R.layout.card_0, null);
                    break;
                }
            }
        }
        HashMap<String, String> dish = data.get(position);
        switch (cardType) {
            case 0: {
                String displayLanguage = Locale.getDefault().getDisplayLanguage();
                TextView name = (TextView) vi.findViewById(R.id.name);
//                ImageView thumb_image = (ImageView) vi.findViewById(R.id.thumbnail);
                TextView price = (TextView) vi.findViewById(R.id.price);
                TextView copyright = (TextView) vi.findViewById(R.id.copyright);
                TextView tasteHeader = (TextView) vi.findViewById(R.id.tasteHeader);
                TextView taste = (TextView) vi.findViewById(R.id.taste);
//                TextView genre = (TextView) vi.findViewById(R.id.genre);
                TextView building = (TextView) vi.findViewById(R.id.building);
                TextView photographer = (TextView) vi.findViewById(R.id.photographer);
                TextView restaurant = (TextView) vi.findViewById(R.id.restaurant);
                TextView descriptionHeader = (TextView) vi.findViewById(R.id.descriptionHeader);
                TextView description = (TextView) vi.findViewById(R.id.description);
                mapHeader = (TextView) vi.findViewById(R.id.mapHeader);
//                ImageButton refreshButton = (ImageButton) vi.findViewById(R.id.refreshButton);


                //换一个得到frag_map的地方  不需要了
                frag_map = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.smallmap)).getMap();
                String lat, lng;
                lat = dish.get(CardListViewActivity.KEY_LAT);
                lng = dish.get(CardListViewActivity.KEY_LNG);
                //设置camera
                frag_map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(
                        new LatLng(Float.valueOf(lat), Float.valueOf(lng)))
                        .zoom(ZOOM)
                        .bearing(BEARING)
                        .tilt(TILT)
                        .build()
                ));
                //在地图上加标签
                frag_map.addMarker(new MarkerOptions()
                        .position(new LatLng(Float.valueOf(lat), Float.valueOf(lng)))
                        .title(dish.get(CardListViewActivity.KEY_BUILDING) +
                                "·" + dish.get(CardListViewActivity.KEY_RESTAURANT)));

                //若lat lng 不为实际数值 这里要加健壮性  z
                Log.e("MAP_LOCATION_SET", lat + lng);

                name.setText(dish.get(CardListViewActivity.KEY_NAME));
                price.setText(dish.get(CardListViewActivity.KEY_PRICE));
//                taste.setText(dish.get(CardListViewActivity.KEY_TASTE));
                building.setText(dish.get(CardListViewActivity.KEY_BUILDING));
                restaurant.setText(dish.get(CardListViewActivity.KEY_RESTAURANT));
                description.setText(dish.get(CardListViewActivity.KEY_DESCRIPTION));
                photographer.setText(dish.get(CardListViewActivity.KEY_PHOTOGRAPHER));
                taste.setText(dish.get(CardListViewActivity.KEY_TASTE));

                name.setTypeface(Kangxi);
                price.setTypeface(HelveticaU);
//                genre.setTypeface(Kangxi);

                if (displayLanguage.equals("English")) {
                    tasteHeader.setTypeface(Helvetica);
                    descriptionHeader.setTypeface(Helvetica);
                    mapHeader.setTypeface(Helvetica);
                }

                RelativeLayout nameWrap = (RelativeLayout) vi.findViewById(R.id.name_wrap);

                Display display = activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height;
                if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180)
                    height = size.y;
                else
                    height = size.x;
                nameWrap.setLayoutParams(new RelativeLayout.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        height));
            }
        }

        return vi;
    }

    public void refreshData(ArrayList<HashMap<String, String>> d) {
        data = d;
        notifyDataSetChanged();
    }

}
