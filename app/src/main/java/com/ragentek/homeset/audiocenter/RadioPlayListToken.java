package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.messages.http.audio.RadioResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class RadioPlayListToken extends PlayListToken {
    private static final String TAG = "RadioPlayListToken";
    private int currentPage = 0;

    public RadioPlayListToken(TagDetail tag, FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler handler) {
        super(tag, activity, handler);
    }


    @Override
    protected void loadData(IPlayListLoadListener listener) {

    }

    @Override
    public void updateLocalPlayList(long id) {

    }


//    @Override
//    public void loadMore() {
//        if (isInitted) {
//            getTAGRadio();
//            return;
//        }
//        LogUtil.e(TAG, "loadMore error not init isInitted: " + isInitted);
//    }
//
//    @Override
//    void updateLocalPlayList() {
//
//    }


    private void getTAGRadio(final IPlayListLoadListener listener) {
        LogUtil.d(TAG, "getTAGFav: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<RadioResultVO> mloadDataSubscriber = new Subscriber<RadioResultVO>() {
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
            public void onNext(RadioResultVO tagResult) {
                if (tagResult == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getRadios() == null) {
                    listener.onLoadData(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    List<PlayListItem> playListItems = new ArrayList<PlayListItem>();
                    for (RadioVO radio : tagResult.getRadios()) {
                        PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_RADIO, mTagDetail.getCategoryID(), radio.getId());
                        LogUtil.d(TAG, "fav:" + radio.getFavorite());
                        item.setFav(radio.getFavorite());
                        item.setGroup(Constants.GROUP_RADIO);
                        item.setAudio(radio);
                        playListItems.add(item);
                    }
                    int totalSize = tagResult.getRadios().size();
                    LogUtil.d(TAG, "totalSize: " + totalSize);
                    // new add fav is on the top

                    listener.onLoadData(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                }
            }
        };
        AudioCenterHttpManager.getInstance(mActivity).getRadiosByTAG(mloadDataSubscriber, mTagDetail.getRadioType(), mTagDetail.getProvince(), currentPage, PAGE_COUNT);

    }


}
