package cn.kkserver.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import cn.kkserver.view.document.CanvasElement;
import cn.kkserver.view.document.Element;
import cn.kkserver.view.document.IViewElement;
import cn.kkserver.view.document.LayoutElement;
import cn.kkserver.view.document.TouchElement;
import cn.kkserver.view.document.ViewElement;
import cn.kkserver.view.event.Event;
import cn.kkserver.view.reuse.IReuseGetter;
import cn.kkserver.view.reuse.IReuseSetter;
import cn.kkserver.view.reuse.ViewReuse;
import cn.kkserver.view.value.Rect;
import cn.kkserver.view.value.Size;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class ElementView extends ViewGroup implements IViewElement {

    private SparseArray<Element> _touchElements = new SparseArray<Element>();
    private final Event.Callback _callback = new EventCallback(this) ;

    private static class EventCallback extends Event.WeakCallback<ElementView> {

        public EventCallback(ElementView object) {
            super(object);
        }

        @Override
        public boolean onEvent(Event event) {

            ElementView v = object();

            if(v != null) {
                return v.onElementEvent(event);
            }

            return true;
        }

    }

    private boolean _needsLayout = false;

    public void setNeedsLayout() {
        _needsLayout = true;
    }

    protected boolean onElementEvent(Event event) {

        if(event instanceof Element.ElementCanceledEvent) {

            Event ev = ((Element.ElementCanceledEvent) event).event;

            if(ev instanceof TouchElement.ElementTouchEvent) {

                int touchId = ((TouchElement.ElementTouchEvent) ev).touchId;

                Element el = _touchElements.get(touchId);

                if(el != null){
                    el.sendEvent(ev);
                    _touchElements.remove(touchId);
                }
            }


        }
        else if (event instanceof CanvasElement.CanvasNeedsDisplay) {
            postInvalidate();
            event.cancelBubble();
        } else if (event instanceof Element.ElementRemovedEvent) {
            removeViewElement(((Element.ElementRemovedEvent) event).element);
            postInvalidate();
        } else if (event instanceof Element.ElementAddedEvent) {
            addViewElement(((Element.ElementAddedEvent) event).element);
            postInvalidate();
        } else if (event instanceof LayoutElement.ElementLayoutedEvent) {
            _needsLayout = true;
            requestLayout();
        }

        return true;
    }

    private Element _element;

    public Element element() {
        return _element;
    }

    protected void onLoadViewElement(ViewElement viewElement,View view) {

    }

    protected void addViewElement(Element element) {

        if(element instanceof ViewElement) {

            String display = element.stringValue("display",null);

            if("none".equals(display)) {

            }
            else {

                ViewElement viewElement = (ViewElement) element;

                View v = viewElement.view();

                if (v == null) {
                    IReuseGetter<View> getter = ViewReuse.getter.peek();
                    if (getter != null) {
                        Class<?> viewClass = viewElement.viewClass();
                        while (((v = getter.pull()) != null)) {
                            if (v.getClass() == viewClass) {
                                break;
                            } else if (v.getParent() != null) {
                                ((ViewGroup) v.getParent()).removeView(v);
                            }
                        }
                        if (v != null) {
                            viewElement.setView(v);
                        }
                    }
                }

                if (v == null) {
                    v = viewElement.loadView(getContext());
                    onLoadViewElement(viewElement, v);
                }

                if (v != null && v.getParent() != this) {
                    if (v.getParent() != null) {
                        ((ViewGroup) v.getParent()).removeView(v);
                    }
                    addView(v);
                }
            }

        }
        else if(element instanceof LayoutElement) {
            Element p = element.firstChild();
            while(p != null) {
                addViewElement(p);
                p = p.nextSibling();
            }
        }
    }

    protected void removeViewElement(Element element) {

        if(element instanceof ViewElement) {

            ViewElement viewElement = (ViewElement) element;

            View v = viewElement.view();

            if(v != null) {

                IReuseSetter<View> setter = ViewReuse.setter.peek();

                if(setter != null) {
                    setter.add(v);
                    viewElement.setView(null);
                    return ;
                }

                if(v.getParent() == this){
                    removeView(v);
                }
            }
        }
        else if(element instanceof LayoutElement) {
            Element p = element.firstChild();
            while(p != null) {
                removeViewElement(p);
                p = p.nextSibling();
            }
        }

    }

    protected void onChangeElement(Element element) {



    }

    public void setElement(Element element) {

        if(_element != element) {

            _touchElements.clear();

            if(_element != null) {
                _element.off(null,_callback);
                Element p = _element.firstChild();
                while(p != null) {
                    removeViewElement(p);
                    p = p.nextSibling();
                }
            }

            _element = element;

            if(_element != null) {
                _element.on(Pattern.compile("^element\\..*$"),_callback);
                Element p = _element.firstChild();
                while(p != null) {
                    addViewElement(p);
                    p = p.nextSibling();
                }
                onChangeElement(element);
            }

            postInvalidate();
            requestLayout();
        }
    }

    protected void init(AttributeSet attrs) {
        setWillNotDraw(false);
    }

    public ElementView(Context context) {
        super(context);
        init(null);
    }

    public ElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private Size _layoutSize;

    public Size layoutSize() {
        return _layoutSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(Integer.MAX_VALUE,widthMeasureSpec);
        int height = getDefaultSize(Integer.MAX_VALUE,heightMeasureSpec);

        if(_element != null && _element instanceof LayoutElement) {

            LayoutElement el = (LayoutElement) _element;

            if(! el.isLayouted() || _needsLayout) {
                _layoutSize = new Size(width,height);
                el.layout(_layoutSize);
            }
            else if(_layoutSize == null) {
                _layoutSize = new Size(width,height);
            }
            else if(_layoutSize.width != width || _layoutSize.height != height) {
                _layoutSize = new Size(width,height);
                el.layout(_layoutSize);
            }

            Rect r = el.frame();

            if(width == Integer.MAX_VALUE) {
                width = r.width;
            }

            if(height == Integer.MAX_VALUE) {
                height = r.height;
            }

        }

        if(width == Integer.MAX_VALUE) {
            width = 0;
        }

        if(height == Integer.MAX_VALUE) {
            height = 0;
        }

        setMeasuredDimension(width,height);

        measureChildren(widthMeasureSpec,heightMeasureSpec);

    }

    public int width() {
        if(_element != null && _element instanceof  LayoutElement && ((LayoutElement) _element).isLayouted()) {
            Rect frame = ((LayoutElement) _element).frame();
            return frame.width;
        }
        return getWidth();
    }

    public int height() {
        if(_element != null && _element instanceof  LayoutElement && ((LayoutElement) _element).isLayouted()) {
            Rect frame = ((LayoutElement) _element).frame();
            return frame.height;
        }
        return getHeight();
    }

    protected  void onElementLayout(Element element, int left , int top) {

        if(element instanceof ViewElement) {

            View v = ((ViewElement) element).view();

            if(v != null) {

                Rect frame = ((ViewElement) element).frame();
                v.layout(left + frame.x, top + frame.y , left + frame.right(),top + frame.bottom());
            }

        }
        else if(element instanceof LayoutElement) {

            Rect frame = ((LayoutElement) element).frame();

            Element p = element.firstChild();

            while(p != null) {

                if(p instanceof LayoutElement) {
                    onElementLayout(p,left + frame.x , top + frame.y);
                }

                p = p.nextSibling();
            }

        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(_element != null) {

            Element p = _element.firstChild();

            while(p != null) {

                if(p instanceof LayoutElement) {
                    onElementLayout(p,l , t);
                }

                p = p.nextSibling();
            }
        }

    }

    protected void drawElement(Canvas canvas, int width,int height, Element element){

        if(element != _element && element instanceof ViewElement) {
            return;
        }

        if(element instanceof LayoutElement) {

            if(element instanceof CanvasElement) {
                if(((CanvasElement) element).isHidden()) {
                    return ;
                }
            }

            Rect frame = ((LayoutElement)element).frame();

            int left = element == _element ? 0 : frame.x;
            int top = element == _element ? 0 : frame.y;
            int right = left + frame.width;
            int bottom = top + frame.height;

            right = Math.min(right,width);
            bottom = Math.min(bottom,height);

            int w = right - left;
            int h = bottom - top;

            if(w > 0 && h > 0) {

                canvas.save();

                canvas.translate(left, top );

                if(element instanceof CanvasElement){

                    canvas.clipRect(0, 0, w, h );

                    ((CanvasElement) element).draw(canvas);

                }
                else {
                    canvas.clipRect(0, 0, w , h );
                }

                Element p = element.firstChild();

                while(p != null) {

                    drawElement(canvas,w,h,p);

                    p = p.nextSibling();

                }


                canvas.restore();

            }
        }


    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(_element != null){
            drawElement(canvas,canvas.getWidth(),canvas.getHeight(),_element);
        }
    }

    /**
     * 延时处理开始事件
     */

    private static class DelayElementTouchBeginEvent implements Runnable {

        private final WeakReference<ElementView> _ref;

        public DelayElementTouchBeginEvent(ElementView view) {
            _ref = new WeakReference<ElementView>(view);
        }

        private TouchElement.ElementTouchEvent _event;

        public void sendEvent(TouchElement.ElementTouchEvent event) {

            ElementView v = _ref.get();

            if(v != null) {

                cancelEvent();

                _event = event;

                v.getHandler().postDelayed(this,100);

            }

        }

        public void cancelEvent() {

            ElementView v = _ref.get();

            if(v != null) {

                if(_event != null) {
                    v.getHandler().removeCallbacks(this);
                    _event = null;
                }

            }
        }

        public boolean doEvent(int touchId) {


            ElementView v = _ref.get();

            if(v != null) {

                if(_event != null && _event.touchId == touchId) {

                    v.getHandler().removeCallbacks(this);

                    Element el = v._touchElements.get(_event.touchId);

                    if(el != null) {
                        el.sendEvent(_event);
                    }

                    _event = null;

                    return true;
                }

            }

            return false;
        }

        public boolean doEvent() {

            ElementView v = _ref.get();

            if(v != null) {

                if(_event != null) {

                    v.getHandler().removeCallbacks(this);

                    Element el = v._touchElements.get(_event.touchId);

                    if(el != null) {
                        el.sendEvent(_event);
                    }

                    _event = null;

                    return true;
                }

            }

            return false;

        }

        @Override
        public void run() {

            ElementView v = _ref.get();

            if(v != null) {

                if(_event != null) {
                    Element el = v._touchElements.get(_event.touchId);
                    if(el != null) {
                        el.sendEvent(_event);
                    }
                    _event = null;
                }

            }

        }
    }

    private DelayElementTouchBeginEvent _delayElementTouchBeginEvent;

    public boolean isDelayElementTouchBeginEvent() {
        return _delayElementTouchBeginEvent != null;
    }

    public void setDelayElementTouchBeginEvent(boolean value) {
        if(value && _delayElementTouchBeginEvent == null) {
            _delayElementTouchBeginEvent = new DelayElementTouchBeginEvent(this);
        }
        else if(! value && _delayElementTouchBeginEvent != null) {
            _delayElementTouchBeginEvent.cancelEvent();
            _delayElementTouchBeginEvent = null;
        }
    }

    protected boolean onElementTouchBeginEvent(Element element,TouchElement.ElementTouchEvent event) {
        if(_delayElementTouchBeginEvent != null) {
            _delayElementTouchBeginEvent.sendEvent(event);
        }
        else {
            element.sendEvent(event);
        }
        return true;
    }

    public void cancelDelayElementTouchBeginEvent() {
        if(_delayElementTouchBeginEvent != null) {
            _delayElementTouchBeginEvent.cancelEvent();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(_element != null) {

            TouchElement.ElementTouchEvent.TouchType touchType = TouchElement.ElementTouchEvent.TouchType.END;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    touchType = TouchElement.ElementTouchEvent.TouchType.BEGIN;
                break;
                case MotionEvent.ACTION_UP:
                    touchType = TouchElement.ElementTouchEvent.TouchType.END;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchType = TouchElement.ElementTouchEvent.TouchType.MOVE;
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_CANCEL:
                    touchType = TouchElement.ElementTouchEvent.TouchType.CANCELED;
                    break;
            }

            if(touchType == TouchElement.ElementTouchEvent.TouchType.BEGIN) {

                for(int i=0;i<event.getPointerCount();i++){

                    TouchElement.ElementTouchEvent e = new TouchElement.ElementTouchEvent(this,_element
                            ,event.getPointerId(i),(int) event.getX(i),(int) event.getY(i),touchType);

                    Element p = _element.lastChild();
                    Element el = null;

                    while(p != null) {

                        el = (Element) p.dispatchEvent(e);

                        if(el != null) {
                            break;
                        }

                        p = p.prevSibling();
                    }

                    if(el != null) {
                        if( onElementTouchBeginEvent(el,e) ) {
                            _touchElements.put(e.touchId,el);
                            return true;
                        }
                        return false;
                    }
                    else {
                        _touchElements.remove(e.touchId);
                    }

                }
            }
            else {

                for(int i=0;i<event.getPointerCount();i++){


                    final TouchElement.ElementTouchEvent e = new TouchElement.ElementTouchEvent(this,_element
                            ,event.getPointerId(i),(int) event.getX(i),(int) event.getY(i),touchType);


                    final Element el = _touchElements.get(e.touchId);

                    if(el != null){

                        if(_delayElementTouchBeginEvent != null ) {

                            if(_delayElementTouchBeginEvent.doEvent(e.touchId) && touchType == TouchElement.ElementTouchEvent.TouchType.END) {

                                getHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        el.sendEvent(e);
                                    }
                                },200);

                                _touchElements.remove(e.touchId);

                                continue;
                            }

                        }

                        el.sendEvent(e);

                        if(touchType == TouchElement.ElementTouchEvent.TouchType.END
                                || touchType == TouchElement.ElementTouchEvent.TouchType.CANCELED) {
                            _touchElements.remove(e.touchId);
                        }
                    }

                }

            }

        }

        return super.onTouchEvent(event);
    }
}
