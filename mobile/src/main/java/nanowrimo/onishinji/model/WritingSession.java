package nanowrimo.onishinji.model;

import java.util.Date;

/**
 * Created by Silwek on 29/03/2015.
 */
public class WritingSession {

    protected String mName;
    protected Date mStartDate;

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
}
