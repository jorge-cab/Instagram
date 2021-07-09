package com.example.instagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.w3c.dom.Text;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {


    private Context context;
    private List<Post> posts;
    private OnClickListener clickListener;

    public interface OnClickListener {
        void onPostClicked(int position);
        void onProfileClicked(int position);
    }

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
        this.clickListener = clickListener;
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private ImageView ivProfilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();

            Glide.with(context)
                    .load(post.getUser().getParseFile("profileImage").getUrl())
                    .into(ivProfilePicture);

            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clickListener.onProfileClicked(getAdapterPosition()); }
            });
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clickListener.onPostClicked(getAdapterPosition()); }
            });

            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivImage);
            }
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
