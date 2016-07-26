package cn.kkserver.view.value;

import java.util.Stack;

/**
 * Created by zhanghailong on 16/7/8.
 */
public class Pool<T> {

    private final ThreadLocal<Stack<T>> _local = new ThreadLocal<Stack<T>>();

    public void push(T object) {

        Stack<T> v = _local.get();

        if(v == null) {
            v = new Stack<T>();
            _local.set(v);
        }

        v.push(object);

    }

    public T peek() {

        Stack<T> v = _local.get();

        if(v != null && v.size() > 0) {
            return v.peek();
        }

        return null;
    }

    public T pop() {

        Stack<T> v = _local.get();

        if(v != null && v.size() > 0) {
            return v.pop();
        }

        return null;
    }

}
