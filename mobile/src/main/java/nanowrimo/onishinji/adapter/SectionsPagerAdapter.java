package nanowrimo.onishinji.adapter;


import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.ui.fragment.UserFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends SortableFragmentStatePagerAdapter {

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

        String id = dataSource.getUsers().get(position);

        UserFragment f = new UserFragment();
        f.setId(id);
        f.setUsername(getPageTitle(position));
        f.setOnRemoveListener(listener);
        f.setPosition(position);

        Log.d("fragment", " getItem " + position + " " + id);

        return f;
    }

    @Override
    public long getItemId(int position) {
        return dataSource.getUsers().get(position).toString().hashCode();
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
       // Log.d("fragment", " getCount " + dataSource.getUsers().size());

        if(dataSource != null) {
            return dataSource.getUsers().size();
        }

        return 0;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return dataSource.getNiceTitle(position);
    }
/*
    @Override
    public int getItemPosition(Object object){

        UserFragment fragment = (UserFragment) object;

        int newPos = dataSource.getUsers().indexOf(fragment.getUserId());

        if(fragment.getPosition() == newPos) {
            Log.d("fragment", " alors l'ancienne position du fragment " + fragment.getPosition() + " est encore à la bonne place" + newPos);
            return POSITION_UNCHANGED;
        }

        Log.d("fragment", " le fragment n'est plus le même pour " + newPos + " vs " + fragment.getPosition());

        return POSITION_NONE;
    }
*/
    public void setDatabase(Database database) {
        this.dataSource = database;
    }

}