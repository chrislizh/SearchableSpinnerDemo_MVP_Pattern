package au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.searchableSpinner;

import java.util.List;

/**
 * Created by cli on 13/07/2016.
 */
public class SearchableSpinnerItemMapping implements ISearchableSpinnerItemMapping {
    @Override
    public int getSpinnerPositionFromListPosition(List itemList, Object item) {
        if (itemList != null && item != null) {
            return itemList.indexOf(item);
        }
        return -1;
    }
}
