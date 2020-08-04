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
import com.mcres.octarus.model.Section;
import com.mcres.octarus.model.Topic;
import com.mcres.octarus.model.TopicList;
import com.mcres.octarus.utils.TimeAgo;
import com.mcres.octarus.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_PROGRESS = 0;
    private static final int VIEW_ITEM_NEWS = 100;
    private static final int VIEW_ITEM_TOPIC = 200;
    private static final int VIEW_ITEM_SECTION = 300;

    private List items = new ArrayList<>();

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemNewsClick(View view, News obj, int position);

        void onItemTopicClick(View view, Topic obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.onItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterHome(Context context, RecyclerView view) {
        ctx = context;
        lastItemViewDetector(view);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_loading);
        }
    }

    public class ItemNewsViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView date;
        public TextView update;
        public TextView featured;
        public ImageView image;
        public TextView txt_type;
        public View lyt_parent;
        public TextView total_view;


        public ItemNewsViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            date = v.findViewById(R.id.date);
            update = v.findViewById(R.id.updated);
            featured = v.findViewById(R.id.featured);
            image = v.findViewById(R.id.image);
            txt_type = v.findViewById(R.id.txt_type);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            total_view = v.findViewById(R.id.total_view);
        }
    }

    public static class ItemTopicViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public RecyclerView recycler_view;

        public ItemTopicViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            recycler_view = v.findViewById(R.id.recycler_view);
        }
    }

    public static class ItemSectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ItemSectionViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM_NEWS) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
            vh = new ItemNewsViewHolder(v);
        } else if (viewType == VIEW_ITEM_TOPIC) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_topic_home, parent, false);
            vh = new ItemTopicViewHolder(v);
        } else if (viewType == VIEW_ITEM_SECTION) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_title, parent, false);
            vh = new ItemSectionViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Object obj = items.get(position);
        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        } else if (holder instanceof ItemNewsViewHolder) {
            final News news = (News) obj;
            ItemNewsViewHolder v = (ItemNewsViewHolder) holder;
            v.title.setText(news.title);
            v.date.setText(TimeAgo.get(ctx, news.date));
            Tools.displayImageThumb(ctx, v.image, Constant.getURLimgNews(news.image), 0.5f);
            v.featured.setVisibility(news.featured == 1 ? View.VISIBLE : View.GONE);
            v.total_view.setText(Tools.bigNumberFormat(news.total_view) + " ");
            if (news.type.equalsIgnoreCase("MCPACK")) {
                v.txt_type.setText(R.string.content_type_mcpack);
            } else if (news.type.equalsIgnoreCase("OTHER")) {
                v.txt_type.setText(R.string.content_type_other);
            } else if (news.title.contains("Server")) {
                v.txt_type.setText("McServer");
            } else if (news.title.contains("Realm")) {
                v.txt_type.setText("McRealm");
            } else {
                v.txt_type.setText(R.string.content_type_mcworld);
            }
            v.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemNewsClick(view, news, position);
                    }
                }
            });

            if (news.date != news.last_update) {
                v.update.setVisibility(View.VISIBLE);
            } else {
                v.update.setVisibility(View.GONE);
            }

        } else if (holder instanceof ItemTopicViewHolder) {
            TopicList topic = (TopicList) obj;
            ItemTopicViewHolder v = (ItemTopicViewHolder) holder;

            v.recycler_view.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
            //set data and list adapter
            AdapterTopicHome mAdapter = new AdapterTopicHome(ctx, topic.topics);
            v.recycler_view.setAdapter(mAdapter);
            //v.recycler_view.setOnFlingListener(null);
            //new StartSnapHelper().attachToRecyclerView(v.recycler_view);

            mAdapter.setOnItemClickListener(new AdapterTopicHome.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Topic obj, int position) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemTopicClick(view, obj, position);
                    }
                }
            });

        } else if (holder instanceof ItemSectionViewHolder) {
            Section section = (Section) obj;
            ItemSectionViewHolder v = (ItemSectionViewHolder) holder;
            v.title.setText(section.title);
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = items.get(position);
        if (obj instanceof News) {
            return VIEW_ITEM_NEWS;
        } else if (obj instanceof TopicList) {
            return VIEW_ITEM_TOPIC;
        } else if (obj instanceof Section) {
            return VIEW_ITEM_SECTION;
        } else {
            return VIEW_PROGRESS;
        }
    }

    public void insertData(List items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void addData(Object items) {
        setLoaded();
        int positionStart = getItemCount();
        this.items.add(items);
        notifyItemInserted(positionStart);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
                return;
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

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
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
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}