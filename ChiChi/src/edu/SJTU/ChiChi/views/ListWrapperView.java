package edu.SJTU.ChiChi.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import edu.SJTU.ChiChi.R;

public class ListWrapperView extends LinearLayout {
	ListView list = null;

	public ListWrapperView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
        LayoutInflater mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.listwrapper, this);
        list = (ListView) findViewById(R.id.listView);
	}

}
