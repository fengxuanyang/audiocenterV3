package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.messages.http.audio.AlbumResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class AlbumPlayListToken extends PlayListToken {
    private static final String TAG = "AlbumPlayListToken";
    private int currentPage = 1;

    public AlbumPlayListToken(TagDetail tag, FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler handler) {
        super(tag, activity, handler);
    }


    @Override
    public void onLocalPlayListUpdate(int index, PushAudioFavEvent fav) {

    }

    @Override
    public void loadData(final IPlayListLoadListener listener) {
        LogUtil.d(TAG, "loadData   : " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<AlbumResultVO> mloadDataSubscriber = new Subscriber<AlbumResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(AlbumResultVO tagResult) {
                LogUtil.d(TAG, "loadData   onNext: ");

                if (tagResult == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getAlbums() == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    //for audio playlist  start
                    List<PlayListItem> playListItems = getPlayListItems(tagResult);
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                }
            }
        };
        AudioCenterHttpManager.getInstance(mActivity).getAlbums(mloadDataSubscriber, mTagDetail.getCategoryID(), mTagDetail.getName() == null ? Constants.DEFULT_CROSS_TALK : mTagDetail.getName(), currentPage, PAGE_COUNT);

    }

    private List<PlayListItem> getPlayListItems(AlbumResultVO tagResult) {
        List<PlayListItem> playListItems = new ArrayList<>();
        for (AlbumVO album : tagResult.getAlbums()) {
            PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_ALBUM, mTagDetail.getCategoryID(), album.getId());
            LogUtil.d(TAG, "fav:" + album.getFavorite());
            LogUtil.d(TAG, album.getTitle());
            item.setFav(album.getFavorite());
            item.setGroup(Constants.GROUP_ALBUM);
            item.setAudio(album);
            playListItems.add(item);
        }
        return playListItems;
    }


}
