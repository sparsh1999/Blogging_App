package com.example.sparsh.blogging_app;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFrag extends Fragment {

    private RecyclerView recyclerView;
    private List<Blog> blog_list;
    private FirebaseFirestore firestore;
    private BlogRecyclerAdapter adapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public HomeFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        blog_list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new BlogRecyclerAdapter(blog_list);
        firestore = FirebaseFirestore.getInstance();

        adapter.addActivity(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        if (FirebaseAuth.getInstance() != null) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean last = !recyclerView.canScrollVertically(-1);
                    if(last)
                    loadNextBlogs();
                }
            });

            Query firstQuery = firestore.collection("Posts_Details");

            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (isFirstPageFirstLoad)
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blog_id = doc.getDocument().getId();

                            Blog blog_post = doc.getDocument().toObject(Blog.class).withId(blog_id);

                            if (isFirstPageFirstLoad)
                                blog_list.add(blog_post);
                            else
                                blog_list.add(0, blog_post);

                            adapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            });
        }
        return view ;

    }

    public void loadNextBlogs()
    {
        Query nextQuery = firestore.collection("Posts_Details").orderBy("time",Query.Direction.DESCENDING)
                          .startAfter(lastVisible).limit(3);

        nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty())
                {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blog_id = doc.getDocument().getId();

                            Blog blog_post = doc.getDocument().toObject(Blog.class).withId(blog_id);
                            blog_list.add(blog_post);
                            adapter.notifyDataSetChanged();
                        } else {

                        }
                    }

                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
