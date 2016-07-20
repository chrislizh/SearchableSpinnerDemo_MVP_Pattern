package au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.searchableSpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import au.com.example.chrisli.searchablespinnerdemo_mvp_pattern.R;


/**
 * Created by cli on 12/07/2016.
 */
public class SearchableListDialog extends DialogFragment implements ISearchableListDialogView {

    private static final String SEARCHABLE_LIST_DIALOG_TAG = "searchable_list_dialog_tag";

    private ISearchableSpinnerPresenter iSearchableSpinnerPresenter_;
    private List originalItemList_;
    private String searchHint_;
    private String searchText_;
    private ListView listView_;

    //Description: a function to instruct it's time to show the SearchableListDialog (called by SearchableSpinner)
    //Author: Chris Li
    @Override
    public void showDialog(Context context) {
        if (context != null) {
            FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
            if (fragmentManager != null) {
                this.show(fragmentManager, SEARCHABLE_LIST_DIALOG_TAG);
            }
        }
    }

    //Description: a function to set the original item list on the list view (called by SearchableSpinner)
    //Author: Chris Li
    public void setOriginalItemList(List itemList) {
        this.originalItemList_ = itemList;
    }

    //Description: a function to set the presenter (called by SearchableSpinner)
    //Author: Chris Li
    @Override
    public void setPresenter(ISearchableSpinnerPresenter iSearchableSpinnerPresenter) {
        this.iSearchableSpinnerPresenter_ = iSearchableSpinnerPresenter;
    }

    //Description: a function to set the search hint on the SearchView (called by SearchableSpinner)
    //Author: Chris Li
    @Override
    public void setSearchHint(String searchHint) {
        this.searchHint_ = searchHint;
    }

    //Description: a function to set the search text on the SearchView (called by SearchableSpinner)
    //Author: Chris Li
    @Override
    public void setSearchText(String searchText) {
        this.searchText_ = searchText;
    }

    //Description: a function to get the item list (called by presenter)
    //Author: Chris Li
    @Override
    public List getInstantItemList() {
        ArrayList<Object> itemList = new ArrayList<>();
        if (listView_ != null) {
            ListAdapter listAdapter = listView_.getAdapter();
            if (listAdapter != null) {
                for (int i=0; i < listAdapter.getCount(); i++) {
                    itemList.add(listAdapter.getItem(i));
                }
            }
        }

        return itemList;
    }


    //Description: DialogFragment callback function
    //Author: Chris Li
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (iSearchableSpinnerPresenter_ != null) {
            final View searchableListDialogView = View.inflate(getActivity(), R.layout.searchable_spinner, null);

            final ListView listView = (ListView) searchableListDialogView.findViewById(R.id.lvSearchableSpinnerListView);
            if (listView != null) {
                listView_ = listView;
                listView.setTextFilterEnabled(true);
                listView.setDivider(null);
                final ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.searchable_spinner_list_item, originalItemList_);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (iSearchableSpinnerPresenter_ != null) {
                            iSearchableSpinnerPresenter_.listItemSelected(position);
                            //tell the presenter to delegate the showing state of the dialog (isShowing = false)
                            iSearchableSpinnerPresenter_.delegateSearchableListDialogShowingState(false);
                        }
                        hideSoftKeyboard(listView);
                        SearchableListDialog.this.dismiss(); //dismiss the dialog
                    }
                });
            }

            final SearchView searchView = (SearchView) searchableListDialogView.findViewById(R.id.svSearchableSpinnerSearchView);
            if (searchView != null) {
                searchView.setQueryHint(searchHint_);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        handleSearchTextChange(newText);
                        return true;
                    }
                });
                searchView.setQuery(searchText_, true);//Submit the change right now, make sure the list has been set up before calling this function.
            }

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setNeutralButton("CLOSE", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (iSearchableSpinnerPresenter_ != null) {
                        iSearchableSpinnerPresenter_.delegateSearchableListDialogShowingState(false);
                    }
                    hideSoftKeyboard(searchableListDialogView);
                    SearchableListDialog.this.dismiss();
                }
            }).create();
            alertDialog.setView(searchableListDialogView);
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_STATE_HIDDEN);

            //tell the presenter to delegate the showing state of the dialog (isShowing = true)
            iSearchableSpinnerPresenter_.delegateSearchableListDialogShowingState(true);

            alertDialog.setCanceledOnTouchOutside(true);
            return alertDialog;
        }

        return null;
    }

    //Description: DialogFragment callback function
    //Author: Chris Li
    @Override
    public void onCancel(DialogInterface dialog) {
        //tell the presenter to delegate the showing state of the dialog (isShowing = false)
        if (iSearchableSpinnerPresenter_ != null) {
            iSearchableSpinnerPresenter_.delegateSearchableListDialogShowingState(false);
        }
        super.onCancel(dialog);
    }

    //Description: a function to handle what to do when the search text changes
    //Author: Chris Li
    private void handleSearchTextChange(String searchText) {
        if (listView_ != null) {
            ((ArrayAdapter) listView_.getAdapter()).getFilter().filter(searchText);
            if (iSearchableSpinnerPresenter_ != null) {
                iSearchableSpinnerPresenter_.delegateSearchableListDialogSearchText(searchText);
            }
        }
    }

    //Description: a function to hide the soft keyboard
    //Author: Chris Li
    private void hideSoftKeyboard(View view) {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0); //hide the soft keyboard
    }

}
