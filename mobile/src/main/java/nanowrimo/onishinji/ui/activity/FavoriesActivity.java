package nanowrimo.onishinji.ui.activity;

import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.adapter.SectionsPagerAdapter;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.ui.fragment.RankingFragment;
import nanowrimo.onishinji.ui.fragment.UserFragment;
import nanowrimo.onishinji.utils.DialogUtils;
import nanowrimo.onishinji.utils.StringUtils;


public class FavoriesActivity extends ToolbarActivity implements UserFragment.OnRemoveListener, RankingFragment.RankingListener {


    protected Database mDatabase;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private PagerTitleStrip mPagerTitleStrip;
    private String mKnowsUsers;
    private ArrayList<User> mUsers;
    private ArrayList<String> mUsersIds;
    private int mLastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favories);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);

        mKnowsUsers = null;
        mUsersIds = new ArrayList<>(0);

        reloadViewPager();

        if (getIntent() != null) {
            int index = mDatabase.getUsers().indexOf(getIntent().getStringExtra("from_widget_id"));
            if (index != -1) {
                mViewPager.setCurrentItem(index);
            }
        }

        setTitle(R.string.dashboard_my_favories);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void reloadViewPager() {
        mDatabase = Database.getInstance(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(mDatabase, mUsersIds, this, this, getSupportFragmentManager(), this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        if (mLastPosition >= 0 && mLastPosition < mSectionsPagerAdapter.getCount()) {
            mViewPager.setCurrentItem(mLastPosition);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_user:
                DialogUtils.displayAddUserDialog(this, new DialogUtils.CallbackWithUser() {
                    @Override
                    public void onSuccess(User user) {
                        onAddUser(user);
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

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


    public void onAddTab(String username, Boolean selectTab) {

        reloadViewPager();

        if (selectTab) {
            mViewPager.setCurrentItem(0);
        }
    }

    public void onRemoveTab(int position) {

        final String username = mSectionsPagerAdapter.getUserId(position);
        mUsersIds.remove(username);

        reloadViewPager();

        if (position - 1 >= 0 && position - 1 < mSectionsPagerAdapter.getCount()) {
            mViewPager.setCurrentItem(position - 1);
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        mKnowsUsers = mDatabase.getUsersString();
        outState.putStringArrayList("users", mUsersIds);
        outState.putString("knowUsers", mDatabase.getUsersString());
        outState.putInt("currentItem", mViewPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mDatabase = Database.getInstance(this);

            mKnowsUsers = savedInstanceState.getString("knowUsers");
            mUsersIds = savedInstanceState.getStringArrayList("users");
            mLastPosition = savedInstanceState.getInt("currentItem");
            reloadViewPager();
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

        /*
        if (mKnowsUsers != null && !mKnowsUsers.equals(mDatabase.getUsersString())) {

            reloadViewPager();

            if (mLastPosition < mDatabase.getUsers().size() && mLastPosition >= 0) {
                mViewPager.setCurrentItem(mLastPosition);
            }

            checkEmptyDatabase();
        }*/

    }

    @Override
    public void onSelectUser(String username) {
        final int position = mSectionsPagerAdapter.getPosition(username);
        if (position >= 0)
            mViewPager.setCurrentItem(position);
    }

    @Override
    public void onSortUsers(ArrayList<User> users) {
        mUsers = users;
        final String currentUser = Database.getInstance(this).getUsers().get(0);
        ArrayList<String> list = new ArrayList<>(users.size() - 1);
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (!StringUtils.safeEquals(currentUser, u.getId())) {
                list.add(u.getId());
            }
        }
        String newUsers = TextUtils.join(",", list);
        if (!StringUtils.safeEquals(newUsers, mKnowsUsers)) {
            mKnowsUsers = newUsers;
            mUsersIds = list;
            mLastPosition = mViewPager.getCurrentItem();
            reloadViewPager();
        }
    }

    @Override
    public ArrayList<User> getDefaultUsers() {
        return mUsers;
    }
}
