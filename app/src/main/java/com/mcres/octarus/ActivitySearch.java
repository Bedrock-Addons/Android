package com.mcres.octarus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcres.octarus.adapter.AdapterContent;
import com.mcres.octarus.connection.API;
import com.mcres.octarus.connection.RestAdapter;
import com.mcres.octarus.connection.response.ResponseNews;
import com.mcres.octarus.data.AppConfig;
import com.mcres.octarus.data.Constant;
import com.mcres.octarus.data.GDPR;
import com.mcres.octarus.data.SharedPref;
import com.mcres.octarus.data.ThisApp;
import com.mcres.octarus.model.News;
import com.mcres.octarus.model.SearchBody;
import com.mcres.octarus.model.SearchFilter;
import com.mcres.octarus.utils.NetworkCheck;
import com.mcres.octarus.utils.Tools;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySearch extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_HINT = "key.EXTRA_HINT";

    public static void navigate(Activity activity, SearchFilter searchFilter, String hint) {
        Intent i = new Intent(activity, ActivitySearch.class);
        i.putExtra(EXTRA_OBJECT, searchFilter);
        i.putExtra(EXTRA_HINT, hint);
        activity.startActivity(i);
    }

    private Call<ResponseNews> callbackCall = null;
    private AdView mAdView;
    private EditText et_search;
    private RecyclerView recyclerView;
    private AdapterContent mAdapter;
    private Button btn_filter, search_faq, search_help;
    private SearchFilter searchFilter = new SearchFilter();
    //private AdView mAdView;
    private SharedPref sharedPref;

    private int post_total = 0;
    private int failed_page = 0;
    private String hint = "";
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sharedPref = new SharedPref(this);
        hint = "search";

        if (getIntent().getSerializableExtra(EXTRA_OBJECT) != null) {
            searchFilter = (SearchFilter) getIntent().getSerializableExtra(EXTRA_OBJECT);
        }

        if (getIntent().getStringExtra(EXTRA_HINT) != null) {
            hint = getIntent().getStringExtra(EXTRA_HINT);
        }

        initToolbar();
        initComponent();
        prepareAds();
        hideKeyboard();
        Tools.RTLMode(getWindow());
        ThisApp.get().saveClassLogEvent(getClass());
    }

    private void initToolbar() {
        Tools.setSmartSystemBar(this);
    }

    private void initComponent() {
        et_search = findViewById(R.id.et_search);
        btn_filter = findViewById(R.id.btn_filter);
        search_faq = findViewById(R.id.search_faq);
        search_help = findViewById(R.id.search_help);
        recyclerView = findViewById(R.id.recyclerView);

        et_search.setHint(Html.fromHtml(hint));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        //set data and list adapter
        mAdapter = new AdapterContent(this, recyclerView, Constant.CONTENT_PER_REQUEST);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterContent.OnItemClickListener() {
            @Override
            public void onItemClick(View v, News obj, int pos) {
                ActivityContentDetails.navigate(ActivitySearch.this, obj);
            }
        });

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterContent.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > mAdapter.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityFilterSearch.navigate(ActivitySearch.this, searchFilter);
            }
        });

        search_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityFaq.navigate(ActivitySearch.this);
            }
        });

        search_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.openInAppBrowser(ActivitySearch.this, Constant.HELP, false);
            }
        });

        (findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showNoItemView(true);
        checkFilterIsActive();

        //animate filter button
        if (!searchFilter.isDefault()) btn_filter.setVisibility(View.INVISIBLE);
        btn_filter.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (searchFilter.isDefault()) return;
                btn_filter.setVisibility(View.VISIBLE);
                Tools.bounceView(btn_filter);
            }
        }, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityFilterSearch.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            searchFilter = (SearchFilter) data.getSerializableExtra(ActivityFilterSearch.RESULT_DATA);
            hint = getString(R.string.hint_default);
            checkFilterIsActive();
        }
    }

    private void checkFilterIsActive() {
        if (searchFilter == null) return;
        if (searchFilter.isDefault()) {
            btn_filter.setTextColor(getResources().getColor(R.color.grey_40));
        } else {
            btn_filter.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        searchAction();
    }

    private void searchAction() {
        et_search.setHint(hint);
        query = et_search.getText().toString().trim();
        if (!query.equals("") || !searchFilter.isDefault()) {
            mAdapter.resetListData();
            requestAction(1);
        } else {
            ((TextView) findViewById(R.id.failed_message)).setText("Type to search");
        }
    }

    private void requestAction(final int page_no) {
        showNoItemView(false);
        if (page_no == 1) {
            showProgress(true);
        } else {
            mAdapter.setLoading();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListNews(page_no);
            }
        }, 1000);
    }

    private void requestListNews(final int page_no) {
        API api = RestAdapter.createAPI();
        SearchBody body = new SearchBody(page_no, Constant.CONTENT_PER_REQUEST, query);
        body.topic_id = searchFilter.topic.id;
        body.col = searchFilter.sort_by.column;
        body.ord = searchFilter.sort_by.order;

        callbackCall = api.getListNewsAdv(body);
        callbackCall.enqueue(new Callback<ResponseNews>() {
            @Override
            public void onResponse(Call<ResponseNews> call, Response<ResponseNews> response) {
                ResponseNews resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    post_total = resp.count_total;
                    displayApiResult(resp.news);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<ResponseNews> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void displayApiResult(final List<News> items) {
        mAdapter.insertData(items);
        showProgress(false);
        if (items.size() == 0) showNoItemView(true);
        hideKeyboard();
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        showProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text), R.drawable.img_failed);
        } else {
            showFailedView(true, getString(R.string.no_internet_text), R.drawable.img_failed);
        }
    }

    private void showFailedView(boolean show, String message, @DrawableRes int icon) {
        View lyt_failed = findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        (findViewById(R.id.failed_retry)).setVisibility(View.VISIBLE);
        (findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_failed);
        (findViewById(R.id.failed_retry)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.failed_message)).setText(getString(R.string.no_results));
        if (show) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void showProgress(final boolean show) {
        View progress_loading = findViewById(R.id.progress_loading);
        progress_loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void prepareAds() {
        if (!AppConfig.ADS_SEARCH_PAGE || !NetworkCheck.isConnect(getApplicationContext())) return;

        // banner
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) mAdView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) mAdView.resume();
    }
}