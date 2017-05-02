package com.ragentek.homeset.audiocenter.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ragentek.homeset.audiocenter.AlbumToken;
import com.ragentek.homeset.audiocenter.AudioToken;
import com.ragentek.homeset.audiocenter.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.audiocenter.adapter.TrackListAdapter;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.widget.RecycleItemDecoration;
import com.ragentek.homeset.audiocenter.view.widget.RecycleViewEndlessOnScrollListener;
import com.ragentek.homeset.core.HomesetApp;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.TrackVO;
import com.ragentek.protocol.messages.http.audio.TrackResultVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by xuanyang.feng on 2017/2/17.
 * * for  the  category of
 * public static final int CROSS_TALK = 12;
 * public static final int CHINA_ART = 16;
 * public static final int HEALTH = 7;
 * public static final int STORYTELLING = 3;
 * public static final int STOCK = 8;
 * public static final int HISTORY = 9
 */

public class AlbumFragment extends PlayBaseFragment<List<TrackVO>, AlbumToken.AlbumAudioControl> {
    private ListItemBaseAdapter<List<TrackVO>, TrackListAdapter.AlbumItemAdapterViewHolder> mTrackListAdapter;
    private int currentPage = 1;
    public static final int PAGE_COUNT = 20;
    private int currentPlayIndext = 0;
    private List<TrackVO> wholePlayList;

    @BindView(R.id.tv_album_title)
    TextView mAlbumTitle;
    @BindView(R.id.image_album)
    SimpleDraweeView mSimpleDraweeView;
    @BindView(R.id.rv_album_playlist)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh_playlist)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static AlbumFragment newInstances() {
        AlbumFragment fragment = new AlbumFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.audioenter_fragment_album_detail, container, false);
        ButterKnife.bind(this, view);
        inteView();

        return view;
    }

    private void inteView() {

        LogUtil.d(TAG, "inteView  >>: " + SystemClock.currentThreadTimeMillis());
        mSwipeRefreshLayout.setRefreshing(true);
//        mSwipeRefreshLayout.
        mTrackListAdapter = new TrackListAdapter(this.getContext());
        mTrackListAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.d(TAG, "onItemClick: " + position);
                currentPlayIndext = position;
                mTrackListAdapter.updateSellect(position);
                mIAudioControl.playSellected(currentPlayIndext);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new RecycleItemDecoration(getActivity(), RecycleItemDecoration.VERTICAL_LIST));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mTrackListAdapter);
        mRecyclerView.addOnScrollListener(new RecycleViewEndlessOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                LogUtil.d(TAG, "inteView onLoadMore");

                mSwipeRefreshLayout.setRefreshing(true);
                mIAudioControl.getMoreData();
            }

            @Override
            public void onUpdata(int currentPage) {
                LogUtil.d(TAG, "onUpdata  ");

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        LogUtil.d(TAG, "inteView  <<: " + SystemClock.currentThreadTimeMillis());
        updateData();
    }

    private void updateData() {
        List<TrackVO> current = mIAudioControl.getData();
        if (current == null || current.size() <= 0) {
            mIAudioControl.setDataChangerListener(getIAudioDataChangerListener());
        } else {
            wholePlayList = current;
            updateView();
            mIAudioControl.playSellected(currentPlayIndext);
        }
    }

    private void updateView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mTrackListAdapter.setDatas(wholePlayList);
        updateTitle();
        updateAlbumart();
    }

    private void updateTitle() {
        LogUtil.d(TAG, "updateTitle: ");
        mAlbumTitle.setText(getCurrentPlayTrack().getAlbum_title());
        mAlbumTitle.getPaint().setFakeBoldText(true);
    }

    private TrackVO getCurrentPlayTrack() {
        return wholePlayList.get(currentPlayIndext);
    }

    private void updateAlbumart() {
        if (wholePlayList.get(currentPlayIndext).getCover_url() == null) {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(wholePlayList.get(currentPlayIndext).getCover_url()));
        }
    }

    @Override
    IAudioDataChangerListener<List<TrackVO>> getIAudioDataChangerListener() {
        return mIAudioDataChangerListener;
    }

    IAudioDataChangerListener<List<TrackVO>> mIAudioDataChangerListener = new IAudioDataChangerListener<List<TrackVO>>() {
        @Override
        public void onGetData(int resultCode, List<TrackVO> data) {
            LogUtil.d(TAG, "onGetData  resultCode: " + resultCode);

            switch (resultCode) {
                case AudioToken.PLAYLIST_RESULT_SUCCESS: {
                    if (wholePlayList == null) {
                        wholePlayList = data;
                    } else {
                        wholePlayList.addAll(data);
                    }
                    updateView();
                }
                break;
                case AudioToken.PLAYLIST_RESULT_NONE: {
                    //TODO
                }
                break;
                case AudioToken.PLAYLIST_RESULT_ERROR_NET: {
                    //TODO
                }
                break;
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtil.d(TAG, "onHiddenChanged hidden: " + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mSwipeRefreshLayout.setRefreshing(true);
            wholePlayList = null;
            currentPlayIndext = 0;
            updateData();
        } else {
            mIAudioControl.setDataChangerListener(null);
        }
    }
}
