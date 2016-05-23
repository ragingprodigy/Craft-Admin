package ng.softcom.bespoke.craftadmin.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import ng.softcom.bespoke.craftadmin.R;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.views in Craft Admin
 */
public class ESViewPager extends ViewPager {

    private boolean swipeable;

    public ESViewPager(Context context) {
        super(context);
    }

    public ESViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ESViewPager);
        try {
            swipeable = a.getBoolean(R.styleable.ESViewPager_swipeable, true);
        } finally {
            a.recycle();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeable && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeable && super.onTouchEvent(event);
    }
}
