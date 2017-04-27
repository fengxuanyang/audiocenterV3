package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.protocol.commons.audio.TrackVO;
import com.ragentek.protocol.messages.http.audio.TrackResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


/**
 * Created by xuanyang.feng on 2017/4/20.
 */

public class AlbumToken extends AudioToken<List<TrackVO>> {
    private static final String TAG = "AlbumToken";
    private int currentPage = 1;
    private static final int PAGE_COUNT = 20;
    private List<TrackVO> wholePlayList;

    AlbumToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        PlayBaseFragment mPlayBaseFragment = AlbumFragment.newInstances();
        wholePlayList = new ArrayList<>();
        return mPlayBaseFragment;
    }

    @Override
    public void getPlayListAsync(final AudioPlayListResultListener listener, PlayListItem item) {
        LogUtil.d(TAG, "loadData: ");
        Subscriber<TrackResultVO> getTagSubscriber = new Subscriber<TrackResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onCompleted : ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError : " + e.toString());
            }

            @Override
            public void onNext(TrackResultVO tagResult) {
                LogUtil.d(TAG, "onNext : " + tagResult);
                if (tagResult != null) {
                    List<PlayItem> list = new ArrayList<>();
                    if (tagResult.getTracks() != null) {
                        for (int i = 0; i < tagResult.getTracks().size(); i++) {
                            TrackVO trackvo = tagResult.getTracks().get(i);
                            LogUtil.d(TAG, "onNext : " + trackvo.getTitle());
                            PlayItem item = new PlayItem();
                            item.setPlayUrl(trackvo.getPlay_url());
                            item.setDuration(trackvo.getDuration());
                            item.setCoverUrl(trackvo.getCover_url());
                            item.setTitle(trackvo.getTitle());
                            list.add(item);
                        }
                        wholePlayList.addAll(tagResult.getTracks());
                        listener.onPlayAudioListGet(PLAYLIST_RESULT_SUCCESS, list, tagResult.getTracks());
                    }
                    currentPage++;
                }
            }
        };

        AudioCenterHttpManager.getInstance(mActivity).getTracks(getTagSubscriber, item.getId(), currentPage, PAGE_COUNT);
    }

    @Override
    protected List<TrackVO> getPlayList() {
        return wholePlayList;
    }
}


