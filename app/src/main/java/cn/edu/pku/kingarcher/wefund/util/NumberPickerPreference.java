package cn.edu.pku.kingarcher.wefund.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.NumberPicker;

import java.net.MalformedURLException;

import cn.edu.pku.kingarcher.wefund.R;

/**
 * Created by xtrao on 2016/4/2.
 */
public class NumberPickerPreference extends DialogPreference {

    private final int DEFAULT_VALUE = 60;

    private NumberPicker mNumberPicker;
    private int mCurrentValue;
    private final int MIN_VALUE;
    private final int MAX_VALUE;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_numberpicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPref);
        MIN_VALUE = a.getInt(R.styleable.NumberPickerPref_min_value, 5);
        MAX_VALUE = a.getInt(R.styleable.NumberPickerPref_max_value, 300);
        a.recycle();
        //mCurrentValue;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mNumberPicker = (NumberPicker)view.findViewById(R.id.number_picker);
        mNumberPicker.setMinValue(MIN_VALUE);
        mNumberPicker.setMaxValue(MAX_VALUE);
        mNumberPicker.setValue(mCurrentValue);
        mNumberPicker.setWrapSelectorWheel(false);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mCurrentValue = mNumberPicker.getValue();
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mCurrentValue = getPersistedInt(DEFAULT_VALUE);
        } else {
            mCurrentValue = (Integer)defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState myState = new SavedState(superState);

        myState.value = mNumberPicker.getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        mCurrentValue = myState.value;
    }




    private static class SavedState extends BaseSavedState {

        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
