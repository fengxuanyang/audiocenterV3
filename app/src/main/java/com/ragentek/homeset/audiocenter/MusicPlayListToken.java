package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.messages.http.audio.MusicResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class MusicPlayListToken extends PlayListToken {
    private static final String TAG = "MusicPlayListToken";
    private int currentPage = 1;

    public MusicPlayListToken(TagDetail tag, FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler handler) {
        super(tag, activity, handler);
    }


    @Override
    public void onLocalPlayListUpdate(int index, PushAudioFavEvent fav) {

    }

    @Override
    public void loadData(IPlayListLoadListener listener) {
        getTAGMusic(listener);
    }


    private void getTAGMusic(final IPlayListLoadListener listener) {
        LogUtil.d(TAG, "getTAGMusics: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<MusicResultVO> mloadDataSubscriber = new Subscriber<MusicResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "getTAGMusics onCompleted: ");
                currentPage++;
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "getTAGMusics onError: " + e.getMessage());

            }

            @Override
            public void onNext(MusicResultVO tagResult) {
                if (tagResult == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getMusics() == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    //for audio playlist  start
                    List<PlayListItem> playListItems = new ArrayList<PlayListItem>();
                    //filterred is used for musicplayfragment
                    List<MusicVO> filterred = new ArrayList<>();
                    for (int i = 0; i < tagResult.getMusics().size(); i++) {
                        MusicVO music = tagResult.getMusics().get(i);
                        if (music != null && music.getSong_name() != null) {
                            LogUtil.d(TAG, "getTAGMusics :" + music.getSong_name());
                            LogUtil.d(TAG, "getCover_url :" + music.getCover_url());

                            PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_SINGLE_MUSIC, mTagDetail.getCategoryID(), music.getId());
                            item.setAudio(music);
                            item.setFav(music.getFavorite());
                            item.setGroup(Constants.GROUP_MUSIC);
                            playListItems.add(item);
                            filterred.add(music);
                            LogUtil.d(TAG, "add :i:" + i + "" + music.getSong_name() + "" + music.getPlay_url());
                        }
                    }
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                }
            }
        };
        AudioCenterHttpManager.getInstance(mActivity).getMusics(mloadDataSubscriber, mTagDetail.getName(), currentPage, PAGE_COUNT);
    }


}
