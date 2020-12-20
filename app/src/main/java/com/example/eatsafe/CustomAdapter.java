package com.example.eatsafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<itemModel> arrayList;
    private ArrayList<itemModel> orig;

    CustomAdapter(Context context, ArrayList<itemModel> arrayList){
        super();
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount(){
        return arrayList.size();
    }

    @Override
    public Object getItem(int position){
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        }
        TextView name;
        name = convertView.findViewById(R.id.name);
        name.setText(arrayList.get(position).getTitle() + "\n" + arrayList.get(position).getDate());

        return convertView;
    }

    @Override
    public Filter getFilter(){
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint){
                final FilterResults oReturn = new FilterResults();
                final ArrayList<itemModel> results = new ArrayList<>();
                if(orig == null)
                    orig = arrayList;
                if(constraint != null){
                    if(orig != null && orig.size() > 0){
                        for(final itemModel g : orig){
                            if(g.getTitle().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void publishResults(CharSequence constraint, FilterResults results){
                arrayList = (ArrayList<itemModel>)results.values;
                notifyDataSetChanged();
            }
        };
    }
}
