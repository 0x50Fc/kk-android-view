package cn.kkserver.view.value;

/**
 * Created by zhanghailong on 16/8/1.
 */
public class Loader {

    private final static Pool<ClassLoader> _classLoader = new Pool<ClassLoader>();

    public final static void push(ClassLoader v) {
        _classLoader.push(v);
    }

    public final static ClassLoader peek() {
        return _classLoader.peek();
    }

    public final static ClassLoader pop() {
        return _classLoader.pop();
    }

}
