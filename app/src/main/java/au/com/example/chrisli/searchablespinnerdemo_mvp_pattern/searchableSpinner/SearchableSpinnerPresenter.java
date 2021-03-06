package au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.searchableSpinner;

import java.util.List;

/**
 * Created by cli on 12/07/2016.
 */
public class SearchableSpinnerPresenter implements ISearchableSpinnerPresenter {

    final private ISearchableSpinnerView iSearchableSpinnerView_;
    final private ISearchableListDialogView iSearchableListDialogView_;

    public SearchableSpinnerPresenter(ISearchableSpinnerView iSearchableSpinnerView, ISearchableListDialogView iSearchableListDialogView) {
        iSearchableSpinnerView_ = iSearchableSpinnerView;
        iSearchableListDialogView_ = iSearchableListDialogView;
    }

    //Description: a function to handle what to do when an item in the searchable list view is selected (called by SearchableListDialog)
    //Author: Chris Li
    @Override
    public void listItemSelected(int position) {
        if (iSearchableListDialogView_ != null) {
            List itemListOfListView = iSearchableListDialogView_.getInstantItemList();
            if (position >= 0 && position < itemListOfListView.size()) {
                Object selectedListItem = itemListOfListView.get(position);
                if (iSearchableSpinnerView_ != null) {
                    ISearchableSpinnerItemMapping iSearchableSpinnerItemMapping = new SearchableSpinnerItemMapping();
                    int itemPositionInSpinner = iSearchableSpinnerItemMapping.getSpinnerPositionFromListPosition(iSearchableSpinnerView_.getSpinnerItemList(), selectedListItem);
                    if (itemPositionInSpinner >= 0) {
                        iSearchableSpinnerView_.setSpinnerSelection(itemPositionInSpinner);
                    }
                    iSearchableSpinnerView_.setSearchableListDialogSearchText(null); //clear the search text as the user has made a selection
                }
            }
        }
    }

    //Description: a function to delegate the showing state of SearchableListDialog to SearchableSpinner (called by SearchableListDialog)
    //Author: Chris Li
    @Override
    public void delegateSearchableListDialogShowingState(boolean isShowing) {
        if (iSearchableSpinnerView_ != null) {
            iSearchableSpinnerView_.setSearchableListDialogShowingState(isShowing);
        }
    }

    //Description: a function to delegate the search text of SearchableListDialog to SearchableSpinner (called by SearchableListDialog)
    //Author: Chris Li
    @Override
    public void delegateSearchableListDialogSearchText(String searchText) {
        if (iSearchableSpinnerView_ != null) {
            iSearchableSpinnerView_.setSearchableListDialogSearchText(searchText);
        }
    }
}
