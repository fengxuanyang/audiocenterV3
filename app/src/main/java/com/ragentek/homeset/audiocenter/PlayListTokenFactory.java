package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.protocol.constants.Category;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

public class PlayListTokenFactory {
    private static final String TAG = "PlayListTokenFactory";

    public static PlayListToken getPlayListToken(FragmentActivity activity, TagDetail tag, MediaPlayerManager.MediaPlayerHandler handler) {
        PlayListToken token = null;
        switch (tag.getCategoryID()) {
            case Category.ID.CROSS_TALK:
            case Category.ID.CHINA_ART:
            case Category.ID.HEALTH:
            case Category.ID.STORYTELLING:
            case Category.ID.STOCK:
            case Category.ID.HISTORY:
                token = new AlbumPlayListToken(tag, activity, handler);
                break;
            case Category.ID.RADIO:
                token = new RadioPlayListToken(tag, activity, handler);
                break;
            case Category.ID.MUSIC:
                token = new MusicPlayListToken(tag, activity, handler);
                break;
            case Constants.CATEGORY_FAV:
                token = new FavPlayListToken(tag, activity, handler);
                break;
            case Constants.CATEGORY_MUSIC_SEARCH:
                token = new MusicPlayListToken(tag, activity, handler);
                break;

            default:
                LogUtil.e(TAG, "bo not contain " + tag.toString());
        }
        return token;
    }
}
