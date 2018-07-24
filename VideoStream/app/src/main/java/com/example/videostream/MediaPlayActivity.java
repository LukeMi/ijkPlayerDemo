package com.example.videostream;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function4;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaMeta;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;

public class MediaPlayActivity extends BaseActivity {
    private final String TAG = "MediaPlayActivity";
    @BindView(R.id.vv_media)
    VideoView vvMedia;
    @BindView(R.id.tv_title_toolbar_typ1)
    TextView tvTitleToolbarTyp1;
    @BindView(R.id.ll_detail_param)
    LinearLayout llDetailParam;
    @BindView(R.id.tv_revolution)
    TextView tvRevolution;
    @BindView(R.id.tv_encode_format)
    TextView tvEncodeFormat;
    @BindView(R.id.tv_pkg_format)
    TextView tvPkgFormat;
    @BindView(R.id.tv_code_rate)
    TextView tvCodeRate;
    @BindView(R.id.ll_revolution)
    LinearLayout llRevolution;
    @BindView(R.id.ll_encode_format)
    LinearLayout llEncodeFormat;
    @BindView(R.id.ll_pkg_format)
    LinearLayout llPkgFormat;
    @BindView(R.id.ll_code_rate)
    LinearLayout llCodeRate;

    @BindView(R.id.fl_contain)
    VideoPlayerIJK mVideoPlayerIJK;


    private MediaBean mediaBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = mediaBean.getPath();
        initIjkPlayer();

    }

    @Override
    public int bindLayoutRes() {
        return R.layout.activity_play_media;
    }

    @Override
    protected void getIntentData() {
        mediaBean = (MediaBean) getIntent().getSerializableExtra("mediaBean");
        mediaBean = new MediaBean();
        mediaBean.setFileType(MediaBean.Media_VIDEO_TYPE);
        mediaBean.setPath("http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8");
    }

    @Override
    protected void initView() {

        String title = mediaBean.getFileType() == MediaBean.Media_AUDIO_TYPE ? "视频播放" : "视频播放";
        tvTitleToolbarTyp1.setText(title);

        vvMedia.setOnPreparedListener(mOnPreparedListener);
        //vvMedia.setOnInfoListener(mOnInfoListener);
        vvMedia.setOnCompletionListener(mOnCompletionListener);
        vvMedia.setOnErrorListener(mOnErrorListener);

        InitialValueObservable<CharSequence> tvRevolutionValueObservable = RxTextView.textChanges(tvRevolution);
        InitialValueObservable<CharSequence> tvEncodeFormatValueObservable = RxTextView.textChanges(tvEncodeFormat);
        InitialValueObservable<CharSequence> tvPkgFormatValueObservable = RxTextView.textChanges(tvPkgFormat);
        InitialValueObservable<CharSequence> tvCodeRateValueObservable = RxTextView.textChanges(tvCodeRate);

        Disposable subscribe = Observable.combineLatest(tvRevolutionValueObservable, tvEncodeFormatValueObservable
                , tvPkgFormatValueObservable, tvCodeRateValueObservable, new Function4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence revolution, CharSequence encodeFormat, CharSequence pkgFormat, CharSequence codeRate) throws Exception {
                        llPkgFormat.setVisibility(!TextUtils.isEmpty(pkgFormat) ? View.VISIBLE : View.GONE);
                        return !TextUtils.isEmpty(revolution) || !TextUtils.isEmpty(encodeFormat)
                                || !TextUtils.isEmpty(pkgFormat) || !TextUtils.isEmpty(codeRate);
                    }
                }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });
    }

    @OnClick({R.id.img_back_toolbar_typ1, R.id.btn_play, R.id.btn_pause, R.id.btn_stop, R.id.btn_re_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_toolbar_typ1:
                finish();
                break;
            case R.id.btn_play:
                if (vvMedia.isPlaying()) return;
                String path = mediaBean.getPath();

                vvMedia.setVideoURI(Uri.parse(path));
                vvMedia.start();
                break;
            case R.id.btn_pause:
                if (vvMedia.isPlaying()) vvMedia.pause();
                break;
            case R.id.btn_stop:
                vvMedia.stopPlayback();
                break;
            case R.id.btn_re_play:
                vvMedia.seekTo(0);
                vvMedia.start();
                break;
        }
    }

    private void initIjkPlayer() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        mVideoPlayerIJK.setListener(mVideoPlayerListener);
        mVideoPlayerIJK.setVideoPath(mediaBean.getPath());
        mVideoPlayerIJK.start();
    }

    private VideoPlayerListener mVideoPlayerListener = new VideoPlayerListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//            Logcat.i("onBufferingUpdate");
        }

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            Logcat.i("onCompletion");
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            Logcat.i("onError");
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            Logcat.i("onInfo");
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            Logcat.i("videoWidth: "+videoWidth);
            Logcat.i("videoHeight: "+videoHeight);
            MediaInfo mediaInfo = iMediaPlayer.getMediaInfo();
            Logcat.i("mediaInfo.mAudioDecoder = " + mediaInfo.mAudioDecoder);
            IjkMediaMeta mMeta = mediaInfo.mMeta;
            IjkMediaMeta.IjkStreamMeta mAudioStream = mMeta.mAudioStream;
            IjkMediaMeta.IjkStreamMeta mVideoStream = mMeta.mVideoStream;

            int mAudioSampleRate  = 0;
            int mFpsDen =0;

            if (mAudioStream != null){
                mAudioSampleRate   = mAudioStream.mSampleRate;
            }
            if (mVideoStream != null){
                mFpsDen = mVideoStream.mFpsDen;
            }

            Logcat.i("mAudioSampleRate: "+mAudioSampleRate);
            Logcat.i("mVideoSampleRate: "+mFpsDen);

            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Logcat.i("onPrepared");
        }

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            Logcat.i("onSeekComplete");
        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            Logcat.i("onVideoSizeChanged");
        }
    };

    /**
     * 播放完成接口
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }
    };

    /**
     * 播放信息接口
     */
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
            return false;
        }
    };

    /**
     * 缓存更新接口
     */
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

        }
    };

    /**
     * 准备接口
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

        }
    };

    /**
     * 拖拽接口
     */
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {

        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case -1004:
                    Log.d(TAG, "MEDIA_ERROR_IO");
                    break;
                case -1007:
                    Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                    break;
                case 200:
                    Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                    break;
                case 100:
                    Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                    break;
                case -110:
                    Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                    break;
                case 1:
                    Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                    break;
                case -1010:
                    Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                    break;
            }
            switch (extra) {
                case 800:
                    Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                    break;
                case 702:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                    break;
                case 701:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 802:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 801:
                    Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                    break;
                case 1:
                    Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                    break;
                case 3:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                    break;
                case 700:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                    break;
            }
            return false;
        }

    };

}
