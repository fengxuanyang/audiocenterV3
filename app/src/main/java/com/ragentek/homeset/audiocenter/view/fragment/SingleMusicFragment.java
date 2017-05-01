package com.ragentek.homeset.audiocenter.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ragentek.homeset.audiocenter.IAudioControl;
import com.ragentek.homeset.audiocenter.SingleMusicToken;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.MusicVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/14.
 * * for  the  favorite of  music
 */
public class SingleMusicFragment extends PlayBaseFragment<MusicVO, SingleMusicToken.SingleMusicAudioControl> {
    private static final String TAG = "SingleMusicFragment";
    @BindView(R.id.tv_music_album)
    TextView albumText;
    @BindView(R.id.tv_music_singer)
    TextView singerText;
    @BindView(R.id.tv_music_name)
    TextView musicName;

    @BindView(R.id.image_music_album)
    SimpleDraweeView mSimpleDraweeView;

    @BindView(R.id.progress_music_load)
    ProgressBar mProgressBar;

    public static SingleMusicFragment newInstence() {

        return new SingleMusicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "SingleMusicFragment onCreateView: " + this);
        View view = inflater.inflate(R.layout.audioenter_fragment_music_detail, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        updateView(mIAudioControl.getData());
    }

    private void updateView(MusicVO music) {
        LogUtil.d(TAG, "SingleMusicFragment updateView: " + music.getSong_name());

        albumText.setText(music.getAlbum_name());
        musicName.setText(music.getSong_name());
        singerText.setText(music.getSinger_name());
        if (music.getCover_url() == null) {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(music.getCover_url()));
        }
    }

    @Override
    IAudioDataChangerListener<MusicVO> getIAudioDataChangerListener() {
        return mIAudioDataChangerListener;
    }

    IAudioDataChangerListener<MusicVO> mIAudioDataChangerListener = new IAudioDataChangerListener<MusicVO>() {
        @Override
        public void onGetData(int resultCode, MusicVO data) {
            updateView(data);
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtil.d(TAG, "onHiddenChanged hidden: " + hidden);

        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateView(mIAudioControl.getData());
        }
    }
}
