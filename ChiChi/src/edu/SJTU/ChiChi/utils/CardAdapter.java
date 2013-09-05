package edu.SJTU.ChiChi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.activities.CardListViewActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-23
 * Time: 下午3:43
 */
@SuppressLint("NewApi")
public class CardAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> data;
    private int cardType;
    private static LayoutInflater inflater = null;
    private Typeface Sung;
    private Typeface Segoe;
    private Typeface Helvetica;
    private Typeface HelveticaU;
    private Activity activity;

    public ImageLoader imageLoader;

    public CardAdapter(Activity a, ArrayList<HashMap<String, String>> d, int c) {
        activity = a;
        data = d;
        cardType = c;
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(a.getApplicationContext());
        Sung = Typeface.createFromAsset(a.getAssets(), "fonts/ChekiangSung.otf");
        Segoe = Typeface.createFromAsset(a.getAssets(), "fonts/segoeuil.ttf");
        Helvetica = Typeface.createFromAsset(a.getAssets(), "fonts/HelveticaNeueLTPro-ThEx.otf");
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
                TextView name = (TextView) vi.findViewById(R.id.name);
//                ImageView thumb_image = (ImageView) vi.findViewById(R.id.thumbnail);
                TextView price = (TextView) vi.findViewById(R.id.price);
                TextView taste = (TextView) vi.findViewById(R.id.taste);
                TextView genre = (TextView) vi.findViewById(R.id.genre);
                TextView building = (TextView) vi.findViewById(R.id.building);
                TextView restaurant = (TextView) vi.findViewById(R.id.restaurant);
                TextView description = (TextView) vi.findViewById(R.id.description);
                ImageButton refreshButton = (ImageButton) vi.findViewById(R.id.refreshButton);

                name.setText(dish.get(CardListViewActivity.KEY_NAME));
                name.setTypeface(Sung);

                price.setTypeface(HelveticaU);
                price.setText(dish.get(CardListViewActivity.KEY_PRICE));

//                taste.setText(dish.get(CardListViewActivity.KEY_TASTE));
                genre.setTypeface(Sung);
                building.setText(dish.get(CardListViewActivity.KEY_BUILDING));

                restaurant.setText(dish.get(CardListViewActivity.KEY_RESTAURANT));

                description.setText(dish.get(CardListViewActivity.KEY_DESCRIPTION));
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
