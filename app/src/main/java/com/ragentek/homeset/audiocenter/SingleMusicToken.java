package com.ragentek.homeset.audiocenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.audiocenter.view.fragment.SingleMusicFragment;
import com.ragentek.protocol.commons.audio.MusicVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

public class SingleMusicToken extends AudioToken<MusicVO, SingleMusicToken.SingleMusicAudioControl> {
    private static final String TAG = "SingleMusicToken";

    private MusicVO mMusicVO;

    private MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    private int currentPlayIndext = 0;

    SingleMusicToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
        LogUtil.d(TAG, " SingleMusicToken: ");
        mMediaPlayer = mediaPlayer;
        mMusicVO = (MusicVO) item.getAudio();
        LogUtil.d(TAG, " SingleMusicToken  getSong_name: " + mMusicVO.getSong_name());

    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        PlayBaseFragment singleFragment;
        if (view == null) {
            singleFragment = SingleMusicFragment.newInstence();
        } else {
            singleFragment = (PlayBaseFragment) view;
        }
        singleFragment.setAudioControl(new SingleMusicAudioControl());
        return singleFragment;
    }

    @Override
    protected void playAudio(int index) {
        LogUtil.d(TAG, "    playAudio: " + index);
        currentPlayIndext = index;
        PlayItem item = new PlayItem();
        item.setPlayUrl(mMusicVO.getPlay_url());
        item.setCoverUrl(mMusicVO.getCover_url());
        item.setTitle(mMusicVO.getSong_name());

        List<PlayItem> list = new ArrayList<>();
        list.add(item);

        mMediaPlayer.setPlayList(list, currentPlayIndext);
    }


    public class SingleMusicAudioControl implements IAudioControl {

        @Override
        public void setDataChangerListener(PlayBaseFragment.IAudioDataChangerListener mListener) {

        }

        public MusicVO getData() {
            LogUtil.d(TAG, "getPlayData   currentMusic: " + mMusicVO.getSong_name());
            return mMusicVO;

        }
    }
}