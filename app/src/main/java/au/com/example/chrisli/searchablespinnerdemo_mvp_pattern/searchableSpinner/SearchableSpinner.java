package au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.searchableSpinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.R;


/**
 * Created by cli on 12/07/2016.
 */
public class SearchableSpinner extends Spinner implements ISearchableSpinnerView {

    private Context context_;
    private ISearchableListDialogView iSearchableListDialog_;
    private List itemList_;
    private boolean showingSearchableListDialog_;
    private String searchableListDialogSearchText_;

    private static class SearchableSpinnerState implements Parcelable {

        boolean showingSearchableListDialog_;

        String searchableListDialogSearchText_;
        Parcelable superClassState_;

        public boolean isShowingSearchableListDialog() {
            return showingSearchableListDialog_;
        }

        public String getSearchableListDialogSearchText() {
            return searchableListDialogSearchText_;
        }

        public Parcelable getSuperClassState() {
            return superClassState_;
        }

        public SearchableSpinnerState(boolean showingSearchableListDialog, String searchableListDialogSearchText, Parcelable superClassState) {
            showingSearchableListDialog_ = showingSearchableListDialog;
            searchableListDialogSearchText_ = searchableListDialogSearchText;
            superClassState_ = superClassState;
        }

        protected SearchableSpinnerState(Parcel in) {
            showingSearchableListDialog_ = (Boolean) in.readValue(Boolean.class.getClassLoader());
            searchableListDialogSearchText_ = in.readString();
            superClassState_ = in.readParcelable(Parcelable.class.getClassLoader());
        }

        public static final Creator<SearchableSpinnerState> CREATOR = new Creator<SearchableSpinnerState>() {
            @Override
            public SearchableSpinnerState createFromParcel(Parcel in) {
                return new SearchableSpinnerState(in);
            }

            @Override
            public SearchableSpinnerState[] newArray(int size) {
                return new SearchableSpinnerState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(showingSearchableListDialog_);
            dest.writeString(searchableListDialogSearchText_);
            dest.writeParcelable(superClassState_, PARCELABLE_WRITE_RETURN_VALUE); //superClassState_ is a return value of super.onSaveInstanceState()
        }
    }

    public SearchableSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public SearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //Description: an init function
    //Author: Chris Li
    private void init(Context context, AttributeSet attrs) {
        context_ = context;
        showingSearchableListDialog_ = false;
        iSearchableListDialog_ = new SearchableListDialog();
        ISearchableSpinnerPresenter iSearchableSpinnerPresenter = new SearchableSpinnerPresenter(this, iSearchableListDialog_);
        iSearchableListDialog_.setPresenter(iSearchableSpinnerPresenter);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner);
            if (typedArray != null) {
                for (int i = 0; i < typedArray.getIndexCount(); i++) {
                    int attribute = typedArray.getIndex(i);
                    if (attribute == R.styleable.SearchableSpinner_searchHint) {
                        iSearchableListDialog_.setSearchHint(typedArray.getString(i));
                        break;
                    }
                }
                typedArray.recycle();
            }
        }
        if (itemList_ != null) {
            //item list is already ready as SetAdapter() can be called before this function is called
            iSearchableListDialog_.setOriginalItemList(itemList_);
        }
    }

    //Description: View callback function
    //Author: Chris Li
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled() && this.getVisibility() == VISIBLE) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (iSearchableListDialog_ != null) {
                    iSearchableListDialog_.showDialog(context_);
                }
            }
        }
        return true;
    }

    //Description: AdapterView callback function
    //Author: Chris Li
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        if (adapter != null) {
            itemList_ = new ArrayList();
            for (int i = 0; i < adapter.getCount(); i++) {
                itemList_.add(adapter.getItem(i));
            }
            if (iSearchableListDialog_ != null) { //it could be null as this function can be called before init() is called
                iSearchableListDialog_.setOriginalItemList(itemList_);
            }
        }
        super.setAdapter(adapter);
    }

    //Description: a function to set the user selection(with a remapped position) of the spinner(called by the presenter)
    //Author: Chris Li
    @Override
    public void setSpinnerSelection(int position) {
        this.setSelection(position);
    }

    //Description: a function to get the item list of the spinner(called by presenter)
    //Author: Chris Li
    @Override
    public List getSpinnerItemList() {
        ArrayList<Object> itemList = new ArrayList<>();
        SpinnerAdapter spinnerAdapter = this.getAdapter();
        if (spinnerAdapter != null) {
            for (int i=0; i < spinnerAdapter.getCount(); i++) {
                itemList.add(spinnerAdapter.getItem(i));
            }
        }

        return itemList;
    }

    //Description: a function to set the showing state of SearchableListDialog(called by presenter)
    //Author: Chris Li
    @Override
    public void setSearchableListDialogShowingState(boolean isShowing) {
        showingSearchableListDialog_ = isShowing;
    }

    //Description: a function to set the search text of SearchableListDialog(called by presenter)
    //Author: Chris Li
    @Override
    public void setSearchableListDialogSearchText(String searchText) {
        searchableListDialogSearchText_ = searchText;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (showingSearchableListDialog_) {
            return new SearchableSpinnerState(true, searchableListDialogSearchText_, super.onSaveInstanceState());
        }
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SearchableSpinnerState) {
            SearchableSpinnerState searchableSpinnerState = (SearchableSpinnerState) state;
            super.onRestoreInstanceState(searchableSpinnerState.getSuperClassState());
            searchableListDialogSearchText_ = searchableSpinnerState.getSearchableListDialogSearchText();
            showingSearchableListDialog_ = searchableSpinnerState.isShowingSearchableListDialog();
            if (showingSearchableListDialog_) {
                if (iSearchableListDialog_ != null) {
                    iSearchableListDialog_.setSearchText(searchableListDialogSearchText_);
                    iSearchableListDialog_.showDialog(context_);
                }
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
