package app.tomasatto.lpg.activity;

import android.app.ProgressDialog;
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

public class RegistrationActivity  extends AppCompatActivity
{


    private TextView mSignUp;
    private String TAG ="Registration Response";
    private String PARAMETER_EMAIL = "EmailID";
    private String PARAMETER_MOBILE_NAME ="Mobile_No";
    private String PARAMETER_PASSWORD ="Password";
    private String PARAMETER_CUSTOMERNAME = "CustomerName";

    private String REGISTRATION_ACTION ="http://tempuri.org/UserRegistration";
    private String NAMESPACE_REGISTRATION ="http://tempuri.org/";
    private String METHOD_NAME_REGISTRATION ="UserRegistration";
    private EditText mEmailId;
    private EditText mCustomerName;
    private EditText mMobileNumber;
    private EditText mPassword;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mSignUp = (TextView)findViewById(R.id.textViewSignUp);
        mEmailId = (EditText)findViewById(R.id.editTextEmailId);
        mCustomerName = (EditText)findViewById(R.id.editTextCustomerName);
        mMobileNumber  = (EditText)findViewById(R.id.editTextMobile);
        mPassword = (EditText)findViewById(R.id.editTextPassword);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validationLogin())
                new CallWebService().execute(mMobileNumber.getText().toString(),mPassword.getText().toString()
                        ,mCustomerName.getText().toString(),mEmailId.getText().toString());
            }
        });

    }

    private boolean validationLogin(){
        boolean isValidDetail = true;
        if (mMobileNumber.getText().toString().trim().equalsIgnoreCase("")) {
            mMobileNumber.setError("This field cannot be empty");
            isValidDetail = false;
        }/*else if(isValidMobile(mMobileNumber.getText())){
            mMobileNumber.setError("Please enter a valid mobile number");
            isValidDetail = false;
        }*/
        if (mCustomerName.getText().toString().trim().equalsIgnoreCase("")) {
            mCustomerName.setError(" This field cannot be empty");
            isValidDetail = false;
        }
        if (mEmailId.getText().toString().trim().equalsIgnoreCase("") ) {
            mEmailId.setError("This field cannot be empty");
            isValidDetail = false;
        }/*else if(isValidEmail(mEmailId.getText())){
            mEmailId.setError("Please enter a valid Email-Id");
            isValidDetail = false;
        }*/
        if (mPassword.getText().toString().trim().equalsIgnoreCase("")) {
            mPassword.setError(" This field cannot be empty");
            isValidDetail = false;
        }
        return  isValidDetail;
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean isValidMobile(CharSequence phone) {
        if (phone == null) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    private void showDialog(){
        dismissDialog();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Registering...");
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
        protected void onPostExecute(String s) {
            Log.d(TAG,s);
            dismissDialog();
            if(s== null) {
                Toast.makeText(RegistrationActivity.this, "Error while Registration", Toast.LENGTH_LONG).show();;
            }else {
                Toast.makeText(RegistrationActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();;
            }
            RegistrationActivity.this.finish();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            SoapObject soapObject = new SoapObject(NAMESPACE_REGISTRATION, METHOD_NAME_REGISTRATION);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName(PARAMETER_MOBILE_NAME);
            propertyInfo.setValue(params[0]);
            propertyInfo.setType(String.class);

            PropertyInfo propertyInfo2 = new PropertyInfo();
            propertyInfo2.setName(PARAMETER_PASSWORD);
            propertyInfo2.setValue(params[1]);
            propertyInfo2.setType(String.class);

            PropertyInfo propertyInfoCustomer = new PropertyInfo();
            propertyInfoCustomer.setName(PARAMETER_CUSTOMERNAME);
            propertyInfoCustomer.setValue(params[2]);
            propertyInfoCustomer.setType(String.class);

            PropertyInfo propertyInfoEmail = new PropertyInfo();
            propertyInfoEmail.setName(PARAMETER_EMAIL);
            propertyInfoEmail.setValue(params[3]);
            propertyInfoEmail.setType(String.class);


            PropertyInfo propertyInfoAddress = new PropertyInfo();
            propertyInfoAddress.setName("Address");
            propertyInfoAddress.setValue("");
            propertyInfoAddress.setType(String.class);

            PropertyInfo propertyInfoCustomerId = new PropertyInfo();
            propertyInfoCustomerId.setName("CustomerID");
            propertyInfoCustomerId.setValue("0");
            propertyInfoCustomerId.setType(String.class);

            soapObject.addProperty(propertyInfo);
            soapObject.addProperty(propertyInfo2);
            soapObject.addProperty(propertyInfoCustomerId);
            soapObject.addProperty(propertyInfoAddress);
            soapObject.addProperty(propertyInfoCustomer);
            soapObject.addProperty(propertyInfoEmail);

            SoapSerializationEnvelope envelope =  getSoapSerializationEnvelope(soapObject);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE ht = new HttpTransportSE(Constant.BASE_URL);
            ht.debug = true;
            ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
            try {
                ht.call(REGISTRATION_ACTION, envelope);
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
