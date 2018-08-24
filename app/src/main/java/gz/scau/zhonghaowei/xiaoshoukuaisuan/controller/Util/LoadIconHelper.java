package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import gz.scau.zhonghaowei.xiaoshoukuaisuan.Model.DataServer;
import gz.scau.zhonghaowei.xiaoshoukuaisuan.R;

public class LoadIconHelper {

    private static void loadIcon(Context context, @DrawableRes int resource, ImageView imageView){
        Glide.with(context).load(resource).into(imageView);
    }

    public static void loadIconByClass(Context context, String classes, ImageView imageView){
        switch (classes){
            //食品酒水
            case "充饭卡":
                loadIcon(context,R.drawable.cfk,imageView);
                break;
            case "早午晚餐":
                loadIcon(context,R.drawable.zwwc,imageView);
                break;
            case "烟酒茶":
                loadIcon(context,R.drawable.yjc,imageView);
                break;
            case "水果零食":
                loadIcon(context,R.drawable.sgls,imageView);
                break;

            //衣服饰品
            case "衣服裤子":
                loadIcon(context,R.drawable.cloth,imageView);
                break;
            case "化妆饰品":
                loadIcon(context,R.drawable.hzsp,imageView);
                break;
            case "鞋帽包包":
                loadIcon(context,R.drawable.xmbb,imageView);
                break;

            //居家物业
            case "日常用品":
                loadIcon(context,R.drawable.rcyp,imageView);
                break;
            case "水电煤气":
                loadIcon(context,R.drawable.sdmq,imageView);
                break;
            case "房租":
                loadIcon(context,R.drawable.fz,imageView);
                break;
            case "物业管理":
                loadIcon(context,R.drawable.wygl,imageView);
                break;
            case "维修保养":
                loadIcon(context,R.drawable.wx,imageView);
                break;



            //行车交通
            case "公共交通":
                loadIcon(context,R.drawable.ggjt,imageView);
                break;
            case "打车租车":
                loadIcon(context,R.drawable.dczc,imageView);
                break;
            case "私家车费用":
                loadIcon(context,R.drawable.sjc,imageView);
                break;

            //交流通讯
            case "座机费":
                loadIcon(context,R.drawable.zjf,imageView);
                break;
            case "手机费":
                loadIcon(context,R.drawable.sjf,imageView);
                break;
            case "上网费":
                loadIcon(context,R.drawable.swf,imageView);
                break;
            case "邮寄费":
                loadIcon(context,R.drawable.yjf,imageView);
                break;


            //休闲娱乐
            case "运动健身":
                loadIcon(context,R.drawable.ydjs,imageView);
                break;
            case "腐败聚会":
                loadIcon(context,R.drawable.fbjh,imageView);
                break;
            case "休闲游戏":
                loadIcon(context,R.drawable.xxyl,imageView);
                break;
            case "宠物宝贝":
                loadIcon(context,R.drawable.cwbb,imageView);
                break;
            case "旅游度假":
                loadIcon(context,R.drawable.lvdj,imageView);
                break;


            //学习进修
            case "书报杂志":
                loadIcon(context,R.drawable.sbzz,imageView);
                break;
            case "培训进修":
                loadIcon(context,R.drawable.pxjx,imageView);
                break;
            case "数码装备":
                loadIcon(context,R.drawable.smsb,imageView);
                break;

            //人情往来
            case "送礼请客":
                loadIcon(context,R.drawable.slqk,imageView);
                break;
            case "孝敬家长":
                loadIcon(context,R.drawable.xjjz,imageView);
                break;
            case "还人钱物":
                loadIcon(context,R.drawable.hrqw,imageView);
                break;
            case "慈善捐助":
                loadIcon(context,R.drawable.csjz,imageView);
                break;


            //医疗保健
            case "药品费":
                loadIcon(context,R.drawable.ypf,imageView);
                break;
            case "保健费":
                loadIcon(context,R.drawable.bjf,imageView);
                break;
            case "美容费":
                loadIcon(context,R.drawable.mrf,imageView);
                break;
            case "治疗费":
                loadIcon(context,R.drawable.zlf,imageView);
                break;
            case "检查费":
                loadIcon(context,R.drawable.jcf,imageView);
                break;


            //金融保险
            case "银行手续":
                loadIcon(context,R.drawable.yhsx,imageView);
                break;
            case "投资亏损":
                loadIcon(context,R.drawable.tzks,imageView);
                break;
            case "按揭还款":
                loadIcon(context,R.drawable.ajhk,imageView);
                break;
            case "消费税收":
                loadIcon(context,R.drawable.xfns,imageView);
                break;
            case "利息支出":
                loadIcon(context,R.drawable.lxzc,imageView);
                break;
            case "赔偿罚款":
                loadIcon(context,R.drawable.pcfk,imageView);
                break;


            //其他杂项
            case "其他支出":
                loadIcon(context,R.drawable.qtzc,imageView);
                break;
            case "意外丢失":
                loadIcon(context,R.drawable.ywds,imageView);
                break;
            case "烂账损失":
                loadIcon(context,R.drawable.lzss,imageView);
                break;

            //收入部分
            //职业收入
            case "工资收入":
                loadIcon(context,R.drawable.gzsr,imageView);
                break;
            case "兼职收入":
                loadIcon(context,R.drawable.jzsr,imageView);
                break;
            case "加班收入":
                loadIcon(context,R.drawable.jbsr,imageView);
                break;
            case "经营收入":
                loadIcon(context,R.drawable.jysd,imageView);
                break;
            case "奖金收入":
                loadIcon(context,R.drawable.jjsr,imageView);
                break;

            //理财收入
            case "投资收入":
                loadIcon(context,R.drawable.tzsr,imageView);
                break;
            case "利息收入":
                loadIcon(context,R.drawable.lxsr,imageView);
                break;


            //其他收入
            case "礼金收入":
                loadIcon(context,R.drawable.ljsr,imageView);
                break;
            case "中奖收入":
                loadIcon(context,R.drawable.zjsr,imageView);
                break;
            case "意外收入":
                loadIcon(context,R.drawable.ywlq,imageView);
                break;


            //账户部分

            case "现金(CNY)":
                loadIcon(context,R.drawable.xj,imageView);
                break;
            case "银行卡(CNY)":
                loadIcon(context,R.drawable.yhk,imageView);
                break;
            case "支付宝(CNY)":
                loadIcon(context,R.drawable.zfb,imageView);
                break;
            case "微信支付(CNY)":
                loadIcon(context,R.drawable.wxzh,imageView);
                break;

            case "应付款项(CNY)":
                loadIcon(context,R.drawable.yfkx,imageView);
                break;
            case "花呗(CNY)":
                loadIcon(context,R.drawable.hb,imageView);
                break;
            case "京东白条(CNY)":
                loadIcon(context,R.drawable.jd,imageView);
                break;
            case "分期乐(CNY)":
                loadIcon(context,R.drawable.fql,imageView);
                break;
            case "应收款项(CNY)":
                loadIcon(context,R.drawable.yskx,imageView);
                break;
            case "余额宝(CNY)":
                loadIcon(context,R.drawable.yeb,imageView);
                break;
            case "基金账户(CNY)":
                loadIcon(context,R.drawable.jj,imageView);
                break;
            case "股票账户(CNY)":
                loadIcon(context,R.drawable.gp,imageView);
                break;
            case "其他理财账户(CNY)":
                loadIcon(context,R.drawable.qtlc,imageView);
                break;
        }

    }
}
