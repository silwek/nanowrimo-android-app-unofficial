package nanowrimo.onishinji.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filippudak.ProgressPieView.ProgressPieView;

import java.text.NumberFormat;
import java.util.Locale;

import nanowrimo.onishinji.R;

/**
 * TODO: document your custom view class.
 */
public class WordCountProgress extends RelativeLayout {


    private TextView mTextView;

    public ProgressPieView getProgressPieView() {
        return mProgressPieView;
    }

    private ProgressPieView mProgressPieView;
    private TextView mProgressValue;


    private int mDefaultProgressColor;
    private int mOverflowProgressColor;
    private int mCurrentProgressColor;
    private int mIconRessource;
    private ImageView mIconView;

    public WordCountProgress(Context context) {
        super(context);
        init(context, null);
    }

    public WordCountProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WordCountProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.progress_pie, this, true);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.WordCountProgress, 0, 0);

        mIconRessource = a.getResourceId(R.styleable.WordCountProgress_iconProgressViewPie, -1);


        //configureView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        configureView();
    }

    public void setIconRessource(int ressourceId) {
        mIconRessource = ressourceId;
    }

    public void configureView() {

        mTextView = (TextView) findViewById(R.id.progressPieValue);
        mIconView = (ImageView) findViewById(R.id.progress_view_icon);

        if (mIconRessource == -1) {
            mIconView.setVisibility(View.GONE);
        } else {
            mIconView.setBackgroundResource(mIconRessource);
        }

        mProgressPieView = (ProgressPieView) findViewById(R.id.progressPieView);
        mProgressValue = (TextView) findViewById(R.id.progressPieValue);

        mDefaultProgressColor = getResources().getColor(R.color.small_widget_progress_color);
        mOverflowProgressColor = getResources().getColor(R.color.small_widget_progress_win);
        mCurrentProgressColor = mDefaultProgressColor;

        mProgressPieView.setProgressColor(mCurrentProgressColor);
        mProgressPieView.setText("");

        // Set color
        mProgressPieView.setBackgroundColor(getResources().getColor(R.color.small_widget_progress_background));
        mProgressPieView.setProgressColor(getResources().getColor(R.color.small_widget_progress_color));
        mProgressPieView.setStrokeColor(getResources().getColor(android.R.color.transparent));


    }

    public void compute(float current, float target, boolean withAnimation) {

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());

        float ratio = 100;
        if(target > 0) {
            ratio = current / target;
        }

        int p = (int) Math.max(Math.min(ratio * 100, 100), 0);

        if (withAnimation) {
            mProgressPieView.animateProgressFill(p);
        } else {
            mProgressPieView.setProgress(p);
        }

        int color = mDefaultProgressColor;
        if (current > target) {
            color = mOverflowProgressColor;
        }
        if (color != mCurrentProgressColor) {
            mCurrentProgressColor = color;
            mProgressPieView.setProgressColor(mCurrentProgressColor);
        }

        final String text = numberFormat.format(current);
        final String text2 = numberFormat.format(target);

        mProgressValue.setText(text + "\n" + text2);

        setText(text, text2);
    }

    public void setText(String top, String bottom) {

        mProgressValue.setText(top + "\n" + bottom);
    }

}
