package app.tomasatto.lpg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.activity.HomeActivity;
import app.tomasatto.lpg.adapter.ProductListAdapter;
import app.tomasatto.lpg.bean.ScanProduct;

/**
 * Created by Megha on 07-07-2017.
 */

public class ProductListFragment extends Fragment {

    private Context context;
    private RecyclerView mRecyclerView;
    private ProductListAdapter mProductListAdapter;
    private TextView mEmptyView;
    private List<ScanProduct> scanProductList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_history,null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        scanProductList = new ArrayList<>();
        if(getActivity() instanceof HomeActivity){
            scanProductList = ((HomeActivity)getActivity()).getScanProductList();
        }
        mProductListAdapter = new ProductListAdapter(scanProductList);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);

        RecyclerView.LayoutManager mLayoutManager;

        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductListAdapter);
        updateView();
        return view;
    }

    public void updateScanProductList(List<ScanProduct> scanProductList) {
        this.scanProductList = scanProductList;
        mProductListAdapter.setScanProductList(this.scanProductList );
        mProductListAdapter.notifyDataSetChanged();
        updateView();
    }

    private void updateView(){
        if (scanProductList==null || scanProductList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }
}
