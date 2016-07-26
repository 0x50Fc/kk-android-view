package cn.kkserver.view.io;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by zhanghailong on 16/7/17.
 */
public class IO {

    private HandlerThread _thread;
    private Handler _handler;

    protected Handler getHandler() {

        if(_handler == null) {
            _thread = new HandlerThread("kk-view IO");
            _thread.start();
            _handler = new Handler(_thread.getLooper());
        }

        return _handler;
    }

    private static IO _current;

    public static IO current() {
        if(_current == null) {
            _current = new IO();
        }
        return _current;
    }

}
