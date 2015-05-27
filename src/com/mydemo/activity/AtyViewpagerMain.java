package com.mydemo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mydemo.R;
import com.mydemo.fragment.TestFragment;
import com.mydemo.view.TabPageIndicator;

public class AtyViewpagerMain extends Fragment{
	 private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	View view=inflater.inflate(R.layout.viewpager_main, null);

    FragmentPagerAdapter adapter = new GoogleMusicAdapter(getActivity().getSupportFragmentManager());

    ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
    pager.setAdapter(adapter);

    TabPageIndicator indicator = (TabPageIndicator)view.findViewById(R.id.indicator);
    indicator.setViewPager(pager);
	return view;
}
class GoogleMusicAdapter extends FragmentPagerAdapter {
    public GoogleMusicAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length].toUpperCase();
    }

    @Override
    public int getCount() {
      return CONTENT.length;
    }
}
}
