package vn.ncdesign.musicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.webkit.WebViewAssetLoader;

public final class MainActivity extends Activity {
    private static final int PICK_AUDIO_REQUEST = 721;
    private static final String LOCAL_URL =
            "https://appassets.androidplatform.net/assets/index.html";

    private WebView playerView;
    private ValueCallback<Uri[]> pendingFileCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WebView.setWebContentsDebuggingEnabled(false);
        playerView = new WebView(this);
        playerView.setBackgroundColor(0xffb8c3cb);
        setContentView(playerView);

        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/",
                        new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        WebSettings settings = playerView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setSupportMultipleWindows(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(false);

        playerView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(
                    WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("appassets.androidplatform.net".equals(uri.getHost())) return false;
                if ("https".equals(uri.getScheme())) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } catch (Exception ignored) {
                        // Remain on the player if there is no external browser.
                    }
                    return true;
                }
                return true;
            }
        });
        playerView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView view,
                    ValueCallback<Uri[]> callback,
                    FileChooserParams params) {
                if (pendingFileCallback != null) {
                    pendingFileCallback.onReceiveValue(null);
                }
                pendingFileCallback = callback;
                try {
                    Intent chooser = params.createIntent();
                    chooser.addCategory(Intent.CATEGORY_OPENABLE);
                    chooser.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(chooser, PICK_AUDIO_REQUEST);
                    return true;
                } catch (Exception exception) {
                    pendingFileCallback = null;
                    callback.onReceiveValue(null);
                    return true;
                }
            }
        });
        playerView.loadUrl(LOCAL_URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_AUDIO_REQUEST || pendingFileCallback == null) return;
        Uri[] result = null;
        if (resultCode == RESULT_OK) {
            result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
        }
        pendingFileCallback.onReceiveValue(result);
        pendingFileCallback = null;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        // Keep WebView and WebAudio alive when rotating a phone or tablet.
    }

    @Override
    public void onBackPressed() {
        if (playerView != null && playerView.canGoBack()) {
            playerView.goBack();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("NC Music Player")
                .setMessage("Đóng ứng dụng phát nhạc?")
                .setNegativeButton("Ở lại", null)
                .setPositiveButton("Đóng", (dialog, which) -> finish())
                .show();
    }

    @Override
    protected void onDestroy() {
        if (pendingFileCallback != null) {
            pendingFileCallback.onReceiveValue(null);
            pendingFileCallback = null;
        }
        if (playerView != null) {
            playerView.stopLoading();
            playerView.destroy();
            playerView = null;
        }
        super.onDestroy();
    }
}
