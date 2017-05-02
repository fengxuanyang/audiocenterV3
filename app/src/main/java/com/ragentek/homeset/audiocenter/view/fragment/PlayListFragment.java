package com.ragentek.homeset.audiocenter.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.IPlayListControl;
import com.ragentek.homeset.audiocenter.PlayListToken;
import com.ragentek.homeset.audiocenter.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.audiocenter.adapter.PlayListAdapter;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.widget.RecycleViewEndlessOnScrollListener;
import com.ragentek.homeset.core.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xuanyang.feng on 2017/3/13.
 * view for the playlist
 */

public class PlayListFragment extends DialogFragment {
    private static final String TAG = "PlayListFragment";
    private Context mContext;

    public static final String TAG_PLAYINDEX = "playindex";
    private int playindex;
    private PlayListAdapter mPlayListAdapter;
    private IPlayListControl mIPlayListControl = new NULLIPlayListControl();
    private List<PlayListItem> currentPlaylist = new ArrayList<>();


    @BindView(R.id.rv_playlist)
    RecyclerView playlistRV;
    @BindView(R.id.tv_close)
    TextView closeTextView;
    @BindView(R.id.tv_listname)
    TextView listNameTextView;
    @BindView(R.id.swiperefresh_playlist)
    SwipeRefreshLayout swipeRefresh;

    public static PlayListFragment newInstance(int playindex) {
        PlayListFragment fragment = new PlayListFragment();
        Bundle b = new Bundle();
        b.putInt(PlayListFragment.TAG_PLAYINDEX, playindex);
        fragment.setArguments(b);

        return fragment;
    }

    public void setPlayControl(IPlayListControl control) {
        LogUtil.d(TAG, "");
        mIPlayListControl = control;
        mIPlayListControl.addDataListener(new PlayListToken.OnDataChangeListTokenListener() {
            @Override
            public void onDataUpdate(int resultCode, PlayListItem item) {

            }

            @Override
            public void onGetData(int resultCode, List<PlayListItem> data) {
                //playListAdapter == null also  means  fragment not init
                if (mPlayListAdapter != null && data.size() > 0) {
                    currentPlaylist.addAll(data);
                    mPlayListAdapter.addDatas(data);
                }
            }
        });
    }

    @Override
    public void onAttach(Context activity) {
        LogUtil.d(TAG, "onAttach: ");
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomDialog);
        Bundle argument = getArguments();
        if (argument != null) {
            playindex = argument.getInt(TAG_PLAYINDEX);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.audioenter_fragment_dialog_playlist, container);
        ButterKnife.bind(this, view);
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().setAttributes(params);
        initView();
        return view;
    }

    private void initView() {
        LogUtil.d(TAG, "initView: " + playindex);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.d(TAG, "onRefresh: ");
            }
        });
        currentPlaylist = mIPlayListControl.getData();
        mPlayListAdapter = new PlayListAdapter(mContext, playindex);
        if (currentPlaylist == null) {
            //TODO data error wait for

        } else if (currentPlaylist.size() == 0) {
            //TODO none data

        } else {
            mPlayListAdapter.setDatas(currentPlaylist);
        }
        mPlayListAdapter.updateSellect(playindex);

        playlistRV.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        playlistRV.setLayoutManager(mLayoutManager);
        playlistRV.setAdapter(mPlayListAdapter);
        mPlayListAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                playindex = position;
                //update the playlist view
                mPlayListAdapter.updateSellect(position);
                mIPlayListControl.playSellected(position);

            }
        });

        playlistRV.addOnScrollListener(new RecycleViewEndlessOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                swipeRefresh.setRefreshing(true);
                mIPlayListControl.getDataAsync();
            }

            @Override
            public void onUpdata(int currentPage) {
                swipeRefresh.setRefreshing(false);

            }
        });
    }

    @OnClick(R.id.tv_close)
    void closeFragment() {
        LogUtil.d(TAG, "closeFragment: ");
        mIPlayListControl.closePlaylistFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.8);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }

    private void showData() {
        LogUtil.d(TAG, "updateAll: ");
        if (isVisible()) {
            mPlayListAdapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        }
    }


    private class NULLIPlayListControl implements IPlayListControl {

        @Override
        public void addDataListener(PlayListToken.OnDataChangeListTokenListener listener) {

        }

        @Override
        public void removeDataListener(PlayListToken.OnDataChangeListTokenListener listener) {

        }

        @Override
        public List<PlayListItem> getData() {
            return null;
        }

        @Override
        public void getDataAsync() {

        }

        @Override
        public void playSellected(int position) {

        }

        @Override
        public void closePlaylistFragment() {

        }

        @Override
        public int getCurrentPlayIndex() {
            return 0;
        }

        @Override
        public int getLoadDatastate() {
            return 0;
        }
    }


}
