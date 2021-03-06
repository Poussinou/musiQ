package com.dihanov.musiq.ui.detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dihanov.musiq.ui.detail.detail_fragments.ArtistSpecificsAlbum;
import com.dihanov.musiq.ui.detail.detail_fragments.ArtistSpecificsBiography;
import com.dihanov.musiq.ui.detail.detail_fragments.detail_fragments_top_tracks.ArtistSpecificsTopTracks;

/**
 * Created by dimitar.dihanov on 9/29/2017.
 */

public class ArtistDetailsViewPagerAdapter extends FragmentStatePagerAdapter {
    private static int TAB_COUNT = 3;

    public ArtistDetailsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ArtistSpecificsBiography.newInstance();
            case 1:
                return ArtistSpecificsAlbum.newInstance();
            case 2:
                return ArtistSpecificsTopTracks.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return ArtistSpecificsBiography.TITLE;
            case 1:
                return ArtistSpecificsAlbum.TITLE;
            case 2:
                return ArtistSpecificsTopTracks.TITLE;
        }
        return super.getPageTitle(position);
    }
}
