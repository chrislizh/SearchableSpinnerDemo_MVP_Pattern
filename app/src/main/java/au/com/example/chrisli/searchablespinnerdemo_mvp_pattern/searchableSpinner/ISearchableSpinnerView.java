package au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.searchableSpinner;

import java.util.List;

/**
 * Created by cli on 12/07/2016.
 */
interface ISearchableSpinnerView {
    void setSpinnerSelection(int position);
    List getSpinnerItemList();
    void setSearchableListDialogShowingState(boolean isShowing);
    void setSearchableListDialogSearchText(String searchText);
}
