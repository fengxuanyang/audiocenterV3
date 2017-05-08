package com.ragentek.homeset.core.task.foreground;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.RemoteException;

import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.base.SpeechEngine;
import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.ForegroundTask;
import com.ragentek.homeset.core.task.TaskManager;
import com.ragentek.homeset.core.task.event.SpeechEvent;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.core.utils.PlayerUtils;
import com.ragentek.homeset.speech.IRecognitionListener;
import com.ragentek.homeset.speech.ISpeechRecognitionClient;
import com.ragentek.homeset.speech.ISpeechSynthesizerClient;
import com.ragentek.homeset.speech.ISynthesizerListener;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;
import com.ragentek.homeset.speech.domain.SpeechCommonQADomain;
import com.ragentek.homeset.speech.domain.SpeechDomainUtils;
import com.ragentek.homeset.speech.domain.SpeechWeatherDomain;
import com.ragentek.homeset.ui.speech.SpeechActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

public class SpeechTask extends ForegroundTask {
    private static final String TAG = SpeechTask.class.getSimpleName();
    private static final boolean DEBUG = true;

    private final static String AUDIO_PATH = "audio/";
    private final static String AUDIO_WAIT = AUDIO_PATH + "tone_wait_ogg.jet";
    private final static String AUDIO_START = AUDIO_PATH + "tone_start_ogg.jet";
    private final static String AUDIO_ERROR = AUDIO_PATH + "tone_error_ogg.jet";
    private final static String AUDIO_RESULT = AUDIO_PATH + "tone_result_ogg.jet";

    private BaseContext mBaseContext;
    private Context mContext;
    private PlayerUtils mPlayer;
    private EventCallBack mCallback;

    private TaskManager mTaskManager = null;
    private ISpeechSynthesizerClient mSynthesizerClient = null;
    private ISpeechRecognitionClient mRecognitionClient = null;
    private IRecognitionListener mRecognitionListener = null;

    public interface EventCallBack {
        void onSpeechEvent(SpeechEvent event);
        void onFinish();
    }

    public class NullEventCallBack implements EventCallBack {
        @Override
        public void onSpeechEvent(SpeechEvent event) {}
        @Override
        public void onFinish() {}
    }

    public SpeechTask(BaseContext baseContext, OnFinishListener listener) {
        super(baseContext, listener);
        mBaseContext = baseContext;
        mContext = baseContext.getAndroidContext();
        mPlayer = new PlayerUtils(mContext);
        mCallback = new NullEventCallBack();

        SpeechEngine speechEngine = (SpeechEngine) mBaseContext.getEngine(EngineManager.ENGINE_SPEECH);
        mSynthesizerClient = speechEngine.getSynthesizerClient();
        mRecognitionClient = speechEngine.getRecognitionClient();
        mRecognitionListener = new RecognizeListener();
    }

    public void setEventCallback(EventCallBack callback) {
        if(callback == null) {
            return;
        }

        mCallback = callback;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().postSticky(this);
    }

    @Override
    protected void onStartCommand(TaskEvent event) {
        if (event != null && event.getType() != TaskEvent.TYPE.SYSTEM) {
            finish();
            return;
        }

        mTaskManager = (TaskManager) event.getData();
        startRecognitionActivity();
    }

