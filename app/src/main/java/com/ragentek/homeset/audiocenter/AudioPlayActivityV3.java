package com.ragentek.homeset.audiocenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
//import com.ragentek.homeset.audiocenter.model.bean.PlayListDetail;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.service.MyMediaPlayerControl;
import com.ragentek.homeset.audiocenter.utils.AudioCenterUtils;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment;
import com.ragentek.homeset.audiocenter.view.fragment.MusicFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayListFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayStateFragment;
import com.ragentek.homeset.audiocenter.view.fragment.RadioFragment;
import com.ragentek.homeset.audiocenter.view.fragment.SingleMusicFragment;
import com.ragentek.homeset.audiocenter.view.widget.ImageWithText;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.task.event.BackHomeEvent;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.constants.Category;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AudioPlayActivityV3 extends AudioCenterBaseActivity {
    private static final String TAG = "AudioPlayActivityV3";

    private AudioPlayerHandler mhandler = new AudioPlayerHandler();
    private static final String STATEFRAGMENTTAG = "playstatefragment";

    //for media player
    private MediaPlayerManager.MediaPlayerHandler mediaPlayerHandler;

    private PlayListToken mPlayListToken;

    private boolean needUpdatePlayProgress = true;
    private final String PERCENTAGE = "percentage";
    private TagDetail mTagDetail;
    private String eventType;

    @BindView(R.id.image_playorpause)
    ImageWithText playorpause;
    @BindView(R.id.image_play_next)
    ImageWithText playNext;
    @BindView(R.id.image_play_pre)
    ImageWithText playPre;
    @BindView(R.id.image_play_list)
    ImageWithText playList;

    @BindView(R.id.image_fav)
    ImageWithText favIV;
    @BindView(R.id.image_play_mode)
    ImageWithText playMode;

    @BindView(R.id.iv_back)
    ImageView backIV;
    @BindView(R.id.audio_name)
    TextView audioName;
    @BindView(R.id.play_seek)
    SeekBar playSeeBar;
    @BindView(R.id.tv_play_currenttime)
    TextView currenttime;
    @BindView(R.id.tv_play_totaltime)
    TextView totaltime;
    @BindView(R.id.top_progressbar)
    ProgressBar loadProgress;
    @BindView(R.id.bottom_bar)
    View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        setContentView(R.layout.audioenter_activity_play);
        ButterKnife.bind(this);
        updateView();
        updateAudioData();
        EventBus.getDefault().register(this);
        mediaPlayerHandler = MediaPlayerManager.getInstance(this).geMediaPlayerHandler();
        mediaPlayerHandler.addMediaPlayerListener(mMediaPlayerListener);
    }


    private void updateView() {
        updatePlayControl(false);
        playSeeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                needUpdatePlayProgress = true;
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_SEEKBAR_CHANGED;
                Bundle bundle = new Bundle();
                bundle.putFloat(PERCENTAGE, seekBar.getProgress() / (float) seekBar.getMax());
                msg.setData(bundle);
                mhandler.sendMessage(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                needUpdatePlayProgress = false;

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });
    }

    private void updateAudioData() {
        LogUtil.d(TAG, "updateAudioData ::" + mTagDetail);
        //TAG contains music radio etc
        if (mTagDetail == null) {
            TagDetail tag = getTagDetail();
            LogUtil.d(TAG, "onCreate getName:" + tag.getName());
            LogUtil.d(TAG, "onCreate getCategoryID:" + tag.getCategoryID());
            if (tag != null) {
                if (tag.getName() == null) {
                    for (int i = 0; i < Constants.CATEGORYTAG.values().length; i++) {
                        if (Constants.CATEGORYTAG.values()[i].getType() == tag.getCategoryID()) {
                            tag.setName(Constants.CATEGORYTAG.values()[i].getName());
                            break;
                        }
                    }
                }
                mTagDetail = tag;
            }
        }
        LogUtil.d(TAG, "updateAudioData ::" + mTagDetail.getName());
        mPlayListToken = PlayListTokenFactory.getPlayListToken(this, mTagDetail);
        audioName.setText(mTagDetail.getName());
        mPlayListToken.init();
    }

    private TagDetail getTagDetail() {
        TagDetail currentTag = null;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            eventType = bundle.getString(Constants.TASKEVENT_TYPE);
            currentTag = (TagDetail) bundle.getSerializable(Constants.CATEGORY_TAG);
        }
        return currentTag;
    }


    @Override
    public void onBackPressed() {
        //fav mode
        if (mPlayListToken instanceof FavPlayListToken) {
            EventBus.getDefault().post(new BackHomeEvent());
        }
        finish();
    }


    //TODO for test
    @OnClick(R.id.image_play_pre)
    void playPre() {
        mPlayListToken.playPre();
    }


    @OnClick(R.id.image_play_next)
    void playNext() {
        mPlayListToken.playNext();

    }

    //TODO for test switch   the fragment
    @OnClick(R.id.image_play_mode)
    void switchPlayMode() {
//        LogUtil.d(TAG, "switchPlayMode:");
//        if (mPlayListToken..getAudioType() == Constants.AUDIO_TYPE_ALBUM){
//            RadioVO radio = new RadioVO();
//            PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_RADIO, 0, 0L);
//            item.setAudio(radio);
//            switchPlayFragment(getAndUpdateAudioPlayFragment(getCurrentPlayItem()), Constants.AUDIO_TYPE_MUSIC + "");
//            updatePlayControlFavUI();
//        } else{
////            updateAudioType();
//        }

    }


    @OnClick(R.id.image_play_list)
    void showPlayList() {
        LogUtil.d(TAG, "showPlayList");
        mPlayListToken.showPlayList();
    }

    @OnClick(R.id.iv_back)
    void doBack() {
        //fav mode and speech command ,back to launcher
        if (mPlayListToken instanceof FavPlayListToken || Constants.TASKEVENT_TYPE_SPEECH.equals(eventType)) {
            EventBus.getDefault().post(new BackHomeEvent());
        }
        finish();
    }


    @OnClick(R.id.image_fav)
    void setFav() {
        LogUtil.d(TAG, "setFav ");
        mPlayListToken.updateFav2Server(new PlayListManagerListener() {
            @Override
            public void onUpdate2ServerComplete(int resultcode, int fav) {
                updatePlayControlFavUI(fav);
            }
        });
    }


    private void updatePlayControlFavUI(int fav) {
        favIV.setImageResource(fav == Constants.FAV ? R.drawable.control_fav : R.drawable.control_unfav);
    }

    @OnClick(R.id.image_playorpause)
    void playorpause() {
        LogUtil.d(TAG, "playorpause:");
        Message msg = new Message();
        msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_PAUSE;
        mhandler.sendMessage(msg);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        LogUtil.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (mediaPlayerHandler != null) {
            mediaPlayerHandler.clearPlayList();
        }
        mhandler = null;
    }


    @Subscribe
    public void onAudioFavEvent(final PushAudioFavEvent fav) {
        //TODO
//        mPlayListToken.updateLocalPlayList();

    }


    //TODO div music and radio
    private void updatePlayControl(boolean clickAble) {
        playNext.setClickable(clickAble);
        playorpause.setClickable(clickAble);
        playPre.setClickable(clickAble);
        favIV.setClickable(clickAble);
        playMode.setClickable(clickAble);
    }

    private IMediaPlayerListener mMediaPlayerListener = new IMediaPlayerListener.Stub() {
        @Override
        public IBinder asBinder() {
            return super.asBinder();
        }

        @Override
        public void initComplete() throws RemoteException {
            LogUtil.d(TAG, "initComplete ::");
            if (mhandler != null) {
                mhandler.isMediaInitComplete = true;
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_INIT_COMPLETE;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onSoundPrepared() throws RemoteException {
            LogUtil.d(TAG, "onSoundPrepared ::");
        }

        @Override
        public void onPlayStart() throws RemoteException {
            LogUtil.i(TAG, "onPlayStart");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_START;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onPlayProgress(int currPos, int duration) throws RemoteException {
            LogUtil.d(TAG, "onPlayProgress :currPos:" + currPos + ",duration:" + duration);
            if (mhandler != null) {
                mhandler.currPos = currPos;
                mhandler.duration = duration;
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_ONPROGRESS;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onPlayStop() throws RemoteException {
            LogUtil.i(TAG, "onPlayStop");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_STOP;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onSoundPlayComplete() throws RemoteException {
            LogUtil.i(TAG, "onSoundPlayComplete");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_SOUME_PREPARED;
                mhandler.sendMessage(msg);
            }
        }
    };


    private class PlayListListener implements PlayListManagerListener {


        @Override
        public void onUpdate2ServerComplete(int resultcode, int fav) {
            LogUtil.e(TAG, "onUpdate2ServerComplete : " + resultcode);

            switch (resultcode) {
                case PlayListToken.PLAYLISTMANAGER_RESULT_SUCCESS:
                    updatePlayControlFavUI(fav);
                    break;
                case PlayListToken.PLAYLISTMANAGER_RESULT_NONE:
                    LogUtil.e(TAG, "PLAYLISTMANAGER_RESULT_NONE : ");
                    Toast.makeText(AudioPlayActivityV3.this, "PLAYLISTMANAGER_RESULT_NONE", Toast.LENGTH_SHORT);

                    break;
                case PlayListToken.PLAYLISTMANAGER_RESULT_ERROR_NET:
                    Toast.makeText(AudioPlayActivityV3.this, "PLAYLISTMANAGER_RESULT_ERROR_NET", Toast.LENGTH_SHORT);
                    break;
            }
        }
    }


    private class AudioPlayerHandler extends Handler {
        private static final int MSG_MEDIA_INIT_COMPLETE = 0;
        private static final int MSG_MEDIA_PLAYLIST_COMPLETE = MSG_MEDIA_INIT_COMPLETE + 1;
        private static final int MSG_MEDIA_PLAY_SELECTED = MSG_MEDIA_INIT_COMPLETE + 2;
        private static final int MSG_MEDIA_PLAY_PAUSE = MSG_MEDIA_INIT_COMPLETE + 3;
        private static final int MSG_MEDIA_PLAY_ONPROGRESS = MSG_MEDIA_INIT_COMPLETE + 4;
        private static final int MSG_MEDIA_PLAY_START = MSG_MEDIA_INIT_COMPLETE + 5;
        private static final int MSG_MEDIA_PLAY_STOP = MSG_MEDIA_INIT_COMPLETE + 6;
        private static final int MSG_MEDIA_SEEKBAR_CHANGED = MSG_MEDIA_INIT_COMPLETE + 7;
        private static final int MSG_MEDIA_UPDATE_PLAYLIST = MSG_MEDIA_INIT_COMPLETE + 8;
        private static final int MSG_MEDIA_SOUME_PREPARED = MSG_MEDIA_INIT_COMPLETE + 9;
        boolean isMediaInitComplete;
        int position;
        int duration;
        int currPos;

        @Override
        public void handleMessage(Message msg) {
            LogUtil.d(TAG, "MSG:" + msg.what + ",isMediaInitComplete:" + isMediaInitComplete);
            switch (msg.what) {
                case MSG_MEDIA_INIT_COMPLETE:

                    break;
                case MSG_MEDIA_PLAYLIST_COMPLETE:

                    break;
                case MSG_MEDIA_PLAY_SELECTED:
                    if (isMediaInitComplete) {
                        mediaPlayerHandler.play(position);
                    }
                    break;
                case MSG_MEDIA_PLAY_PAUSE:
                    if (isMediaInitComplete) {
                        mediaPlayerHandler.playOrPause();
                    }
                    break;
                case MSG_MEDIA_PLAY_ONPROGRESS:
                    currenttime.setText(AudioCenterUtils.formatTime(currPos));
                    totaltime.setText(AudioCenterUtils.formatTime(duration));
                    if (needUpdatePlayProgress && duration != 0) {
                        playSeeBar.setProgress((int) (100 * currPos / (float) duration));
                    }
                    break;
                case MSG_MEDIA_PLAY_STOP:
                    playorpause.setImageResource(R.drawable.player_play);
                    break;
                case MSG_MEDIA_PLAY_START:
                    updatePlayControl(true);
                    playorpause.setImageResource(R.drawable.player_pause);
                    break;
                case MSG_MEDIA_SEEKBAR_CHANGED:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        float percentage = bundle.getFloat(PERCENTAGE);
                        LogUtil.d(TAG, "percentage:" + percentage);
                        mediaPlayerHandler.seekToByPercent(percentage);
                    }
                    break;
                case MSG_MEDIA_SOUME_PREPARED:
                    playNext();
                    break;
            }
        }
    }
}
