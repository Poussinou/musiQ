package com.dihanov.musiq.ui.detail.detail_fragments;

import com.dihanov.musiq.R;
import com.dihanov.musiq.service.LastFmApiClient;
import com.dihanov.musiq.ui.main.AlbumDetailsPopupWindow;
import com.dihanov.musiq.ui.view_holders.AlbumViewHolder;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by dimitar.dihanov on 10/6/2017.
 */

public class ArtistSpecificsPresenter implements ArtistSpecificsContract.Presenter{
    private final LastFmApiClient lastFmApiClient;

    private ArtistSpecificsContract.View artistDetailsFragment;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private com.dihanov.musiq.ui.detail.ArtistDetailsContract.View artistDetailsActivity;

    @Inject
    public ArtistSpecificsPresenter(LastFmApiClient lastFmApiClient) {
        this.lastFmApiClient = lastFmApiClient;
    }

    @Override
    public void takeView(ArtistSpecificsContract.View view) {
        this.artistDetailsFragment = (ArtistSpecifics) view;
        this.artistDetailsActivity = artistDetailsFragment.getArtistDetailsActivity();
    }

    @Override
    public void leaveView() {
        this.artistDetailsFragment = null;
        compositeDisposable.clear();
    }


    @Override
    public void setClickListenerFetchEntireAlbumInfo(AlbumViewHolder viewHolder, String artistName, String albumName) {
        AlbumDetailsPopupWindow albumDetailsPopupWindow = new AlbumDetailsPopupWindow(lastFmApiClient, artistDetailsActivity);
        albumDetailsPopupWindow.showPopupWindow(artistDetailsActivity, viewHolder, artistName, albumName, R.id.detail_content);
    }
}
