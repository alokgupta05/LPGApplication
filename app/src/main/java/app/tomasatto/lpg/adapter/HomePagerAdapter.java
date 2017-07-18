package app.tomasatto.lpg.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ConcurrentHashMap;

import app.tomasatto.lpg.R;
import app.tomasatto.lpg.fragment.ContactUsFragment;
import app.tomasatto.lpg.fragment.ProductListFragment;
import app.tomasatto.lpg.fragment.ScanFragment;

/**
 * Created by Megha on 07-07-2017.
 */

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    String tabTitles[] = new String[] { "Home", "History", "Profile" };
    final int[] ICONS = new int[]{
            R.drawable.ic_home,
            R.drawable.ic_history_big,
            R.drawable.ic_profile_big};
    private Context context;
    private ConcurrentHashMap<Integer,Fragment> mPageReferenceMap = new ConcurrentHashMap();

    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment myFragment =null;
        if(position == 0)
            myFragment =  new ScanFragment();
        else if (position==1)
            myFragment = new ProductListFragment();
        else{
            myFragment = new ContactUsFragment();
        }
        mPageReferenceMap.put(position, myFragment);
        return myFragment;
        //PageFragment.getInstance(pageContents.get(position), position);
    }

    public Fragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }
    @Override
    public int getCount() {
        return tabTitles.length ;
    }

    @Override
    public CharSequence getPageTitle(int position) {

            return tabTitles[position];
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(context).inflate(R.layout.view_custom_tab, null);
        TextView tv = (TextView) tab.findViewById(R.id.custom_text);
        ImageView imageView =(ImageView)tab.findViewById(R.id.imageView);
        imageView.setImageResource(ICONS[position]);
        tv.setText(tabTitles[position]);
        return tab;
    }


}