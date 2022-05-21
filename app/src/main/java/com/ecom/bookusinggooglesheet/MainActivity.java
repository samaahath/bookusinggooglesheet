package com.ecom.bookusinggooglesheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ArrayList<gradelistitems> itemArrayList;
    private MyAppAdapter myAppAdapter; //Array Adapter
    private ListView listView; // Listview
    private ProgressBar spinner;
    private RelativeLayout parentlayout;
    private RelativeLayout emptylayout;
    private TextView emptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemArrayList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.gradelistview); //Listview Declaration

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        parentlayout = (RelativeLayout) findViewById(R.id.parentlayout);
        emptylayout = (RelativeLayout) findViewById(R.id.emptylayout);
        RelativeLayout outerparentlayout = (RelativeLayout) findViewById(R.id.outerparentlayout);
        checkinternet();
        outerparentlayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkinternet();
            }
        });

    }
    private void checkinternet() {
        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            // connected to the internet
            // Toast.makeText(MainActivity.this, "Unable to connect to Mobile data!", Toast.LENGTH_LONG).show();

            emptylayout.setVisibility(View.GONE);
            parentlayout.setVisibility(View.VISIBLE);
            getDataFromAPI();
//            if (!cm.getNetworkCapabilities(cm.getActiveNetwork()).hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || !cm.getNetworkCapabilities(cm.getActiveNetwork()).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                 Toast.makeText(getContext(), "No internet connection, click to try again!", Toast.LENGTH_LONG).show();
//            }
        } else {
            emptylayout.setVisibility(View.VISIBLE);
            parentlayout.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);

        }
    }
    private void getDataFromAPI() {

        // creating a string variable for URL.
        String url = "https://spreadsheets.google.com/feeds/list/1QzuRWXh61P7GFd_xNcVbOwTwYq632zkezDoEMQc3BoM/od6/public/values?alt=json?gsx$id='1'";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // creating a variable for our JSON object request and passing our URL to it.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                spinner.setVisibility(View.GONE);
                try {
                    JSONObject feedObj = response.getJSONObject("feed");
                    JSONArray entryArray = feedObj.getJSONArray("entry");
                    for(int i=0; i<entryArray.length(); i++){
                        JSONObject entryObj = entryArray.getJSONObject(i);
                        String id = entryObj.getJSONObject("gsx$id").getString("$t");
                        String grade = entryObj.getJSONObject("gsx$name").getString("$t");
                        itemArrayList.add(new gradelistitems(id, grade));

                        // passing array list to our adapter class.
                        myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);

                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        listView.setAdapter(myAppAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handline on error listener method.
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // calling a request queue method
        // and passing our json object
        queue.add(jsonObjectRequest);
    }

    public class MyAppAdapter extends BaseAdapter        //has a class viewholder which holds
    {

        class ViewHolder {
            TextView txtgrade;
        }

        final List<gradelistitems> parkingList;

        public final Context context;
        final ArrayList<gradelistitems> arraylist;

        private MyAppAdapter(List<gradelistitems> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<>();
            arraylist.addAll(parkingList);
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
        {

            View rowView = convertView;
            ViewHolder viewHolder;


            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.gradelistview, parent, false);
                viewHolder = new ViewHolder();

                viewHolder.txtgrade = (TextView) rowView.findViewById(R.id.txgrade);
                rowView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }



            viewHolder.txtgrade.setText(parkingList.get(position).getName());

            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                }
            });
            return rowView;


        }

    }
}