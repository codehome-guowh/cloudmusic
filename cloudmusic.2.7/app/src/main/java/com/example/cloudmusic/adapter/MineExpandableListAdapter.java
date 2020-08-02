package com.example.cloudmusic.adapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activity.IndexActivity;
import com.example.cloudmusic.activity.SongSheetActivity;
import com.example.cloudmusic.beans.ChildData;
import com.example.cloudmusic.beans.GroupData;
import java.util.List;
public class MineExpandableListAdapter implements ExpandableListAdapter {
    private Context context;
    private List<GroupData> groupData;
    private List<List<ChildData>> childData;



   static class GroupViewHolder{
        ImageView arrowImage;
        TextView songSheetClass;
        TextView songSheetCount;
        ImageView toolMenuGroup;
    }
    static class ChildViewHolder{
        ImageView songSheetHeader;
        TextView songSheetName;
        TextView songCount;
        ImageView toolMenuChild;

    }

    public MineExpandableListAdapter(Context context, List<GroupData> groupData, List<List<ChildData>> childData) {
        this.context = context;
        this.groupData = groupData;
        this.childData = childData;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        int count=0;
        if(groupData!=null){
            count=groupData.size();
        }
        return count;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count=0;
        if(childData!=null){
            count=childData.get(groupPosition).size();
        }
        return count;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


//主体方法
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        GroupViewHolder holder=null;
        if(view==null){
            view=LayoutInflater.from(context).inflate(R.layout.mine_bottom_expandlist_group,viewGroup,false);
            holder=new GroupViewHolder();
            holder.songSheetClass=view.findViewById(R.id.text_songsheetclass);
            holder.songSheetCount=view.findViewById(R.id.text_songsheetcount);
            holder.arrowImage=view.findViewById(R.id.imageview_mine_bottom_arrow);
            holder.toolMenuGroup=view.findViewById(R.id.imageview_mine_bottom_groupmenu);
            view.setTag(holder);
        }
        else {
            holder=(GroupViewHolder)view.getTag();
        }


        GroupData groupData=this.groupData.get(groupPosition);
        if(isExpanded){
            holder.arrowImage.setImageResource(R.drawable.down);
        }
        else {
            holder.arrowImage.setImageResource(R.drawable.right);
        }

        holder.songSheetClass.setText(groupData.getSonsheetclass());
        holder.songSheetCount.setText(groupData.getSonsheetcount());
        holder.toolMenuGroup.setImageResource(R.drawable.menuright);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean b, View view, ViewGroup viewGroup) {
        final ChildViewHolder childHolder=new ChildViewHolder();

            view=LayoutInflater.from(context).inflate(R.layout.mine_bottom_expandlist_child,viewGroup,false);

            childHolder.songSheetHeader=view.findViewById(R.id.imageview_mine_bottom_songheader);
            childHolder.songSheetName=view.findViewById(R.id.text_mine_songsheetname);
            childHolder.songCount=view.findViewById(R.id.text_songamount);
            childHolder.toolMenuChild=view.findViewById(R.id.imageview_mine_bottom_childmenu);




        Log.d("music45",""+childPosition);
        final ChildData childInf=this.childData.get(groupPosition).get(childPosition);
        childHolder.songSheetHeader.setImageResource(childInf.getSongSheetHeaderId());
        childHolder.songSheetName.setText(childInf.getSongSheetName());
        childHolder.songCount.setText("("+childInf.getSongCount()+")");
        childHolder.toolMenuChild.setImageResource(R.drawable.deletehide);

//        触发删除事件

            childHolder.toolMenuChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });






//        歌单点击事件(未完)
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Intent intentToInfo=new Intent(context, SongSheetActivity.class);
                intentToInfo.putExtra("songAmount",childInf.getSongCount());
                context.startActivity(intentToInfo);

            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }



//    无须改动部分*****

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }


    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);

        builder.setTitle("温馨提示");
        builder.setMessage("歌单已删除！");
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentRefresh=new Intent(context, IndexActivity.class);
                        context.startActivity(intentRefresh);

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
}





