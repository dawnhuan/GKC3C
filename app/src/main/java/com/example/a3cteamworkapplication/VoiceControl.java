package com.example.a3cteamworkapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

public class VoiceControl extends AppCompatActivity {
    private CarControl control;
    private ImageButton Ibutton;
    private TextView text;
    private SpeechRecognizer mIat;
    private static String TAG = VoiceControl.class.getSimpleName();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);

        control = new CarControl(MainActivity.client);
        Ibutton = (ImageButton) findViewById(R.id.imageButton);
        text = (TextView) findViewById(R.id.textView);
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);


        //初始化识别无UI识别对象
//使用SpeechRecognizer对象，可根据回调消息自定义界面；
        InitListener mInitListener = new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    showTip("初始化失败，错误码：" + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
            }
        };
        mIat = SpeechRecognizer.createRecognizer(VoiceControl.this, mInitListener);
        if( null == mIat ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
            return;
        }
//设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        mIat.setParameter( SpeechConstant.SUBJECT, null );
//设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
//此处engineType为“cloud”
        mIat.setParameter( SpeechConstant.ENGINE_TYPE, "cloud" );
//设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
// 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
//取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
//设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
//自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
//设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,"0");

//开始识别，并设置监听器
        //mIat.startListening(mRecogListener);

        Ibutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mIat.startListening(mRecognizerListener);
                    Ibutton.setImageResource(android.R.drawable.presence_audio_online);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mIat.stopListening();
                    Ibutton.setImageResource(android.R.drawable.ic_btn_speak_now);
                }
                return false;
            }
        });

    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。

            showTip(error.getPlainDescription(true));

        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            //System.out.println(flg++);
            String command = results.getResultString();
            text.setText(command);
            String[] commands = command.split(",|。|，| |再|然后|最后");
            for (int cnum = 0; cnum < commands.length; cnum++) {
                command = commands[cnum];
                //方向
                int direction = -1;
                for (int i = 0; i < command.length(); i++) {
                    if (command.charAt(i) == '前') {
                        direction = 0;
                        break;
                    } else if (command.charAt(i) == '后' || command.charAt(i) == '退') {
                        direction = 1;
                        break;
                    } else if (command.charAt(i) == '左') {
                        direction = 2;
                        break;
                    } else if (command.charAt(i) == '右') {
                        direction = 3;
                        break;
                    } else if (command.charAt(i) == '停') {
                        direction = 4;
                        break;
                    }
                }
                //数字
                double number = 0;
                int unit = 0;
                for (int i = 0; i < command.length(); i++) {
                    int end = i;
                    while ((command.charAt(end) <= '9' && command.charAt(end) >= '0') || command.charAt(end) == '.')
                        end++;
                    if (end != i) {
                        number = Double.parseDouble(command.substring(i, end));
                        unit = end;
                        break;
                    }

                    if (command.charAt(i) == '一') {
                        number = 1; unit = i+1;
                        break;
                    } else if (command.charAt(i) == '二' || command.charAt(i) == '两') {
                        number = 2; unit = i+1;
                        break;
                    } else if (command.charAt(i) == '三') {
                        number = 3; unit = i+1;
                        break;
                    } else if (command.charAt(i) == '半') {
                        number = 0.5; unit = i+1;
                        break;
                    }
                }
                //单位
                if (command.charAt(unit) == '度' && (direction == 2 || direction == 3))
                {                }
                else if (command.charAt(unit) == '秒' && (direction == 0 || direction == 1))
                {                }
                else if (number == 0)
                {                }
                else
                    continue;

                switch (direction) {
                    case 0:
                        if (number == 0) {
                            control.go();
                        } else {
                            control.go_wait(number*1000);
                        }
                        break;
                    case 1:
                        if (number == 0) {
                            control.back();
                        } else {
                            control.back_wait(number*1000);
                        }
                        break;
                    case 2:
                        if (number == 0) {
                            control.left();
                        } else {
                            control.left_wait(number);
                        }
                        break;
                    case 3:
                        if (number == 0) {
                            control.right();
                        } else {
                            control.right_wait(number);
                        }
                        break;
                    case 4:
                        control.stop();
                        break;
                }
            }

        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };
}
