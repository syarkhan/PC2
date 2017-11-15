package com.example.sheryarkhan.projectcity.adapters;

/**
 * Created by Sheryar Khan on 9/12/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.activities.LocationAutoCompleteActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Note that this adapter requires a valid {@link GoogleApiClient}.
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the {@link Places#GEO_DATA_API} API. f
 */
public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {

    private static final String TAG = "PlacesAutoCompleteAdapter";
    private static ArrayList<PlaceAutocomplete> mResultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;
    AutocompletePredictionBuffer autocompletePredictions;

    public static List <Address> addresses;
    private ArrayList resultList;

    private Context mContext;
    //private Context context;
    //private int layout;

    public PlacesAutoCompleteAdapter(Context context,LatLngBounds bounds, GoogleApiClient googleApiClient, AutocompleteFilter filter) {
        mContext = context;
        //layout = resource;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
//    public void setBounds(LatLngBounds bounds) {
//        mBounds = bounds;
//    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    private ArrayList<PlaceAutocomplete> getAutocomplete(final CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i("", "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.

            final PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.



                    autocompletePredictions = results
                            .await(60, TimeUnit.SECONDS);



            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(mContext, "Error contacting API: " + status.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.e("", "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i("", "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();


//                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                        .getPlaceById(mGoogleApiClient, prediction.getPlaceId());
//                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
//                    @Override
//                    public void onResult(PlaceBuffer places) {
//                        if(places.getCount()==1){
//                            //Do the things here on Click.....
//                            //Toast.makeText(getApplicationContext(),String.valueOf(places.get(0).getPlaceTypes())+" , "+places.get(0).getId()+" , "+places.get(0).getAddress(),Toast.LENGTH_SHORT).show();
//                            Place place = places.get(0);
//                            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
//                            try {
//                                addresses = geocoder.getFromLocation(places.get(0).getLatLng().latitude,places.get(0).getLatLng().longitude,1);
////                                Toast.makeText(context,
////                                        addresses.get(0).getSubLocality()+" -- "+
////                                                addresses.get(0).getLocality()
////
////                                        ,Toast.LENGTH_LONG).show();
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }else {
//                            Toast.makeText(mContext,"Something went wrong!",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
//                if(addresses.get(0) != null) {
//                    if (addresses.get(0).getSubLocality() != null) {
                        resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                                prediction.getPrimaryText(null),prediction.getSecondaryText(null)));
//                    } else {
//                        resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
//                                prediction.getFullText(null), ""));
//                    }
                }


            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }
        Log.e("", "Google API client is not connected for autocomplete query.");
        return null;
    }

    public ArrayList<PlaceAutocomplete> getResultsList()
    {
        return resultList;
    }

    @Override
    public PredictionHolder onCreateViewHolder(ViewGroup parent, int i) {
        //LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInflater.from(context).inflate(R.layout.activity_location_autocomplete, parent, false)
        //context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.autocomplete_row_list_item,parent,false);

        PredictionHolder mPredictionHolder;
        mPredictionHolder = new PredictionHolder(convertView);
        return mPredictionHolder;
    }

    @Override
    public void onBindViewHolder(PredictionHolder mPredictionHolder, final int i) {
        mPredictionHolder.txtPrimaryPlace.setText(mResultList.get(i).primaryPlace);
        mPredictionHolder.txtSecondaryPlace.setText(mResultList.get(i).secondaryPlace);
        /*mPredictionHolder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetLatLonCallback.getLocation(resultList.get(i).toString());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        if(mResultList != null)
            return mResultList.size();
        else
            return 0;
    }

    public static PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder {

        TextView txtPrimaryPlace,txtSecondaryPlace;
        ConstraintLayout mRow;
        public PredictionHolder(View itemView) {

            super(itemView);
            txtPrimaryPlace = (TextView) itemView.findViewById(R.id.txtPrimaryPlace);
            txtSecondaryPlace = (TextView) itemView.findViewById(R.id.txtSecondaryPlace);
            mRow=(ConstraintLayout)itemView.findViewById(R.id.autocompleteRow);
        }

    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence primaryPlace,secondaryPlace;


        PlaceAutocomplete(CharSequence placeId, CharSequence primaryPlace,CharSequence secondaryPlace) {
            this.placeId = placeId;
            this.primaryPlace = primaryPlace;
            this.secondaryPlace = secondaryPlace;

        }

        @Override
        public String toString() {

                return placeId.toString()+" - "+primaryPlace.toString();

        }
    }


}
