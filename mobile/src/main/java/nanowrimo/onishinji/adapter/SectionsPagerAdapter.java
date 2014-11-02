package nanowrimo.onishinji.adapter;


import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.ui.fragment.UserFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private  UserFragment.OnRemoveListener listener;
    private Database dataSource;

    private ArrayList<UserFragment> tabInfos;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public SectionsPagerAdapter(Database dataSource, UserFragment.OnRemoveListener listener, FragmentManager fm) {
        super(fm);
        this.listener = listener;
        this.dataSource = dataSource;
        tabInfos = new ArrayList<UserFragment>();

    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        UserFragment f = new UserFragment();
        f.setUsername(dataSource.getUsers().get(position));
        f.setOnRemoveListener(listener);

//        tabInfos.add(position, f);

        return f;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return dataSource.getUsers().size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return dataSource.getUsers().get(position);
    }

}