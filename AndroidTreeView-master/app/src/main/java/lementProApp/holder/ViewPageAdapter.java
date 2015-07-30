package lementProApp.holder;

/**
 * Created by Saadi on 16/07/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import lementProApp.fragment.CheckPointsTabFragment;
import lementProApp.fragment.DescriptionTabFragment;
import lementProApp.fragment.DiscussionsTabFragment;
import lementProApp.fragment.HistoryTabFragment;
import lementProApp.fragment.FilesTabFragment;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPageAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPageAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new DescriptionTabFragment();
            case 1:
                return new DiscussionsTabFragment();
            case 2:
                return new CheckPointsTabFragment();
            case 3:
                return new FilesTabFragment();
            case 4:
                return new HistoryTabFragment();
        }

        return null;


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}