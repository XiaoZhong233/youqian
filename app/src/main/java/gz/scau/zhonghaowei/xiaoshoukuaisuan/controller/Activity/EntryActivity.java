package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Activity;

import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.RecordLab;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.CalculateFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.QitaFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.RecordListFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.TubiaoFragment;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Fragment.ZhanghuFragment;

public class EntryActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 用于展示流水的fragment
     */
    private Fragment recordListFragment;
    /**
     * 用于展示AA计算的fragment
     */
    private Fragment calculateFragment;

    /**
     * 用于展示账户信息的fragment
     */
    private Fragment zhanghuFragment;

    /**
     * 用于展示图表统计信息
     */
    private Fragment tubiaoFragment;

    /**
     * 用于展示其他信息
     */
    private Fragment qitaFragment;
    /**
     * 导航栏按钮布局
     */
    public static ConstraintLayout bottom_layout;
    private View jiantouLayout;
    private View liushuiLayout;
    private View zhanghuLayout;
    private View tubiaoLayout;
    private View jisuanLayout;
    private View qitaLayout;

    /**
     * 抽屉布局
     */
    private View navi_Layout;

    /**
     * 抽屉控件
     */
    private CircleImageView touxiang;
    private TextView user_name;
    private View defaulyBook;
    private View familyBook;

    /**
     * 控件
     */
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    /**
     * 容器布局
     */
    private FrameLayout container;

    /**
     * 用于管理fragment
     */
    private FragmentManager fragmentManager;
    private static final String TAG = "EntryActivity";


    /** 生命周期------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化，默认透明状态栏和黑色导航栏
        ImmersionBar.with(this)
                .keyboardEnable(false)//解决软键盘与底部输入框冲突问题
                .init();
        setContentView(R.layout.activity_entry);
        //初始化布局元素
        initView();
        //获取Manager
        fragmentManager=getSupportFragmentManager();
        setTabSection(0);

    }

    @Override
    protected void onDestroy() {
        RecordLab.getRecordLab(this).saveData();
        //CountLab.getCountLab(this).saveData();
        super.onDestroy();
        //不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        ImmersionBar.with(this).destroy();
    }

    /** 生命周期------------------------------------------------------------------------------------*/

    /** 初始化------------------------------------------------------------------------------------*/
    private void initView(){
        jiantouLayout=findViewById(R.id.jiantou);
        liushuiLayout=findViewById(R.id.liushui);
        zhanghuLayout=findViewById(R.id.zhanghu);
        tubiaoLayout=findViewById(R.id.tubiao);
        jisuanLayout=findViewById(R.id.jisuan);
        qitaLayout=findViewById(R.id.qita);
        container=findViewById(R.id.container);
        //toolbar=findViewById(R.id.toolbar);
        drawerLayout=findViewById(R.id.drawerLayout);
        navi_Layout=findViewById(R.id.navi_layout);
        touxiang=navi_Layout.findViewById(R.id.touxiang);
        user_name=navi_Layout.findViewById(R.id.user_name);
        defaulyBook=navi_Layout.findViewById(R.id.defaultBook);
        familyBook=navi_Layout.findViewById(R.id.familyBook);
        Glide.with(EntryActivity.this).load(R.mipmap.touxiang).into(touxiang);
        bottom_layout=findViewById(R.id.linearLayout);


        jiantouLayout.setOnClickListener(this);
        liushuiLayout.setOnClickListener(this);
        zhanghuLayout.setOnClickListener(this);
        tubiaoLayout.setOnClickListener(this);
        jisuanLayout.setOnClickListener(this);
        qitaLayout.setOnClickListener(this);
        //setSupportActionBar(toolbar);
        setNaviClickListener();
    }
    /** 初始化------------------------------------------------------------------------------------*/


    /** UI控制------------------------------------------------------------------------------------*/

    /**
     * 设置导航栏点击事件
     */
    private void setNaviClickListener(){
        touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(EntryActivity.this,"click",Toast.LENGTH_SHORT).show();
                Animation animation = AnimationUtils.loadAnimation(EntryActivity.this,R.anim.anim_round_rotate);
                AccelerateDecelerateInterpolator adi = new AccelerateDecelerateInterpolator();
                animation.setInterpolator(adi);
                if(animation!=null){
                    touxiang.startAnimation(animation);
                }
            }
        });

        familyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TastyToast.makeText(EntryActivity.this,"暂未开发,敬请期待",TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            }
        });

        defaulyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TastyToast.makeText(EntryActivity.this,"暂未开发,敬请期待",TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            }
        });
    }


    /**
     * 设置某个tab布局
     * @param view
     */
    private void recoverColor(View view){
        view.setBackgroundColor(Color.parseColor("#808080"));
        view.setAlpha(1.0f);
    }

    private void setSelectedColor(View view){
        view.setBackgroundColor(Color.parseColor("#5B5B5B"));
        view.setAlpha(0.7f);
    }


    /**
     * 还原全部tab布局样式
     */
    private void clearTabSection(){
        recoverColor(liushuiLayout);
        recoverColor(zhanghuLayout);
        recoverColor(tubiaoLayout);
        recoverColor(jisuanLayout);
        recoverColor(qitaLayout);
    }


    /**
     * 设置每一个tab对应的点击事件
     * @param index
     */
    private void setTabSection(int index){
        //每次选中前要恢复原样
        clearTabSection();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        //隐藏所有的fragment
        hideAllFragment(fragmentTransaction);
        switch (index){
            case 0:
                setSelectedColor(liushuiLayout);
                if(recordListFragment == null){
                    recordListFragment = RecordListFragment.newIntance();
                    fragmentTransaction.add(R.id.container,recordListFragment);
                }else{
                    fragmentTransaction.show(recordListFragment);
                }
                break;
            case 1:
                setSelectedColor(zhanghuLayout);
                if(zhanghuFragment == null){
                    zhanghuFragment = ZhanghuFragment.newIntance();
                    fragmentTransaction.add(R.id.container,zhanghuFragment);
                }else {
                    fragmentTransaction.show(zhanghuFragment);
                }
                break;
            case 2:
                setSelectedColor(tubiaoLayout);
                if(tubiaoFragment == null){
                    tubiaoFragment= TubiaoFragment.newIntance();
                    fragmentTransaction.add(R.id.container,tubiaoFragment);
                }else{
                    fragmentTransaction.show(tubiaoFragment);
                }
                break;
            case 3:
                setSelectedColor(jisuanLayout);
                if(calculateFragment == null){
                    calculateFragment = CalculateFragment.newIntance();
                    fragmentTransaction.add(R.id.container,calculateFragment);
                }else{
                    fragmentTransaction.show(calculateFragment);
                }
                break;
            case 4:
                setSelectedColor(qitaLayout);
                if(qitaFragment == null){
                    qitaFragment= QitaFragment.newIntance();
                    fragmentTransaction.add(R.id.container,qitaFragment);
                }else{
                    fragmentTransaction.show(qitaFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }


    /**
     * 隐藏fragment,直接隐藏可以避免重新开始frament的生命周期
     * @param transaction
     */
    private void hideAllFragment(FragmentTransaction transaction){
        if(recordListFragment!=null){
            transaction.hide(recordListFragment);
        }
        if(calculateFragment!=null){
            transaction.hide(calculateFragment);
        }
        if(zhanghuFragment!=null){
            transaction.hide(zhanghuFragment);
        }
        if(tubiaoFragment!=null){
            transaction.hide(tubiaoFragment);
        }
        if(qitaFragment!=null){
            transaction.hide(qitaFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.liushui:
                setTabSection(0);
                break;
            case R.id.zhanghu:
                setTabSection(1);
                break;
            case R.id.tubiao:
                setTabSection(2);
                break;
            case R.id.jisuan:
                setTabSection(3);
                break;
            case R.id.qita:
                setTabSection(4);
                break;
            case R.id.jiantou:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
                break;
        }

    }
    /** UI控制------------------------------------------------------------------------------------*/

}
