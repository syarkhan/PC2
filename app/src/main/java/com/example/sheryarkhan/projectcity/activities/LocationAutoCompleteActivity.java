package com.example.sheryarkhan.projectcity.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.adapters.PlacesAutoCompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class LocationAutoCompleteActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    //67.9256,24.8478,67.1886,24.0141
    public static LatLngBounds BOUNDS_PAKISTAN = null;

//    private static final LatLngBounds BOUNDS_PAKISTAN = new LatLngBounds(
//            new LatLng(-0,0), new LatLng(0,0));

    private GoogleApiClient mGoogleApiClient;

    private EditText editTextSearchLocation;
    ImageView imgRemoveTextFromSearch;
    RecyclerView autocompleteRecyclerView;
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    boolean isInRadius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_autocomplete);

        buildGoogleApiClient();


        BOUNDS_PAKISTAN = new LatLngBounds(
                new LatLng(24.801982, 66.994012), new LatLng(24.840821, 67.043880)); //SW,NE


        //24.8102354,67.0238575 , Ibn Qasim Bagh
        //24.7975133,67.0424375 , hot n spicy badar comm. hjgsd


        //boolean is = BOUNDS_PAKISTAN.contains(latLng);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter
                .Builder()
                .setCountry("PK")
                .build();

        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(LocationAutoCompleteActivity.this, Constants.KARACHI_BOUNDS, mGoogleApiClient, autocompleteFilter);

        editTextSearchLocation = (EditText) findViewById(R.id.editTextSearchLocation);
        imgRemoveTextFromSearch = (ImageView) findViewById(R.id.imgRemoveTextFromSearch);
        autocompleteRecyclerView = (RecyclerView) findViewById(R.id.autocompleteRecyclerView);
        autocompleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        autocompleteRecyclerView.setAdapter(placesAutoCompleteAdapter);

        editTextSearchLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("") && mGoogleApiClient.isConnected()) {
                    placesAutoCompleteAdapter.getFilter().filter(editable.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(getApplicationContext(), "API not connected!", Toast.LENGTH_SHORT).show();
                    Log.e("Places", "API not connected!");
                }
            }
        });

        autocompleteRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = PlacesAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.i("TAG", "Autocomplete item selected: " + item.primaryPlace);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(final PlaceBuffer places) {
                                if (places.getCount() == 1) {
                                    //Do the things here on Click.....
//                                    Toast.makeText(getApplicationContext(),places.get(0).getId()+" , "+
//                                            places.get(0).getAddress()+" - "+
//                                            places.get(0).getLatLng()
//                                            ,Toast.LENGTH_LONG).show();


                                    //RequestQueue requestQueue = Volley.newRequestQueue(LocationAutoCompleteActivity.this);

                                    final String lat = String.valueOf(places.get(0).getLatLng().latitude);
                                    final String lon = String.valueOf(places.get(0).getLatLng().longitude);

                                    LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                                    //ArrayList<String> townsInBounds = new ArrayList<>();
                                    //Map<String,Double> distances = new HashMap<>();
                                    //int i=0;
                                    double leastValue=60;
                                    String nearestTown=null;
                                    for (Constants.Areas areas : Constants.getTownsAndAreas()) {
                                        if (areas.isHeaderOrTown()) {
                                            isInRadius = inBounds(latLng.latitude, latLng.longitude, areas.getLatLngBounds());
                                            if (isInRadius) {
                                                //townsInBounds.add(areas.getTown());
                                                LatLng latLng1 = areas.getLatLngBounds().getCenter();


                                                double distanceInKm = distanceBetweenLocations(Double.valueOf(lat),Double.valueOf(lon),latLng1.latitude,latLng1.longitude);
                                                //leastValue = distanceInKm;

                                                if(distanceInKm < leastValue)
                                                {
                                                    leastValue = distanceInKm;
                                                    nearestTown = areas.getTown();
                                                }
//                                                LatLng latLng2 = midPoint(areas.getLatLngBounds().southwest.latitude,
//                                                        areas.getLatLngBounds().southwest.longitude,
//                                                        areas.getLatLngBounds().northeast.latitude,
//                                                        areas.getLatLngBounds().northeast.longitude);
                                                //distances.put(areas.getTown(),distanceInKm);
//                                                int i=0;
//                                                int a =i+i;
                                            }
                                        }
                                    }

                                    Toast.makeText(getApplicationContext(), "Town= " + nearestTown, Toast.LENGTH_LONG).show();
                                    //townsInBounds.clear();
                                    Intent intent = new Intent();
                                    //intent.putExtra("town", townsInBounds); //value should be your string from the edittext
                                    intent.putExtra("town", nearestTown);
                                    intent.putExtra("primaryLocation", places.get(0).getName() + ", " + places.get(0).getAddress()); //value should be your string from the edittext
                                    setResult(Activity.RESULT_OK, intent); //The data you want to send back
                                    finish();

//                                    String url = "http://nominatim.openstreetmap.org/reverse?format=json&lat="+lat+"&lon="+lon+"&addressdetails=1" +
//                                            "&email=studiosumbra@gmail.com&accept-language=en";
//
//
//                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                                                @Override
//                                                public void onResponse(JSONObject response) {
//
//                                                    String town=null;
//                                                    String suburb=null;
//                                                    String city=null;
//                                                    try {
//                                                        String address = response.getJSONObject("address").toString();
//                                                        if(response.getJSONObject("address").has("town")) {
//                                                            town = response.getJSONObject("address").getString("town");
//                                                        }
//                                                        if(response.getJSONObject("address").has("suburb")) {
//                                                            suburb = response.getJSONObject("address").getString("suburb");
//                                                        }
//                                                        if(response.getJSONObject("address").has("city")) {
//                                                            city = response.getJSONObject("address").getString("city");
//                                                        }
//
//
//                                                    Log.d(lat+","+lon+",postmandada",response.toString());
//
//                                                        Toast.makeText(getApplicationContext(),"Town= "+town+", Suburb= "+suburb+" , City= "+city,Toast.LENGTH_LONG).show();
//                                                        Intent intent = new Intent();
//
//                                                        ArrayList<PlacesAutoCompleteAdapter.PlaceAutocomplete> getArray = placesAutoCompleteAdapter.getResultsList();
//                                                        intent.putExtra("secondaryLocation", address); //value should be your string from the edittext
//                                                        intent.putExtra("primaryLocation", places.get(0).getName()+", "+places.get(0).getAddress()); //value should be your string from the edittext
//                                                        setResult(Activity.RESULT_OK, intent); //The data you want to send back
//                                                        finish();
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }, new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError error) {
//                                                    Toast.makeText(getApplicationContext(),
//                                                            error.networkResponse+"error", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                    requestQueue.add(jsonObjectRequest);
//                                    Geocoder geocoder = new Geocoder(LocationAutoCompleteActivity.this, Locale.getDefault());
//                                    try {
//                                        List<Address> addresses = geocoder.getFromLocation(places.get(0).getLatLng().latitude,places.get(0).getLatLng().longitude,1);
//                                        Toast.makeText(getApplicationContext(),
//                                                places.get(0).getId()+" , "+places.get(0).getAddress()+" -- "+
//                                                addresses.get(0).getSubLocality()+" -- "+
//                                                addresses.get(0).getLocality()
//                                                ,Toast.LENGTH_LONG).show();
//
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        Log.i("TAG", "Clicked: " + item.primaryPlace);
                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                    }
                })
        );


        imgRemoveTextFromSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSearchLocation.setText("");
            }
        });


    }

    private double distanceBetweenLocations(double lat1, double lon1, double lat2, double lon2) {

        Location locationA = new Location("Point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("Point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);


        return ((locationA.distanceTo(locationB))/1000);
    }

    //Long boundsNElat, Long boundsNElong, Long boundsSWlat, Long boundsSWlong


    public static LatLng midPoint(double lat1, double lon1, double lat2, double lon2) {

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        Log.d("inCenterdada", Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));
        return new LatLng(lat3, lon3);
        //print out in degrees

    }

    public boolean inBounds(double pointLat, double pointLong, LatLngBounds bounds) {
        boolean eastBound = pointLong < bounds.northeast.longitude;
        boolean westBound = pointLong > bounds.southwest.longitude;

        boolean inLong;
        boolean inLat;
        if (bounds.northeast.longitude < bounds.southwest.longitude) {
            inLong = eastBound || westBound;
        } else {
            inLong = eastBound && westBound;
        }

        inLat = pointLat > bounds.southwest.latitude && pointLat < bounds.northeast.latitude;
        return inLat && inLong;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Google API Callback", "Connection Failed");
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, "API not connected!", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }
}
