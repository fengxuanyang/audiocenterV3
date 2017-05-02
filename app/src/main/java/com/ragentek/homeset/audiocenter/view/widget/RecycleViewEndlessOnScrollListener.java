package com.ragentek.homeset.audiocenter.view.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.ragentek.homeset.audiocenter.utils.LogUtil;


/**
 * Created by xuanyang.feng on 2017/4/6.
 */

public abstract class RecycleViewEndlessOnScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "RecycleViewEndlessOnScrollListener";
    private LayoutManagerType layoutManagerType;
    private int currentPage = 0;
    private int previousTotal = 0;
    private int lastVisibleItemPosition;
    private int firstVisibleItemPosition;

    private int currentScrollState = 0;
    private boolean loading = false;
    private boolean isUp = false;


    public abstract void onLoadMore(int currentPage);

    public abstract void onUpdata(int currentPage);

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        LogUtil.d(TAG, "onScrollStateChanged  isUp: " + isUp);
        LogUtil.d(TAG, "onScrollStateChanged  loading : " + loading);
        LogUtil.d(TAG, "onScrollStateChanged   firstVisibleItemPosition" + firstVisibleItemPosition);


        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        currentScrollState = newState;
        LogUtil.d(TAG, "onScrollStateChanged  currentScrollState : " + currentScrollState);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        LogUtil.d(TAG, "onScrollStateChanged  totalItemCount - visibleItemCount : " + (totalItemCount - visibleItemCount));

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        } else if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                !loading && lastVisibleItemPosition + 1 == totalItemCount && totalItemCount - visibleItemCount <= lastVisibleItemPosition) {
            LogUtil.d(TAG, "onScrollStateChanged  onLoadMore: ");
            loading = true;
            onLoadMore(currentPage);
            currentPage++;
        } else if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                !loading && !isUp && firstVisibleItemPosition == 0) {
            loading = true;
            onUpdata(currentPage);
            LogUtil.d(TAG, "onScrollStateChanged  onUpdata: ");

        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        isUp = dy > 0;
        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LinearLayout;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GridLayout;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.StaggeredGridLayout;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LinearLayout:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
            case GridLayout:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
            case StaggeredGridLayout:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                int[] firstPositions = new int[staggeredGridLayoutManager.getSpanCount()];

                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);

                staggeredGridLayoutManager.findFirstVisibleItemPositions(firstPositions);
                firstVisibleItemPosition = findMin(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                break;
        }


    }

    private int findMin(int[] lastPositions) {
        int min = lastPositions[0];
        for (int value : lastPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    /**
     * for gridview
     *
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }


    public enum LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }
}
