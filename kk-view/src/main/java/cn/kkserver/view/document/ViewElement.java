package cn.kkserver.view.document;

import android.content.Context;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import cn.kkserver.view.ElementView;
import cn.kkserver.view.event.Event;
import cn.kkserver.view.event.EventDispatcher;
import cn.kkserver.view.value.Loader;
import cn.kkserver.view.value.Point;
import cn.kkserver.view.value.Pool;
import cn.kkserver.view.value.Rect;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class ViewElement extends LayoutElement {

    private WeakReference<View> _view;

    public View view() {
        return _view == null ? null : _view.get();
    }

    public void setView(View view) {

        View v = _view == null ? null : _view.get();

        if(v != view) {
            if(v != null && v instanceof IViewElement) {
                ((IViewElement) v).setElement(null);
            }
            if(view == null) {
                _view = null;
            }
            else {
                _view = new WeakReference<View>(view);
                if(view instanceof IViewElement) {
                    ((IViewElement) view).setElement(this);
                }
            }
        }

    }

    public ViewElement(Document document, String name, int elementId) {
        super(document, name, elementId);
    }

    public Class<?> viewClass () {

        String name = stringValue("view",null);

        if(name == null) {
            return ElementView.class;
        }
        else {
            try {
                return Loader.peek().loadClass(name);
            }
            catch(Throwable e) {
                Log.d("kk-view",e.getMessage(),e);
                return ElementView.class;
            }
        }

    }

    public View loadView(Context context) {

        View v = view();

        if(v == null) {

            String name = stringValue("view",null);

            if(name == null) {
                v = new ElementView(context);
            }
            else {
                try {
                    Class<?> clazz = Loader.peek().loadClass(name);
                    Constructor<?> constructor = clazz.getConstructor(Context.class);
                    v = (View) constructor.newInstance(context);
                }
                catch(Throwable e) {
                    v = new ElementView(context);
                    Log.d("kk-view",e.getMessage(),e);
                }
            }

            setView(v);

        }

        return v;
    }

    @Override
    public void sendEvent(Event event) {

        if(event instanceof CanvasElement.CanvasNeedsDisplay
                || event instanceof ElementRemovedEvent
                || event instanceof ElementAddedEvent
                || event instanceof ElementLayoutedEvent) {
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

        if(event instanceof TouchElement.ElementTouchEvent) {

            TouchElement.ElementTouchEvent e = (TouchElement.ElementTouchEvent) event;
            Point p = e.locationInElement(this);
            Rect frame = this.frame();

            if(p.x >=0 && p.x < frame.width
                    && p.y >= 0 && p.y < frame.height) {

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

}

