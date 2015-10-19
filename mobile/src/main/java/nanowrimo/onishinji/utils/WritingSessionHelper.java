package nanowrimo.onishinji.utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.Database;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.model.WritingSession;

/**
 * Created by Silwek on 29/03/2015.
 */
public class WritingSessionHelper {

    private static WritingSessionHelper mInstance;

    public static WritingSessionHelper getInstance() {
        if (mInstance == null) {
            mInstance = new WritingSessionHelper();
        }
        return mInstance;
    }

    //==================================

    protected WritingSession mWritingSession;
    protected User mUser;

    public WritingSessionHelper() {
        mWritingSession = new WritingSession();
    }

    //==================================
    // WritingSession
    //==================================

    public void setNewSession(String sessionName, Date startDate, int type) {
        mWritingSession.setName(sessionName);
        mWritingSession.setStartDate(startDate);
        mWritingSession.setType(type);
    }

    public String getSessionName() {
        return mWritingSession != null ? mWritingSession.getName() : "";
    }

    public Date getSessionStart() {
        return mWritingSession.getStartDate();
    }

    public int getSessionType() {
        return mWritingSession.getType();
    }

    public void setSessionName(String sessionName) {
        mWritingSession.setName(sessionName);
    }

    public void setSessionStart(Date startDate) {
        mWritingSession.setStartDate(startDate);
    }

    public void setSessionType(int type) {
        mWritingSession.setType(type);
    }

    public int getSessionLastDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(mWritingSession.getStartDate());
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public boolean isSessionExpired() {
        Calendar session = Calendar.getInstance();
        session.setTime(mWritingSession.getStartDate());
        Calendar curSession;
        if (mWritingSession.getType() == WritingSession.NANOWRIMO) {
            curSession = getNanowrimoSession();
        } else {
            curSession = getCampSession();
        }
        if (equalSessions(session, curSession)) {
            return false;
        }
        return true;
    }

    public boolean isSessionStarted() {
        Calendar session = Calendar.getInstance();
        session.setTime(mWritingSession.getStartDate());
        Calendar now = getNow();
        return now.after(session);
    }

    public boolean isSessionEnded() {
        Calendar session = Calendar.getInstance();
        session.setTime(mWritingSession.getStartDate());
        Calendar now = getNow();
        return now.after(session) && !equalSessions(session, now);
    }

    public String getRelativeSessionTime(Context context) {
        Calendar session = Calendar.getInstance();
        session.setTime(mWritingSession.getStartDate());
        Calendar now = getNow();
        final boolean isStarted = now.after(session);
        final boolean isEnded = !equalSessions(session, now);
        if (!isStarted) {
            final int remainingDays = getTimeRemaining();
            if (remainingDays == 1) {
                return context.getString(R.string.relative_time_start_tomorrow, mWritingSession.getName());
            } else {
                return context.getString(R.string.relative_time_days_remaining_to_start, remainingDays);
            }
        } else if (!isEnded) {
            final int day = now.get(Calendar.DAY_OF_MONTH);
            final int lastDay = getSessionLastDay();
            if (day == 0) {
                return context.getString(R.string.relative_time_start_today);
            } else if (day == lastDay - 1) {
                return context.getString(R.string.relative_time_end_today);
            } else if (day == lastDay) {
                return context.getString(R.string.relative_time_end_tomorrow, mWritingSession.getName());
            } else {
                return context.getString(R.string.relative_time_days_remaining_to_end, lastDay - day);
            }
        }

        return "";
    }

    public int getTimeRemaining() {
        Calendar session = Calendar.getInstance();
        session.setTime(mWritingSession.getStartDate());
        Calendar now = getNow();

        return getTimeRemaining(now, session);
    }

