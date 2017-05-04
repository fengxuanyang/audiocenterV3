package com.ragentek.homeset.audiocenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/2/23.
 */

public abstract class ListItemBaseAdapter<T, R extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<R> {
    private static final String TAG = "ListItemBaseAdapter";
    OnItemClickListener mOnItemClickListener;
    private ArrayList mData = new ArrayList<T>();
    Context mContext;
    int curSellect = 0;

    public ListItemBaseAdapter(Context context, int initindex) {
        curSellect = initindex;
        mContext = context;
    }

    public ListItemBaseAdapter(Context context) {
        mContext = context;
    }

    public void updateSellect(int index) {
        LogUtil.d(TAG, "updateSellect  index : " + index);
        LogUtil.d(TAG, "updateSellect  curSellect : " + curSellect);
//        int preSellect = curSellect;
        curSellect = index;
        notifyDataSetChanged();
//
//        notifyItemChanged(preSellect);
//        notifyItemChanged(curSellect);
    }


    /**
     * @param data
     * @param start start of items to ba added
     */
    public void insertDatas(List<T> data, int start) {
        LogUtil.d(TAG, "insertDatas: " + start + ",size" + data.size());

        LogUtil.d(TAG, ",size" + mData.size());
        mData.addAll(start, data);

        LogUtil.d(TAG, ",size" + mData.size());

        notifyDataSetChanged();
    }

    public synchronized void addDatas(List<T> data) {
        LogUtil.d(TAG, "addDatas  date: " + data.size());
        LogUtil.d(TAG, "addDatas  mData: " + mData);

        LogUtil.d(TAG, "addDatas  mData before: " + mData.size());
        mData.addAll(data);

        LogUtil.d(TAG, "addDatas  mData after: " + mData.size());

        notifyDataSetChanged();
    }

    /**
     * new  data
     *
     * @param data
     */
    public void updateData(Object data) {
        LogUtil.d(TAG, "updateData: " + mData.contains(data));

        if (mData.contains(data)) {
            notifyItemChanged(mData.indexOf(data));
        }
        //re init curSellect ,start play from the top
    }

    /**
     * new  data
     *
     * @param data
     */
    public void setDatas(List<T> data) {
        LogUtil.d(TAG, "setDatas  data: " + data.size());

        mData.clear();
        mData.addAll(data);
        LogUtil.d(TAG, "setDatas mData: " + mData.size());

        //re init curSellect ,start play from the top
        curSellect = 0;
        notifyDataSetChanged();
    }

    public void removeDate(int index) {
        LogUtil.d(TAG, "removeDate: " + index);

        if (mData != null) {
            mData.remove(index);
        }
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public List<T> getData() {
        return mData;
    }

    public int getStart() {
        return mData.size();
    }
}
