package com.example.myapplication.adapter;
import com.example.myapplication.NewPost;
import java.util.List;
public interface DataSender {
    public void onDataRecived(List<NewPost> listData);
}
