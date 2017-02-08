package nanowrimo.onishinji.utils;

import org.junit.Test;

import java.util.Calendar;

import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.model.WritingSession;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Amandine Ferrand on 08/02/2017.
 */
public class WritingSessionHelperTest {
    @Test
    public void getNextNanowrimoSession() throws Exception {
        Calendar current = Calendar.getInstance();
        Calendar next;
        current.set(Calendar.YEAR, 2000);
        current.set(Calendar.DAY_OF_MONTH, 1);

        //01/01/2000 -> expected -> 01/11/2000
        current.set(Calendar.MONTH, Calendar.JANUARY);
        next = WritingSessionHelper.getNextNanowrimoSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.NOVEMBER));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/10/2000 -> expected -> 01/11/2000
        current.set(Calendar.MONTH, Calendar.OCTOBER);
        next = WritingSessionHelper.getNextNanowrimoSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.NOVEMBER));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/11/2000 -> expected -> 01/11/2000
        current.set(Calendar.MONTH, Calendar.NOVEMBER);
        next = WritingSessionHelper.getNextNanowrimoSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.NOVEMBER));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/12/2000 -> expected -> 01/11/2001
        current.set(Calendar.MONTH, Calendar.DECEMBER);
        next = WritingSessionHelper.getNextNanowrimoSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.NOVEMBER));
        assertThat(next.get(Calendar.YEAR), is(2001));
    }

    @Test
    public void getNextCampSession() throws Exception {
        Calendar current = Calendar.getInstance();
        Calendar next;
        current.set(Calendar.YEAR, 2000);
        current.set(Calendar.DAY_OF_MONTH, 1);

        //01/01/2000 -> expected -> 01/04/2000
        current.set(Calendar.MONTH, Calendar.JANUARY);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.APRIL));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/03/2000 -> expected -> 01/04/2000
        current.set(Calendar.MONTH, Calendar.MARCH);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.APRIL));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/04/2000 -> expected -> 01/04/2000
        current.set(Calendar.MONTH, Calendar.APRIL);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.APRIL));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/05/2000 -> expected -> 01/07/2000
        current.set(Calendar.MONTH, Calendar.MAY);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.JULY));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/06/2000 -> expected -> 01/07/2000
        current.set(Calendar.MONTH, Calendar.JUNE);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.JULY));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/07/2000 -> expected -> 01/07/2000
        current.set(Calendar.MONTH, Calendar.JULY);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.JULY));
        assertThat(next.get(Calendar.YEAR), is(2000));

        //01/08/2000 -> expected -> 01/04/2001
        current.set(Calendar.MONTH, Calendar.AUGUST);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.APRIL));
        assertThat(next.get(Calendar.YEAR), is(2001));

        //01/12/2000 -> expected -> 01/04/2001
        current.set(Calendar.MONTH, Calendar.DECEMBER);
        next = WritingSessionHelper.getNextCampSession(current);
        assertThat(next.get(Calendar.MONTH), is(Calendar.APRIL));
        assertThat(next.get(Calendar.YEAR), is(2001));
    }


    @Test
    public void getDefaultDailyTarget() throws Exception {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, 2000);
        start.set(Calendar.MONTH, Calendar.NOVEMBER);
        start.set(Calendar.DAY_OF_MONTH, 1);
        WritingSession session = new WritingSession();
        session.setType(WritingSession.NANOWRIMO);
        session.setStartDate(start.getTime());
        session.setName("Nanowrimo test");

        User user = null;
        assertThat(WritingSessionHelper.getDefaultDailyTarget(session, user), is(1667));
        user = new User();
        user.setName("User test");
        user.setGoal(50000);
        assertThat(WritingSessionHelper.getDefaultDailyTarget(session, user), is(1667));
        user.setGoal(25000);
        assertThat(WritingSessionHelper.getDefaultDailyTarget(session, user), is(834));
        user.setGoal(30);
        assertThat(WritingSessionHelper.getDefaultDailyTarget(session, user), is(1));
    }
}