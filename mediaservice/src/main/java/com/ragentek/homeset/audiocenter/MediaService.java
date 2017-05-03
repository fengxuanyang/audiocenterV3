package com.ragentek.homeset.audiocenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmDataCallback;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;


public class MediaService extends Service {
    private static final String TAG = "MediaService";

    private final String XMLY_APPSECRET = "c8305c13038e87298c9bc2bd1aa5b116 ";
    private XmPlayerManager mXMLYPlayerManager;
    private CommonRequest mXimalaya;
    private MediaServiceStub mMediaServiceStub;
    private Context mContext;
    private boolean mInitComplete;
    private int currentPlayIndxt;
    private List<Track> currentPlayList = new ArrayList<>();
    private IMediaPlayerInitListener mInitListener;
    private List<IMediaPlayerPlayListener> mPlayListeners = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mContext = getApplicationContext();
        mMediaServiceStub = new MediaServiceStub(this);
        mXimalaya = CommonRequest.getInstanse();
        mXimalaya.init(mContext, XMLY_APPSECRET);
        mInitComplete = false;
    }


    private void setInitListener(IMediaPlayerInitListener listener) {
        Log.d(TAG, "setInitListener, listener=" + listener + " mInitComplete=" + mInitComplete);
        if (listener == null) {
            Log.e(TAG, "setInitListener listener is null ");
            return;
        }
        mInitListener = listener;
        if (listener != null) {
            if (mInitComplete) {
                try {
                    listener.initComplete();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void removeMediaPlayerPlayListener(IMediaPlayerPlayListener listener) {
        Log.d(TAG, "removeMediaPlayerPlayListener, listener=" + listener + " mInitComplete=" + mInitComplete);
        if (listener == null) {
            Log.e(TAG, "removeMediaPlayerPlayListener listener is null ");
            return;
        }
        boolean result = mPlayListeners.remove(listener);
        Log.e(TAG, "removeMediaPlayerPlayListener  result:" + result);

    }

    private void addMediaPlayerPlayListener(IMediaPlayerPlayListener listener) {
        Log.d(TAG, "addMediaPlayerPlayListener, listener=" + listener + " mInitComplete=" + mInitComplete);
        if (listener == null) {
            Log.e(TAG, "addMediaPlayerPlayListener listener is null ");
            return;
        }
        mPlayListeners.add(listener);
    }

    public void init() {
        if (!mInitComplete) {
            mXMLYPlayerManager = XmPlayerManager.getInstance(mContext);
            mXMLYPlayerManager.init();
            mXMLYPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
            mXMLYPlayerManager.setOnConnectedListerner(new XmPlayerManager.IConnectListener() {
                @Override
                public void onConnected() {
                    mXimalaya.setDefaultPagesize(50);
                    Log.d(TAG, "onConnected: " + Thread.currentThread().getName());
                    mInitComplete = true;
                    try {
                        mInitListener.initComplete();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            });
        } else {
            try {
                mInitListener.initComplete();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private void release() {
        Log.d(TAG, "release");
        mXMLYPlayerManager.resetPlayList();
        mXMLYPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        XmPlayerManager.release();
        mInitComplete = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand, intent=" + intent + " flags=" + flags + " startId=" + startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind, intent=" + intent);
        release();
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind, intent=" + intent);
        return mMediaServiceStub;
    }

    private static class MediaServiceStub extends IMediaService.Stub {
        MediaService mService;

        public MediaServiceStub(MediaService mService) {
            this.mService = mService;
        }

        @Override
        public void init(IMediaPlayerInitListener listener) throws RemoteException {
            mService.setInitListener(listener);


            mService.init();
        }


        @Override
        public void addMediaPlayerPlayListener(IMediaPlayerPlayListener listener) throws RemoteException {
            Log.d(TAG, "addMediaPlayerPlayListener: " + listener);
            mService.addMediaPlayerPlayListener(listener);
        }

        @Override
        public void removeMediaPlayerPlayListener(IMediaPlayerPlayListener listener) throws RemoteException {
            mService.removeMediaPlayerPlayListener(listener);

        }

        @Override
        public void addPlayList(List<MyTrack> list, int startIndex) throws RemoteException {
            mService.addPlayList(list);
        }

        @Override
        public void setPlayList(List<MyTrack> list, int startIndex) throws RemoteException {
            mService.setPlayList(list, startIndex);
        }

        @Override
        public List<MyTrack> getPlayList() throws RemoteException {
            return mService.getPlayList();
        }


        @Override
        public void play(int index) throws RemoteException {
            mService.play(index);

        }


        @Override
        public void playNext() throws RemoteException {
            mService.playNext();
        }

        @Override
        public void playPre() throws RemoteException {
            mService.playPre();
        }

        @Override
        public void pause() throws RemoteException {
            mService.pause();
        }

        @Override
        public void resume() throws RemoteException {
            mService.resume();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.isPlaying();
        }

        @Override
        public void seekToByPercent(float percent) throws RemoteException {
            mService.seekToByPercent(percent);
        }

        @Override
        public void clearPlayList() throws RemoteException {
            mService.resetPlayList();
        }
    }


    /**
     * @param list
     */
    private void addPlayList(List<MyTrack> list) {
        Log.d(TAG, "addPlayList");
        if (list != null) {
            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Track track = list.get(i).getTrack();
                tracks.add(track);
            }
            if (currentPlayList == null) {
                currentPlayList = tracks;
            } else {
                currentPlayList.addAll(tracks);
            }
        }
    }

    private void setPlayList(List<MyTrack> list, int startIndex) {
        Log.d(TAG, "setPlayList, list=" + list + " startIndex=" + startIndex);
        if (list != null) {
            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Track track = list.get(i).getTrack();
                tracks.add(track);
            }
            currentPlayList = tracks;
            play(startIndex);
        }
    }

    private List<MyTrack> getPlayList() {
        Log.d(TAG, "getPlayList");
        List<MyTrack> list = new ArrayList<>();
        List<Track> tracks = mXMLYPlayerManager.getPlayList();
        if (tracks != null) {
            for (int i = 0; i < tracks.size(); i++) {
                MyTrack myTrack = new MyTrack();
                myTrack.setTrack(tracks.get(i));
                list.add(myTrack);
            }
        }

        return list;
    }

    private void resetPlayList() {
        Log.d(TAG, "resetPlayList");
        mXMLYPlayerManager.resetPlayList();
        mXMLYPlayerManager.stop();
        currentPlayList.clear();
    }

    private void play(int index) {
        Log.d(TAG, "play");
        List<Track> templayList = new ArrayList<>();
        if (mXMLYPlayerManager.isPlaying()) {
            mXMLYPlayerManager.stop();
        }
        mXMLYPlayerManager.resetPlayList();
        Log.d(TAG, "play :" + currentPlayList.get(index));

        templayList.add(currentPlayList.get(index));
        mXMLYPlayerManager.setPlayList(templayList, 0);
        currentPlayIndxt = index;
    }

    private void playNext() {
        Log.d(TAG, "playNext");
        if (currentPlayIndxt < currentPlayList.size()) {
            currentPlayIndxt++;
        }
        play(currentPlayIndxt);
    }

    private void playPre() {
        Log.d(TAG, "playPre");
        Log.d(TAG, "playNext");
        if (currentPlayIndxt > 1) {
            currentPlayIndxt--;
        }
        play(currentPlayIndxt);
    }

    private void pause() {
        Log.d(TAG, "pause");
        mXMLYPlayerManager.pause();
    }

    private void resume() {
        Log.d(TAG, "resume");
        mXMLYPlayerManager.play();
    }

    private boolean isPlaying() {
        Log.d(TAG, "isPlaying");
        return mXMLYPlayerManager.isPlaying();
    }

    private void seekToByPercent(float percent) {
        Log.d(TAG, "seekToByPercent, percent=" + percent);
        mXMLYPlayerManager.seekToByPercent(percent);
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Log.d(TAG, "onSoundPrepared");
            try {
                for (IMediaPlayerPlayListener mMediaPlayerListener : mPlayListeners) {
                    mMediaPlayerListener.onSoundPrepared();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mXMLYPlayerManager.play();
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
        }

        @Override
        public void onPlayStop() {
            Log.d(TAG, "onPlayStop:");
            try {
                for (IMediaPlayerPlayListener mMediaPlayerListener : mPlayListeners) {
                    mMediaPlayerListener.onPlayStop();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayStart() {
            Log.d(TAG, "onPlayStart:");
            try {
                for (IMediaPlayerPlayListener mMediaPlayerListener : mPlayListeners) {
                    mMediaPlayerListener.onPlayStart();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            Log.d(TAG, "onPlayProgress:" + currPos + ",duration" + duration);
            try {
                for (IMediaPlayerPlayListener mMediaPlayerListener : mPlayListeners) {
                    mMediaPlayerListener.onPlayProgress(currPos, duration);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onPlayPause() {
            try {
                for (IMediaPlayerPlayListener mMediaPlayerListener : mPlayListeners) {
                    mMediaPlayerListener.onPlayStop();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSoundPlayComplete() {
            Log.d(TAG, "onSoundPlayComplete");
            try {
                for (IMediaPlayerPlayListener mMediaPlayerListener : mPlayListeners) {
                    mMediaPlayerListener.onSoundPlayComplete();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public boolean onError(XmPlayerException exception) {
            Log.e(TAG, "onError" + exception.getMessage());
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            Log.d(TAG, "onBufferProgress :" + position);
        }

        public void onBufferingStart() {
            Log.d(TAG, "onBufferingStart");
        }

        public void onBufferingStop() {
            Log.d(TAG, "onBufferingStop");
        }
    };


}