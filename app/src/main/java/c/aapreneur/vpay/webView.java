package c.aapreneur.vpay;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import c.aapreneur.vpay.app.utils.DualProgressView;

public class webView extends AppCompatActivity {
    private WebView webView;
    String postURL,title;
    private DualProgressView spinner;
    String ShowOrHideWebViewInitialUse = "show";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webView);
        spinner = findViewById(R.id.progressBar1);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            postURL = extras.getString("URL");
            title = extras.getString("TITLE");
            getSupportActionBar().setTitle(title);
            webView.setWebViewClient(new CustomWebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(postURL);
            webView.setHorizontalScrollBarEnabled(false);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(true);
            webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        }
    }
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {

            // only make it invisible the FIRST time the app is run
            if (ShowOrHideWebViewInitialUse.equals("show")) {
                webview.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            ShowOrHideWebViewInitialUse = "hide";
            spinner.setVisibility(View.GONE);

            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);

        }
    }

}
