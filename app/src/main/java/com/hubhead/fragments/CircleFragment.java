package com.hubhead.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.test.IsolatedContext;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hubhead.R;
import com.hubhead.helpers.TextHelper;

public class CircleFragment extends Fragment {
    private static final String MY_PREF = "MY_PREF";
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    public static final String ARG_CIRCLE_ID = "circle_id";
    public static final String ARG_CIRCLE_NAME = "circle_name";
    public int mCircleIdSelected;
    private String mCircleNameSelected;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    private TabsCollectionPagerAdapter mTabsCollectionPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mPageChangeListener;


    public CircleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_pager_tab_strip, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mTabsCollectionPagerAdapter = new TabsCollectionPagerAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mTabsCollectionPagerAdapter);
        mViewPager.setCurrentItem(getActivity().getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).getInt("select_tab", 0));
        mPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int pos) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).edit();
                editor.putInt("select_tab", pos);
                editor.commit();
            }
        };
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        PagerTabStrip mPagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_title_strip);
        mPagerTabStrip.setDrawFullUnderline(true);

        mCircleIdSelected = getArguments().getInt(ARG_CIRCLE_ID);
        mCircleNameSelected = getArguments().getString(ARG_CIRCLE_NAME);
        getActivity().setTitle(mCircleNameSelected);

        return rootView;
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public class TabsCollectionPagerAdapter extends FragmentStatePagerAdapter {

        private final Context mContext;

        public TabsCollectionPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int i) {
            Bundle args = getBundleArgs();
            Log.d(TAG, "getItem:" + i);
            switch (i) {
                case 0: {
                    NotificationsFragment nfr = new NotificationsFragment();
                    nfr.setArguments(args);
                    return nfr;
                }
                case 1: {
                    OverviewListFragment ofr = new OverviewListFragment();
                    ofr.setArguments(args);
                    return ofr;
                }
                default: {
                    return new Fragment();
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    return mContext.getResources().getString(R.string.word_notifications).toUpperCase();
                }
                case 1: {
                    return mContext.getResources().getString(R.string.word_overview).toUpperCase();
                }
                default: {
                    return mContext.getResources().getString(R.string.word_undefined).toUpperCase();
                }
            }
        }
    }

    public Bundle getBundleArgs() {
        Bundle args = new Bundle();
        args.putInt(CircleFragment.ARG_CIRCLE_ID, mCircleIdSelected);
        args.putString(CircleFragment.ARG_CIRCLE_NAME, mCircleNameSelected);
        return args;
    }
}
