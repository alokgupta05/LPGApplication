package app.tomasatto.lpg.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.utils.Constant;

/**
 * Created by Megha on 28-07-2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity
{

    private String PARAMETER_MOBILE_NAME = "Mobile_No";
    private  final String METHOD_NAME_FORGOT =  "ForgetUserPassword";
    private  final String NAMESPACE_FORGOT = "http://tempuri.org/";
    private String TAG = "Forgot Password : ";
    private ProgressDialog mProgressDialog;
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        editText = (EditText) findViewById(R.id.editTextMobile);
        ImageView view = (ImageView)findViewById(R.id.imageView5);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity(view);
            }
        });

        TextView view1 = (TextView)findViewById(R.id.textViewReset);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordService(view);
            }
        });
    }

    private void closeActivity(View view){
        finish();
    }

    private void  resetPasswordService(View view){
        if(editText.getText().length()==10)
            new CallWebService().execute(editText.getText().toString());
        else{
            Toast.makeText(ForgotPasswordActivity.this,"Please enter a valid number",Toast.LENGTH_LONG).show();
        }
    }


    private void showDialog(){
        dismissDialog();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Resetting...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissDialog(){
        if(mProgressDialog!=null&& mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }
    class CallWebService extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            dismissDialog();
            if(response==null){
                return;
            }
            if(response.equalsIgnoreCase("Success")){
                Toast.makeText(ForgotPasswordActivity.this,"We have send an email to your registered Email-ID for password reset",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ForgotPasswordActivity.this,response,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            SoapObject soapObject = new SoapObject(NAMESPACE_FORGOT, METHOD_NAME_FORGOT);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName(PARAMETER_MOBILE_NAME);
            propertyInfo.setValue(params[0]);
            propertyInfo.setType(String.class);



            soapObject.addProperty(propertyInfo);
            SoapSerializationEnvelope envelope =  getSoapSerializationEnvelope(soapObject);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpTransportSE = new HttpTransportSE(Constant.BASE_URL);
            httpTransportSE.debug = true;
            httpTransportSE.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");

            try {
                httpTransportSE.call(NAMESPACE_FORGOT+METHOD_NAME_FORGOT, envelope);
                SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                result = soapPrimitive.toString();
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
}
