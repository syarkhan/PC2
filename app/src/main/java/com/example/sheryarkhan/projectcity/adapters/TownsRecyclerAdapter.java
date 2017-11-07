package com.example.sheryarkhan.projectcity.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sheryar Khan on 10/10/2017.
 */

public class TownsRecyclerAdapter extends RecyclerView.Adapter<TownsRecyclerAdapter.MainViewHolder> implements Filterable {

    //public static List<Address> addresses;
    //private ArrayList towns;
    private static ArrayList<Constants.Areas> TOWN;
    private static ArrayList<String> ifTownsAddedList = new ArrayList<>();
    //List<List<String>> AREAS;
    private int i = 0;
    //int a = 0;
    //private boolean isArea = false;

    private Context mContext;
    //private Context context;
    //private int layout;

    private static final int TYPE_TOWN = 1;
    private static final int TYPE_AREA = 2;

    public TownsRecyclerAdapter(Context context) {
        mContext = context;
        //AREAS = Constants.getAreas();
        TOWN = Constants.getTownsAndAreas();


    }

    @Override
    public int getItemViewType(int position) {


        //ArrayList<String> areas  = (ArrayList<String>) new ArrayList<>(TOWN.get(position).values()).get(0);
        //int size = areas.size();

        if (TOWN.get(position).isHeaderOrTown()) {
            return TYPE_TOWN;
        } else {
            return TYPE_AREA;
        }

        //return super.getItemViewType(position);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInflater.from(context).inflate(R.layout.activity_location_autocomplete, parent, false)
        //context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView;
        switch (viewType) {
            case TYPE_AREA:

                convertView = layoutInflater.inflate(R.layout.area_autocomplete_row_list_item, parent, false);
                TownsRecyclerAdapter.AreaHolder mAreaHolder;
                mAreaHolder = new TownsRecyclerAdapter.AreaHolder(convertView);
                return mAreaHolder;
            //return new NewsFeedRecyclerAdapter.OnlyPostImageViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_list_item, parent, false));

            case TYPE_TOWN:

                convertView = layoutInflater.inflate(R.layout.town_autocomplete_row_list_item, parent, false);
                TownsRecyclerAdapter.TownHolder mTownHolder;
                mTownHolder = new TownsRecyclerAdapter.TownHolder(convertView);
                return mTownHolder;
            //return new NewsFeedRecyclerAdapter.ShareNewsPostViewHolder(LayoutInflater.from(context).inflate(R.layout.share_news_item_layout, parent, false));

        }
        return null;
    }


    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {

        if (holder.getItemViewType() == TYPE_TOWN) {
            i++;
            TownsRecyclerAdapter.TownHolder mholder = (TownsRecyclerAdapter.TownHolder) holder;
            //String t = (String) new ArrayList<>(TOWN.get(position).values()).get(1);
            mholder.txtPrimaryPlace.setText(TOWN.get(position).getTown());
            //i++;
        }
        if (holder.getItemViewType() == TYPE_AREA) {
            i++;
            TownsRecyclerAdapter.AreaHolder mholder = (TownsRecyclerAdapter.AreaHolder) holder;
            //ArrayList<String> areas = (ArrayList<String>) new ArrayList<>(TOWN.get(position).values()).get(0);
            mholder.txtArea.setText(TOWN.get(position).getArea());
        }


        //if(getItemViewType(position) == TYPE_AREA)
//        if(TOWN.get((new ArrayList<>(TOWN.keySet()).get(position))).get(position) )


        //mTownHolder.txtPrimaryPlace.setText((new ArrayList<>(TOWN.keySet()).get(position)));
        //mTownHolder.txtPrimaryPlace.setText(mResultList.get(i).primaryPlace);
        //mTownHolder.txtSecondaryPlace.setText(mResultList.get(i).secondaryPlace);
        /*mTownHolder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetLatLonCallback.getLocation(resultList.get(i).toString());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        if (TOWN != null)
            return TOWN.size();
        else
            return 0;
    }

    public ArrayList<Constants.Areas> getTOWNs(){
        return TOWN;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                //Implement filter logic
                // if edittext is null return the actual list
                if (charSequence == null || charSequence.length() == 0) {
                    //No need for filter
                    TOWN.clear();
                    ifTownsAddedList.clear();
                    TOWN = Constants.getTownsAndAreas();
                    results.values = TOWN;
                    results.count = TOWN.size();

                } else {
                    //Need Filter
                    // it matches the text  entered in the edittext and set the data in adapter list
                    ArrayList<Constants.Areas> fRecords = new ArrayList<>();

                    //TOWN.clear();
                    //TOWN.addAll((Vector<>)results.values);
                    TOWN.clear();
                    ifTownsAddedList.clear();
                    TOWN = Constants.getTownsAndAreas();
                    for (Constants.Areas s : TOWN) {
                        if (!s.isHeaderOrTown()) { //NO TOWN
                            if (s.getArea().toUpperCase().trim().contains(charSequence.toString().toUpperCase().trim())) {
                                int index = TOWN.indexOf(s);
                                for (int i = index; i >= 0; i--) {
                                    if (TOWN.get(i).isHeaderOrTown()) {
                                        if (ifTownsAddedList.size() > 0) {
                                            if (ifTownsAddedList.contains(TOWN.get(i).getTown())) {
                                                break;
                                            }
                                        }
                                        if (TOWN.get(i).isHeaderOrTown()) {
                                            fRecords.add(TOWN.get(i));
                                            ifTownsAddedList.add(TOWN.get(i).getTown());
                                            break;
                                        }
                                    }
                                }
                                fRecords.add(s);
                            }
                        }
                        /*else { // IS TOWN
                            if (ifTownsAddedList.size() > 0) {
                                if (ifTownsAddedList.contains(TOWN.get(i).getTown())) {
                                    break;
                                } else {
                                    fRecords.add(TOWN.get(i));
                                    ifTownsAddedList.add(TOWN.get(i).getTown());
                                }
                            }
//                            if (s.getTown().toUpperCase().trim().contains(charSequence.toString().toUpperCase().trim())) {
//                                fRecords.add(s);
//                            }
                        }*/
                    }
                    results.values = fRecords;
                    results.count = fRecords.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    if (charSequence == null || charSequence.length() == 0) {
                        notifyDataSetChanged();
                    } else {
                        TOWN.clear();
                        //TOWN.addAll((Constants.Areas)results.values);
                        List<Constants.Areas> areas = (List<Constants.Areas>) results.values;
                        //List<String> filteredAreas = new ArrayList<>();

                        for (int i = 0; i < areas.size(); i++) {
                            TOWN.add(areas.get(i));
                        }
                        notifyDataSetChanged();
                    }
                } else {
                    // The API did not return any results, invalidate the data set.
                    TOWN.clear();
                    notifyDataSetChanged();

                }
            }
        };
        return filter;
    }

    public String getItem(int position) {
        return TOWN.get(position).getTown();
    }

    public class TownHolder extends MainViewHolder {

        TextView txtPrimaryPlace;
        ConstraintLayout mRow;

        public TownHolder(View itemView) {


            super(itemView);
            txtPrimaryPlace = (TextView) itemView.findViewById(R.id.txtPrimaryPlace);
            mRow = (ConstraintLayout) itemView.findViewById(R.id.autocompleteRow);
        }

    }


    public class AreaHolder extends MainViewHolder {

        TextView txtArea;

        //ConstraintLayout mRow;
        public AreaHolder(View itemView) {

            super(itemView);
            txtArea = (TextView) itemView.findViewById(R.id.txtArea);

            //mRow=(ConstraintLayout)itemView.findViewById(R.id.autocompleteRow);
        }

    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder(View v) {
            super(v);
        }
    }
}
