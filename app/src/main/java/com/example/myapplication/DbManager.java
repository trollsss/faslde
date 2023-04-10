package com.example.myapplication;

import androidx.annotation.NonNull;

import com.example.myapplication.adapter.DataSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private Query mQuery;
    private List<NewPost> newPostsList;
    private DataSender dataSender;
    private FirebaseDatabase db;
    private int cat_ads_counter = 0;
    private String[] category_ads = {"Машины", "Компьютеры", "Смартфоны", "Бытовая техника"};

    public DbManager(DataSender dataSender) {
        this.dataSender = dataSender;
        newPostsList = new ArrayList<>();
        db = FirebaseDatabase.getInstance();
    }

    public void getDataFromDb(String path) {
        DatabaseReference dbRef = db.getReference(path);
        mQuery = dbRef.orderByChild("anuncios/time");
        readDataUpdate();
    }
    public void getMyAdsDataFromDb(String uid) {
        if (newPostsList.size() > 0) newPostsList.clear();
        DatabaseReference dbRef = db.getReference(category_ads[0]);
        mQuery = dbRef.orderByChild("anuncios/uid").equalTo(uid);
        readMyAdsDataUpdate(uid);
        cat_ads_counter++;
    }
    public void readDataUpdate() {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (newPostsList.size() > 0) newPostsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    NewPost newPost = ds.child("anuncio").getValue(NewPost.class);
                    newPostsList.add(newPost);
                }

                dataSender.onDataRecived(newPostsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void readMyAdsDataUpdate(final String uid) {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    NewPost newPost = ds.child("anuncio").getValue(NewPost.class);
                    newPostsList.add(newPost);
                }
                if (cat_ads_counter > 3)
                {
                    dataSender.onDataRecived(newPostsList);
                    newPostsList.clear();
                    cat_ads_counter = 0;
                }
                else
                {
                    DatabaseReference dbRef = db.getReference(category_ads[cat_ads_counter]);
                    mQuery = dbRef.orderByChild("anuncios/uid").equalTo(uid);
                    readMyAdsDataUpdate(uid);
                    cat_ads_counter++;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}