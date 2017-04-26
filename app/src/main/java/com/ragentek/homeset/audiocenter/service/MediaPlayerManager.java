package com.ragentek.homeset.audiocenter.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ragentek.homeset.audiocenter.IMediaPlayerListener;
import com.ragentek.homeset.audiocenter.IMediaService;
import com.ragentek.homeset.audiocenter.MediaServiceInitEvent;
import com.ragentek.homeset.audiocenter.MyTrack;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/3/21.
 */

public class MediaPlayerManager {
    private static MediaPlayerHandler mMediaPlayerhandler;
    private static final String TAG = "MediaPlayerManager";
    public static final int RESULT_INIT_ERROE = -1;
    public static final int RESULT_INIT_COMPLETE = 0;

    private static final ComponentName MEDIA_SERVICE_COMPONENT = new ComponentName(
            "com.ragentek.homeset.audiocenter", "com.ragentek.homeset.audiocenter.MediaService");

    private MediaServiceConnection mMediaServiceConnection = new MediaServiceConnection();
    private List<PlayItem> wholePlayList;
    private boolean mIsMediaServiceBound;
    private boolean mIsMediaServiceInit = false;
    private WeakReference<Context> weakContext;

    private static MediaPlayerManager mMediaPlayerManager;
    IMediaService mMediaService;

    private MediaPlayerManager(Context context) {
        this.weakContext = new WeakReference<Context>(context.getApplicationContext());
        bindMediaService();
    }

    public static MediaPlayerManager getInstance(Context context) {
        if (mMediaPlayerManager == null) {
            synchronized (MediaPlayerManager.class) {
                if (mMediaPlayerManager == null) {
                    mMediaPlayerManager = new MediaPlayerManager(context);
                }
            }
        }
        return mMediaPlayerManager;
    }

    public void setMediaPlayerListener(final IMediaPlayerListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    Log.d(TAG, "run mIsMediaServiceBound: " + mIsMediaServiceBound);
                } while (!mIsMediaServiceBound);
                try {
                    mMediaService.setMediaPlayerListener(listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public void init() {
        if (mIsMediaServiceBound) {
            try {
                mMediaService.init();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mIsMediaServiceInit = true;
        } else {
            LogUtil.e(TAG, "MediaPlayerManager is not  bound complete");
        }

    }

    public void release() {
        LogUtil.d(TAG, "release: ");
        mIsMediaServiceInit = false;
        unbindMediaService();
    }

    public MediaPlayerHandler geMediaPlayerHandler() {
        if (mIsMediaServiceBound) {
            return mMediaPlayerhandler;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("error:");
            if (!mIsMediaServiceInit) {
                LogUtil.e(TAG, "MediaPlayerManager not init: please init first ");
            } else if (!mIsMediaServiceBound) {
                LogUtil.e(TAG, "MediaPlayerManager   init  error ");
            }
            return null;
        }
    }

    private void bindMediaService() {
        LogUtil.d(TAG, "bindMediaService: ");
        Intent intent = new Intent().setComponent(MEDIA_SERVICE_COMPONENT);
        weakContext.get().startService(intent);
        weakContext.get().bindService(intent, mMediaServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindMediaService() {
        LogUtil.d(TAG, "unbindMediaService: " + mIsMediaServiceBound);
        if (mIsMediaServiceBound) {
            weakContext.get().unbindService(mMediaServiceConnection);
        }
        mIsMediaServiceBound = false;
    }


    private class MediaServiceConnection implements ServiceConnection {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaService = IMediaService.Stub.asInterface(service);
            mMediaPlayerhandler = new MediaPlayerHandler(mMediaService);
            mIsMediaServiceBound = true;
            LogUtil.d(TAG, "onServiceConnected: " + mIsMediaServiceBound);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "onServiceDisconnected: ");
            mIsMediaServiceBound = false;
        }

    }

    public class MediaPlayerHandler {
        private IMediaService mMediaService = null;

        private MediaPlayerHandler(IMediaService service) {
            mMediaService = service;
        }


        public void clearPlayList() {
            LogUtil.d(TAG, "clearPlayList ");
            try {
                mMediaService.clearPlayList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void setPlayList(List<PlayItem> list, int position) {
            LogUtil.d(TAG, "setPlayList size: " + list.size());
            wholePlayList = list;
            try {
                List<MyTrack> tracks = new ArrayList();
                LogUtil.d(TAG, "add size: " + tracks.size());
                for (PlayItem item : list) {
                    tracks.add(getTrack(item));
                }
                LogUtil.d(TAG, "add size ---: " + tracks.size());

                mMediaService.setPlayList(tracks, position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void addPlayList(List<PlayItem> list) {
            LogUtil.d(TAG, "addPlayList size: " + list.size());
            try {
                List<MyTrack> tracks = new ArrayList();
                LogUtil.d(TAG, "add size: " + tracks.size());
                for (PlayItem item : list) {
                    tracks.add(getTrack(item));
                }
                mMediaService.addPlayList(tracks, -1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void play(int index) {
            LogUtil.d(TAG, "play :" + index);
            try {
                mMediaService.play(index);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


        public void seekToByPercent(float percent) {
            LogUtil.d(TAG, "seekToByPercent :" + percent);
            try {
                mMediaService.seekToByPercent(percent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


        public void pause() {
            LogUtil.d(TAG, "playOrPause ");
            try {
                if (mMediaService.isPlaying()) {
                    mMediaService.pause();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void resume() {
            LogUtil.d(TAG, "playOrPause ");
            try {
                if (!mMediaService.isPlaying()) {
                    mMediaService.resume();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void playNext() {
            try {
                mMediaService.playNext();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void playPre() {
            try {
                mMediaService.playPre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


        private MyTrack getTrack(PlayItem item) {
            LogUtil.d(TAG, "url: " + item.getPlayUrl());
            Track track = new Track();
            track.setKind(PlayableModel.KIND_TRACK);
            track.setCoverUrlMiddle(item.getCoverUrl());
            track.setDuration(item.getDuration());
            track.setPlayUrl32(item.getPlayUrl());
            track.setOrderNum(item.getIndex());
            if (item.getTitle() == null) {
                track.setTrackTitle(item.getPlayUrl());
            } else {
                track.setTrackTitle(item.getTitle());
            }
            track.setDataId(-1 * track.getTrackTitle().hashCode());
            MyTrack myTrack = new MyTrack();
            myTrack.setTrack(track);

            return myTrack;
        }
    }


}

