package edu.SJTU.ChiChi.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
public class CardAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> data;
    private int cardType;
    private static LayoutInflater inflater = null;

    public ImageLoader imageLoader;

    public CardAdapter(Activity a, ArrayList<HashMap<String, String>> d, int c) {
        data = d;
        cardType = c;
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(a.getApplicationContext());
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
                ImageView thumb_image = (ImageView) vi.findViewById(R.id.thumbnail);
                TextView price = (TextView) vi.findViewById(R.id.price);
                TextView taste = (TextView) vi.findViewById(R.id.taste);
                TextView location = (TextView) vi.findViewById(R.id.location);

                name.setText(dish.get(CardListViewActivity.KEY_NAME));
                price.setText(dish.get(CardListViewActivity.KEY_PRICE));
                taste.setText(dish.get(CardListViewActivity.KEY_TASTE));
                location.setText(dish.get(CardListViewActivity.KEY_LOCATION));
                imageLoader.DisplayImage(dish.get(CardListViewActivity.KEY_THUMB_URL), thumb_image);
            }
        }

        return vi;
    }

}
