package com.ragentek.homeset.audiocenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.AudioCenterUtils;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

import com.ragentek.homeset.audiocenter.view.widget.ImageWithText;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.task.event.BackHomeEvent;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import static com.ragentek.homeset.audiocenter.PlayListToken.PLAYLISTMANAGER_RESULT_SUCCESS;


public class AudioPlayActivity extends AudioCenterBaseActivity implements View.OnClickListener {
    private static final String TAG = "AudioPlayActivity";

    private AudioPlayerHandler mhandler = new AudioPlayerHandler();
    private static final String STATEFRAGMENTTAG = "playstatefragment";

    //for media player
    private MediaPlayerManager.MediaPlayerHandler mediaPlayerHandler;
    private PlayListToken mPlayListToken;

    private boolean needUpdatePlayProgress = true;
    private final String PERCENTAGE = "percentage";
    private TagDetail mTagDetail;
    private String eventType;

    private ImageWithText playorpause;
    private ImageWithText playNext;
    private ImageWithText playPre;
    private ImageWithText playList;

    private ImageWithText favIV;
    private ImageWithText playMode;

    private ImageView backIV;
    private TextView audioName;
    private SeekBar playSeeBar;
    private TextView currenttime;
    private TextView totaltime;
    private MediaPlayerManager mMediaPlayerManager;
    private ProgressBar mTopProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        setContentView(R.layout.audioenter_activity_play);
        initExtraData();
        initMediaPlayer();
        initView();
        initPlayToken();
    }

    //TODO
    private void initPlayToken() {
//        PlayListTokenFactory.getPlayListToken()
    }


    private void initExtraData() {
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


    private void initMediaPlayer() {
        mMediaPlayerManager = MediaPlayerManager.getInstance(this);
        mMediaPlayerManager.init(mMediaInitListener);
    }


    private void mediaPlayerInitComplete() {
        LogUtil.d(TAG, "serviceInitReady");
        mediaPlayerHandler = MediaPlayerManager.getInstance(AudioPlayActivity.this).geMediaPlayerHandler();
        mediaPlayerHandler.addMeidaPlayListener(mMediaPlayerPlayListener);
        mPlayListToken = PlayListTokenFactory.getPlayListToken(this, mTagDetail, mediaPlayerHandler);
        LogUtil.d(TAG, "serviceInitReady  mPlayListToken:" + mPlayListToken);

        mPlayListToken.addDataChangeListener(new PlayListToken.PlayDataChangeListTokenListener() {
            @Override
            public void onDataUpdate(int resultCode, PlayListItem item) {

                //TODO fail result code
                if (resultCode == PLAYLISTMANAGER_RESULT_SUCCESS && mPlayListToken.getCurrentPlayItem().getId() == item.getId()) {
                    updatePlayControlFavUI(item.getFav());
                }
            }

            @Override
            public void onGetData(int resultCode, List<PlayListItem> data) {
                LogUtil.d(TAG, "onGetData   :" + resultCode);

            }

            @Override
            public void onPlayStart(PlayListItem data) {
                mTopProgressBar.setVisibility(View.GONE);
                updatePlayControlFavUI(data.getFav());
            }

            @Override
            public void onPlayIndexChanged(int index) {
                LogUtil.d(TAG, "onPlayIndexChanged   :" + index);
            }
        });
        mPlayListToken.init();
    }

    private void initView() {
        mTopProgressBar = (ProgressBar) this.findViewById(R.id.top_progressbar);
        playNext = (ImageWithText) this.findViewById(R.id.image_play_next);
        playNext.setOnClickListener(this);
        playPre = (ImageWithText) this.findViewById(R.id.image_play_pre);
        playPre.setOnClickListener(this);
        playList = (ImageWithText) this.findViewById(R.id.image_play_list);
        playList.setOnClickListener(this);
        playorpause = (ImageWithText) this.findViewById(R.id.image_playorpause);
        playorpause.setOnClickListener(this);
        favIV = (ImageWithText) this.findViewById(R.id.image_fav);
        favIV.setOnClickListener(this);
        playMode = (ImageWithText) this.findViewById(R.id.image_play_mode);
        backIV = (ImageView) this.findViewById(R.id.iv_back);
        backIV.setOnClickListener(this);
        audioName = (TextView) this.findViewById(R.id.audio_name);
        audioName.setText(mTagDetail.getName());
        playSeeBar = (SeekBar) this.findViewById(R.id.play_seek);
        currenttime = (TextView) this.findViewById(R.id.tv_play_currenttime);
        totaltime = (TextView) this.findViewById(R.id.tv_play_totaltime);
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

    @Override
    public void onClick(View view) {
        LogUtil.i(TAG, "onClick:" + view.getId());

        switch (view.getId()) {
            case (R.id.image_play_next):
                playNext();
                break;
            case (R.id.image_play_pre):
                playPre();
                break;
            case (R.id.image_playorpause):
                playorpause();
                break;
            case (R.id.image_fav):
                setFav();
                break;
            case (R.id.iv_back):
                doBack();
                break;
            case (R.id.image_play_list):
                showPlayList();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onBackPressed() {
        //fav mode
        if (mPlayListToken instanceof FavPlayListToken) {
            EventBus.getDefault().post(new BackHomeEvent());
        }
        finish();
    }

    private void playPre() {
        LogUtil.d(TAG, "playPre");
        mPlayListToken.playPre();
    }

    private void playNext() {
        LogUtil.d(TAG, "playNext");
        mPlayListToken.playNext();
    }


    private void showPlayList() {
        LogUtil.d(TAG, "showPlayList");
        mPlayListToken.showPlayList();
    }


    //TODO for test switch   the fragment
    private void switchPlayMode() {

    }


    private void doBack() {
        //fav mode and speech command ,back to launcher
        if (mPlayListToken instanceof FavPlayListToken || Constants.TASKEVENT_TYPE_SPEECH.equals(eventType)) {
            EventBus.getDefault().post(new BackHomeEvent());
        }
        finish();
    }


    private void setFav() {
        LogUtil.d(TAG, "setFav ");
        mPlayListToken.updateFav2Server();

    }

    private void updatePlayControlFavUI(int fav) {
        favIV.setImageResource(fav == Constants.FAV ? R.drawable.control_fav : R.drawable.control_unfav);
    }


    private void playorpause() {
        LogUtil.d(TAG, "playorpause:");
        Message msg = new Message();
        msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_PAUSE;
        mhandler.sendMessage(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (mediaPlayerHandler != null) {
            mediaPlayerHandler.removeMeidaPlayListener(mMediaPlayerPlayListener);
            mediaPlayerHandler.clearPlayList();
        }
        mPlayListToken.release();
        mMediaPlayerManager.release();
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

    private IMediaPlayerInitListener mMediaInitListener = new IMediaPlayerInitListener.Stub() {

        @Override
        public void initComplete() throws RemoteException {
            LogUtil.d(TAG, "MediaPlayer initComplete ::");
            if (mhandler != null) {
                mhandler.isMediaInitComplete = true;
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_INIT_COMPLETE;
                mhandler.sendMessage(msg);
            }
        }
    };

    private MediaPlayerManager.MediaPlayerPlayListener mMediaPlayerPlayListener = new MediaPlayerManager.MediaPlayerPlayListener() {

        @Override
        public void onPlayStart() {
            LogUtil.i(TAG, "onPlayStart");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_START;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
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
        public void onPlayStop() {
            LogUtil.i(TAG, "onPlayStop");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_STOP;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onSoundPlayComplete() {
            LogUtil.i(TAG, "onSoundPlayComplete");

        }
    };

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
                    mediaPlayerInitComplete();
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
                        mediaPlayerHandler.pause();
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
