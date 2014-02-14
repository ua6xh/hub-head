package com.hubhead.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.hubhead.R;
import com.hubhead.ui.CirclesActivity;

public class CircleFragment extends Fragment {
    private static final String TAG = "CircleFragment";
    public static final String ARG_CIRCLES_NAMES = "circles_names";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    TabsCollectionPagerAdapter mTabsCollectionPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;

    public static final String ARG_CIRCLE_ID = "circle_id";

    public CircleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_pager_tab_strip, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mTabsCollectionPagerAdapter = new TabsCollectionPagerAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mTabsCollectionPagerAdapter);

        PagerTabStrip mPagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_title_strip);
        mPagerTabStrip.setDrawFullUnderline(true);

        int i = getArguments().getInt(ARG_CIRCLE_ID);
//        CirclesActivity circlesActivity = (CirclesActivity) getActivity();
//        String[] names  = circlesActivity.getCirclesTitles();
//        String circle = names[i];
//        getActivity().setTitle(circle);

        return rootView;
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class TabsCollectionPagerAdapter extends FragmentStatePagerAdapter {

        private final Context mContext;

        public TabsCollectionPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new NotificationsListFragment();
                case 1:
                    return new OverviewListFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getResources().getString(R.string.word_notifications).toUpperCase();
                case 1:
                    return mContext.getResources().getString(R.string.word_overview).toUpperCase();
                default:
                    return mContext.getResources().getString(R.string.word_undefined).toUpperCase();
            }
        }
    }
}
