package com.example.neteasemusic.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.example.neteasemusic.R;


public class SimpleVideoView extends RelativeLayout implements View.OnClickListener {
    private View mView;
    private VideoView mVideoView;//视频控件
    private ImageView mBigPlayBtn;//大的播放按钮
    private ImageView mPlayBtn;//播放按钮
    private SeekBar mPlayProgressBar;//播放进度条
    private TextView mPlayTime;//播放时间

    private LinearLayout mControlPanel;
    private RelativeLayout mRelativeLayout;
    private TextView mBigTitle;//大标题
    private ImageView mHeadImg;//头像
    private TextView mUser;//用户
    private ImageView mFinger;//点赞按钮
    private TextView mFingerNumber;//点赞数

    private Uri mVideoUri = null;

    private Animation outAnima;//控制面板出入动画
    private Animation inAnima;//控制面板出入动画

    private int mVideoDuration;//视频毫秒数
    private int mCurrentProgress;//毫秒数
    private int mIntFingerNumber;

    private Runnable mUpdateTask;
    private Thread mUpdateThread;

    private final int UPDATE_PROGRESS = 0;
    private final int EXIT_CONTROL_PANEL = 1;
    private boolean stopThread = true;//停止更新进度线程标志
    private boolean fingered = false;//判断点赞标志
    private static SimpleVideoView This;//全局变量用以实现单视频播放

