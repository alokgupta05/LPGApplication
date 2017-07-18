package app.tomasatto.lpg.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.activity.LoginActivity;

import static android.content.Context.MODE_PRIVATE;
import static app.tomasatto.lpg.activity.LoginActivity.CUSTOMER_ID;
import static app.tomasatto.lpg.activity.LoginActivity.MY_PREFS_NAME;

/**
 * Created by Megha on 07-07-2017.
 */

public class ContactUsFragment extends Fragment {

    private Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_contactus,null);
        TextView textView = (TextView)view.findViewById(R.id.textViewLogout);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogin = new Intent(getActivity(),LoginActivity.class);
                startActivity(intentLogin);
                SharedPreferences preferences = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                preferences.edit().remove(CUSTOMER_ID).commit();
                getActivity().finish();
            }
        });
        return view;
    }
}
