package com.example.cloudmusic.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.cloudmusic.fragments.FindsFragment;
import com.example.cloudmusic.fragments.FriendsFragment;
import com.example.cloudmusic.fragments.MineFragment;
import com.example.cloudmusic.fragments.VideoFragment;


public class IndexViewPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT=4;
    private FindsFragment findsFragment;
    private MineFragment mineFragment;
    private VideoFragment videoFragment;
    private FriendsFragment friendsFragment;

    public IndexViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        findsFragment=new FindsFragment();
        friendsFragment=new FriendsFragment();
        mineFragment=new MineFragment();
        videoFragment=new VideoFragment();
    }
    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=mineFragment;
                break;
            case 1:
                fragment=findsFragment;
                break;
            case 2:
                fragment=friendsFragment;
                break;
            case 3:
                fragment=videoFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
