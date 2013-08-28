package edu.SJTU.ChiChi.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.SJTU.ChiChi.R;
import edu.SJTU.ChiChi.activities.CardListViewActivity;
import edu.SJTU.ChiChi.views.VerticalTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-23
 * Time: 下午3:43
 */
public class CardAdapter extends BaseAdapter {

    public static int VerticalTextHeightFactor = 80;

    private ArrayList<HashMap<String, String>> data;
    private int cardType;
    private static LayoutInflater inflater = null;
    private Typeface Sung;
    private Typeface Segoe;
    private Typeface Helvetica;
    private Typeface HelveticaU;

    public ImageLoader imageLoader;

    public CardAdapter(Activity a, ArrayList<HashMap<String, String>> d, int c) {
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
                VerticalTextView name = (VerticalTextView) vi.findViewById(R.id.name);
//                ImageView thumb_image = (ImageView) vi.findViewById(R.id.thumbnail);
                TextView price = (TextView) vi.findViewById(R.id.price);
                TextView taste = (TextView) vi.findViewById(R.id.taste);
                TextView building = (TextView) vi.findViewById(R.id.building);
                TextView restaurant = (TextView) vi.findViewById(R.id.restaurant);
                TextView description = (TextView) vi.findViewById(R.id.description);

                name.setColumnSpacing(1);
                name.setHeight(dish.get(CardListViewActivity.KEY_NAME).length() * VerticalTextHeightFactor);
                name.setVerticalText(dish.get(CardListViewActivity.KEY_NAME), true);
                name.setTypeface(Sung);

                price.setTypeface(HelveticaU);
                price.setText(dish.get(CardListViewActivity.KEY_PRICE));

                taste.setText(dish.get(CardListViewActivity.KEY_TASTE));

                building.setText(dish.get(CardListViewActivity.KEY_BUILDING));

                restaurant.setText(dish.get(CardListViewActivity.KEY_RESTAURANT));

                description.setText(dish.get(CardListViewActivity.KEY_DESCRIPTION));

//                imageLoader.DisplayImage(dish.get(CardListViewActivity.KEY_THUMB_URL), thumb_image);
            }
        }

        return vi;
    }

}
