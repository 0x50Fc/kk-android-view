package cn.kkserver.view.obs;

/**
 * Created by zhanghailong on 16/7/13.
 */
public interface Listener {

    public void onChanged(IObserver observer, String[] keys);

}
