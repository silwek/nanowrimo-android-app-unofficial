package nanowrimo.onishinji.utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.model.WritingSession;

/**
 * Created by Silwek on 29/03/2015.
 */
public class WritingSessionHelper {

    private static WritingSessionHelper mInstance;

    public static WritingSessionHelper getInstance(){
        if(mInstance == null){
            mInstance = new WritingSessionHelper();
        }
        return mInstance;
    }

    //==================================

    protected WritingSession mWritingSession;
    protected User mUser;

    //==================================
    // WritingSession
    //==================================

    public void setNewSession(String sessionName, Date startDate){
        if(mWritingSession == null){
            mWritingSession = new WritingSession();
        }
        mWritingSession.setName(sessionName);
        mWritingSession.setStartDate(startDate);
    }

    public String getSessionName(){
        return mWritingSession != null ? mWritingSession.getName():"";
    }

    public Date getSessionStart(){
        return mWritingSession.getStartDate();
    }

    public int getSessionLastDay(){
        Calendar c = Calendar.getInstance();
        c.setTime(mWritingSession.getStartDate());
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    //==================================
    // Current User
    //==================================

    public void setUser(User user) {
        mUser = user;
    }
    public void setUserName(String username) {
        mUser = new User();
        mUser.setName(username);
    }

    public String getUserName(){
        return mUser.getName();
    }

    public void saveConfig(Context context){
        PreferencesHelper.setSessionName(context, mWritingSession.getName());
        PreferencesHelper.setSessionStart(context, mWritingSession.getStartDate());
        new Database(context).addUser(mUser.getId(), mUser.getName());
    }

//

    public int getGoal(){
        return 10000;
    }

    public int getDailyTarget(){
        return 500;
    }
}
