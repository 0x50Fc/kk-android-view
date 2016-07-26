package cn.kkserver.view.event;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zhanghailong on 16/7/7.
 */
public class EventEmitter {

    private List<PatternCallback> _callbacks = new LinkedList<PatternCallback>();

    /**
     * 监听事件
     * @param pattern
     * @param callback
     * @return
     */
    public EventEmitter on(Pattern pattern, Event.Callback callback) {
        _callbacks.add(new PatternCallback(pattern,callback));
        return this;
    }

    /**
     * 取消监听
     * @param pattern
     * @param callback
     * @return
     */
    public EventEmitter off(Pattern pattern, Event.Callback callback) {

        Iterator<PatternCallback> i = _callbacks.iterator();

        while(i.hasNext()) {
            PatternCallback p = i.next();

            if(p.callback.get() == null) {
                i.remove();
                continue;
            }

            if((pattern == null || pattern.equals(p.pattern))
                    && (callback == null || callback.equals(p.callback))) {
                i.remove();
            }
        }

        return this;
    }

    /**
     * 出发事件
     * @param event
     * @return
     */
    public EventEmitter emit(Event event) {

        List<PatternCallback> vs = new LinkedList<PatternCallback>();

        Iterator<PatternCallback> i = _callbacks.iterator();

        while(i.hasNext()) {

            PatternCallback p = i.next();

            if(p.callback.get() == null) {
                i.remove();
                continue;
            }

            if(p.pattern.matcher(event.name).find()) {
                vs.add(p);
            }

        }

        for(PatternCallback p: vs) {

            Event.Callback v = p.callback.get();

            if(v != null && v.onEvent(event) ==false) {
                break;
            }

        }

        return this;
    }

    private static class PatternCallback {

        public final Pattern pattern;
        public final WeakReference<Event.Callback> callback;

        public PatternCallback(Pattern pattern, Event.Callback callback) {
            this.pattern = pattern;
            this.callback = new WeakReference<Event.Callback>(callback);
        }
    }


}
