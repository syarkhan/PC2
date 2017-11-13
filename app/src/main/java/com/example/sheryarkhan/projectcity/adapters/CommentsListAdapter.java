package com.example.sheryarkhan.projectcity.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.HelperFunctions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Comment;

/**
 * Created by Sheryar Khan on 9/7/2017.
 */

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentsHolder> {

    private List<Comment> commentsList = Collections.emptyList();
    private StorageReference storageReference;

    public CommentsListAdapter(ArrayList<Comment> commentsList){
        this.commentsList = commentsList;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public CommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new CommentsHolder(LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false));
    }



    @Override
    public void onBindViewHolder(CommentsHolder holder, int position) {

        Context context = holder.itemView.getContext();
        CommentsListAdapter.CommentsHolder mholder = holder;
        SetUpCommentsView(context,mholder,position);

    }

    private void SetUpCommentsView(final Context context, final CommentsHolder mholder, int position) {

        final Comment currentData = commentsList.get(mholder.getAdapterPosition());
        mholder.txtName.setText(currentData.getUserInfo().getUsername());
        mholder.txtTimestamp.setText(HelperFunctions.getTimeAgo(currentData.getTimestamp()));
        //mholder.txtLikes.setText("126 Likes");
        mholder.txtComment.setText(currentData.getCommentText());
        if (currentData.getUserInfo().getProfilePicture() != null) {
            StorageReference filePath = storageReference.child("profilepictures").child(currentData.getUserInfo().getProfilePicture());
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        GlideApp.with(context)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .circleCrop()
                                .transition(DrawableTransitionOptions.withCrossFade(1000))
                                .error(R.color.link)
                                .into(mholder.imgProfilePic);
                    } catch (Exception ex) {
                        Log.d("error", ex.toString());
                    }
                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return commentsList.size() == 0 ? 0 : commentsList.size();
    }

    class CommentsHolder extends RecyclerView.ViewHolder{

        TextView txtName;
        TextView txtTimestamp;
        TextView txtComment;
        //TextView txtLikes;
        ImageView imgProfilePic;

        private CommentsHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtTimestamp = (TextView) itemView.findViewById(R.id.txtTimestamp);
            txtComment = (TextView) itemView.findViewById(R.id.txtComment);
//            txtLikes = (TextView) itemView.findViewById(R.id.txtLikes);
            imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);


        }
    }



}


