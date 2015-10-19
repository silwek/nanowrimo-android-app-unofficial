package nanowrimo.onishinji.ui.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.adapter.SectionsPagerAdapter;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.fragment.UserFragment;


public class MyActivity extends DrawerActivity implements UserFragment.OnRemoveListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private ActionBar.Tab mLastTab;
    private boolean mTurnOff = false;
    private PagerTitleStrip mPagerTitleStrip;
    private String mKnowsUsers = null;
    private int mLastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        reloadViewPager();

        mPagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);

        checkEmptyDatabase();

        if (getIntent() != null) {
            int index = mDatabase.getUsers().indexOf(getIntent().getStringExtra("from_widget_id"));
            if (index != -1) {
                mViewPager.setCurrentItem(index);
            }
        }
    }

    private void reloadViewPager() {
        mDatabase = Database.getInstance(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(mDatabase, mDatabase.getUsers(), this, null, getSupportFragmentManager(), this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private void checkEmptyDatabase() {
        ArrayList<String> users = mDatabase.getUsers();
        if (users.size() == 0) {
            //displayAddUserDialog(false);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if (id == R.id.add_user) {
//            displayAddUserDialog(true);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    protected void onAddUser(User user) {
        onAddTab(user.getId(), true);
    }

    @Override
    public void remove(String username) {

        int oldPos = mDatabase.getUsers().indexOf(username);
        mDatabase.deleteUser(username);
        onRemoveTab(oldPos);
    }

    @Override
    public String getNiceTitle(String username) {
        return mDatabase.getNiceTitle(username);
    }


    public void onAddTab(String text, Boolean selectLastTab) {

        reloadViewPager();

        if (selectLastTab) {
            mViewPager.setCurrentItem(mDatabase.getUsers().size());
        }
    }

    public void onRemoveTab(int position) {

        reloadViewPager();

        if (position - 1 >= 0 && position - 1 < mDatabase.getUsers().size()) {
            mViewPager.setCurrentItem(position--);
        }

        checkEmptyDatabase();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        mKnowsUsers = mDatabase.getUsersString();
        outState.putString("knowUsers", mDatabase.getUsersString());
        outState.putInt("currentItem", mViewPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mDatabase = Database.getInstance(this);
            if (!savedInstanceState.getString("knowUsers").equals(mDatabase.getUsersString())) {

                mSectionsPagerAdapter = new SectionsPagerAdapter(mDatabase, mDatabase.getUsers(), this, null, getSupportFragmentManager(), this);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                int lastPosition = savedInstanceState.getInt("currentItem");
                if (lastPosition < mDatabase.getUsers().size() && lastPosition >= 0) {
                    mViewPager.setCurrentItem(lastPosition);
                }


                checkEmptyDatabase();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mKnowsUsers = mDatabase.getUsersString();
        mLastPosition = mViewPager.getCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabase = Database.getInstance(this);

        if (mKnowsUsers != null && !mKnowsUsers.equals(mDatabase.getUsersString())) {

            reloadViewPager();

            if (mLastPosition < mDatabase.getUsers().size() && mLastPosition >= 0) {
                mViewPager.setCurrentItem(mLastPosition);
            }

            checkEmptyDatabase();
        }

    }
}
