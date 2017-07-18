package app.tomasatto.lpg.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.adapter.HomePagerAdapter;
import app.tomasatto.lpg.bean.ScanProduct;
import app.tomasatto.lpg.fragment.ProductListFragment;
import app.tomasatto.lpg.utils.Constant;

import static app.tomasatto.lpg.activity.LoginActivity.CUSTOMER_ID;
import static app.tomasatto.lpg.activity.LoginActivity.MY_PREFS_NAME;

/**
 * Created by Megha on 07-07-2017.
 */

public class HomeActivity extends AppCompatActivity
{
    private HomePagerAdapter homePagerAdapter;
    private ViewPager viewPager;

    private String PARAMETER_CUSTOMERID = "CustomerID";
    private  final String METHOD_NAME_LIST =  "GetScanedHistory";
    private  final String NAMESPACE_LIST = "http://tempuri.org/";
    private String TAG = "ProductList Response : ";
    private String PRODUCTLIST_ACTION =NAMESPACE_LIST+METHOD_NAME_LIST;
    private List<ScanProduct> scanProductList = new ArrayList<>();
    private String customerId ="";

    public String getCustomerId() {
        return customerId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),this);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(homePagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(homePagerAdapter.getTabView(i));
        }
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        customerId = prefs.getString(CUSTOMER_ID, "");

        new CallWebService().execute(customerId);
    }

    public void updateBackUpDate(){
        new CallWebService().execute(customerId);
    }

    public List<ScanProduct> getScanProductList() {
        return scanProductList;
    }

    private void updateViews(){
        HomePagerAdapter adapter = ((HomePagerAdapter)viewPager.getAdapter());
        Fragment fragment = adapter.getFragment(1);
        if(fragment instanceof ProductListFragment){
            ((ProductListFragment)fragment).updateScanProductList(scanProductList);
        }
        //viewPager.getAdapter().notifyDataSetChanged();
    }

    class CallWebService extends AsyncTask<String, Void, SoapObject> {

        @Override
        protected void onPostExecute(SoapObject s) {

            try {
                Log.d(TAG,s.toString());
                scanProductList.clear();
                SoapObject soapObjectOne = (SoapObject) s.getProperty("diffgram");
                SoapObject soapObjectTwo = (SoapObject) soapObjectOne.getProperty("DocumentElement");

                for (int i = 0; i < soapObjectTwo.getPropertyCount(); i++) {
                    ScanProduct scanProduct = new ScanProduct();
                    SoapObject soapObject = (SoapObject) soapObjectTwo.getProperty(i);
                    scanProduct.productName =  soapObject.getProperty("Product_Name").toString();
                    scanProduct.standardCode = soapObject.getProperty("Standard_Code").toString();
                    scanProduct.productVersion = soapObject.getProperty("Product_Version").toString();
                    scanProduct.productImgUrl = soapObject.getProperty("ProductImgUrl").toString();

                    scanProductList.add(scanProduct);
                    Log.d("name", soapObject.getProperty("Product_Name") + "");
                    //Log.d("Rec_Status", soapObject.getProperty("Rec_Status") + "");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            updateViews();

        }

        @Override
        protected SoapObject doInBackground(String... params) {
            SoapObject soapPrimitive =null;
            SoapObject soapObject = new SoapObject(NAMESPACE_LIST, METHOD_NAME_LIST);

            PropertyInfo propertyInfoCustomerId = new PropertyInfo();
            propertyInfoCustomerId.setName(PARAMETER_CUSTOMERID);
            propertyInfoCustomerId.setValue(params[0]);
            propertyInfoCustomerId.setType(String.class);

            soapObject.addProperty(propertyInfoCustomerId);

            SoapSerializationEnvelope envelope =  getSoapSerializationEnvelope(soapObject);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE ht = new HttpTransportSE(Constant.BASE_URL);
            ht.debug = true;
            //ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
            try {
                ht.call(PRODUCTLIST_ACTION, envelope);
                 soapPrimitive = (SoapObject)envelope.getResponse();

                //result = soapPrimitive.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return soapPrimitive;
        }
        private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setAddAdornments(false);
            envelope.setOutputSoapObject(request);

            return envelope;
        }
    }

}
