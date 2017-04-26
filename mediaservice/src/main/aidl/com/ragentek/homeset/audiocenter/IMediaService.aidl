// IMediaService.aidl
package com.ragentek.homeset.audiocenter;

// Declare any non-default types here with import statements
import com.ragentek.homeset.audiocenter.IMediaPlayerListener;
import com.ragentek.homeset.audiocenter.MyTrack;
import java.util.List;

interface IMediaService {

    void setMediaPlayerListener(in IMediaPlayerListener listener);

    void addPlayList(in List<MyTrack> list, int startIndex);
    void setPlayList(in List<MyTrack> list, int startIndex);
    List<MyTrack> getPlayList();
    void init();

    void play(int index);

    void playNext();

    void playPre();

    void pause();
    void resume();


    boolean isPlaying();

    void seekToByPercent(float percent);

    void clearPlayList();

}