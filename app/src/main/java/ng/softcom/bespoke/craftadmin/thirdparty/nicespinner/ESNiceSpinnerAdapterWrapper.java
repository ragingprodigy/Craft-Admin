package ng.softcom.bespoke.craftadmin.thirdparty.nicespinner;

import android.content.Context;
import android.widget.ListAdapter;

/**
 * @author angelo.marchesin
 */

public class ESNiceSpinnerAdapterWrapper extends ESNiceSpinnerBaseAdapter {

    private final ListAdapter mBaseAdapter;

    public ESNiceSpinnerAdapterWrapper(Context context, ListAdapter toWrap) {
        super(context);
        mBaseAdapter = toWrap;
    }

    @Override
    public int getCount() {
        return mBaseAdapter.getCount() - 1;
    }

    @Override
    public Object getItem(int position) {
        if (position >= mSelectedIndex) {
            return mBaseAdapter.getItem(position + 1);
        } else {
            return mBaseAdapter.getItem(position);
        }
    }

    @Override
    public Object getItemInDataset(int position) {
        return mBaseAdapter.getItem(position);
    }
}