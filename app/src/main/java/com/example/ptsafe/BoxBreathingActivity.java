package com.example.ptsafe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BoxBreathingActivity extends AppCompatActivity {
    @BindView(R.id.tv_circle)
    TextView tvCircle;
    @BindView(R.id.tv_circle_txt)
    TextView tvCircleTxt;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.tv_four)
    TextView tvFour;
    @BindView(R.id.ll_show)
    LinearLayout llShow;
    @BindView(R.id.ll)
    LinearLayout ll;

    private int SHOW = 0;
    private int STATE = 0;
    boolean start = true;
    /**
     * countdown marker
     */
    public static final int COUNTDOWN_TIME_CODE = 99999;
    /**
     * countdown interval
     */
    public static final int DELAY_MILLIS = 1000;
    /**
     * countdown maximum
     */
    public int MAX_COUNT = 5;

    private CountDownTimer timer;
    private long timeStamp = 8640000;
    private boolean end = false;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_breathing);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.bensound_relaxing);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        ButterKnife.bind(this);

        timer = new CountDownTimer(60000*3, 1000) {
            public void onTick(long millisUntilFinished) {
                long secend = millisUntilFinished / 1000 / 60;
                if ((millisUntilFinished - secend * 60 * 1000) / 1000 < 10) {
                    tvAll.setText("0" + secend + ":" + "0"+(millisUntilFinished - secend * 60 * 1000) / 1000 + "");
                }
                else {
                    tvAll.setText("0" + secend + ":" + (millisUntilFinished - secend * 60 * 1000) / 1000 + "");
                }
            }

            public void onFinish() {
                end = true;
                tvCircle.setText("");
                tvCircleTxt.setText("");
                ll.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "well done", Toast.LENGTH_SHORT).show();
            }
        };
        llShow.setVisibility(View.GONE);
        //create a handler
        CountdownTimeHandler handler = new CountdownTimeHandler(this);
        //Create a new message
        Message message = Message.obtain();
        message.what = COUNTDOWN_TIME_CODE;
        message.arg1 = MAX_COUNT;
        //Send a message for the first time
        handler.sendMessageDelayed(message, DELAY_MILLIS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    private int current = 1;
    private int ACTION = 0;
    private int ACTION_NUMBER = 0;
    private boolean ACTION_BOOL = false;
    private boolean ACTION_BOOLs = false;

    public static class CountdownTimeHandler extends Handler {
        /**
         * countdown minimum
         */
        public static int MIN_COUNT = 0;
        //Create a weak reference to MainActivity
        final WeakReference<BoxBreathingActivity> mWeakReference;

        public CountdownTimeHandler(BoxBreathingActivity activity) {
            this.mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Get weak reference to MainActivity
            BoxBreathingActivity activity = mWeakReference.get();
            switch (msg.what) {
                case COUNTDOWN_TIME_CODE:
                    int value = msg.arg1;
                    //Control of sending messages cyclically
                    if (value >= MIN_COUNT) {
                        if (activity.SHOW > 1) {
                            if (activity.start) {
                                activity.start = false;
                                activity.timer.start();
                            }
                            activity.tvCircle.setText(String.valueOf(value));
                        }
                        value--;
                        Message message = Message.obtain();
                        message.what = COUNTDOWN_TIME_CODE;
                        message.arg1 = value;
                        sendMessageDelayed(message, DELAY_MILLIS);
                    } else {
                        activity.tvCircle.setText("");
                        Message message = Message.obtain();
                        message.what = COUNTDOWN_TIME_CODE;
                        message.arg1 = activity.MAX_COUNT;
                        sendMessageDelayed(message, DELAY_MILLIS);
                        activity.SHOW += 1;
                        activity.STATE += 1;
                    }
                    if (activity.SHOW == 0) {
                        activity.tvCircleTxt.setText("Relax and get comfortable");

                    } else if (activity.SHOW == 1) {
                        activity.tvCircleTxt.setText("focus on your breathing");

                    } else {
                        if (activity.STATE == 2 || ((activity.STATE - 2) % 3 == 0)) {
                            if (!activity.end) {
                                activity.tvCircleTxt.setText("breathe in ");
                            }
                            activity.MAX_COUNT = 2;
                        }
                        else if ((activity.STATE % 3 == 0)) {
                            activity.tvOne.setBackgroundResource(R.drawable.text_tt);
                            activity.tvTwo.setBackgroundResource(R.drawable.text_tt);
                            activity.tvThree.setBackgroundResource(R.drawable.text_tt);
                            activity.tvFour.setBackgroundResource(R.drawable.text_tt);
                            activity.llShow.setVisibility(View.VISIBLE);
                            if (activity.current == 1) {
                                activity.tvCircle.setText("");
                                if (!activity.end){
                                    activity.tvCircleTxt.setText("hold");
                                }
                                activity.tvOne.setBackgroundResource(R.drawable.txt_bg);
                                activity.current++;
                            } else if (activity.current == 2) {
                                activity.tvCircle.setText("");
                                if (!activity.end){
                                    activity.tvCircleTxt.setText("hold");
                                }
                                activity.tvTwo.setBackgroundResource(R.drawable.txt_bg);
                                activity.current++;
                            } else if (activity.current == 3) {
                                activity.tvCircle.setText("");
                                if (!activity.end){
                                    activity.tvCircleTxt.setText("hold");
                                }
                                activity.tvThree.setBackgroundResource(R.drawable.txt_bg);
                                activity.current++;
                            } else if (activity.current == 4) {
                                activity.tvCircle.setText("");
                                activity.tvFour.setBackgroundResource(R.drawable.txt_bg);
                                if (!activity.end){
                                    activity.tvCircleTxt.setText("hold");
                                }
                                activity.current = 1;
                                activity.MAX_COUNT = 5;
                            }
                        }
                        else {
                            activity.tvOne.setBackgroundResource(R.drawable.text_tt);
                            activity.tvTwo.setBackgroundResource(R.drawable.text_tt);
                            activity.tvThree.setBackgroundResource(R.drawable.text_tt);
                            activity.tvFour.setBackgroundResource(R.drawable.text_tt);
                            activity.llShow.setVisibility(View.INVISIBLE);
                            if (!activity.end){
                                activity.tvCircleTxt.setText("breathe out");
                            }
                        }
                    }
                    break;
            }
        }
    }
}