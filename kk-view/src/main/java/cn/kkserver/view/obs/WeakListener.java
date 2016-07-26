package cn.kkserver.view.obs;

import java.lang.ref.WeakReference;

/**
 * Created by zhanghailong on 16/7/13.
 */
public abstract class WeakListener<T extends Object> implements Listener {

    private WeakReference<T> _ref;

    public WeakListener(T object) {
        _ref = new WeakReference<T>(object);
    }

    public T object() {
        return _ref.get();
    }

    @Override
    public void onChanged(IObserver observer, String[] keys) {
        onChanged(observer,keys,_ref.get());
    }

    abstract protected void onChanged(IObserver observer,String[] keys, T v);

}

