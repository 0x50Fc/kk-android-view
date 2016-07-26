package cn.kkserver.view.document;

import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.regex.Pattern;

import cn.kkserver.view.event.Event;
import cn.kkserver.view.event.EventDispatcher;
import cn.kkserver.view.style.StyleSheet;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class Document extends EventDispatcher {

    private int _elementId = 0;
    private Element _rootElement;
    private StyleSheet _styleSheet;
    private SparseArray<WeakReference<Element>> _elementsById = new SparseArray<WeakReference<Element>>(4);

    /**
     * 获取根节点
     * @return
     */
    public Element rootElement() {
        return _rootElement;
    }

    /**
     * 设置根节点
     * @param element
     */
    public void setRootElement(Element element) {
        _rootElement = element;
    }

    /**
     * 获取样式表
     * @return
     */
    public StyleSheet styleSheet() {
        return _styleSheet;
    }

    private final static Pattern PATTERN_STYLESHEET_CHANGED = Pattern.compile("^styleStyle\\.change$");

    private Event.WeakCallback<Document> _styleSheetChanged = new Event.WeakCallback<Document>(this) {
        @Override
        public boolean onEvent(Event event) {

            Document v = object();

            if(v != null) {
                v.dispatchEvent(event);
            }

            return true;
        }
    };

    /**
     * 设置样式表
     * @param styleSheet
     */
    public void setStyleSheet(StyleSheet styleSheet) {
        if(_styleSheet != styleSheet) {
            if(_styleSheet != null) {
                _styleSheet.off(PATTERN_STYLESHEET_CHANGED,_styleSheetChanged);
            }
            _styleSheet = styleSheet;
            if(_styleSheet != null) {
                _styleSheet.on(PATTERN_STYLESHEET_CHANGED,_styleSheetChanged);
            }
            dispatchEvent(new StyleSheet.StyleStyleChangedEvent());
        }
    }

    /**
     * 创建节点
     * @param name
     * @return
     */
    public Element createElement(String name) {
        String v = _styleSheet != null ? _styleSheet.get(name,"element") : null;
        Element e;

        if(v == null) {
            e = new Element(this,name,++ _elementId);
        }
        else {
            try {
                Class<?> clazz = Class.forName(v);
                Constructor<?> constructor = clazz.getConstructor(Document.class,String.class,int.class);
                e = (Element) constructor.newInstance(this,name, ++ _elementId);
            }
            catch (Throwable ex) {
                Log.d("kk-view",ex.getMessage(),ex);
                e = new Element(this,name,++ _elementId);
            }
        }

        _elementsById.put(e.elementId(),new WeakReference<Element>(e));

        return e;
    }

    /**
     * 获取节点
     * @param elementId
     * @return
     */
    public Element elementById(int elementId) {
        WeakReference<Element> ref = _elementsById.get(elementId);
        Element v = ref.get();
        if(v == null) {
            _elementsById.remove(elementId);
        }
        return v;
    }

    /**
     * 派发事件
     * @param event
     * @return
     */
    public EventDispatcher dispatchEvent(Event event) {

        EventDispatcher r = this;

        if(_rootElement != null) {
            r = _rootElement.dispatchEvent(event);
        }

        return r;
    }

}
