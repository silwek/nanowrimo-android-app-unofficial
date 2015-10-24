package nanowrimo.onishinji.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import nanowrimo.onishinji.R;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.utils.StringUtils;

/**
 * Created by Silwek on 13/10/15.
 */
public class RankingAdapter extends ArrayAdapter<User> {
    final protected LayoutInflater mInflater;
    final protected int mCurrentUserColor;

    public RankingAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        mCurrentUserColor = ContextCompat.getColor(context, R.color.wrimo_color_secondary);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(position, parent);
        }
        bindView(position, convertView);
        return convertView;
    }

    protected View newView(int position, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.item_rank, parent, false);
        RankingViewHolder holder = new RankingViewHolder();
        holder.view = v;
        holder.tfName = (TextView) v.findViewById(R.id.rank_name);
        holder.tfWordcount = (TextView) v.findViewById(R.id.rank_wordcount);
        v.setTag(holder);
        return v;
    }

    protected void bindView(int position, View convertView) {
        User u = getItem(position);
        RankingViewHolder holder = (RankingViewHolder) convertView.getTag();

        if (u.getWordcount() == User.NO_DATA_WORDCOUNT) {
            holder.tfWordcount.setText("-");
            holder.tfName.setText(u.getName());
        } else {
            holder.tfWordcount.setText(String.valueOf(u.getWordcount()));
            holder.tfName.setText(u.getName());
        }

        if (StringUtils.safeEquals(mUserId, u.getId())) {
            holder.view.setBackgroundColor(mCurrentUserColor);
        } else {
            holder.view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    protected String mUserId;

    public void setUserId(String userId) {
        mUserId = userId;
    }

    protected class RankingViewHolder {
        View view;
        TextView tfWordcount;
        TextView tfName;
    }
}
