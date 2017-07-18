package app.tomasatto.lpg.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
 * Created by Megha on 07-07-2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String LOGIN_ACTION  = "http://tempuri.org/UserLogin";;
    public static final String MY_PREFS_NAME = "TOM" ;
    public static final String CUSTOMER_ID = "CustomerID";
    private TextView mSignInView;
    private TextView mSignUpView;
    private String PARAMETER_MOBILE_NAME = "Mobile_No";
    private String PARAMETER_PASSWORD ="Password";
    private  final String METHOD_NAME_LOGIN =  "UserLogin";
    private  final String NAMESPACE_LOGIN = "http://tempuri.org/";
    private String TAG = "Login Response : ";
    private EditText mTextInputMobile;
    private EditText mTextInputPassword;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

        mSignInView = (TextView) findViewById(R.id.textViewSignIn);
        mSignUpView = (TextView)findViewById(R.id.textViewSignUp);
        mSignInView.setOnClickListener(this);
        mSignUpView.setOnClickListener(this);
        mTextInputMobile = (EditText)findViewById(R.id.editTextMobile);
        mTextInputPassword = (EditText)findViewById(R.id.editTextPassword);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.textViewSignIn && validationLogin()){
                    new CallWebService().execute(mTextInputMobile.getText().toString(),mTextInputPassword.getText().toString());

        }else if(view.getId() == R.id.textViewSignUp){
            Intent intentRegistration = new Intent(this,RegistrationActivity.class);
            startActivity(intentRegistration);
        }
    }

    private boolean validationLogin(){
        if (mTextInputMobile.getText().toString().trim().equalsIgnoreCase("")) {
            mTextInputMobile.setError("Username field cannot be empty");
            return false;
        }
        if (mTextInputPassword.getText().toString().trim().equalsIgnoreCase("")) {
            mTextInputPassword.setError(" Password field cannot be empty");
            return false;
        }
        return  true;
    }

    private void showDialog(){
        dismissDialog();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing In...");
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
        protected void onPostExecute(String customerId) {
            dismissDialog();
            Log.d(TAG,customerId);
            if(isFinishing() == true)
                return;
            if(customerId.equalsIgnoreCase("0")){
                Toast.makeText(LoginActivity.this,"Invaild Login Credentials",Toast.LENGTH_LONG).show();
            }else{

                Intent intentRegistration = new Intent(LoginActivity.this,HomeActivity.class);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(CUSTOMER_ID, customerId);
                editor.apply();
                editor.commit();
                startActivity(intentRegistration);
                LoginActivity.this.finish();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            SoapObject soapObject = new SoapObject(NAMESPACE_LOGIN, METHOD_NAME_LOGIN);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName(PARAMETER_MOBILE_NAME);
            propertyInfo.setValue(params[0]);
            propertyInfo.setType(String.class);

            PropertyInfo propertyInfo2 = new PropertyInfo();
            propertyInfo2.setName(PARAMETER_PASSWORD);
            propertyInfo2.setValue(params[1]);
            propertyInfo2.setType(String.class);

            soapObject.addProperty(propertyInfo);
            soapObject.addProperty(propertyInfo2);
            SoapSerializationEnvelope envelope =  getSoapSerializationEnvelope(soapObject);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpTransportSE = new HttpTransportSE(Constant.BASE_URL);
            httpTransportSE.debug = true;
            httpTransportSE.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");

            try {
                httpTransportSE.call(LOGIN_ACTION, envelope);
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
