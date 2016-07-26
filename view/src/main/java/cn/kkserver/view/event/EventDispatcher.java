package cn.kkserver.view.event;

/**
 * Created by zhanghailong on 16/7/8.
 */
public class EventDispatcher extends EventEmitter {

    /**
     * 派发事件
     * @param event
     * @return
     */
    public EventDispatcher dispatchEvent(Event event) {
        return this;
    }

    /**
     * 发送事件
     * @param event
     */

    public void sendEvent(Event event) {
        emit(event);
    }

}
