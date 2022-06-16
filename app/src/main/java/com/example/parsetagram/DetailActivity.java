package com.example.parsetagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;
//import org.w3c.dom.Comment;

import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    ImageView ivPostImageDetail;
    TextView tvHandle;
    TextView tvCaption;
    TextView tvTimestamp;
    RecyclerView rvComments;
    CommentsAdapter adapter;
    Post post;

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshComments();
    }

    private void refreshComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo(Comment.KEY_POST, post);
        query.orderByDescending("createdAt");
        query.include(Comment.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e("Failed to get comments", e.getMessage());
                    return;
                }
                adapter.mComments.clear();
                adapter.mComments.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView tvUsername = findViewById(R.id.tvUser);
        TextView tvDate = findViewById(R.id.tvDate);
        ImageView ivPhoto = findViewById(R.id.ivPic);
        TextView tvCaption = findViewById(R.id.tvCaption);
        ImageButton ibHeart = findViewById(R.id.ibPostLikes);
        TextView tvLikes = findViewById(R.id.tvPostLikes);
        rvComments = findViewById(R.id.rvComments);
        adapter = new CommentsAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);
        post = getIntent().getParcelableExtra(Post.class.getSimpleName());
        refreshComments();

        ibHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ParseUser> likedBy = post.getLikedBy();
                ParseUser user = ParseUser.getCurrentUser();

                if(post.isLikedByCurrentUser()) {
                    post.unlike();
                    ibHeart.setBackgroundResource(R.drawable.ufi_heart);
                } else {
                    post.like();
                    ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
                }

                if(likedBy.contains(ParseUser.getCurrentUser())) {
                    likedBy.remove(ParseUser.getCurrentUser());
                    //TODO: CHANGE THE UNLIKE IMAGE
                    ibHeart.setBackgroundResource(R.drawable.ufi_heart);

                } else {
                    likedBy.add(ParseUser.getCurrentUser());
                }
                post.setLikedBy(likedBy);
                post.saveInBackground(); //uploads new value back to parse
            }
        });
        ImageButton ibComment = findViewById(R.id.ibPostComments);

        ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to compose comment activity
                Intent i = new Intent(DetailActivity.this, CommentActivity.class);
                i.putExtra("post", post);
                startActivity(i);
            }
        });


        Date createdAt = post.getCreatedAt();
        String timeago = Post.calculateTimeAgo(createdAt);

        if(post.getLikedBy().contains(ParseUser.getCurrentUser())){
            ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
        } else {
            ibHeart.setBackgroundResource(R.drawable.ufi_heart);
        }
        tvLikes.setText(post.getLikesCount());
        tvDate.setText(timeago);
        tvCaption.setText(post.getDescription());
        Glide.with(this).load(post.getImage().getUrl()).into(ivPhoto);
    }
}
