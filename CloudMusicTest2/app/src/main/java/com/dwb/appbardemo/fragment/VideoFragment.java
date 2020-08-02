package com.dwb.appbardemo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dwb.appbardemo.R;
import com.dwb.appbardemo.adapter.VideoRecyclerAdapter;
import com.dwb.appbardemo.model.VideoModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JzvdStd;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class VideoFragment extends Fragment {




    RecyclerView mainlv;
    String url = "https://baobab.kaiyanapp.com/api/v4/tabs/selected";
    //?udid=11111&vc=168&vn=3.3.1&deviceModel=Huawei6&first_channel=eyepetizer_baidu_market&system_version_code=20
    List<VideoModel.ItemListBean> mDatas;
    VideoRecyclerAdapter adapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1){
                String json = (String) msg.obj;
                //解析数据
                VideoModel videoModel = new Gson().fromJson(json,VideoModel.class);
                //过滤不需要的数据
                List<VideoModel.ItemListBean> itemList = videoModel.getItemList();
                for (int i = 0; i < itemList.size(); i++) {
                    VideoModel.ItemListBean itemListBean = itemList.get(i);
                    if (itemListBean.getType().equals("video")){
                        mDatas.add(itemListBean);
                    }
                }
                //提示适配器更新数据
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_video, container, false);

        initView(view);

        return view;
    }

    private void initView(View view){

        mainlv = view.findViewById(R.id.lv_main);
        mainlv.setLayoutManager(new GridLayoutManager(getContext(),1));
        //数据源
        mDatas = new ArrayList<>();
        //创建适配器对象
        adapter = new VideoRecyclerAdapter(getContext(), mDatas);
        //设置适配器
        mainlv.setAdapter(adapter);

        //加载网络数据
        loadData();

    }

    private void loadData() {

        //创建新的线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String jsonContent = response.body().string();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = jsonContent;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        JzvdStd.releaseAllVideos();//释放正在被播放的视频信息
    }
}
