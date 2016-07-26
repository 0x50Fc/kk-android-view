package cn.kkserver.view.document;

import android.os.Handler;
import android.view.View;

import cn.kkserver.view.event.Event;
import cn.kkserver.view.event.EventDispatcher;
import cn.kkserver.view.value.Point;
import cn.kkserver.view.value.Rect;

/**
 * Created by zhanghailong on 16/7/22.
 */
public class TouchElement extends LayoutElement {

    private Handler _touchHandler;

    public TouchElement(Document document, String name, int elementId) {
        super(document, name, elementId);
        _touchHandler = new Handler();
    }

    private Boolean _enabledTouch ;

    public boolean isEnabledTouch() {

        if(_enabledTouch == null) {
            return booleanValue("enabled",true);
        }

        return _enabledTouch;
    }

    public void setEnabledTouch(boolean value) {
        _enabledTouch = value;
    }

    private boolean _hover = false;
    public boolean isHover() {
        return _hover;
    }

    public void setHover(boolean hover) {
        if(_hover != hover) {
            _hover = hover;
            onChangeKey("status");

            Element p = firstChild();

            while( p != null) {

                if(p instanceof TouchElement) {
                    ((TouchElement) p).setHover(hover);
                }

                p = p.nextSibling();
            }
        }
    }

    @Override
    public String status() {

        String v = super.status();

        if(_hover && "".equals(v)) {
            return "hover";
        }

        return v;
    }

    private boolean _touchInAction;

    /**
     * 发送事件
     * @param event
     */
    @Override
    public void sendEvent(Event event) {

        if(event instanceof ElementTouchEvent && isEnabledTouch()) {

            ElementTouchEvent e = (ElementTouchEvent) event;

            if(e.type == ElementTouchEvent.TouchType.END) {

                String action = stringValue("action",null);

                if(_touchInAction && action != null && isHover()) {

                    final ElementTouchActionEvent ev = new ElementTouchActionEvent(this,action);

                    _touchHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            sendEvent(ev);
                        }
                    });

                }

            }

            if (e.type == ElementTouchEvent.TouchType.CANCELED || e.type == ElementTouchEvent.TouchType.END) {
                setHover(false);
            } else {
                Point p = e.locationInElement(this);
                Rect frame = this.frame();
                _touchInAction = p.x >= 0 && p.x < frame.width
                        && p.y >= 0 && p.y < frame.height;
                setHover(_touchInAction);
            }

            event.cancelBubble();

        }

        super.sendEvent(event);
    }


    /**
     * 派发事件
     * @param event
     * @return
     */
    public EventDispatcher dispatchEvent(Event event) {

        if(event instanceof ElementTouchEvent) {

            if(! isEnabledTouch()) {
                return null;
            }

            ElementTouchEvent e = (ElementTouchEvent) event;
            Point p = e.locationInElement(this);
            Rect frame = this.frame();

            if(p.x >=0 && p.x < frame.width
                    && p.y >= 0 && p.y < frame.height) {

                Element el = lastChild();

                while(el != null) {

                    EventDispatcher r = el.dispatchEvent(event);

                    if(r != null) {
                        return r;
                    }

                    el = el.prevSibling();

                }

                return this;

            }
            else {
                return null;
            }
        }
        else {
            return super.dispatchEvent(event);
        }
    }

    public static class ElementTouchEvent extends Event {

        public final View view;
        public final Element element;
        public final int touchId;
        public final int x;
        public final int y;
        public final TouchType type;

        public ElementTouchEvent(View view, Element element,int touchId,int x, int y,TouchType type) {
            super("element.touch");
            this.view = view;
            this.element = element;
            this.type = type;
            this.touchId = touchId;
            this.x = x;
            this.y = y;
        }

        public Point locationInElement(Element element) {
            return locationInElement(element,null);
        }

        public Point locationInElement(Element element,Point point) {

            if(point == null) {
                point = new Point(0,0);
            }

            point.x = x;
            point.y = y;

            while(element != this.element && element != null) {

                if(element instanceof LayoutElement) {
                    Rect frame = ((LayoutElement) element).frame();
                    point.x -= frame.x;
                    point.y -= frame.y;
                }

                element = element.parentElement();
            }

            return point;
        }

        public static enum TouchType {
            BEGIN,MOVE,END,CANCELED
        }

    }

    public static class ElementTouchActionEvent extends Event {

        public final String action;
        public final Element element;

        public ElementTouchActionEvent(Element element,String action) {
            super("element.action");
            this.element = element;
            this.action = action;
        }
    }

}
