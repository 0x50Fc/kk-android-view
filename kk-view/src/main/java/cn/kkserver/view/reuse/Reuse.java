package cn.kkserver.view.reuse;

import java.util.LinkedList;
import java.util.Queue;

import cn.kkserver.view.value.Pool;

/**
 * Created by zhanghailong on 16/7/22.
 */
public class Reuse<T> implements IReuseGetter<T>,IReuseSetter<T>{

    private Queue<T> _queue = new LinkedList<T>();

    @Override
    public T poll() {
        return _queue.poll();
    }

    @Override
    public void add(T value) {
        _queue.add(value);
    }

}
