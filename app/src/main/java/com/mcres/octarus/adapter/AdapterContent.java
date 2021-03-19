package com.mcres.octarus.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcres.octarus.R;
import com.mcres.octarus.data.Constant;
import com.mcres.octarus.model.News;
import com.mcres.octarus.room.table.NewsEntity;
import com.mcres.octarus.utils.TimeAgo;
import com.mcres.octarus.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterContent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<News> items = new ArrayList<>();

    private int pagination = 0;
    private boolean loading;
    private AdapterContent.OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private AdapterContent.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, News obj, int position);
    }

    public void setOnItemClickListener(final AdapterContent.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterContent(Context context, RecyclerView view, int pagination) {
        ctx = context;
        this.pagination = pagination;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView date;
        public TextView update;
        public TextView featured;
        public ImageView image;
        public ImageView img_type;
        public TextView txt_type;
        public View lyt_parent;
        public TextView total_view;

        public OriginalViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            date = v.findViewById(R.id.date);
            update = v.findViewById(R.id.updated);
            featured = v.findViewById(R.id.featured);
            image = v.findViewById(R.id.image);
            img_type = v.findViewById(R.id.img_type);
            txt_type = v.findViewById(R.id.txt_type);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            total_view = v.findViewById(R.id.total_view);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
            vh = new AdapterContent.OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new AdapterContent.ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterContent.OriginalViewHolder) {
            final News news = items.get(position);
            AdapterContent.OriginalViewHolder v = (AdapterContent.OriginalViewHolder) holder;
            v.title.setText(news.title);
            v.date.setText(TimeAgo.get(ctx, news.date));
            Tools.displayImage(ctx, v.image, Constant.getURLcontent(news.image));
            v.featured.setVisibility(news.featured == 1 ? View.VISIBLE : View.GONE);
            v.total_view.setText(Tools.bigNumberFormat(news.total_view));
            if (news.type.equalsIgnoreCase("OTHER")) {
                v.txt_type.setText(R.string.content_type_other);
            } else if (news.type.equalsIgnoreCase("MCWORLD")) {
                v.txt_type.setText(R.string.content_type_mcworld);
            } else if (news.type.equalsIgnoreCase("MCPACK")) {
                v.txt_type.setText(R.string.content_type_mcpack);
            } else {
                v.txt_type.setText(R.string.content_type_other);
            }
            v.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, news, position);
                    }
                }
            });

            if (news.date != news.last_update) {
                v.update.setVisibility(View.VISIBLE);
            } else {
                v.update.setVisibility(View.GONE);
            }

        } else {
            ((AdapterContent.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void insertData(List<News> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void insertEntityData(List<NewsEntity> entities) {
        List<News> items = new ArrayList<>();
        for (NewsEntity e : entities) {
            items.add(e.original());
        }
        insertData(items);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(AdapterContent.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / pagination;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}