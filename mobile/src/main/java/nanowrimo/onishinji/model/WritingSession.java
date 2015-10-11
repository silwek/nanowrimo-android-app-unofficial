package nanowrimo.onishinji.model;

import java.util.Date;

/**
 * Created by Silwek on 29/03/2015.
 */
public class WritingSession {

    public static final int NANOWRIMO = 1;
    public static final int CAMP = 2;

    protected String mName;
    protected Date mStartDate;
    protected int mType;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
