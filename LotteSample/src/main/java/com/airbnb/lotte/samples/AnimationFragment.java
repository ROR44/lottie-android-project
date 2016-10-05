package com.airbnb.lotte.samples;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.airbnb.lotte.LotteAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class AnimationFragment extends Fragment {
    private static final String ARG_FILE_NAME = "file_name";

    static AnimationFragment newInstance(String fileName) {
        AnimationFragment frag = new AnimationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_NAME, fileName);
        frag.setArguments(args);
        return frag;
    }

    @BindView(R.id.animation_view) LotteAnimationView animationView;
    @BindView(R.id.seek_bar) AppCompatSeekBar seekBar;
    @BindView(R.id.loop_button) ToggleButton loopButton;
    @BindView(R.id.frames_per_second) TextView fpsView;
    @BindView(R.id.dropped_frames) TextView droppedFramesView;
    @BindView(R.id.dropped_frames_per_second) TextView droppedFramesPerSecondView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animation, container, false);
        ButterKnife.bind(this, view);

        String fileName = getArguments().getString(ARG_FILE_NAME);
        animationView.setAnimation(fileName);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                startRecordingDroppedFrames();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                recordDroppedFrames();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
                recordDroppedFrames();
                startRecordingDroppedFrames();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                animationView.setProgress(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    @Override
    public void onStop() {
        animationView.cancelAnimation();
        super.onStop();
    }

    @OnClick(R.id.play)
    public void onPlayClicked() {
        animationView.play();
    }

    @OnCheckedChanged(R.id.loop_button)
    public void onLoopChanged(boolean loop) {
        animationView.loop(loop);
    }

    private void startRecordingDroppedFrames() {
        getApplication().startRecordingDroppedFrames();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void recordDroppedFrames() {
        Pair<Integer, Long> droppedFrames = getApplication().stopRecordingDroppedFrames();
        int targetFrames = (int) ((droppedFrames.second / 1000000000f) * animationView.getFrameRate());
        int actualFrames = targetFrames - droppedFrames.first;
        fpsView.setText(String.format("Fps: %.0f", actualFrames / (animationView.getDuration() / 1000f)));
        droppedFramesView.setText("Dropped frames: " + droppedFrames.first);
        float droppedFps = droppedFrames.first / (droppedFrames.second / 1000000000f);
        droppedFramesPerSecondView.setText(String.format("Dropped frames per second: %.0f", droppedFps));
    }

    private ILotteApplication getApplication() {
        return (ILotteApplication) getActivity().getApplication();
    }
}
