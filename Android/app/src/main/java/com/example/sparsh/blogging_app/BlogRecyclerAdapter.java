package com.example.sparsh.blogging_app;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.Placeholder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private List<Blog> blog_list;
    private Context context;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    FragmentActivity c ;
    String user_id ;

    public BlogRecyclerAdapter(List<Blog> blog_list)
    {
        this.blog_list = blog_list;
    }

    public void addActivity(FragmentActivity c)
    {
        this.c = c;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_item,viewGroup,false);
        context = viewGroup.getContext();
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final String blog_id = blog_list.get(i).BlogPostId;

        String desc_data = blog_list.get(i).getContent();
        viewHolder.setDescText(desc_data);

        String img_uri = blog_list.get(i).getImg_uri();
        viewHolder.setImage(img_uri);

        String uid = blog_list.get(i).getUser_id();
        firestore = FirebaseFirestore.getInstance();

        final String date = blog_list.get(i).getTime();
        viewHolder.setBlogDate(date);

        firestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
             if (task.isSuccessful())
             {
                 viewHolder.setUser(task.getResult().get("name").toString());
                 viewHolder.setUserImg(task.getResult().get("image").toString());
             }
             else
             {
                 Toast.makeText(context,"Firestore Error "+task.getException().toString(), Toast.LENGTH_SHORT).show();
             }
            }
        });

        //Get total number of likes
        if (mAuth!=null) {
            firestore.collection("Posts_Details/" + blog_id + "/likes").addSnapshotListener(c, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()) {
                        viewHolder.updateLikes(documentSnapshots.size());
                    } else {
                        viewHolder.updateLikes(0);
                    }
                }
            });

            //like button image change functionality otherwise it won't load without clikging the button i.e on Start
            firestore.collection("Posts_Details/" + blog_id + "/likes").document(user_id).addSnapshotListener(c, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {
                        viewHolder.like_btn.setImageDrawable(context.getDrawable(R.mipmap.ic_fav_color));
                    } else {
                        viewHolder.like_btn.setImageDrawable(context.getDrawable(R.mipmap.ic_favourite));
                    }
                }
            });

            //like's button functionality
            viewHolder.like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firestore.collection("Posts_Details/" + blog_id + "/likes").document(user_id).get().addOnCompleteListener(c, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isComplete()) {
                                if (!task.getResult().exists()) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("time", new Date());
                                    firestore.collection("Posts_Details/" + blog_id + "/likes").document(user_id).set(map);
                                } else {
                                    firestore.collection("Posts_Details/" + blog_id + "/likes").document(user_id).delete();
                                }
                            } else {

                            }
                        }
                    });
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private  View view ;
        private TextView blog_content;
        private ImageView blog_image;
        private TextView user_name;
        private ImageView user_img_uri;
        private TextView blog_date;
        private ImageView like_btn , comment_btn, share_btn;
        private TextView like_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            like_btn = view.findViewById(R.id.favourites_btn);
            comment_btn = view.findViewById(R.id.comments_btn);
            share_btn = view.findViewById(R.id.share_btn);
            like_count = view.findViewById(R.id.like_count);
        }

        public void setLikeCount(int likes)
        {
            String like = likes+" likes";
            like_count.setText(like);
        }

        public void setDescText(String content)
        {
            blog_content = view.findViewById(R.id.blog_content);
            blog_content.setText(content);
        }

        public void setImage(String img_uri)
        {
            blog_image = view.findViewById(R.id.current_blog_image);

            RequestOptions requestOptions  = new RequestOptions();
            requestOptions.placeholder(R.mipmap.rectangle);

            Glide.with(blog_image.getContext()).setDefaultRequestOptions(requestOptions).load(img_uri).into(blog_image);
        }
        public void setUser(String uname)
        {
            user_name = view.findViewById(R.id.blog_uname);
            user_name.setText(uname);
        }
        public void setUserImg(String image)
        {
            user_img_uri = view.findViewById(R.id.blog_user_img);
            // this is the default image icon
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.thumbnail);

            Glide.with(user_img_uri.getContext()).setDefaultRequestOptions(requestOptions).load(image).into(user_img_uri);
        }
        public void setBlogDate(String date)
        {
            blog_date = view.findViewById(R.id.blog_date);
            blog_date.setText(date);
        }
        public void updateLikes(int like)
        {
            like_count.setText(like + " likes");
        }
    }


}
