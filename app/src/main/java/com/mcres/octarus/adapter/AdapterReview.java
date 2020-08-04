package com.mcres.octarus.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcres.octarus.R;
import com.mcres.octarus.data.Constant;
import com.mcres.octarus.model.Comment;
import com.mcres.octarus.utils.TimeAgo;
import com.mcres.octarus.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterReview extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Comment> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Comment obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterReview(Context context, RecyclerView view) {
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name;   //Name of the commenter
        public TextView comment;   //Contents of the comment
        public TextView date;   //Date the comment was written
        public ImageView image;   //Profile picture of the commenter
        public View lyt_parent;   //Entire comment
        public View rating;   //Commenter rating

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            comment = v.findViewById(R.id.comment);
            date = v.findViewById(R.id.date);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            rating = v.findViewById(R.id.rating);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progress_bar;
        public TextView tv_load_more;

        public ProgressViewHolder(View v) {
            super(v);
            progress_bar = v.findViewById(R.id.progress_loading);
            tv_load_more = v.findViewById(R.id.tv_load_more);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loadmore_reviews, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Comment c = items.get(position);
            OriginalViewHolder v_item = (OriginalViewHolder) holder;
            v_item.name.setText(c.name);
            if (c.status.equalsIgnoreCase("SHOW")) {
                v_item.comment.setText(c.comment);
            } else {
                v_item.comment.setText(ctx.getString(R.string.hide_comment_msg));
                v_item.image.setVisibility(View.GONE);
                v_item.date.setVisibility(View.GONE);
                v_item.name.setText(ctx.getString(R.string.hide_user_msg));
                v_item.rating.setVisibility(View.VISIBLE);
            }
            v_item.date.setText(TimeAgo.get(ctx, c.created_at));
            Tools.displayImageCircle(ctx, v_item.image, Constant.getURLimgUser(c.image), 0.5f);
            v_item.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, c, position);
                    }
                }
            });
        } else {
            final ProgressViewHolder v_item = (ProgressViewHolder) holder;
            v_item.progress_bar.setVisibility(View.INVISIBLE);
            v_item.tv_load_more.setVisibility(View.VISIBLE);
            v_item.tv_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onLoadMoreListener == null) return;
                    v_item.progress_bar.setVisibility(View.VISIBLE);
                    v_item.tv_load_more.setVisibility(View.INVISIBLE);
                    int current_page = getItemCount() / Constant.COMMENT_PER_REQUEST;
                    onLoadMoreListener.onLoadMore(current_page);
                }
            });
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

    public void insertData(List<Comment> items) {
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void showLoadMore() {
        items.add(null);
        notifyItemInserted(getItemCount());
    }

    public void hideLoadMore() {
        if (items.size() > 0 && items.get(getItemCount() - 1) == null) {
            int pos = getItemCount() - 1;
            items.remove(pos);
            notifyItemRemoved(getItemCount());
        }
    }

    public void addFirst(Comment comment) {
        items.add(0, comment);
        notifyItemInserted(0);
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }
}