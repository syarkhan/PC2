package com.example.sheryarkhan.projectcity.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.sheryarkhan.projectcity.adapters.CommentsListAdapter;
import com.example.sheryarkhan.projectcity.R;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;
    private CommentsListAdapter commentsListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


//        commentsRecyclerView = (RecyclerView)findViewById(R.id.commentsRecyclerView);
//        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        commentsListAdapter = new CommentsListAdapter();
//        commentsRecyclerView.setAdapter(commentsListAdapter);
//        Intent intent = getIntent();
//        String postId = intent.getStringExtra("postId");
//        Toast.makeText(this,postId,Toast.LENGTH_SHORT).show();



    }
}
