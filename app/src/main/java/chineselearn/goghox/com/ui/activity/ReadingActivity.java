package chineselearn.goghox.com.ui.activity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import chineselearn.goghox.com.bean.CourseBean;
import chineselearn.goghox.com.R;
import chineselearn.goghox.com.bean.ArticleBean;
import chineselearn.goghox.com.global.Server;

/**
 * Created by GogHox on 2018/2/20.
 */

public class ReadingActivity extends AppCompatActivity {
    @BindView(R.id.tv_show) TextView tvShow;
    @BindView(R.id.ib_read) ImageButton ibRead;
    @BindView(R.id.ib_play) ImageButton ibPlay;
    @BindView(R.id.ib_play_music) ImageButton ibPlayMusic;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iv_top) ImageView ivTop;

    private String TAG = "ReadingActivity";
    private String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private boolean mStartRecord = false;
    private boolean mStartPlay   = false;
    private boolean mStartPlayMusic = false;
    private ArticleBean articleBean;
    private CourseBean courseBean;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:  // stop play on finish play
                    if(mPlayer != null) {
                        stopPlaying();
                        ibPlay.setBackgroundResource(R.drawable.play);
                        mStartPlay = !mStartPlay;
                    }
                    break;
            }
            return false;
        }
    });

    private SpeechSynthesizer mTts;         // 语音合成对象
    private String voicer = "xiaoqi";      // 默认发音人
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue ;

    private int mPercentForBuffering = 0;  // 缓冲进度
    private int mPercentForPlaying = 0;    // 播放进度

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private Integer mCourseContentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseContentId = Integer.decode(getIntent().getAction());

        setContentView(R.layout.activity_reading);
        ButterKnife.bind(this);

        initToolbar();

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);
        // 设置语音合成参数
        setParam();
        getDataFromServer();

    }

    private void getDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String courseContent = Server.run(Server.host + "/get_course_content?id=" + mCourseContentId);
                    articleBean = new Gson().fromJson(new JSONArray(courseContent).getJSONObject(0).toString(), ArticleBean.class);
                    String course = Server.run(Server.host + "/get_course?course_content_id=" + mCourseContentId);
                    courseBean = new Gson().fromJson(new JSONArray(course).getJSONObject(0).toString(), CourseBean.class);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ReadingActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(this, R.style.TextAppearance_AppCompat_Subhead_Inverse);
        // 更改图片
        //ivTop.setImageResource(courseBean.imgRes);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        // 显示文章内容
        getSupportActionBar().setTitle(courseBean.getTitle());   // TODO toolbar
        tvShow.setText(articleBean.getContent());
        // 设置图片
        Glide.with(this).load(courseBean.getImg_url()).into(ivTop);
        if(articleBean.getContent() == "")
            tvShow.setText("该文章收费，请先付款，或选择其他文章进行阅读！");

    }

    @OnClick(R.id.ib_play_music)
    public void computerRead(View view) {
        if(!mStartPlayMusic){  // 需要开始播放
            ibPlayMusic.setBackgroundResource(R.drawable.pause);
            mTts.startSpeaking(articleBean.getContent(), mTtsListener);  // 开始合成
            // mTts.resumeSpeaking();  // 继续播放
        }else{            // 需要停止播放
            ibPlayMusic.setBackgroundResource(R.drawable.play);
            mTts.stopSpeaking();  // 停止合成
            // mTts.pauseSpeaking();  // 暂停播放
        }
        mStartPlayMusic = !mStartPlayMusic;
    }

    @OnClick(R.id.ib_play)
    public void onPlay(View view) {
        if(!mStartPlay){  // 需要开始播放
            startPlaying();
            ibPlay.setBackgroundResource(R.drawable.pause);
        }else{            // 需要停止播放
            stopPlaying();
            ibPlay.setBackgroundResource(R.drawable.play);
        }
        mStartPlay = !mStartPlay;
    }

    /**
     * 当点击录音按钮时调用
     */
    @OnClick(R.id.ib_read)
    public void onRecord(View view) {
        if(!mStartRecord){  // 需要开始录音
            startRecording();
            ibRead.setBackgroundResource(R.drawable.pause);
        }else{              // 需要停止录音
            stopRecording();
            ibRead.setBackgroundResource(R.drawable.play);
        }
        mStartRecord = !mStartRecord;  // 改变状态
    }

    /**
     * 开始录音
     * */
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "startRecording: ", e);
        }
        mRecorder.start();
    }

    /**
     * 录音完成
     */
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    /**
     * 开始播放录音
     */
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            int duration = mPlayer.getDuration();
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, duration);

        } catch (IOException e) {Log.e(TAG, "prepare() failed");}
    }

    /**
     * 停止播放录音
     */
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null)
            mRecorder.release(); mRecorder = null;
        if (mPlayer != null)
            mPlayer.release(); mPlayer = null;
    }

    /**
     * 参数设置
     * @return
     */
    private void setParam(){
        // 清空参数
        //mTts.setParameter(SpeechConstant.PARAMS, "");
        // 根据合成引擎设置相应参数
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);    // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.SPEED, "68");        // 设置合成语速
            mTts.setParameter(SpeechConstant.PITCH, "50");        // 设置合成音调
            mTts.setParameter(SpeechConstant.VOLUME, "50");       // 设置合成音量
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");            //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");  // 设置播放合成音频打断音乐播放，默认为true
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override public void onSpeakBegin() {}
        @Override public void onSpeakPaused() {}
        @Override public void onSpeakResumed() {}
        @Override public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            mPercentForBuffering = percent;  // 合成进度
        }
        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            mPercentForPlaying = percent;   // 播放进度
        }
        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                ibPlayMusic.setBackgroundResource(R.drawable.play);
                mStartPlayMusic = !mStartPlayMusic;
            }
        }
    };
    @Override
    protected void onDestroy() {
        mTts.stopSpeaking();  // 停止合成
        super.onDestroy();
    }
}
