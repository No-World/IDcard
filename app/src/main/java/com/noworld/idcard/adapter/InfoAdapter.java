package com.noworld.idcard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noworld.idcard.R;
import com.noworld.idcard.data.PersonIdCard;

import java.util.ArrayList;

public class InfoAdapter extends BaseAdapter {

    private final String TAG = "InfoAdapter";

    private ArrayList<PersonIdCard> person_list = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater = null;


    public InfoAdapter(Context context) {

        mContext = context;
        inflater = LayoutInflater.from(context);

    }

    public void setPerson_list(ArrayList<PersonIdCard> list) {
        this.person_list = list;
        Log.i(TAG, "setPerson_list   " + list.get(0).getIdCard());
    }

    @Override
    public int getCount() {
        return person_list.size();
    }

    @Override
    public Object getItem(int position) {
        return person_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView   ");
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.person_name = (TextView) convertView.findViewById(R.id.person_name);
            holder.person_sex = (TextView) convertView.findViewById(R.id.person_sex);
            holder.person_date = (TextView) convertView.findViewById(R.id.person_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.person_name.setText(person_list.get(position).getName());
        holder.person_sex.setText(person_list.get(position).getSex());
        holder.person_date.setText(person_list.get(position).getDate_of_birth());
        return convertView;
    }

    public class ViewHolder {
        public TextView person_name;
        public TextView person_sex;
        public TextView person_date;
    }
}
