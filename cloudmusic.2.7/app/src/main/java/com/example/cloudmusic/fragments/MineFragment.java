package com.example.cloudmusic.fragments;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.cloudmusic.R;
import com.example.cloudmusic.activity.LocalSourceActivity;
import com.example.cloudmusic.adapter.MineExpandableListAdapter;
import com.example.cloudmusic.beans.ChildData;
import com.example.cloudmusic.beans.GroupData;
import com.example.cloudmusic.views.ExpandableScrollListView;
import java.util.ArrayList;
import java.util.List;


public class MineFragment extends Fragment {
    private List<GroupData> groupDataList;
    private List<List<ChildData>> childDataList;
    String[] songSheetClass,songSheetCount,songSheetNameCreate,songSheetNameCollect;
    int[] songSheetHeader,songCountCreate,songCountCollect;
    private ExpandableListAdapter expandAdapter;
    ContentResolver contentResolver;
    private Uri uriSongSheet;







    @Override
    public void onCreate(Bundle savedInstanceState) {

//        数据的初始化

        super.onCreate(savedInstanceState);
        uriSongSheet=Uri.parse("content://com.example.cloudmusic.songsheetprovider/songsheet");
        songSheetHeader=new int[]{R.drawable.head1,R.drawable.head2,R.drawable.head3,R.drawable.head4,R.drawable.head5,R.drawable.head6,
                R.drawable.head7,R.drawable.head8,R.drawable.head9,R.drawable.head10};
        songSheetClass=new String[]{"创建的歌单","收藏的歌单"};
        songSheetCount=new String[]{"（"+8+"）","（"+2+"）"};
        songSheetNameCreate=new String[]{"我的最爱","英文歌单","中文歌单","粤语歌单","日语歌单","情歌歌单","看书歌单","下雨歌单"};
        songCountCreate=new int[]{1,1,1,1,1,1,1,1};
        songCountCollect=new int[]{1,2};
        songSheetNameCollect=new String[]{"我的最爱","英文歌单"};

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        获取页面布局 及页面控件
        View myLayOut=inflater.inflate(R.layout.fragment_mine,container,false);
        ListView listView = myLayOut.findViewById(R.id.list_mine_top);
        ExpandableScrollListView expandableScrollListView=myLayOut.findViewById(R.id.lt_expandable_mine_bottom);
        contentResolver=getContext().getContentResolver();
        Cursor cursorTest=contentResolver.query(uriSongSheet,null,null,null,null);
        if(!cursorTest.moveToNext()){
            ContentValues contentValues=new ContentValues();
            for(int count=0;count<songSheetNameCreate.length;count++){
                contentValues.put("songsheetname",songSheetNameCreate[count]);
                contentValues.put("amount",songCountCreate[count]);
                contentValues.put("headerid",songSheetHeader[count]);
                contentResolver.insert(uriSongSheet,contentValues);

            }
        }
//        Log.d("action123",""+contentResolver.delete(uriSongSheet,null,null));


//        重写baseadapter

        BaseAdapter mineTopListAdapter = new BaseAdapter() {
            class ViewHolder {
                 ImageView header;
                 TextView listName, songCount;
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                ViewHolder viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(R.layout.mine_top_list, viewGroup, false);
                viewHolder.header = view.findViewById(R.id.imageview_mine_top_header);
                viewHolder.listName = view.findViewById(R.id.text_mine_name);
                viewHolder.songCount = view.findViewById(R.id.text_mine_count);
                switch (position) {
                    case 0:
                        viewHolder.header.setImageResource(R.drawable.music);
                        viewHolder.listName.setText("本地音乐");
                        viewHolder.songCount.setText("(4)");
                        break;
                    case 1:
                        viewHolder.header.setImageResource(R.drawable.play);
                        viewHolder.listName.setText("最近播放");
                        viewHolder.songCount.setText("(4)");
                        break;
                    case 2:
                        viewHolder.header.setImageResource(R.drawable.download);
                        viewHolder.listName.setText("下载管理");
                        viewHolder.songCount.setText("(4)");
                        break;
                    case 3:
                        viewHolder.header.setImageResource(R.drawable.bookmark);
                        viewHolder.listName.setText("我的收藏");
                        viewHolder.songCount.setText("(4)");
                        break;
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(), LocalSourceActivity.class);
                        startActivity(intent);

                    }
                });
                return view;
            }
        };


        listView.setAdapter(mineTopListAdapter);




//        底部expandablelist的实现






        groupDataList=new ArrayList<GroupData>();
        for(int count=0;count<songSheetClass.length;count++){
            GroupData groupData=new GroupData(songSheetClass[count],songSheetCount[count]);
            groupDataList.add(groupData);
        }


        childDataList=new ArrayList<>();
        for(int count=0;count<songSheetClass.length;count++){
            List<ChildData> dataList=new ArrayList<ChildData>();
            switch (count){
                case 0:
                    contentResolver=getContext().getContentResolver();
                    Cursor cursorSongSheetChild=contentResolver.query(uriSongSheet,null,null,null,null);
                    while(cursorSongSheetChild.moveToNext()){
                        Log.d("action123","1");
                        ChildData childData=new ChildData(cursorSongSheetChild.getInt(cursorSongSheetChild.getColumnIndex("headerid")),
                                cursorSongSheetChild.getString(cursorSongSheetChild.getColumnIndex("songsheetname")),
                                cursorSongSheetChild.getInt(cursorSongSheetChild.getColumnIndex("amount")));
                        dataList.add(childData);
                    }
                    childDataList.add(dataList);
                    break;
                case 1:for (int childcount=0;childcount<songSheetNameCollect.length;childcount++){
                    Log.d("music456",""+songSheetNameCollect.length);
                        ChildData childData=new ChildData(songSheetHeader[childcount],songSheetNameCollect[childcount],songCountCollect[childcount]);
                        dataList.add(childData);
                    }
                    childDataList.add(dataList);
                    break;

            }

        }
        expandAdapter=new MineExpandableListAdapter(getContext(),groupDataList,childDataList);
        expandableScrollListView.setAdapter(expandAdapter);
        return myLayOut;

    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }





}
