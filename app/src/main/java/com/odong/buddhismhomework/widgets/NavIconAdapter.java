package com.odong.buddhismhomework.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.NavIcon;

import java.util.List;

/**
 * Created by flamen on 15-2-12.
 */
public class NavIconAdapter extends BaseAdapter {
    public NavIconAdapter(Context context, List<NavIcon> icons) {
        this.context = context;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.nav_icon, null);

            NavIcon icon = icons.get(position);


            ImageButton ib = (ImageButton) grid.findViewById(R.id.btn_nav_icon);
            ib.setImageResource(icon.getImage());
            ib.setOnClickListener(icon.getClick());

            TextView tv = (TextView) grid.findViewById(R.id.tv_nav_icon);
            tv.setText(icon.getTitle());
            tv.setOnClickListener(icon.getClick());

        } else {
            grid = convertView;
        }
        return grid;
    }

    private Context context;
    private List<NavIcon> icons;
}
