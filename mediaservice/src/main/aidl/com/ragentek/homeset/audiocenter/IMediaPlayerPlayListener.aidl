// IMediaPlayerListener.aidl
package com.ragentek.homeset.audiocenter;

// Declare any non-default types here with import statements

interface IMediaPlayerPlayListener {

     void onSoundPrepared();

        void onPlayStart();

        void onPlayProgress(int currPos, int duration);

        void onPlayStop();

        void onSoundPlayComplete();
}