package com.ragentek.homeset.audiocenter.view.fragment;

import android.net.Uri;
import android.os.Bundle;
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

public class AlbumFragment extends PlayBaseFragment<List<TrackVO>> {
    private ListItemBaseAdapter<List<TrackVO>, TrackListAdapter.AlbumItemAdapterViewHolder> mTrackListAdapter;
    private int currentPage = 1;
    public static final int PAGE_COUNT = 20;
    private int pcurrentPlayIndext = 0;

    @BindView(R.id.tv_album_title)
    TextView mAlbumTitle;
    @BindView(R.id.image_album)
    SimpleDraweeView mSimpleDraweeView;
    @BindView(R.id.rv_album_playlist)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh_playlist)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void setInnerSellected(int index) {
        pcurrentPlayIndext = index;
    }

    @Override
    void onDataChanged(List<TrackVO> playdata) {
        LogUtil.d(TAG, "onDataChanged: ");
        updateView();
        currentPage = 1;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.audioenter_fragment_album_detail, container, false);
        ButterKnife.bind(this, view);
        inteView();
        updateView();
        return view;
    }

    private void updateView() {
        if (getPlaydata() != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mTrackListAdapter.setDatas(playdata);
            updateTitle();
            updateAlbumart();
        }
    }

    private void updateTitle() {
        LogUtil.d(TAG, "updateTitle: ");

        mAlbumTitle.setText(playdata.get(pcurrentPlayIndext).getAlbum_title());
        mAlbumTitle.getPaint().setFakeBoldText(true);
    }

    private void inteView() {
        mTrackListAdapter = new TrackListAdapter(this.getContext());
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.addItemDecoration(new RecycleItemDecoration(getActivity(), RecycleItemDecoration.VERTICAL_LIST));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTrackListAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.d(TAG, "onItemClick: " + position);
                pcurrentPlayIndext = position;
                mTrackListAdapter.updateSellect(position);
                if (mAudioFragmentListener != null) {
                    mAudioFragmentListener.onPlayItemClick(position);
                }
            }
        });
        mRecyclerView.setAdapter(mTrackListAdapter);
        mRecyclerView.addOnScrollListener(new RecycleViewEndlessOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                mAudioFragmentListener.onLoadMore();
            }
        });

    }

    private void updateAlbumart() {
        LogUtil.d(TAG, "updateAlbumart Uri: " + playdata.get(pcurrentPlayIndext).getCover_url());
        if (playdata.get(pcurrentPlayIndext).getCover_url() == null) {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(playdata.get(pcurrentPlayIndext).getCover_url()));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume: ");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate: " + this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop: ");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d(TAG, "onDetach: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: " + this);
    }
}
