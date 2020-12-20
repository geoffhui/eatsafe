package com.example.eatsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    ArrayList<itemModel> arrayList;
    String name;
    String date;
    String inspType;
    int numCritical;
    int numNonCritical;
    String hazardRating;
    String violLump;
    String address;
    CustomAdapter adapter;
    JSONArray array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button mapButton = findViewById(R.id.mapBtn);
//        final Button searchButton = findViewById(R.id.searchBtn);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

//        searchButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                listView.setSelectionAfterHeaderView();
//            }
//        });

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(readJSON());
            array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                name = jsonObject.getString("NAME");
                date = jsonObject.getString("InspectionDate");
                inspType = jsonObject.getString("InspType");
                numCritical = jsonObject.getInt("NumCritical");
                numNonCritical = jsonObject.getInt("NumNonCritical");
                hazardRating = jsonObject.getString("HazardRating");
                violLump = jsonObject.getString("ViolLump");
                address = jsonObject.getString("PHYSICALADDRESS");

                itemModel model = new itemModel(name, date, inspType, numCritical, numNonCritical, hazardRating, violLump, address);
                arrayList.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new CustomAdapter(this, arrayList);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        setupSearchView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, RestaurantDetailsActivity.class);
                itemModel select = arrayList.get(position);
                intent.putExtra("name", select.getTitle());
                intent.putExtra("date", select.getDate());
                intent.putExtra("inspection", select.getInspType());
                intent.putExtra("critical", select.getNumCritical());
                intent.putExtra("noncritical", select.getNumNonCritical());
                intent.putExtra("hazard", select.getHazardRating());
                intent.putExtra("violation", select.getViolLump());
                intent.putExtra("address", select.getAddress());
                startActivity(intent);
            }
        });
    }

    private void setupSearchView(){
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    listView.clearTextFilter();
                } else {
                    listView.setFilterText(newText);
                }
                return true;
            }
        });
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");
    }

    public String readJSON() {
        String json = null;

        try {
            InputStream inputStream = getAssets().open("inspection_data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }
}