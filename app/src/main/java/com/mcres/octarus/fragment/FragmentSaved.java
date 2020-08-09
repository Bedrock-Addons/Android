package com.mcres.octarus.fragment;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcres.octarus.ActivityContentDetails;
import com.mcres.octarus.R;
import com.mcres.octarus.adapter.AdapterContent;
import com.mcres.octarus.data.Constant;
import com.mcres.octarus.data.ThisApp;
import com.mcres.octarus.model.News;
import com.mcres.octarus.model.type.SourceType;
import com.mcres.octarus.room.AppDatabase;
import com.mcres.octarus.room.DAO;
import com.mcres.octarus.room.table.NewsEntity;

import java.util.List;

public class FragmentSaved extends Fragment {

    private View root_view;
    private View parent_view;
    private RecyclerView recycler_view;
    private DAO dao;

    public AdapterContent adapter;

    public FragmentSaved() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        dao = AppDatabase.getDb(getActivity()).getDAO();

        ThisApp.get().saveClassLogEvent(getClass());
        return root_view;
    }

    private void initComponent() {
        parent_view = root_view.findViewById(android.R.id.content);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set data and list adapter
        adapter = new AdapterContent(getActivity(), recycler_view, Constant.BOOKMARKS_PAGE);
        recycler_view.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterContent.OnItemClickListener() {
            @Override
            public void onItemClick(View view, News obj, int pos) {
                obj.source_type = SourceType.SAVED;
                ActivityContentDetails.navigate(getActivity(), obj);
            }
        });

        startLoadMoreAdapter();
    }

    private void startLoadMoreAdapter() {
        adapter.resetListData();
        List<NewsEntity> items = dao.getAllNewsByPage(Constant.BOOKMARKS_PAGE, 0);
        adapter.insertEntityData(items);
        showNoItemView();
        final int item_count = dao.getNewsCount();
        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new AdapterContent.OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int current_page) {
                if (item_count > adapter.getItemCount() && current_page != 0) {
                    displayDataByPage(current_page);
                } else {
                    adapter.setLoaded();
                }
            }
        });
    }

    private void displayDataByPage(final int next_page) {
        adapter.setLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<NewsEntity> items = dao.getAllNewsByPage(Constant.BOOKMARKS_PAGE, (next_page * Constant.BOOKMARKS_PAGE));
                adapter.insertEntityData(items);
                showNoItemView();
            }
        }, 1000);
    }

    private void showNoItemView() {
        View lyt_no_item = root_view.findViewById(R.id.lyt_failed);
        (root_view.findViewById(R.id.failed_retry)).setVisibility(View.GONE);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(R.string.no_item);
        if (adapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initComponent();
    }
}
