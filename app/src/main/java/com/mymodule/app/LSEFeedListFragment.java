package com.mymodule.app;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jassayah on 12/8/14.
 */
public class LSEFeedListFragment extends ListFragment {

    public static final String url = "http://sheltered-bastion-2512.herokuapp.com/feed.json";

    private ArrayList<Item> itemArrayList = null;
    private View loadingOverlay;
    private ItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        loadingOverlay = rootView.findViewById(R.id.loading_overlay);
        assert loadingOverlay!=null;

        showLoadingOverlay(true);
        adapter = new ItemAdapter(itemArrayList);
        setListAdapter(adapter);
        requestData();
        return rootView;
    }

    private void requestData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        Type type = new TypeToken<ArrayList<Item>>(){}.getType();
        queue.add(new LSEAsyncRequest(url, type,null,createMyReqSuccessListener(),createMyReqErrorListener()));
        queue.start();
    }

    /**
     * Shows/hides the loading overlay.
     *
     * @param showOverlay True if the overlay be should be shown.
     */
    private void showLoadingOverlay(boolean showOverlay) {
        if (getActivity() != null) {
            if (loadingOverlay != null) {
                if (showOverlay) {
                    loadingOverlay.setVisibility(View.VISIBLE);
                } else {
                    loadingOverlay.setVisibility(View.GONE);
                }
            }
        }
    }

    private Response.Listener<ArrayList<Item>> createMyReqSuccessListener() {
        return new Response.Listener<ArrayList<Item>>() {
            @Override
            public void onResponse(ArrayList<Item> response) {
                showLoadingOverlay(false);
                adapter.setItemsList(response);
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadingOverlay(false);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Item item = adapter.getItem(position);
        if (item!=null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getHref()));
            startActivity(browserIntent);
        }
    }

    /**
     * A ListView adapter class for saved addresses.
     */
    public class ItemAdapter extends BaseAdapter {

        private ArrayList<Item> itemsList;

        /**
         * Default constructor that takes in an array of Item to display.
         */
        public ItemAdapter(ArrayList<Item> itemsList) {
            this.itemsList = itemsList;
        }

        @Override
        public int getCount() {
            if (itemsList != null) {
                return itemsList.size();
            } else {
                return 0;
            }
        }

        /**
         * Set the saved addresses to display.
         *
         * @param itemList The item list.
         */
        public void setItemsList(ArrayList<Item> itemList) {
            this.itemsList = itemList;
            notifyDataSetChanged();
        }

        @Override
        public Item getItem(int position) {
            return itemsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Context context = parent.getContext();

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false);
            }

            NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.imageView);
            TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewDescription);
            NetworkImageView imageViewAvatar = (NetworkImageView) convertView.findViewById(R.id.imageViewAvatar);
            TextView textViewUserName = (TextView) convertView.findViewById(R.id.textViewUserName);
            TextView textViewLink = (TextView) convertView.findViewById(R.id.textViewLink);

            assert imageViewAvatar != null;
            assert imageView != null;
            assert textViewTitle != null;
            assert textViewUserName != null;
            assert textViewLink != null;

            Item item = getItem(position);

            if (item != null) {

                // This is to respect the JSON file but it looks ugly. I decided to take it off
                // I would talk to product in a real case and show them both screenshot for them to decide
                //LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(item.getUser().getAvatar().getWidth(),item.getUser().getAvatar().getHeight());
                //imageViewAvatar.setLayoutParams(parms);

                LSEAsyncImageRequest.fetchImageAsync(getActivity(), item.getUser().getAvatar().getSrc(), imageViewAvatar, R.drawable.default_user);
                LSEAsyncImageRequest.fetchImageAsync(getActivity(), item.getSrc(), imageView, R.drawable.default_image);
                textViewTitle.setText(item.getDesc());
                textViewUserName.setText(item.getUser().getUsername());
                textViewLink.setText(item.getHref());
            }
            return convertView;
        }
    }
}
