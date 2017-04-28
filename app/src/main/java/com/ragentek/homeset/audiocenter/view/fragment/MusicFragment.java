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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ragentek.homeset.audiocenter.MusicToken;
import com.ragentek.homeset.audiocenter.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.audiocenter.adapter.MusicListAdapter;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.widget.RecycleItemDecoration;
import com.ragentek.homeset.audiocenter.view.widget.RecycleViewEndlessOnScrollListener;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.MusicVO;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/14.
 * for  the  category of music
 */
public class MusicFragment extends PlayBaseFragment<MusicVO, MusicToken.MusicAudioControl> {
    private static final String TAG = "MusicFragment";
    private ListItemBaseAdapter mTrackListAdapter;
    private int currentPlayIndex = 0;

    @BindView(R.id.tv_album_title)
    TextView mAlbumTitle;
    @BindView(R.id.image_album)
    SimpleDraweeView mSimpleDraweeView;
    @BindView(R.id.rv_album_playlist)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh_playlist)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static MusicFragment newInstances() {
        MusicFragment fragment = new MusicFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.audioenter_fragment_album_detail, container, false);
        ButterKnife.bind(this, view);
        inteView();
        updateView(mIAudioControl.getPlayData());
        return view;
    }

    private void updateView(MusicVO music) {
        String cover = music.getCover_url();
        LogUtil.d(TAG, "updateAlbumart coverUri: " + cover);
        if (cover == null) {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(cover));
        }
        mAlbumTitle.setText(music.getAlbum_name());
        mAlbumTitle.getPaint().setFakeBoldText(true);
    }


    private void inteView() {
        mTrackListAdapter = new MusicListAdapter(this.getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new RecycleItemDecoration(getActivity(), RecycleItemDecoration.VERTICAL_LIST));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTrackListAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.d(TAG, "onItemClick: " + position);
                currentPlayIndex = position;
                mTrackListAdapter.updateSellect(position);
            }
        });

        mRecyclerView.setAdapter(mTrackListAdapter);
        mRecyclerView.addOnScrollListener(new RecycleViewEndlessOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
//                PlayListFragment.PlayListListener listListener = (PlayListFragment.PlayListListener) getActivity();
//                listListener.onLoadMore();
//                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }


    @Override
    void onDataChanged(int resultCode, MusicVO data) {
        updateView(data);
    }


}




