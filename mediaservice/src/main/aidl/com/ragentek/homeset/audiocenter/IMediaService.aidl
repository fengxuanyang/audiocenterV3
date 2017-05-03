// IMediaService.aidl
package com.ragentek.homeset.audiocenter;

// Declare any non-default types here with import statements
import com.ragentek.homeset.audiocenter.IMediaPlayerPlayListener;
import com.ragentek.homeset.audiocenter.IMediaPlayerInitListener;
import com.ragentek.homeset.audiocenter.MyTrack;
import java.util.List;

interface IMediaService {

    void init(in IMediaPlayerInitListener listener);
    void addMediaPlayerPlayListener(in IMediaPlayerPlayListener listener);
    void removeMediaPlayerPlayListener(IMediaPlayerPlayListener listener);
    void addPlayList(in List<MyTrack> list, int startIndex);
    void setPlayList(in List<MyTrack> list, int startIndex);
    List<MyTrack> getPlayList();

    void play(int index);

    void playNext();

    void playPre();

    void pause();
    void resume();


    boolean isPlaying();

    void seekToByPercent(float percent);

    void clearPlayList();

}