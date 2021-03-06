package org.mozilla.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mozilla.gecko.GeckoView;
import org.mozilla.gecko.GeckoViewChrome;
import org.mozilla.gecko.GeckoViewContent;
import org.mozilla.gecko.PrefsHelper;

public class DetailActivity extends Fragment {

    private static final String LOGTAG = "DetailActivity";
    private GeckoView mGeckoView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.activity_detail, container, false);


        mGeckoView = (GeckoView) mainView.findViewById(R.id.gecko_view);

        mGeckoView.setChromeDelegate(new MyGeckoViewChrome());
        mGeckoView.setContentDelegate(new SearchGeckoView());

        PrefsHelper.setPref("privacy.clearOnShutdown.cache", true);
        PrefsHelper.setPref("privacy.clearOnShutdown.cookies", true);


        if (null == mGeckoView.getCurrentBrowser()) {
            // This pageload allows Fennec to be loaded in a background fragment.
            // Without supplying a URL, it doesn't look like Fennec will get loaded?
            mGeckoView.addBrowser("https://search.yahoo.com/search?p=firefox%20android");

        }

        return mainView;
    }


    public void setUrl(String url) {
        if (null == mGeckoView.getCurrentBrowser()) {
            mGeckoView.addBrowser(url);
        } else {
            mGeckoView.getCurrentBrowser().loadUrl(url);
        }
    }


    private static class MyGeckoViewChrome extends GeckoViewChrome {
        @Override
        public void onReady(GeckoView view) {
            Log.i(LOGTAG, "Gecko is ready");

            PrefsHelper.setPref("devtools.debugger.remote-enabled", true);

            // The Gecko libraries have finished loading and we can use the rendering engine.
            // Let's add a browser (required) and load a page into it.
        }

    }


    private class SearchGeckoView extends GeckoViewContent {

        @Override
        public void onPageStart(GeckoView geckoView, GeckoView.Browser browser, String s) {
            Log.i("OnPageStart", s);
            // Only load this page if it's the Yahoo search page that we're using.
            // TODO: Make this check more robust, and allow for other search providers.
            if (s.contains("//search.yahoo.com")) {
                super.onPageStart(geckoView, browser, s);


            } else {
                browser.stop();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(s));
                startActivity(i);
            }
        }

        @Override
        public void onPageShow(GeckoView geckoView, GeckoView.Browser browser) {

        }
    }
}
