package chineselearn.goghox.com.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import chineselearn.goghox.com.R;
import chineselearn.goghox.com.adapter.MyRecyclerAdapter;
import chineselearn.goghox.com.global.AppData;
import chineselearn.goghox.com.global.FucUtil;
import chineselearn.goghox.com.global.JsonParser;
import chineselearn.goghox.com.global.Server;
import chineselearn.goghox.com.ui.view.MyDividerItemDecoration;

/**
 * Created by GogHox on 2018/3/6.
 */

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvSingle;
    private Toolbar toolbar;
    private TextView tvInfo;
    private TextView tvScore;
    private Toast mToast;
    private String TAG = "TestActivity";
    private SpeechRecognizer mAsr;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private int ret;
    private int mType;
    private TextView tvResult;
    private TextView tvTime;
    @BindView(R.id.btn_start_recognize) Button btnStartRecognize;
    private int mAlreadyFinishWord = 0;
    private int mScore = 0;
    public static final String PAGE_TAG = "type";
    public static final int FIRST_PAGE = 1;
    public static final int SECOND_PAGE = 2;
    public static final int THREE_PAGE = 3;
    Handler mHandler = new Handler();

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };
    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                String text = JsonParser.parseGrammarResult(result.getResultString());
                // 显示
                judgmentResult(text);
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip("onError Code：" + error.getErrorCode());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

    };
    private char[] mNoneWords = new char[]{'，', '。', '？', '、', '：', '“', '”', '！', ',', '.', '"', ':', '!'};
    private MyRecyclerAdapter mRecyclerAdapter;

    /**
     * 判断说的结果对错
     *
     * @param text 说的字
     */
    private void judgmentResult(String text) {
        if (mType == FIRST_PAGE) {  // 单字测试判断对错
            SingleWordJudgment(text);
        }
        // 词语判断
        else if (mType == SECOND_PAGE) {
            doubleWordJudgment(text);
        }
        else if(mType == 3){
            FourWordJudgment(text);
        }
    }

    private void FourWordJudgment(String text) {
        if (mAlreadyFinishWord >= AppData.moreWordTest.size()){  // 说了不止4个字
            showTip("已测试完成!");
            return;
        }
        String moreWord = new String();
        // 将读的多组文字组成一组文字 （因为科大讯飞API会自动将文字分割开）
        boolean isWord = true;
        for (char item : text.toCharArray()) {
            isWord = true;
            // 判断text是个字，并组成字符串
            for (char noneWord : mNoneWords) {
                if (item == noneWord) {
                    isWord = false;
                    break;
                }
            }
            if(isWord) moreWord += item;
        }

        if (moreWord.length() != 4) {
            showTip("请重新朗读");
            return;
        }
        tvResult.setTextColor(Color.RED);
        // 比较对错
        try {
            String requireWord = PinyinHelper.convertToPinyinString(AppData.moreWordTest.get(mAlreadyFinishWord), ",", PinyinFormat.WITH_TONE_NUMBER);
            String inputWord = PinyinHelper.convertToPinyinString(moreWord, ",", PinyinFormat.WITH_TONE_NUMBER);
            tvResult.setText("您的："+PinyinHelper.convertToPinyinString(moreWord, ",", PinyinFormat.WITH_TONE_MARK)+"---要求："+AppData.moreWordTest.get(mAlreadyFinishWord));
            if (requireWord.equals(inputWord)) {
                tvResult.setTextColor(Color.GREEN);
                mScore += 5;
                tvScore.setText("得分：" + mScore);
                if (mScore > 0) tvScore.setTextColor(Color.GREEN);
            }
            ++mAlreadyFinishWord;
            tvInfo.setText("已测试：" + mAlreadyFinishWord + " 词");
            if (mAlreadyFinishWord > 0)
                tvInfo.setTextColor(Color.GREEN);
            // 测试完成
            if (mAlreadyFinishWord >= AppData.moreWordTest.size())
                tvInfo.setText("测试完成，总得分：" + mScore);
            return;  // 只读一个字

        } catch (PinyinException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void doubleWordJudgment(String text) {
        if (mAlreadyFinishWord >= AppData.doubleWordTest.size()){
            showTip("已测试完成!");
            return;
        }
        String doubleWord = new String();
        // 将读的两个字组成一个字
        boolean isWord = true;
        for (char item : text.toCharArray()) {
            isWord = true;
            // 判断text是个字，并组成字符串
            for (char noneWord : mNoneWords) {
                if (item == noneWord) {
                    isWord = false;
                    break;
                }
            }
            if(isWord)
                doubleWord += item;
        }

        if (doubleWord.length() != 2) {
            //showTip("请再次朗读");
            return;
        }
        tvResult.setTextColor(Color.RED);
        // 比较对错
        try {
            String requireWord = PinyinHelper.convertToPinyinString(AppData.doubleWordTest.get(mAlreadyFinishWord), ",", PinyinFormat.WITH_TONE_NUMBER);
            String inputWord = PinyinHelper.convertToPinyinString(doubleWord, ",", PinyinFormat.WITH_TONE_NUMBER);
            tvResult.setText("您的："+PinyinHelper.convertToPinyinString(doubleWord, ",", PinyinFormat.WITH_TONE_MARK)+"---要求："+AppData.doubleWordTest.get(mAlreadyFinishWord));
            if (requireWord.equals(inputWord)) {   // 正确
                tvResult.setTextColor(Color.GREEN);
                mScore += 6;
                tvScore.setText("得分：" + mScore);
                if (mScore > 0)
                    tvScore.setTextColor(Color.GREEN);
            }
            ++mAlreadyFinishWord;
            tvInfo.setText("已测试：" + mAlreadyFinishWord + " 词");
            if (mAlreadyFinishWord > 0) tvInfo.setTextColor(Color.GREEN);
            // 测试完成
            if (mAlreadyFinishWord >= AppData.doubleWordTest.size()) showTip("测试完成，总得分：" + mScore);
            return;  // 只读一个字
        } catch (PinyinException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void SingleWordJudgment(String text) {
        if (mAlreadyFinishWord >= AppData.singleWordTest.size()){showTip("已测试完成!");return;}
        boolean isWord = true;
        for (char item : text.toCharArray()) {
            isWord = true;
            // 判断text是个字
            for (char noneWord : mNoneWords) {if (item == noneWord) {isWord = false;break;}}
            if (!isWord) continue;  // 如果不是字，则跳过后续判断
            if (mAlreadyFinishWord > AppData.singleWordTest.size()) return;
            tvResult.setTextColor(Color.RED);
            // 判断字是否正确
            // 将字转换为拼音做比对
            try {
                String requireWordPy = PinyinHelper.convertToPinyinString(String.valueOf(AppData.singleWordTest.get(mAlreadyFinishWord).charAt(0)), ",", PinyinFormat.WITH_TONE_NUMBER);
                String inputWordPy = PinyinHelper.convertToPinyinString(String.valueOf(item), ",", PinyinFormat.WITH_TONE_NUMBER);
                tvResult.setText("您的："+PinyinHelper.convertToPinyinString(String.valueOf(item), ",", PinyinFormat.WITH_TONE_MARK)+"---要求："+AppData.singleWordTest.get(mAlreadyFinishWord).charAt(0));
                if (requireWordPy.equals(inputWordPy)) {  // 字正确
                    tvResult.setTextColor(Color.GREEN);
                    mScore += 5;
                    tvScore.setText("得分：" + mScore);
                    if (mScore > 0) tvScore.setTextColor(Color.GREEN);  // 有分数之后提示变为绿色
                }
                ++mAlreadyFinishWord;
                tvInfo.setText("已测试：" + mAlreadyFinishWord + " 字");
                if (mAlreadyFinishWord > 0)
                    tvInfo.setTextColor(Color.GREEN);
                // 测试完成
                if (mAlreadyFinishWord >= AppData.singleWordTest.size())return;  // 只读一个字
            } catch (PinyinException e) {
                e.printStackTrace();
                finish();
            }

        }
    }

    private Object mCloudGrammar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mType = getIntent().getIntExtra(PAGE_TAG, FIRST_PAGE);
        ButterKnife.bind(this);
        // 初始化语音识别对象
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        mCloudGrammar = FucUtil.readFile(this, "grammar_sample.abnf", "utf-8");
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        initView();
        initData();
        setTimeLimit();
    }
    private void setTimeLimit() {
        long countDown;
        if(mType == FIRST_PAGE){
            countDown = 1000 * (3 * 60 + 30);  // 3 min and 30s
        }else if(mType == SECOND_PAGE){
            countDown = 1000 * (2 * 60 + 30);  // 2 min and 30s
        }else {
            countDown = 1000 * (1 * 60);       // 1 min
        }
        new CountDownTimer(countDown, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long second = millisUntilFinished / 1000 + 1;  // +1s是为了显示从整开始
                if(second / 60 != 0) {
                    tvTime.setText("倒计时：" + second / 60 + "分" + second % 60 + "秒");
                }else{
                    tvTime.setText("倒计时：" + second % 60 + "秒");
                }
            }

            @Override
            public void onFinish() {
                btnStartRecognize.setEnabled(false);
                btnStartRecognize.setText("时间到");
                tvTime.setText("倒计时：0秒");
            }
        }.start();
    }

    private void initData() {
        setToolbar();
        getDataFromServer();
        // 设置为瀑布流式
        if (mType == FIRST_PAGE) {
            rvSingle.setLayoutManager(new StaggeredGridLayoutManager(10, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerAdapter = new MyRecyclerAdapter(this, AppData.singleWordTest);
        } else if (mType == SECOND_PAGE) {
            rvSingle.setLayoutManager(new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerAdapter = new MyRecyclerAdapter(this, AppData.doubleWordTest);
        } else {
            rvSingle.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerAdapter = new MyRecyclerAdapter(this, AppData.moreWordTest);
        }
        // 设置分隔线
        MyDividerItemDecoration myDividerItemDecoration = new MyDividerItemDecoration(this);
        rvSingle.addItemDecoration(myDividerItemDecoration);

        rvSingle.setAdapter(mRecyclerAdapter);
    }

    private void getDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> temp = null;
                if(mType == FIRST_PAGE){
                    temp = Server.getSingleWord();
                    //AppData.singleWordTest.addAll(singleWords);
                }else if(mType == SECOND_PAGE){
                    temp = Server.getDoubleWord();
                }else if(mType == THREE_PAGE){
                    temp = Server.getMoreWord();
                }
                final ArrayList<String> result = temp;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result == null){
                            Toast.makeText(TestActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }else{
                            if(mType == FIRST_PAGE){
                                AppData.singleWordTest.clear();
                                AppData.singleWordTest.addAll(result);
                            }else if(mType == SECOND_PAGE){
                                AppData.doubleWordTest.clear();
                                AppData.doubleWordTest.addAll(result);
                            }else if(mType == THREE_PAGE){
                                AppData.moreWordTest.clear();
                                AppData.moreWordTest.addAll(result);
                            }
                            mRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).start();

    }

    private void initView() {
        rvSingle = (RecyclerView) findViewById(R.id.rv_single);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvScore = (TextView) findViewById(R.id.tv_score);
        tvResult = (TextView)findViewById(R.id.tv_result);
        tvTime = (TextView)findViewById(R.id.tv_time);
        (findViewById(R.id.btn_start_recognize)).setOnClickListener(this);
    }

    private void setToolbar() {
        toolbar.setTitle("模拟测试");
        toolbar.setTitleTextAppearance(this, R.style.TextAppearance_AppCompat_Subhead_Inverse);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(Color.WHITE);
        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);  // 设置返回可用
        //toolbar.setNavigationIcon(R.drawable.back_16px);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_recognize:  // 开始识别
                if (null == mAsr) {
                    Log.i(TAG, "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
                    finish();
                }//判断唤醒是否在监听，监听的话就停止监听
                if (mAsr.isListening())
                    mAsr.stopListening();

                // 设置参数
                if (!setParam()) {
                    showTip("请先构建语法。");
                    return;
                }
                ret = mAsr.startListening(mRecognizerListener);
                break;
        }
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    /**
     * 参数设置
     *
     * @return
     */
    public boolean setParam() {
        boolean result = false;
        //设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        //设置返回结果为json格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");

        if ("cloud".equalsIgnoreCase(mEngineType)) {
            //设置云端识别使用的语法id
            mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
            result = true;
        }

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");
        return result;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( null != mAsr ){
            // 退出时释放连接
            mAsr.cancel();
            mAsr.destroy();
        }
    }

}

