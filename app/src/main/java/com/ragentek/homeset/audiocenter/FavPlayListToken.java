package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayListDetail;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.AudioCenterUtils;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;
import com.ragentek.protocol.messages.http.audio.FavoriteResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class FavPlayListToken extends PlayListToken {
    private static final String TAG = "FavPlayListManager";
    private int currentPage = 1;
    private final int ADD_FAV_INDEX = 0;


    @Override
    public void onLocalPlayListUpdate(int index, PushAudioFavEvent fav) {
        LogUtil.d(TAG, ": " + index + ",fav" + fav.toString());
        if (fav.getAction() == Constants.FAV) {
            // index < PAGE_COUNT ,add the item to  the local playlist
            PlayListItem item = AudioCenterUtils.decoratorFavoriteVO((fav.getFavoriteVO()));
            if (index != -1) {
                removePlayListItem(index);
            }
            addNewPlayListItem(ADD_FAV_INDEX, item);
            play(ADD_FAV_INDEX);
        } else {
            //change local fav to unfav ,  because  local list contain all  favs ,so  index != -1
            removePlayListItem(index);
            if (getCurrentPlayIndex() == index) {
                playNext();
                //else update the playlist  remove the  index
            } else if (getCurrentPlayIndex() > index) {
                mIPlayListLoadListener.playIndexChanger(getCurrentPlayIndex() - 1);
            }
        }
        //if in fav mode .play the new fav


    }

    private IPlayListLoadListener mIPlayListLoadListener;

    @Override
    public void loadData(IPlayListLoadListener listener) {
        mIPlayListLoadListener = listener;
        getTAGFav(listener);
    }

    public FavPlayListToken(TagDetail tag, FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler handler) {
        super(tag, activity, handler);
    }


    private void getTAGFav(final IPlayListLoadListener listener) {
        LogUtil.d(TAG, "getTAGFav: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<FavoriteResultVO> mloadDataSubscriber = new Subscriber<FavoriteResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "getTAGFav onCompleted: ");
                currentPage++;
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "getTAGFav onError: " + e.getMessage());
            }

            @Override
            public void onNext(FavoriteResultVO tagResult) {
                if (tagResult == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getFavorites() == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    List<PlayListItem> playListItems = new ArrayList<PlayListItem>();

                    int totalSize = tagResult.getFavorites().size();
                    LogUtil.d(TAG, "totalSize: " + totalSize);
                    // new add fav is on the top
                    for (int i = totalSize - 1; i > -1; i--) {
                        playListItems.add(AudioCenterUtils.decoratorFavoriteVO(tagResult.getFavorites().get(i)));
                    }
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                }
            }
        };
        AudioCenterHttpManager.getInstance(mActivity).getFavorites(mloadDataSubscriber, currentPage, PAGE_COUNT);
    }
}
