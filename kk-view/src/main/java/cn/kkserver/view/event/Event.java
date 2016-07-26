package cn.kkserver.view.event;

import java.lang.ref.WeakReference;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class Event {

    /**
     * 事件名称
     */
    public final String name;

    /**
     * 创建事件
     * @param name
     */
    public Event(String name) {
        this.name = name;
    }

    private boolean _cancelBubble = false;

    /**
     * 是否父级传递
     * @return
     */
    public boolean isCancelBubble() {
        return _cancelBubble;
    }

    /**
     * 取消父级传递
     */
    public void cancelBubble() {
        _cancelBubble = true;
    }

    public static interface Callback {

        public boolean onEvent(Event event);

    }

    public static abstract class WeakCallback<T> implements Callback {

        private final WeakReference<T> _ref;

        public WeakCallback(T object) {
            _ref = new WeakReference<T>(object);
        }

        public T object() {
            return _ref.get();
        }

    }
}
