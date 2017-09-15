package com.dihanov.musiq.di.modules;

import com.dihanov.musiq.service.LastFmApiService;
import com.dihanov.musiq.ui.detail.DetailActivity;
import com.dihanov.musiq.ui.detail.DetailPresenter;
import com.dihanov.musiq.ui.detail.DetailPresenterImpl;
import com.dihanov.musiq.ui.detail.DetailView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Dimitar Dihanov on 15.9.2017 г..
 */

@Module
public class DetailActivityModule {
    @Provides
    DetailView provideDetailView(DetailActivity detailActivity){
        return detailActivity;
    }

    @Provides
    DetailPresenter provideMainPresenter(DetailView detailView, LastFmApiService apiService){
        return new DetailPresenterImpl(detailView, apiService);
    }
}
