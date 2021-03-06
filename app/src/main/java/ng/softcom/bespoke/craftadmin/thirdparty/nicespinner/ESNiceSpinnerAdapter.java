package ng.softcom.bespoke.craftadmin.thirdparty.nicespinner;
import android.content.Context;

import java.util.List;

/**
 * @author angelo.marchesin
 */

public class ESNiceSpinnerAdapter<T> extends ESNiceSpinnerBaseAdapter {

    private final List<T> mItems;

    public ESNiceSpinnerAdapter(Context context, List<T> items) {
        super(context);
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size() - 1;
    }

    @Override
    public T getItem(int position) {
        if (position >= mSelectedIndex) {
            return mItems.get(position + 1);
        } else {
            return mItems.get(position);
        }
    }

    @Override
    public T getItemInDataset(int position) {
        return mItems.get(position);
    }
}