    private void startRecognitionActivity() {
        Intent intent = new Intent(mContext, SpeechActivity.class);
        intent.putExtra(SpeechActivity.EXTRA_TYPE, SpeechActivity.TYPE_ASR);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void startRecognition() {
        mPlayer.playAssetsFile(AUDIO_START, false, new PlayerUtils.PlayerListener() {
            @Override
            public void onPlayComplete() {
                try {
                    stopSpeak();
                    mRecognitionClient.startRecognize(mRecognitionListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class RecognizeListener extends IRecognitionListener.Stub {

        @Override
        public void onBeginOfSpeech() throws RemoteException {
            mCallback.onSpeechEvent(new SpeechEvent(SpeechEvent.ASR_SPEECH_BEGIN, null));
        }

        @Override
        public void onEndOfSpeech() throws RemoteException {
            mCallback.onSpeechEvent(new SpeechEvent(SpeechEvent.ASR_SPEECH_END, null));
        }

        @Override
        public void onVolumeChanged(int value) throws RemoteException {
            mCallback.onSpeechEvent(new SpeechEvent(SpeechEvent.ASR_VOLUME_CHANGE, value));
        }

        @Override
        public void onResult(String result) throws RemoteException {
            mCallback.onSpeechEvent(new SpeechEvent(SpeechEvent.ASR_RESULT, result));
            handleResult(result);
        }

        @Override
        public void onError(int errCode, String message) throws RemoteException {
            mCallback.onSpeechEvent(new SpeechEvent(SpeechEvent.ASR_ERROR, null));

            if (DEBUG) {
                LogUtils.d(TAG, "onError, errCode=" + errCode + ", message=" + message);
            }

            handleResult("{}");
        }
    }

    private void handleResult(String result) {
        SpeechBaseDomain speechDomain = SpeechDomainUtils.parseResult(result);
        if (DEBUG) {
            LogUtils.d(TAG, "handleResult, result=" + result);
            LogUtils.d(TAG, "handleResult, speechDomain=" + speechDomain.toString());
        }

        mTaskManager.sendSpeechEvent(speechDomain);
    }


    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    protected boolean onSpeechEvent(SpeechBaseDomain speechDomain) {
        if (!handleSpeechEvent(speechDomain)) {
            handleNoResult(speechDomain);
        }

        return true;
    }

    private boolean handleSpeechEvent(SpeechBaseDomain speechDomain) {
        switch (SpeechDomainUtils.getDomainType(speechDomain)) {
            case WEATHER:
                handleWeather(speechDomain);
                return true;
            case CALC:
            case DATETIME:
            case BAIKE:
            case CHAT:
            case OPENQA:
                handleCommonQA((SpeechCommonQADomain) speechDomain);
                return true;
        }

        return false;
    }

    private void handleWeather(SpeechBaseDomain domain) {
        SpeechWeatherDomain weatherDomain = (SpeechWeatherDomain)domain;
        String city = getCity(weatherDomain);
        String day = getDay(weatherDomain);
        String weather = getWeather(weatherDomain);

        if (city ==null) {
            speakString(mContext.getString(R.string.weather_not_found_city));
            return;
        }

        if (day == null || weather == null) {
            speakString(mContext.getString(R.string.weather_not_found_day));
            return;
        }

        String result = city + ", " + day + ", " + weather;
        startSpeakActivity(result);
    }

    private String getCity(SpeechWeatherDomain weatherDomain) {
        if (weatherDomain.semantic.slots.location.city.isEmpty()) {
            return null;
        }

        SpeechWeatherDomain.Result result = weatherDomain.data.result.get(0);
        if (result.city.isEmpty()) {
            return null;
        }

        return result.city;
    }

    private String getDay(SpeechWeatherDomain weatherDomain) {
        String date = weatherDomain.semantic.slots.datetime.date;
        String dateOrig = weatherDomain.semantic.slots.datetime.dateOrig;
        String timeOrig = weatherDomain.semantic.slots.datetime.timeOrig;

        if (date.isEmpty() || date.equals(SpeechWeatherDomain.CURRENT_DAY)) {
            return mContext.getResources().getString(R.string.today);
        }

        if (timeOrig.isEmpty()) {
            timeOrig = mContext.getString(R.string.day_night);
        }

        return dateOrig + timeOrig;
    }

    private String getWeather(SpeechWeatherDomain weatherDomain) {
        try {
            String date = weatherDomain.semantic.slots.datetime.date;
            ArrayList<SpeechWeatherDomain.Result> results =  weatherDomain.data.result;

            if (date.isEmpty() || date.equals(SpeechWeatherDomain.CURRENT_DAY)) {
                return compositeWeather(results.get(0));
            } else {
                for (SpeechWeatherDomain.Result item: results) {
                    if (item.date.equals(date)) {
                        return compositeWeather(item);
                    }
                }
            }
        } catch (Exception e) {}

        return null;
    }

    private String compositeWeather(SpeechWeatherDomain.Result result) {
        return result.weather + ", " + result.tempRange + ", " + result.wind;
    }


    private void handleCommonQA(SpeechCommonQADomain domain) {
        startSpeakActivity(domain.answer.text);
    }

    private void handleNoResult(SpeechBaseDomain speechDomain) {
        Resources res = mContext.getResources();
        String[] speechTips = res.getStringArray(R.array.speech_result_tips);

        Random rm = new Random(System.currentTimeMillis());
        int index = rm.nextInt(speechTips.length);
        String speakStr = speechTips[index];

        startSpeakActivity(speakStr);
    }


    private void startSpeakActivity(String text) {
        Intent intent = new Intent(mContext, SpeechActivity.class);
        intent.putExtra(SpeechActivity.EXTRA_TYPE, SpeechActivity.TYPE_SPEAK);
        intent.putExtra(SpeechActivity.EXTRA_SPEAK, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void speakString(String text) {
        try {
            mSynthesizerClient.startSpeak(text, new SynthesizerListener());
        } catch (RemoteException e) {
            finish();
        }
    }

    private class SynthesizerListener extends ISynthesizerListener.Stub {

        @Override
        public void onSpeakBegin() throws RemoteException {
            mCallback.onSpeechEvent(new SpeechEvent(SpeechEvent.SPEAK_START, null));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) throws RemoteException {
        }

        @Override
        public void onCompleted(int errorCode, String message) throws RemoteException {
            mCallback.onSpeechEvent(new SpeechEvent(SpeechEvent.SPEAK_END, null));
            finish();
        }
    }

    @Override
    protected void onStop() {
        stopSpeak();
        mCallback.onFinish();
    }

    private void stopSpeak() {
        LogUtils.d(TAG, "stopSpeak");
        try {
            mSynthesizerClient.stopSpeak();
        } catch (RemoteException e) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().removeStickyEvent(SpeechTask.class);
        mPlayer.onDestroy();
    }
}
