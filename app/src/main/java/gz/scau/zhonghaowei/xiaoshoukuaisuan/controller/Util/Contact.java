package gz.scau.zhonghaowei.xiaoshoukuaisuan.controller.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Contact {

    public  static void  onContact(Context context){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:610596547@qq.com"));
        //intent.putExtra(Intent.EXTRA_CC, new String[]
        //        {"610596547@qq.com"});
        intent.putExtra(Intent.EXTRA_EMAIL, "");
        intent.putExtra(Intent.EXTRA_TEXT, "欢迎提供您的意见或建议-----author 小钟");
        context.startActivity(Intent.createChooser(intent, "选择邮件客户端"));
    }
}
