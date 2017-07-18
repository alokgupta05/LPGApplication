package app.tomasatto.lpg.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.activity.HomeActivity;
import app.tomasatto.lpg.utils.Constant;

/**
 * Created by Megha on 07-07-2017.
 */

public class ScanFragment extends Fragment {

    private Context context;
    private ProgressDialog mProgressDialog;
    private String TAG ="Scan Response";
    private String PARAMETER_CUSTOMERID = "CustomerID";
    private String PARAMETER_QRCODE = "QRCode";
    private String SCAN_ACTION ="http://tempuri.org/GetScanedProductDetails";
    private String NAMESPACE_SCAN ="http://tempuri.org/";
    private String METHOD_NAME_SCAN ="GetScanedProductDetails";
    private String NO_QRCODE_TEXT = "No QR code is detected. The Product is not Genuine";
    private String QRCODE_DETECTED = "QR code is detected. The Product is Genuine";
    private TextView txtProductName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_scan,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewScan);
        txtProductName = (TextView)view.findViewById(R.id.textViewProductName);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
        return view;
    }

    private void startScan(){
        txtProductName.setText("");
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        // use forSupportFragment or forFragment method to use fragments instead of activity
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setOrientationLocked(false);
        integrator.setPrompt(this.getString(R.string.scan_bar_code));
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        //integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();

    }

    private void showDialog(){
        dismissDialog();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Verifying...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissDialog(){
        if(mProgressDialog!=null&& mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }

    class CallWebService extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected void onPostExecute(SoapObject s) {
            //Log.d(TAG, s);
            dismissDialog();
            try {

                SoapObject soapObjectOne = (SoapObject) s.getProperty("diffgram");
                SoapObject soapObjectTwo = (SoapObject) soapObjectOne.getProperty("DocumentElement");
                boolean isProductAvailable = false;
                for (int i = 0; i < soapObjectTwo.getPropertyCount(); i++) {
                    SoapObject soapObject = (SoapObject) soapObjectTwo.getProperty(i);
                    Log.d("name", soapObject.getProperty("Product_Name") + "");
                    isProductAvailable = true;
                    break;
                }
                if(isProductAvailable) {
                    txtProductName.setText(QRCODE_DETECTED);
                    if(getActivity() instanceof HomeActivity){
                        ((HomeActivity)getActivity()).updateBackUpDate();
                    }
                }
                else
                    txtProductName.setText(NO_QRCODE_TEXT);


            }catch (Exception e){
                txtProductName.setText(NO_QRCODE_TEXT);
                Log.d("Scan Response", "Error");
                e.printStackTrace();;
            }
           // Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {
            SoapObject result = null;

            SoapObject soapObject = new SoapObject(NAMESPACE_SCAN, METHOD_NAME_SCAN);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName(PARAMETER_CUSTOMERID);
            propertyInfo.setValue(params[0]);
            propertyInfo.setType(String.class);

            PropertyInfo propertyInfo2 = new PropertyInfo();
            propertyInfo2.setName(PARAMETER_QRCODE);
            propertyInfo2.setValue(params[1]);
            propertyInfo2.setType(String.class);


            soapObject.addProperty(propertyInfo);
            soapObject.addProperty(propertyInfo2);

            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(soapObject);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE ht = new HttpTransportSE(Constant.BASE_URL);
            ht.debug = true;
            ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
            try {
                ht.call(SCAN_ACTION, envelope);
                result = (SoapObject) envelope.getResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
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

    /**
     * function handle scan result
     *
     * @param requestCode scanned code
     * @param resultCode  result of scanned code
     * @param intent      intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        //Toast.makeText(getActivity(), "Result  Found", Toast.LENGTH_LONG).show();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
               // Toast.makeText(getActivity(), "No QR code is Detected", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                String customerId = ((HomeActivity) getActivity()).getCustomerId();
                new CallWebService().execute(customerId,result.getContents());

            }
        }
       // Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
    }


}
