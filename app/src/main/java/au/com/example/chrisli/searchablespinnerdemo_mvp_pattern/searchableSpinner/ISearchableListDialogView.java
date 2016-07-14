package au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.searchableSpinner;

import android.content.Context;

import java.util.List;

/**
 * Created by cli on 13/07/2016.
 */
interface ISearchableListDialogView {
    void showDialog(Context context);
    void setOriginalItemList(List originalItemList);
    void setSearchHint(String searchHint);
    void setPresenter(ISearchableSpinnerPresenter iSearchableSpinnerPresenter);

    List getInstantItemList();
}
