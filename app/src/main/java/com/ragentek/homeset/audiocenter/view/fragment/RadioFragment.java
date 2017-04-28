package com.ragentek.homeset.audiocenter.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ragentek.homeset.audiocenter.IAudioControl;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.RadioVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/14.
 * for  the  category of radio;
 */

public class RadioFragment extends PlayBaseFragment<RadioVO, RadioFragment.RadioAudioControl> {
    private static final String TAG = "MusicFragment";
    private int currentPlayIndex = 0;

    @BindView(R.id.tv_radio_name)
    TextView radioNameTV;


    @BindView(R.id.image_radio_album)
    SimpleDraweeView mSimpleDraweeView;

    @BindView(R.id.progress_music_load)
    ProgressBar mProgressBar;

    public static RadioFragment newInstances() {
        RadioFragment fragment = new RadioFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audioenter_fragment_radio_detail, container, false);
        ButterKnife.bind(this, view);
        updateView();
        return view;
    }

    private void updateView() {
        if (currentRadioVO != null) {
            radioNameTV.setText(currentRadioVO.getName());
            updateAlbumart();
        } else {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);

        }
    }


    private void updateAlbumart() {
        if (currentRadioVO.getCover_url() == null) {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(currentRadioVO.getCover_url()));
        }
    }

    private RadioVO currentRadioVO;

    @Override
    void onDataChanged(int resultCode, RadioVO data) {
        currentRadioVO = data;
    }

    public class RadioAudioControl implements IAudioControl {


        @Override
        public void setDataChangerListener(PlayBaseFragment.IAudioDataChangerListener listener) {
        }

    }
}
