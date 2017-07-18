package app.tomasatto.lpg.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.bean.ScanProduct;

/**
 * Created by Megha on 07-07-2017.
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductHolder> {

    private List<ScanProduct> scanProductList;


    public ProductListAdapter(List<ScanProduct> scanProductList) {
        this.scanProductList= scanProductList;
    }

    public void setScanProductList(List<ScanProduct> scanProductList) {
        this.scanProductList = scanProductList;
    }

    @Override
    public ProductListAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_product_list, parent, false);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductListAdapter.ProductHolder holder, int position) {
        ScanProduct scanProduct = scanProductList.get(position);
        holder.productName.setText(scanProduct.productName);
        holder.productVersion.setText(scanProduct.productVersion);
        holder.standardCode.setText(scanProduct.standardCode);

        Picasso.with(holder.productImgUrl.getContext())
                .load(scanProduct.productImgUrl)
                .placeholder(R.drawable.ic_image_not_ava) //this is optional the image to display while the url image is downloading
                .error(R.drawable.ic_image_not_ava)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                .into(holder.productImgUrl);
    }

    @Override
    public int getItemCount() {
        return scanProductList!=null?scanProductList.size():0;
    }

    public class ProductHolder  extends RecyclerView.ViewHolder{
        public TextView productName;
        public ImageView productImgUrl;
        public TextView productVersion;
        public TextView standardCode;

        public ProductHolder(View view) {
            super(view);

            productName = (TextView) view.findViewById(R.id.textViewProductName);
            productVersion = (TextView) view.findViewById(R.id.textViewVersion);
            productImgUrl = (ImageView) view.findViewById(R.id.imageView4);
            standardCode = (TextView)view.findViewById(R.id.textViewCode);

        }
    }
}
