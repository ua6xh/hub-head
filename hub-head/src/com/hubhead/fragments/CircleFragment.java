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
    CirclesCollectionPagerAdapter mCirclesCollectionPagerAdapter;

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
        mCirclesCollectionPagerAdapter = new CirclesCollectionPagerAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mCirclesCollectionPagerAdapter);

        PagerTabStrip mPagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_title_strip);
        mPagerTabStrip.setDrawFullUnderline(true);

        int i = getArguments().getInt(ARG_CIRCLE_ID);
        CirclesActivity circlesActivity = (CirclesActivity) getActivity();
        String[] names  = circlesActivity.getCirclesTitles();
        String circle = names[i];
        getActivity().setTitle(circle);

        return rootView;
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class CirclesCollectionPagerAdapter extends FragmentStatePagerAdapter {

        private final Context mContext;

        public CirclesCollectionPagerAdapter(FragmentManager fm, Context context) {
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

// {"14578989989898989":"2", "14678989998989":"1", "1467899898998989899":"1", "145789899":"11", "14678999":"1", "1467898989":"8", "145789898998989898989":"1", "146789989899":"1", "1457899898989":"1", "14578989899":"9", "1457898989898989":"2", "14578998989":"2", "145789898989989898989":"1", "1467898989989":"3", "1457899":"4", "145789989989":"1", "145789899989898989":"1", "145789989989899":"1", "14578989989989":"1", "1457898989989":"2", "14578989898998989899":"1", "145789898998989":"2", "14678989":"32", "1457898989898989898989989898989":"1", "145789899898989899":"1", "14578989989":"2", "146789":"40", "145789899898989":"2", "146789898989":"8", "1457899899898989989899":"1", "145789898989":"14", "14578989":"44", "1467899":"11", "14578989898989899":"1", "13789":"1", "145789899899989899":"1", "13":"3", "145789898998998989899":"1", "1457898989899":"8", "145789989989898998999":"1", "1457899899":"1", "145789":"30", "146789999":"2", "146789898999":"1", "1467898989898989":"5", "14678989899":"1", "146789899899":"1", "1457898999":"4", "145789898989989":"2", "146789898989899":"2", "1457898989989898989":"1", "14678989898989":"2", "145789899989899":"1", "14578989898998989":"1", "145789898999":"7", "145789898989898989":"2", "14578989898989":"18", "14578999":"4", "14678998999":"1", "145789899898998989":"1", "14578989899898999":"1", "1467898989899":"3", "146789898998999":"1", "14578998989899989898989899":"1", "145789989":"1", "145789899899899989899":"1", "146789899":"5", "145789898989899":"1", "1457898989":"26", "145789989899":"1"}