    protected boolean equalSessions(Calendar session1, Calendar session2) {
        return ((session1.get(Calendar.YEAR) == session2.get(Calendar.YEAR)) && (session1.get(Calendar.MONTH) == session2.get(Calendar.MONTH)));
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

    public String getUserName() {
        return mUser.getName();
    }

    public void saveConfig(Context context) {
        PreferencesHelper.setSessionName(context, mWritingSession.getName());
        PreferencesHelper.setSessionStart(context, mWritingSession.getStartDate());
        PreferencesHelper.setSessionType(context, mWritingSession.getType());
        PreferencesHelper.setUserName(context, mUser.getName());
        Database.getInstance(context).addUser(mUser.getId(), mUser.getName());
    }

    public void restoreConfig(Context context) {
        WritingSessionHelper.getInstance().setSessionName(PreferencesHelper.getSessionName(context));
        WritingSessionHelper.getInstance().setSessionStart(PreferencesHelper.getSessionStart(context));
        WritingSessionHelper.getInstance().setSessionType(PreferencesHelper.getSessionType(context));
        WritingSessionHelper.getInstance().setUserName(PreferencesHelper.getUserName(context));
    }

    public void clearConfig(Context context) {
        Database.getInstance(context).clearUsers();
        PreferencesHelper.clearAllData(context);
    }

    //==================================
    // Global
    //==================================

    public static Calendar getNow() {
        return Calendar.getInstance();
    }

    public static Calendar getNanowrimoSession() {

        Calendar c = getNow();
        final int curMonth = c.get(Calendar.MONTH);
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.SEPTEMBER) {
            c.add(Calendar.YEAR, -1);
        }
        c.set(Calendar.MONTH, Calendar.NOVEMBER);
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c;
    }

    public static Calendar getNextNanowrimoSession() {

        Calendar c = getNow();
        final int curMonth = c.get(Calendar.MONTH);
        if (curMonth >= Calendar.DECEMBER) {
            c.add(Calendar.YEAR, 1);
        }
        c.set(Calendar.MONTH, Calendar.NOVEMBER);
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c;
    }

    public static Calendar getCampSession() {

        Calendar c = getNow();
        final int curMonth = c.get(Calendar.MONTH);
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.FEBRUARY) {
            c.add(Calendar.YEAR, -1);
            c.set(Calendar.MONTH, Calendar.JULY);
        } else if (curMonth >= Calendar.MARCH && curMonth <= Calendar.MAY) {
            c.set(Calendar.MONTH, Calendar.APRIL);
        } else if (curMonth >= Calendar.JUNE) {
            c.set(Calendar.MONTH, Calendar.JULY);
        }
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c;
    }

    public static Calendar getNextCampSession() {

        Calendar c = getNow();
        final int curMonth = c.get(Calendar.MONTH);
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.APRIL) {
            c.set(Calendar.MONTH, Calendar.APRIL);
        } else if (curMonth >= Calendar.MAY && curMonth <= Calendar.JULY) {
            c.set(Calendar.MONTH, Calendar.JULY);
        } else if (curMonth >= Calendar.AUGUST) {
            c.set(Calendar.MONTH, Calendar.APRIL);
            c.add(Calendar.YEAR, 1);
        }
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c;
    }

    public static boolean isSessionAvailableToInscription(Calendar sessionCalendar) {
        Calendar c = (Calendar) sessionCalendar.clone();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, -1);
        final long sessionStart = c.getTime().getTime();
        c = (Calendar) sessionCalendar.clone();
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        final long sessionEnd = c.getTime().getTime();
        c = getNow();
        final long now = c.getTime().getTime();
        if (now >= sessionStart && now < sessionEnd)
            return true;
        return false;
    }

    protected static final int MILLISECOND_IN_DAY = 24 * 60 * 60 * 1000;


    public static int getTimeRemaining(Calendar calendar, Calendar target) {

        absoluteDate(target);
        absoluteDate(calendar);

        // Get the represented date in milliseconds
        long milis1 = target.getTimeInMillis();
        long milis2 = calendar.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(milis2 - milis1);
        final int daysBetweenCalendar = (int) Math.round((float) diff / (float) MILLISECOND_IN_DAY);
        return daysBetweenCalendar;
    }

    private static Calendar absoluteDate(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
