package nanowrimo.onishinji.event;

/**
 * Created by Silwek on 17/10/15.
 */
public class WordcountUpdateEvent {
    final protected int mNewWordcount;

    public WordcountUpdateEvent(int newWordcount) {
        mNewWordcount = newWordcount;
    }

    public int getNewWordcount() {
        return mNewWordcount;
    }
}
