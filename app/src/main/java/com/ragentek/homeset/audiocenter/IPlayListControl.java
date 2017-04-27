package com.ragentek.homeset.audiocenter;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/24.
 */

public interface IPlayListControl {
    void addDataListener(PlayListToken.OnDataChangeListTokenListener listener);

    void removeDataListener(PlayListToken.OnDataChangeListTokenListener listener);


    //TODO need add token ?
    void getDataAsync();

    //TODO need add token ?
    List<PlayListItem> getData();

    void playSellected(int position);

    void closePlaylistFragment();

    int getCurrentPlayIndex();

    int getLoadDatastate();
}
