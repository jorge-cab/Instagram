package com.example.instagram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.EditProfileActivity;
import com.example.instagram.LoginActivity;
import com.example.instagram.Post;
import com.example.instagram.PostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends HomeFragment {

    public static final String TAG = "ProfileFragment";

    public static final int EDIT_ACTIVITY_CODE = 20;

    private TextView tvUsername;
    private TextView tvPostsNumber;
    private ImageView ivProfilePicture;
    private TextView tvDescription;

    private Button btnEditProfile;
    private Button btnLogOut;

    private int limit = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvPostsNumber = view.findViewById(R.id.tvPostsNumber);
        tvDescription = view.findViewById(R.id.tvDescription);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        ParseUser user = ParseUser.getCurrentUser();

        tvUsername.setText(user.getUsername());
        tvDescription.setText(user.getString("userDescription"));

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        try {
            tvPostsNumber.setText(String.valueOf(query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()).count()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Glide.with(getContext())
                .load(user.getParseFile("profileImage").getUrl())
                .into(ivProfilePicture);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();

                // ParseUser currentUser = ParseUser.getCurrentUser();

                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);

                getActivity().finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditProfileActivity.class);
                startActivityForResult(i, EDIT_ACTIVITY_CODE);
            }
        });
    }

    @Override
    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(INIT_LIMIT);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, com.parse.ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // updates the limit
                limit = posts.size();

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void queryPostsUpdate() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // Only query for posts that have our user
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(INIT_LIMIT);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, com.parse.ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                adapter.clear();

                // updates the limit
                limit = posts.size();

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                adapter.addAll(posts);
                // Refreshed finished
                swipeContainer.setRefreshing(false);
                // Reset scrollListener
                scrollListener.resetState();
            }
        });
    }

    @Override
    protected void loadNextData(int page) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // Skip already loaded posts
        Log.i(TAG, "Limit: " + limit);
        query.setSkip(limit);
        // limit query to latest 20 items
        query.setLimit(INIT_LIMIT);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, com.parse.ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                Log.i(TAG, "LoadNextData");
                // Updates the limit
                limit = limit + posts.size();

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                adapter.addAll(posts);
                // Refreshed finished
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvDescription.setText(ParseUser.getCurrentUser().getString("userDescription"));
        Glide.with(getContext())
                .load(ParseUser.getCurrentUser().getParseFile("profileImage").getUrl())
                .into(ivProfilePicture);
    }
}