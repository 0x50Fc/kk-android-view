package cn.kkserver.view.reuse;

import android.view.View;
import cn.kkserver.view.value.Pool;

/**
 * Created by zhanghailong on 16/7/22.
 */
public class ViewReuse extends Reuse<View> {

    public static Pool<IReuseGetter<View>> getter = new Pool<IReuseGetter<View>>();

    public static Pool<IReuseSetter<View>> setter = new Pool<IReuseSetter<View>>();

}
