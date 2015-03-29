package nanowrimo.onishinji.ui.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Silwek on 29/03/2015.
 */
public class SlidingFragment extends Fragment {

    protected SlidingListener mSlidingListener;

    public void setSlidingListener(SlidingListener listener){
        mSlidingListener = listener;
    }

    protected void onWantNextSlide(){
        if(mSlidingListener != null){
            mSlidingListener.onNextSlide(this);
        }
    }
    protected void onWantPreviousSlide(){
        if(mSlidingListener != null){
            mSlidingListener.onPreviousSlide(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mSlidingListener = (SlidingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SlidingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSlidingListener = null;
    }

    public interface SlidingListener{
        void onNextSlide(SlidingFragment fragment);
        void onPreviousSlide(SlidingFragment fragment);
    }
}
