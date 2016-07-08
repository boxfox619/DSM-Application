package com.boxfox.dsm_boxfox.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.boxfox.dsm_boxfox.ArrayListFragment;
import com.boxfox.dsm_boxfox.Sub.CardListFragment;
import com.boxfox.dsm_boxfox.Sub.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends FragmentStatePagerAdapter {
    private final List<String> mFragmentTitleList = new ArrayList<>();
    public MyAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 4;
    }

    public void addFrag(String title) {
        mFragmentTitleList.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show image
                return MainFragment.init(position);
            case 1:
                return CardListFragment.init(1);
            case 2: // Fragment # 1 - This will show image
                return CardListFragment.init(2);
            case 3:
                return ArrayListFragment.init(3);
            default:// Fragment # 2-9 - Will show list
                return null;
        }
    }
}
