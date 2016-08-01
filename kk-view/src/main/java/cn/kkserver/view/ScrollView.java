package cn.kkserver.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import java.util.LinkedHashMap;
import java.util.Map;
import cn.kkserver.view.document.Element;
import cn.kkserver.view.document.LayoutElement;
import cn.kkserver.view.document.TouchElement;
import cn.kkserver.view.document.ViewElement;
import cn.kkserver.view.event.Event;
import cn.kkserver.view.reuse.ViewReuse;
import cn.kkserver.view.value.Rect;
import cn.kkserver.view.value.Size;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class ScrollView extends ElementView {

    private final static float FRICTION = 0.99f;
    private final static float FACTOR = 0.99f;
    private final static int VELOCITY_UNITS = 1000;
    private final static float VELOCITY_DURATION = 0.3f;
    private final static float SCROLL_MIN_VALUE = 15f;

    private int _maximumVelocity;
    private int _minimumVelocity;

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        _maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        _minimumVelocity = configuration.getScaledMinimumFlingVelocity();

        setScrollbarFadingEnabled(true);
        setScrollContainer(true);

    }

    public ScrollView(Context context) {
        super(context);
    }

    public ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private int _scrollX;
    private int _scrollY;

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x,y);
        _scrollX = x;
        _scrollY = y;
    }

    public void scrollTo(int x, int y, boolean animated) {
        if(animated) {
            beginScroller().startScroll(_scrollX,_scrollY,x - _scrollX,y - _scrollY,300);
            invalidate();
        }
        else {
            scrollTo(x,y);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if(_scroller != null ) {

            if(_scroller.computeScrollOffset()) {
                scrollTo(_scroller.getCurrX(), _scroller.getCurrY());
                invalidate();
            }
            else if(_scroller.isFinished()) {
                if(_decelerating == true) {
                    _decelerating = false;
                    onDeceleratingStop();
                }
            }

        }

    }

    public Size contentSize() {
        Element el = element();
        if(el != null) {
            if(el instanceof LayoutElement) {
                if(((LayoutElement) el).isLayouted()) {
                    return ((LayoutElement) el).contentSize();
                }
            }
        }
        return Size.Zero;
    }

    private Scroller _scroller;

    protected Scroller beginScroller() {

        if(_scroller == null) {
            _scroller = new Scroller(getContext(),new DecelerateInterpolator(FACTOR));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                _scroller.setFriction(FRICTION);
            }
        }
        else if(! _scroller.isFinished()) {
            _scroller.abortAnimation();
        }

        return _scroller;
    }

    protected void cancelScroller() {

        if(_scroller != null) {
            _scroller.abortAnimation();
        }

    }

    private VelocityTracker _tracker;

    protected VelocityTracker beginTracker() {

        if(_tracker == null) {
            _tracker = VelocityTracker.obtain();
        }
        else {
            _tracker.clear();
        }

        return _tracker;
    }

    protected void cancelTracker() {

        if(_tracker != null) {
            _tracker.recycle();
            _tracker = null;
        }

    }

    private int _touchId = -1;
    private float _touchX;
    private float _touchY;
    private int _touchScrollX;
    private int _touchScrollY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean r = dispatchScrollTouchEvent(ev);
        return super.dispatchTouchEvent(ev) || r;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l,t,oldl,oldt);

        Element el = element();

        if(el != null) {

            Element p = el.firstChild();

            while(p != null) {

                if(isVisibleElement(p)) {
                    addViewElement(p);
                }
                else {
                    removeViewElement(p);
                }

                p = p.nextSibling();
            }
        }

    }

    private boolean _dragging = false;
    private boolean _decelerating = false;

    protected void onDraggingStart() {

    }

    protected void onDraggingStop() {

    }

    protected void onDeceleratingStart() {

    }

    protected void onDeceleratingStop() {

    }

    private Boolean _paging;

    public boolean isPaging() {

        if(_paging == null) {

            Element el = this.element();

            if(el != null) {
                return el.booleanValue("paging",false);
            }

            return false;
        }

        return _paging.booleanValue();
    }

    public void setPaging(boolean v) {
        _paging = v;
    }

    protected boolean dispatchScrollTouchEvent(MotionEvent ev){

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            {

                if(_touchId == -1) {

                    _touchId = ev.getPointerId(0);
                    _touchX = ev.getX(0);
                    _touchY = ev.getY(0);
                    _touchScrollX = _scrollX;
                    _touchScrollY = _scrollY;

                    cancelScroller();

                    VelocityTracker tracker = beginTracker();

                    tracker.addMovement(ev);

                    return true;
                }
            }
            break;
            case MotionEvent.ACTION_MOVE:
            {

                int touchIndex = -1;
                int c = ev.getPointerCount();

                for(int i=0;i<c;i++){
                    if(ev.getPointerId(i) == _touchId){
                        touchIndex = i;
                        break;
                    }
                }

                if(touchIndex >= 0) {

                    _tracker.addMovement(ev);

                    float dx = ev.getX(touchIndex) - _touchX;
                    float dy = ev.getY(touchIndex) - _touchY;
                    int x = (int) (_touchScrollX - dx);
                    int y = (int) (_touchScrollY - dy);
                    int width = this.width();
                    int height = this.height();

                    Size size = contentSize();

                    if(size.width <= width) {
                        x = 0;
                    }

                    if(size.height <= height) {
                        y = 0;
                    }

                    if(_dragging == false && (Math.abs(dy) > SCROLL_MIN_VALUE && size.height > height)
                            || (Math.abs(dx) > SCROLL_MIN_VALUE && size.width > width)) {

                        for(int i=0;i<getChildCount();i++) {
                            View v = getChildAt(i);
                            if(v instanceof ElementView) {
                                ((ElementView) v).cancelDelayElementTouchBeginEvent();
                            }
                        }

                        Element el = element();

                        if(el != null) {

                            ElementScrollCanceledEvent scrollCanceledEvent = new ElementScrollCanceledEvent(el);

                            Element p = element().lastChild();

                            Element.ElementCanceledEvent cev = new Element.ElementCanceledEvent(el
                                    , new TouchElement.ElementTouchEvent(this, el, ev.getPointerId(touchIndex)
                                    , (int) ev.getX(touchIndex), (int) ev.getY(touchIndex), TouchElement.ElementTouchEvent.TouchType.CANCELED));

                            while (p != null) {
                                p.dispatchEvent(cev);
                                p.dispatchEvent(scrollCanceledEvent);
                                p = p.prevSibling();
                            }

                            if(el.parentElement() != null) {
                                el.parentElement().sendEvent(scrollCanceledEvent);
                            }
                        }

                        _dragging = true;
                        onDraggingStart();

                    }

                    if(_dragging) {
                        scrollTo(x, y);
                        return true;
                    }

                    return false;
                }

            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            {

                int touchIndex = -1;
                int c = ev.getPointerCount();

                for(int i=0;i<c;i++){
                    if(ev.getPointerId(i) == _touchId){
                        touchIndex = i;
                        break;
                    }
                }

                if(touchIndex >=0 && _touchId == 0){

                    if(_dragging == true) {
                        _dragging = false;
                        onDraggingStop();
                    }


                    _touchId = -1;

                    int x = _scrollX;
                    int y = _scrollY;
                    int width = this.width();
                    int height = this.height();

                    Size size = contentSize();

                    int duration = 300;

                    if(x < 0 ){
                        x = 0;
                    }

                    if(size.width > width && x > size.width - width) {
                        x = size.width - width;
                    }

                    if(y < 0 ){
                        y = 0;
                    }

                    if(size.height > height && y > size.height - height) {
                        y = size.height - height;
                    }

                    if(x == _scrollX && y == _scrollY) {

                        if(isPaging()) {

                            _tracker.computeCurrentVelocity(VELOCITY_UNITS,_maximumVelocity);

                            int velocityX = (int) _tracker.getXVelocity();
                            int velocityY = (int) _tracker.getYVelocity();

                            if(size.width > width && width > 0) {

                                int pageIndex = 0;

                                if(velocityX > 0){
                                    pageIndex = _touchScrollX / width - 1 ;
                                }
                                else if(velocityX < 0){
                                    pageIndex = _touchScrollX / width + 1;
                                }
                                else {
                                    pageIndex = _scrollX / width;
                                }

                                if(pageIndex <0) {
                                    pageIndex = 0;
                                }
                                else if(pageIndex >= size.width / width) {
                                    pageIndex = size.width / width;
                                }

                                x = pageIndex * width;

                            }
                            else if(size.height > height && height > 0) {

                                int pageIndex = 0;

                                if(velocityY > 0){
                                    pageIndex = _touchScrollY / height - 1 ;
                                }
                                else if(velocityY < 0){
                                    pageIndex = _touchScrollY / height + 1;
                                }
                                else {
                                    pageIndex = _touchScrollX / height;
                                }

                                if(pageIndex <0) {
                                    pageIndex = 0;
                                }
                                else if(pageIndex >= size.height / height) {
                                    pageIndex = size.height / height;
                                }

                                y = pageIndex * height;

                            }

                        }
                        else {

                            _tracker.addMovement(ev);

                            _tracker.computeCurrentVelocity(VELOCITY_UNITS,_maximumVelocity);

                            int velocityX = (int) _tracker.getXVelocity();
                            int velocityY = (int) _tracker.getYVelocity();

                            if(size.width > width && Math.abs(velocityX) > _minimumVelocity) {
                                x = _scrollX - velocityX;
                                if(x < 0) {
                                    x = 0;
                                }
                                else if(x > size.width - width) {
                                    x = size.width - width;
                                }
                                duration += Math.abs(x - _scrollX) * VELOCITY_DURATION;
                            }
                            else if(size.height > height && Math.abs(velocityY) > _minimumVelocity) {
                                y = _scrollY - velocityY;
                                if(y < 0) {
                                    y = 0;
                                }
                                else if(y > size.height - height) {
                                    y = size.height - height;
                                }
                                duration += Math.abs(y - _scrollY) * VELOCITY_DURATION;
                            }
                        }

                    }

                    cancelTracker();

                    if(x != _scrollX || y != _scrollY) {

                        if(_decelerating == false) {
                            _decelerating = true;
                            onDeceleratingStart();
                        }

                        beginScroller().startScroll(_scrollX,_scrollY,x - _scrollX,y - _scrollY,duration);
                        invalidate();
                    }


                    return true;
                }
            }
            break;
        }

        return false;
    }

    @Override
    protected int computeHorizontalScrollOffset(){

        int x = _scrollX;

        if(x < 0){
            x = 0;
        }

        return x;
    }

    @Override
    protected int computeHorizontalScrollRange(){

        int width = this.width();
        Size size = contentSize();

        if(size.width > width){
            width = size.width - width;
            if(_scrollX < 0){
                return size.width - _scrollX;
            }
            else if(_scrollX > width){
                return size.width + (_scrollX - width);
            }
            return size.width;
        }

        return 0;
    }

    @Override
    protected int computeVerticalScrollOffset(){

        int y = _scrollY;

        if(y < 0){
            y = 0;
        }

        return y;
    }

    @Override
    protected int computeVerticalScrollRange(){

        int height = this.height();
        Size size = contentSize();

        if(size.height > height){
            height = size.height - height;
            if(_scrollY < 0){
                return size.height - _scrollY;
            }
            else if(_scrollY > height){
                return size.height + (_scrollY - height);
            }
            return size.height;
        }

        return 0;
    }

    private Map<String,ViewReuse> _reuse = new LinkedHashMap<String,ViewReuse>();

    protected void onLoadViewElement(ViewElement viewElement,View view) {
        super.onLoadViewElement(viewElement,view);
        if(view instanceof ElementView) {
            ((ElementView) view).setDelayElementTouchBeginEvent(true);
        }
    }

    protected void addViewElement(Element element) {

        if(element instanceof ViewElement) {

            if(isVisibleElement(element)) {

                String key = element.stringValue("reuse","");

                ViewReuse reuse = null;

                if(key != null) {
                    if (_reuse.containsKey(key)) {
                        reuse = _reuse.get(key);
                    } else {
                        reuse = new ViewReuse();
                        _reuse.put(key, reuse);
                    }

                    ViewReuse.getter.push(reuse);
                }

                super.addViewElement(element);

                if(reuse != null) {
                    ViewReuse.getter.pop();
                }

            }

        }
        else {
            super.addViewElement(element);
        }

    }

    protected void removeViewElement(Element element) {

        if(element instanceof ViewElement) {

            String key = element.stringValue("reuse","");

            ViewReuse reuse = null;

            if(key != null) {

                if (_reuse.containsKey(key)) {
                    reuse = _reuse.get(key);
                } else {
                    reuse = new ViewReuse();
                    _reuse.put(key, reuse);
                }

                ViewReuse.setter.push(reuse);
            }

            super.removeViewElement(element);

            if(reuse != null) {
                ViewReuse.setter.pop();
            }

        }
        else {
            super.removeViewElement(element);
        }

    }


    protected boolean isVisibleElement(Element element) {

        if(element instanceof LayoutElement) {

            Rect frame = ((LayoutElement) element).frame();

            int l = _scrollX;
            int t = _scrollY;
            int r = l + this.width();
            int b = t + this.height();

            l = Math.max(l,frame.x);
            t = Math.max(t,frame.y);
            r = Math.min(r,frame.right());
            b = Math.min(b,frame.bottom());

            return r > l && b >t;
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        Element el = element();

        if(el != null) {

            Element p = el.firstChild();

            while(p != null) {

                if(isVisibleElement(p)) {
                    addViewElement(p);
                }
                else {
                    removeViewElement(p);
                }

                p = p.nextSibling();
            }
        }
    }


    @Override
    protected boolean onElementEvent(Event event) {

        if(event instanceof ElementScrollCanceledEvent) {

            if(_touchId != -1) {
                _touchId = -1;
            }

            if(_decelerating) {
                _decelerating = false;
                onDeceleratingStop();
            }

            if(_dragging) {
                _dragging = false;
                onDraggingStop();
            }

            return true;
        }

        if(event instanceof LayoutElement.ElementLayoutedEvent) {

            Element el = element();

            if(el != null) {

                Element p = el.firstChild();

                while(p != null) {

                    if(isVisibleElement(p)) {
                        addViewElement(p);
                    }
                    else {
                        removeViewElement(p);
                    }

                    p = p.nextSibling();
                }
            }

            return true;
        }

        if(event instanceof Element.ElementRemovedEvent) {
            setNeedsLayout();
        }

        if(event instanceof Element.ElementAddedEvent) {
            setNeedsLayout();
        }

        return super.onElementEvent(event);
    }

    /**
     * 取消滚动事件
     */
    public static class ElementScrollCanceledEvent extends Element.ElementEmitEvent {

        public final Element element;

        public ElementScrollCanceledEvent(Element element) {
            super("element.scroll.cancel");
            this.element = element;
        }
    }

}
