package ng.softcom.bespoke.craftadmin.views;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import ng.softcom.bespoke.craftadmin.R;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.views in Craft Admin
 */
public class ESToggleEditText  extends AppCompatEditText {

    private boolean isShowingPassword = false;
    private Drawable drawableEnd;
    private Rect bounds;
    private boolean leftToRight = true;

    @DrawableRes
    private int visibilityIndicatorShow = R.drawable.ic_visibility_grey_900_24dp;
    @DrawableRes
    private int visibilityIndicatorHide = R.drawable.ic_visibility_off_grey_900_24dp;
    private boolean monospace;

    public ESToggleEditText(Context context) {
        super(context);
        init(null);
    }

    public ESToggleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ESToggleEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attrsArray =
                    getContext().obtainStyledAttributes(attrs, R.styleable.ESToggleEditText);

            visibilityIndicatorShow = attrsArray.getResourceId(R.styleable.ESToggleEditText_drawable_show, visibilityIndicatorShow);
            visibilityIndicatorHide = attrsArray.getResourceId(R.styleable.ESToggleEditText_drawable_hide, visibilityIndicatorHide);
            monospace = attrsArray.getBoolean(R.styleable.ESToggleEditText_monospace, true);


            attrsArray.recycle();
        }

        leftToRight = isLeftToRight();

        isShowingPassword = false;
        setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD, true);

        if(!monospace) {
            setTypeface(Typeface.DEFAULT);
        }

        if(!TextUtils.isEmpty(getText())){
            showPasswordVisibilityIndicator(true);
        }

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    showPasswordVisibilityIndicator(true);
                } else {
                    showPasswordVisibilityIndicator(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean isLeftToRight(){
        // If we are pre JB assume always LTR
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
            return true;
        }

        // Other methods, seemingly broken when testing though.
        // return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        // return !ViewUtils.isLayoutRtl(this);

        Configuration config = getResources().getConfiguration();
        return !(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top,
                                     Drawable right, Drawable bottom) {

        //keep a reference to the right drawable so later on touch we can check if touch is on the drawable
        if (leftToRight && right != null){
            drawableEnd = right;
        }
        else if (!leftToRight && left != null){
            drawableEnd = left;
        }

        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP && drawableEnd != null) {
            bounds = drawableEnd.getBounds();
            final int x = (int) event.getX();

            //check if the touch is within bounds of drawableEnd icon
            if ((leftToRight && (x >= (this.getRight() - bounds.width()))) ||
                    (!leftToRight &&  (x <= (this.getLeft() + bounds.width())))){
                togglePasswordVisability();
                //use this to prevent the keyboard from coming up
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(event);
    }

    private void showPasswordVisibilityIndicator(boolean show) {
        if (show) {

            Drawable drawable = isShowingPassword?
                    ContextCompat.getDrawable(getContext(), visibilityIndicatorHide):
                    ContextCompat.getDrawable(getContext(), visibilityIndicatorShow);

            setCompoundDrawablesWithIntrinsicBounds(leftToRight?null:drawable, null, leftToRight?drawable:null, null);

        } else {
            setCompoundDrawables(null, null, null, null);
        }
    }

    private void togglePasswordVisability() {
        if (isShowingPassword) {
            setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD, true);
        } else {
            setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, true);
        }
        isShowingPassword = !isShowingPassword;
        showPasswordVisibilityIndicator(true);
    }

    @Override
    protected void finalize() throws Throwable {
        drawableEnd = null;
        bounds = null;
        super.finalize();
    }


    private void setInputType(int inputType, boolean keepState) {
        int selectionStart = -1;
        int selectionEnd = -1;
        if (keepState) {
            selectionStart = getSelectionStart();
            selectionEnd = getSelectionEnd();
        }
        setInputType(inputType);
        if (keepState) {
            setSelection(selectionStart, selectionEnd);
        }
    }


    public @DrawableRes
    int getvisibilityIndicatorShow() {
        return visibilityIndicatorShow;
    }

    public void setvisibilityIndicatorShow(@DrawableRes int visibilityIndicatorShow) {
        this.visibilityIndicatorShow = visibilityIndicatorShow;
    }

    public @DrawableRes
    int getvisibilityIndicatorHide() {
        return visibilityIndicatorHide;
    }

    public void setvisibilityIndicatorHide(@DrawableRes int visibilityIndicatorHide) {
        this.visibilityIndicatorHide = visibilityIndicatorHide;
    }

    /**
     *
     * @return true if the password is visible | false if hidden
     */
    public boolean isShowingPassword() {
        return isShowingPassword;
    }
}