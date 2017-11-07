package com.example.sheryarkhan.projectcity.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.adapters.TownsRecyclerAdapter;

import java.util.ArrayList;

public class TownAutoCompleteActivity extends AppCompatActivity {


    RecyclerView autocompleteRecyclerView;
    TownsRecyclerAdapter townsRecyclerAdapter;
    EditText editTextSearchTown;
    private static ArrayList<Constants.Areas> areasArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_town_auto_complete);

        autocompleteRecyclerView = (RecyclerView)findViewById(R.id.townAutocompleteRecyclerView);
        autocompleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        editTextSearchTown = (EditText)findViewById(R.id.editTextSearchTown);
        townsRecyclerAdapter = new TownsRecyclerAdapter(this);
        autocompleteRecyclerView.setAdapter(townsRecyclerAdapter);


        areasArrayList = Constants.getTownsAndAreas();

        //text change listner
        editTextSearchTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                townsRecyclerAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autocompleteRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Toast.makeText(TownAutoCompleteActivity.this,townsRecyclerAdapter.getItem(position),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("Town",townsRecyclerAdapter.getItem(position));
                //townsRecyclerAdapter.getTOWNs().clear();
                setResult(Activity.RESULT_OK, intent);
                finish();


            }
        }));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }
}
