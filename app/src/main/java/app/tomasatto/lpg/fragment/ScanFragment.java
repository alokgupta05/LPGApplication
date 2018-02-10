package app.tomasatto.lpg.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.activity.HomeActivity;
import app.tomasatto.lpg.activity.RegistrationActivity;
import app.tomasatto.lpg.utils.Constant;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Megha on 07-07-2017.
 */

public class ScanFragment extends Fragment  {

    private Context context;
    private ProgressDialog mProgressDialog;
    private String TAG ="Scan Response";
    private String PARAMETER_CUSTOMERID = "CustomerID";
    private String PARAMETER_QRCODE = "QRCode";
    private String SCAN_ACTION ="http://tempuri.org/GetScanedProductDetails";
    private String NAMESPACE_SCAN ="http://tempuri.org/";
    private String METHOD_NAME_SCAN ="GetScanedProductDetails";
    private String NO_QRCODE_TEXT = "The Product is not Genuine.";
    private String QRCODE_DETECTED = "The Product is Genuine.";
    private TextView txtProductName;
    private ZBarScannerView mScannerView;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;


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
                //startScan();
                //Intent intentScanner= new Intent(getActivity(),SimpleScannerActivity.class);
                //startActivity(intentScanner);
                launchActivity(SimpleScannerActivity.class);
                txtProductName.setText("");
            }
        });
        //mScannerView = new ZBarScannerView(getActivity());
        return view;
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(getActivity(), clss);
            startActivityForResult(intent,101);
        }
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
            if(s==null || s.getProperty("diffgram")==null )
                return;
            try {

                SoapObject soapObjectOne = (SoapObject) s.getProperty("diffgram");
                if(soapObjectOne.getProperty("DocumentElement")==null)
                    return;
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
                    txtProductName.setTextColor(ContextCompat.getColor(context,android.R.color.black));
                    if(getActivity() instanceof HomeActivity){
                        ((HomeActivity)getActivity()).updateBackUpDate();
                    }
                }
                else {
                    txtProductName.setTextColor(ContextCompat.getColor(context,android.R.color.holo_red_dark));
                    txtProductName.setText(NO_QRCODE_TEXT);
                }


            }catch (Exception e){
                txtProductName.setTextColor(ContextCompat.getColor(context,android.R.color.holo_red_dark));
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

        if (requestCode == 101 && resultCode == RESULT_OK) {
            //if qrcode has nothing in it
            if (intent.getStringExtra("ScannedData") == null) {
               // Toast.makeText(getActivity(), "No QR code is Detected", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                //Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                String customerId = ((HomeActivity) getActivity()).getCustomerId();
                new CallWebService().execute(customerId,intent.getStringExtra("ScannedData"));

            }
        }
       // Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
    }


}
