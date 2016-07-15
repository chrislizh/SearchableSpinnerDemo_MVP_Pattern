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
    private ISearchableSpinnerPresenter iSearchableSpinnerPresenter_;
    private String SearchText_;
    private String searchHint_;
    private boolean showingSearchableListDialog_;

    private static class SearchableSpinnerState implements Parcelable {

        boolean showingSearchableListDialog_;

        String searchText_;
        Parcelable superClassState_;

        public boolean isShowingSearchableListDialog() {
            return showingSearchableListDialog_;
        }

        public String getSearchableListDialogSearchText() {
            return searchText_;
        }

        public Parcelable getSuperClassState() {
            return superClassState_;
        }

        public SearchableSpinnerState(boolean showingSearchableListDialog, String searchableListDialogSearchText, Parcelable superClassState) {
            showingSearchableListDialog_ = showingSearchableListDialog;
            searchText_ = searchableListDialogSearchText;
            superClassState_ = superClassState;
        }

        protected SearchableSpinnerState(Parcel in) {
            showingSearchableListDialog_ = (Boolean) in.readValue(Boolean.class.getClassLoader());
            searchText_ = in.readString();
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
            dest.writeString(searchText_);
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
        iSearchableSpinnerPresenter_ = new SearchableSpinnerPresenter(this, iSearchableListDialog_);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner);
            if (typedArray != null) {
                for (int i = 0; i < typedArray.getIndexCount(); i++) {
                    int attribute = typedArray.getIndex(i);
                    if (attribute == R.styleable.SearchableSpinner_searchHint) {
                        searchHint_ = typedArray.getString(i);
                        break;
                    }
                }
                typedArray.recycle();
            }
        }
    }

    //Description: View callback function
    //Author: Chris Li
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled() && this.getVisibility() == VISIBLE) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showSearchableListDialog();
            }
        }
        return true;
    }

    //Description: a function to set the user selection(with a remapped position) of the spinner(called by the presenter)
    //Author: Chris Li
    @Override
    public void setSpinnerSelection(int position) {
        this.setSelection(position);
    }

    //Description: a function to get the item list of the spinner(called by presenter and this object)
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
        SearchText_ = searchText;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (showingSearchableListDialog_) {
            return new SearchableSpinnerState(true, SearchText_, super.onSaveInstanceState());
        }
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SearchableSpinnerState) {
            SearchableSpinnerState searchableSpinnerState = (SearchableSpinnerState) state;
            super.onRestoreInstanceState(searchableSpinnerState.getSuperClassState());
            SearchText_ = searchableSpinnerState.getSearchableListDialogSearchText();
            showingSearchableListDialog_ = searchableSpinnerState.isShowingSearchableListDialog();
            if (showingSearchableListDialog_) {
                showSearchableListDialog();
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    //Description: a function to set up and show the searchable list dialog view
    //Author: Chris Li
    private void showSearchableListDialog() {
        if (iSearchableListDialog_ != null) {
            iSearchableListDialog_.setPresenter(iSearchableSpinnerPresenter_);
            iSearchableListDialog_.setSearchHint(searchHint_);
            iSearchableListDialog_.setSearchText(SearchText_);
            iSearchableListDialog_.setOriginalItemList(getSpinnerItemList());
            iSearchableListDialog_.showDialog(context_);
        }
    }
}