    public SimpleVideoView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    mPlayProgressBar.setProgress(mCurrentProgress);
                    setPlayTime(mCurrentProgress);
                    break;
                case EXIT_CONTROL_PANEL:
                    //执行退出动画
                    if (mControlPanel.getVisibility() != View.GONE) {
                        mControlPanel.startAnimation(outAnima);
                        mControlPanel.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    //初始化控件
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mView = LayoutInflater.from(context).inflate(R.layout.video, this);
        mBigPlayBtn = mView.findViewById(R.id.big_play_button);
        mPlayBtn = mView.findViewById(R.id.play_button);
        mPlayProgressBar = mView.findViewById(R.id.progress_bar);
        mPlayTime = mView.findViewById(R.id.time);
        mControlPanel = mView.findViewById(R.id.control_panel);
        mVideoView = mView.findViewById(R.id.video_view);
        mBigTitle = mView.findViewById(R.id.bigTitle);
        mHeadImg = mView.findViewById(R.id.headImg);
        mUser = mView.findViewById(R.id.user);
        mFinger = mView.findViewById(R.id.finger);
        mFingerNumber = mView.findViewById(R.id.fingerNumber);
        mRelativeLayout = mView.findViewById(R.id.re_layout_video);

        //加载动画
        outAnima = AnimationUtils.loadAnimation(context, R.anim.exit_from_bottom);
        inAnima = AnimationUtils.loadAnimation(context, R.anim.enter_from_bottom);

        //设置控制面板初始不可见
        mControlPanel.setVisibility(View.GONE);
        //设置大的播放按钮可见
        mBigPlayBtn.setVisibility(View.VISIBLE);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //视频加载完成后才能获取视频时长
                initVideo();
            }
        });
        //视频播放完成监听器
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mPlayBtn.setImageResource(R.drawable.stopping1);
                mVideoView.seekTo(0);
                mPlayProgressBar.setProgress(0);
                setPlayTime(0);
                stopThread = true;
                sendHideControlPanelMessage();
            }
        });
        mView.setOnClickListener(this);
    }

    //初始化视频，设置视频时间和进度条最大值
    private void initVideo() {
        //初始化时间和进度条
        mVideoDuration = mVideoView.getDuration();//毫秒数
        int seconds = mVideoDuration / 1000;
        mPlayTime.setText("00:00/" +
                ((seconds / 60 > 9) ? (seconds / 60) : ("0" + seconds / 60)) + ":" +
                ((seconds % 60 > 9) ? (seconds % 60) : ("0" + seconds % 60)));
        mPlayProgressBar.setMax(mVideoDuration);
        mPlayProgressBar.setProgress(0);
        //更新进度条和时间任务
        mUpdateTask = new Runnable() {
            @Override
            public void run() {
                while (!stopThread) {
                    mCurrentProgress = mVideoView.getCurrentPosition();
                    handler.sendEmptyMessage(UPDATE_PROGRESS);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //设置监听
        mBigPlayBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mRelativeLayout.setOnClickListener(this);
        mFinger.setOnClickListener(this);

        //进度条进度改变监听器
        mPlayProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mVideoView.seekTo(i);//设置视频
                    setPlayTime(i);//设置时间
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(EXIT_CONTROL_PANEL);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(EXIT_CONTROL_PANEL, 3000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mRelativeLayout) {
            if (mBigPlayBtn.getVisibility() == View.VISIBLE) {
                return;
            }
            if (mControlPanel.getVisibility() == View.VISIBLE) {
                //执行退出动画
                mControlPanel.startAnimation(outAnima);
                mControlPanel.setVisibility(View.GONE);
            } else {
                //执行进入动画
                mControlPanel.startAnimation(inAnima);
                mControlPanel.setVisibility(View.VISIBLE);
                sendHideControlPanelMessage();
            }
        } else if (v.getId() == R.id.big_play_button) {//大的播放按钮
            this.check();
            mBigPlayBtn.setVisibility(View.GONE);
            mVideoView.setBackground(null);
            if (!IsPlaying()) {
                mVideoView.start();
                mPlayBtn.setImageResource(R.drawable.starting1);
                //开始更新进度线程
                mUpdateThread = new Thread(mUpdateTask);
                stopThread = false;
                mUpdateThread.start();
            }
        } else if (v.getId() == R.id.play_button) {//播放/暂停按钮
            this.check();
            if (IsPlaying()) {
                mVideoView.pause();
                mPlayBtn.setImageResource(R.drawable.stopping1);
            } else {
                if (mUpdateThread == null || !mUpdateThread.isAlive()) {
                    //开始更新进度线程
                    mUpdateThread = new Thread(mUpdateTask);
                    stopThread = false;
                    mUpdateThread.start();
                }
                mVideoView.start();
                mPlayBtn.setImageResource(R.drawable.starting1);
            }
            sendHideControlPanelMessage();
        } else if (v.getId() == R.id.finger) {
            changeFingerState();
        }
    }

    //设置点赞数
    public void setFingerNumber(int fingerNumber) {
        mFingerNumber.setText(fingerNumber + "");
    }


    //点赞状态改变
    private void changeFingerState() {
        if (fingered == false) {
            mIntFingerNumber = Integer.parseInt(mFingerNumber.getText().toString());
            mIntFingerNumber++;
            mFingerNumber.setText(mIntFingerNumber + "");
            mFinger.setImageResource(R.drawable.video_zan1);
            fingered = true;
        } else {
            mIntFingerNumber = Integer.parseInt(mFingerNumber.getText().toString());
            mIntFingerNumber--;
            mFingerNumber.setText(mIntFingerNumber + "");
            mFinger.setImageResource(R.drawable.video_zan);
            fingered = false;
        }

    }

    //设置当前时间
    private void setPlayTime(int millisSecond) {
        int currentSecond = millisSecond / 1000;
        String currentTime = ((currentSecond / 60 > 9) ? (currentSecond / 60 + "") : ("0" + currentSecond / 60)) + ":" +
                ((currentSecond % 60 > 9) ? (currentSecond % 60 + "") : ("0" + currentSecond % 60));
        StringBuilder text = new StringBuilder(mPlayTime.getText().toString());
        text.replace(0, text.indexOf("/"), currentTime);
        mPlayTime.setText(text);
    }

    //设置仅有单个视频播放
    private synchronized void check() {
        if (This == null) {
            This = this;
            return;
        }
        if (This == this) {
            return;
        }
        This.setVideoPause();
        This = this;
    }

    //两秒后隐藏控制面板
    private void sendHideControlPanelMessage() {
        handler.removeMessages(EXIT_CONTROL_PANEL);
        handler.sendEmptyMessageDelayed(EXIT_CONTROL_PANEL, 3000);
    }

    //设置视频路径
    public void setVideoUri(Uri uri) {
        this.mVideoUri = uri;
        mVideoView.setVideoURI(mVideoUri);
    }

    //设置大标题
    public void setBigVideoTitle(String title) {
        mBigTitle.setText(title);
    }

    //设置头像
    public void setHeadImg(Drawable d) {
        mHeadImg.setBackground(d);
    }

    //设置用户名
    public void setUser(String user) {
        mUser.setText(user);
    }

    //获取视频路径
    public Uri getVideoUri() {
        return mVideoUri;
    }

    //设置视频初始画面
    public void setInitPicture(Drawable d) {
        mVideoView.setBackground(d);
    }

    //挂起视频
    public void suspend() {
        if (mVideoView != null) {
            mVideoView.suspend();
        }
    }

    //暂停视频
    public void setVideoPause() {
        mVideoView.pause();
        mBigPlayBtn.setVisibility(View.VISIBLE);
        mPlayBtn.setImageResource(R.drawable.stopping1);
    }

    //获取视频进度
    public int getVideoProgress() {
        return mVideoView.getCurrentPosition();
    }

    //判断视频是否正在播放
    public boolean IsPlaying() {
        return mVideoView.isPlaying();
    }

}