package com.dihanov.musiq.ui.main.main_fragments;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.dihanov.musiq.models.Artist;
import com.dihanov.musiq.models.ArtistSearchResults;
import com.dihanov.musiq.service.LastFmApiClient;
import com.dihanov.musiq.ui.main.MainActivity;
import com.dihanov.musiq.util.Constants;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Dimitar Dihanov on 20.9.2017 г..
 */

public class ArtistResultFragmentPresenter implements ArtistResultFragmentContract.Presenter {
    private static final long DELAY_IN_MILLIS = 500;
    private static final int limit = 20;

    @Inject LastFmApiClient lastFmApiClient;

    private ArtistResultFragmentContract.View artistResultFragment;
    private Disposable disposable;
    private RecyclerView recyclerView;

    @Inject
    public ArtistResultFragmentPresenter() {
    }

    @Override
    public void takeView(ArtistResultFragmentContract.View view) {
        this.artistResultFragment = view;
        this.recyclerView = view.getRecyclerView();
    }

    @Override
    public void leaveView() {
        if(this.disposable != null){
            disposable.dispose();
        }
        this.artistResultFragment = null;
    }


    @Override
    public void addOnArtistResultClickedListener(final RecyclerView recyclerView) {
//        Observable<PlaceDetailsResult> adapterViewItemClickEventObservable =
//                RxAutoCompleteTextView.itemClickEvents(autoCompleteTextView)
//
//                        .map(new Func1<AdapterViewItemClickEvent, String>() {
//                            @Override
//                            public String call(AdapterViewItemClickEvent adapterViewItemClickEvent) {
//                                NameAndPlaceId item = (NameAndPlaceId) autoCompleteTextView.getAdapter()
//                                        .getItem(adapterViewItemClickEvent.position());
//                                return item.placeId;
//                            }
//                        })
//                        .observeOn(Schedulers.io())
//                        .flatMap(new Func1<String, Observable<PlaceDetailsResult>>() {
//                            @Override
//                            public Observable<PlaceDetailsResult> call(String placeId) {
//                                return RestClient.INSTANCE.getGooglePlacesClient().details(placeId);
//                            }
//                        })
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .retry();
//
//        compositeSubscription.add(adapterViewItemClickEventObservable
//                .subscribe(new Observer<PlaceDetailsResult>() {
//
//                    private static final String TAG = "PlaceDetailsResult";
//
//                    @Override
//                    public void onCompleted() {
//                        Log.i(TAG, "onCompleted");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError", e);
//                    }
//
//                    @Override
//                    public void onNext(PlaceDetailsResult placeDetailsResponse) {
//                        Log.i(TAG, placeDetailsResponse.toString());
//                        updateMap(placeDetailsResponse);
//                    }
//                }));
    }

    @Override
    public void addOnSearchBarTextChangedListener(MainActivity mainActivity, SearchView searchEditText) {
        if(searchEditText == null){
            return;
        }
        Observable<ArtistSearchResults> autocompleteResponseObservable =
                RxSearchView.queryTextChanges(searchEditText)
                        .debounce(DELAY_IN_MILLIS, TimeUnit.MILLISECONDS)
                        .filter(s -> s.length() >= 2)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(s -> {
                            Constants.checkConnection(mainActivity);
                            mainActivity.showProgressBar();
                        })
                        .observeOn(Schedulers.io())
                        .flatMap(new Function<CharSequence, Observable<ArtistSearchResults>>() {
                            @Override
                            public Observable<ArtistSearchResults> apply(CharSequence s) throws Exception {
                                return lastFmApiClient.getLastFmApiService()
                                        .searchForArtist(s.toString(), limit);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry();


        autocompleteResponseObservable
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArtistSearchResults>() {
                    private static final String TAG = "ArtistResult";

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(ArtistSearchResults artistSearchResults) {
//                        mainActivity.showProgressBar();
                        Log.i(TAG, artistSearchResults.toString());

                        List<Artist> result = new ArrayList<>();
                        result.addAll(artistSearchResults.getResults().getArtistmatches().getArtistMatches());
                        if(result.isEmpty()){
                            result = Collections.emptyList();
                        }

                        ArtistAdapter artistAdapter = new ArtistAdapter(mainActivity, result, lastFmApiClient);

                        recyclerView.setAdapter(artistAdapter);
                        mainActivity.hideKeyboard();
                        mainActivity.hideProgressBar();
                    }

                    @Override
                    public void onComplete() {
                        mainActivity.hideProgressBar();
                        disposable.dispose();
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                        mainActivity.hideProgressBar();
                        Log.e(TAG, "onError", e);
                    }
                });
    }
}
