package cc.cnplay.uhf;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;



import java.util.ArrayList;
import java.util.List;

import cc.cnplay.uhf.fragment.KeyDwonFragment;

/**
 * Created by Administrator on 2015-03-10.
 */
public class BaseTabFragmentActivity extends FragmentActivity {

    protected ActionBar mActionBar;


    protected NoScrollViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;


    protected List<KeyDwonFragment> lstFrg = new ArrayList<KeyDwonFragment>();
    protected List<String> lstTitles = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


    }

    protected void initViewPageData() {

    }

    protected void initViewPager() {


        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), lstFrg, lstTitles);

        mViewPager = (NoScrollViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mViewPagerAdapter);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139 || keyCode==280) {

            if (event.getRepeatCount() == 0) {

                if (mViewPager != null) {

                    KeyDwonFragment sf = (KeyDwonFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
                    sf.myOnKeyDwon();


                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
