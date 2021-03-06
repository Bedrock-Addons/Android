package com.mcres.octarus.fragment;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcres.octarus.ActivityContentDetails;
import com.mcres.octarus.ActivitySearch;
import com.mcres.octarus.R;
import com.mcres.octarus.adapter.AdapterHome;
import com.mcres.octarus.connection.API;
import com.mcres.octarus.connection.RestAdapter;
import com.mcres.octarus.connection.response.ResponseHome;
import com.mcres.octarus.connection.response.ResponseNews;
import com.mcres.octarus.data.Constant;
import com.mcres.octarus.data.ThisApp;
import com.mcres.octarus.model.News;
import com.mcres.octarus.model.SearchBody;
import com.mcres.octarus.model.SearchFilter;
import com.mcres.octarus.model.Topic;
import com.mcres.octarus.model.TopicList;
import com.mcres.octarus.utils.NetworkCheck;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment {

    private View root_view;
    private RecyclerView recycler_view;
    private SwipeRefreshLayout swipe_refresh;
    private ShimmerFrameLayout shimmer;

    private Call<ResponseHome> callbackHome;
    private Call<ResponseNews> callbackNews;
    private AdapterHome mAdapter;

    private int count_total = 0;
    private int failed_page = 0;
    private int default_count = 0;

    public FragmentHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_home, container, false);
        initComponent();

        requestAction(0);
        ThisApp.get().saveClassLogEvent(getClass());
        return root_view;
    }

    private void initComponent() {
        recycler_view = root_view.findViewById(R.id.recycler_view);
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        shimmer = root_view.findViewById(R.id.shimmer_home);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view.setHasFixedSize(true);  // Normally True

        //set data and list adapter
        mAdapter = new AdapterHome(getActivity(), recycler_view);
        recycler_view.setAdapter(mAdapter);

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterHome.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                int item_count = mAdapter.getItemCount() - default_count;
                if (count_total > item_count) {
                    int next_page = (item_count / Constant.CONTENT_PER_REQUEST) + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });

        mAdapter.setOnItemClickListener(new AdapterHome.OnItemClickListener() {
            @Override
            public void onItemNewsClick(View view, News obj, int position) {
                ActivityContentDetails.navigate(getActivity(), obj);
            }

            @Override
            public void onItemTopicClick(View view, Topic obj, int position) {
                SearchFilter sf = new SearchFilter(obj);
                ActivitySearch.navigate(getActivity(), sf, getString(R.string.hint_topic) + " " + obj.name);
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackHome != null && callbackHome.isExecuted()) callbackHome.cancel();
                if (callbackNews != null && callbackNews.isExecuted()) callbackNews.cancel();
                count_total = 0;
                failed_page = 0;
                default_count = 0;
                mAdapter.resetListData();
                requestAction(0);
            }
        });
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "", R.drawable.img_failed);
        if (page_no == 0) {
            swipeProgress(true);
        } else {
            mAdapter.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestHomeData(page_no);
            }
        }, 500);
    }

    private void requestHomeData(final int page_no) {
        API api = RestAdapter.createAPI();
        if (page_no == 0) {
            callbackHome = api.getHome();
            callbackHome.enqueue(new Callback<ResponseHome>() {
                @Override
                public void onResponse(Call<ResponseHome> call, Response<ResponseHome> response) {
                    ResponseHome resp = response.body();
                    if (resp != null && resp.status.equals("success")) {
                        displayData(resp);
                        swipeProgress(false);
                    } else {
                        onFailRequest(page_no);
                    }
                }

                @Override
                public void onFailure(Call<ResponseHome> call, Throwable t) {
                    Log.e("onFailure", t.getMessage());
                    if (!call.isCanceled()) onFailRequest(page_no);
                }
            });
        } else {
            SearchBody body = new SearchBody(page_no, Constant.CONTENT_PER_REQUEST, 0);
            callbackNews = api.getListNewsAdv(body);
            callbackNews.enqueue(new Callback<ResponseNews>() {
                @Override
                public void onResponse(Call<ResponseNews> call, Response<ResponseNews> response) {
                    ResponseNews resp = response.body();
                    if (resp != null && resp.status.equals("success")) {
                        count_total = resp.count_total;
                        displayContentData(resp.news);
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
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text), R.drawable.img_failed);
        } else {
            showFailedView(true, getString(R.string.no_internet_text), R.drawable.img_no_internet);
        }
    }

    private void showFailedView(boolean show, String message, @DrawableRes int icon) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed);

        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recycler_view.setVisibility(View.INVISIBLE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recycler_view.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        (root_view.findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void swipeProgress(final boolean show) {
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
        if (!show) {
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            return;
        }
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
    }

    private void displayData(ResponseHome resp) {
        mAdapter.insertData(resp.featured);
        mAdapter.addData(new TopicList(resp.topic));
        default_count = resp.featured.size();
        requestHomeData(1);
        // save featured categories to global variable
        ThisApp.get().setFeaturedTopic(resp.topic);

    }

    private void displayContentData(List<News> items) {
        mAdapter.insertData(items);
    }

    @Override
    public void onDestroy() {
        if (callbackHome != null && !callbackHome.isCanceled()) callbackHome.cancel();
        if (callbackNews != null && !callbackNews.isCanceled()) callbackNews.cancel();
        if (shimmer != null) shimmer.stopShimmer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}