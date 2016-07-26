package cn.kkserver.view.obs;

/**
 * Created by zhanghailong on 16/7/26.
 */
public interface IObserver extends IObject {

    public IObserver change(String[] keys);

    public IObserver on(Listener listener,String[] keys);

    public IObserver off(Listener listener,String[] keys);

    public IWithObserver with(String[] keys);

    public IWithObserver with(String[] keys,Object value);

}
