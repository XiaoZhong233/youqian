package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;


/**
 * @author Zhong.H.W
 */
public abstract class SingleModeFragmentForActivity extends AppCompatActivity{

    public abstract Fragment getFragment();
    public abstract String getTAG();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        FragmentManager fragmentManager=getSupportFragmentManager();
        setFragment(fragmentManager,getTAG());
    }

    private void setFragment(FragmentManager fm,String TAG){
        Fragment fragment=fm.findFragmentById(R.id.fragmenet_container);
        if(fragment==null) {
            fragment=getFragment();

            fm.beginTransaction().add(R.id.fragmenet_container,fragment,TAG).addToBackStack(TAG).commit();
        }
    }
}
