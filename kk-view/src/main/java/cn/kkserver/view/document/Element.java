package cn.kkserver.view.document;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import cn.kkserver.view.event.Event;
import cn.kkserver.view.event.EventDispatcher;
import cn.kkserver.view.style.ComputedStyle;
import cn.kkserver.view.style.Style;
import cn.kkserver.view.style.StyleSheet;
import cn.kkserver.view.value.Color;
import cn.kkserver.view.value.Font;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class Element extends EventDispatcher {

    private Map<String,String> _attributes = new TreeMap<String,String>();

    /**
     * 属性键值
     * @return
     */
    public Set<String> keys() {
        return _attributes.keySet();
    }

    /**
     * 获取属性值
     * @param key
     * @return
     */
    public String attr(String key) {
        return _attributes.containsKey(key) ? _attributes.get(key) : null;
    }

    /**
     * 设置属性
     * @param key
     * @param value
     * @return
     */
    public Element attr(String key,String value) {
        _attributes.put(key,value);
        if(_edittingKeys != null) {
            _edittingKeys.add(key);
        }
        onChangeKey(key);
        return this;
    }

    /**
     * 删除属性
     * @param key
     * @return
     */
    public Element removeAttr(String key) {
        if(_attributes.containsKey(key)) {
            _attributes.remove(key);
            if(_edittingKeys != null) {
                _edittingKeys.add(key);
            }
            onChangeKey(key);
        }
        return this;
    }

    protected void onChangeKey(String key) {
        if("class".equals(key)) {
            if(_style != null) {
                ElementStyleChangedEvent event = new ElementStyleChangedEvent(this);
                dispatchEvent(event);
                sendEvent(event);
            }
        }
    }

    private Set<String> _edittingKeys;

    public Element beginEditting() {
        _edittingKeys = new TreeSet<String>();
        return this;
    }

    public boolean isEditting() {
        return _edittingKeys != null;
    }

    public boolean hasEditting() {
        return _edittingKeys != null && _edittingKeys.size() > 0;
    }

    public Element commitEditting() {
        if(hasEditting()) {
            emit(new ElementChangedEvent(this,_edittingKeys));
        }
        _edittingKeys = null;
        return this;
    }

    public Element cancelEditting() {
        _edittingKeys = null;
        return this;
    }

    private WeakReference<Element> _parentElement;
    private Element _firstChild;
    private Element _lastChild;
    private Element _nextSibling;
    private WeakReference<Element> _prevSibling;

    /**
     * 父级节点
     * @return
     */
    public Element parentElement(){
        if(_parentElement != null) {
            Element v = _parentElement.get();
            if(v == null){
                _parentElement = null;
            }
            return v;
        }
        return null;
    }

    void setParentElement(Element v) {
        if(v == null) {
            _parentElement = null;
        }
        else {
            _parentElement = new WeakReference<Element>(v);
        }
    }

    /**
     * 首子节点
     * @return
     */
    public Element firstChild() {
        return _firstChild;
    }

    void setFirstChild(Element v) {
        _firstChild = v;
    }
    /**
     * 未子节点
     * @return
     */
    public Element lastChild() {
        return _lastChild;
    }

    void setLastChild(Element v) {
        _lastChild = v;
    }

    /**
     * 下一个兄弟节点
     * @return
     */
    public Element nextSibling() {
        return _nextSibling;
    }

    void setNextSibling(Element v) {
        _nextSibling = v;
    }

    /**
     * 上一个兄弟节点
     * @return
     */
    public Element prevSibling() {
        if(_prevSibling != null) {
            Element v = _prevSibling.get();
            if(v == null) {
                _prevSibling = null;
            }
            return v;
        }
        return null;
    }

    void setPrevSibling(Element v) {
        if(v == null) {
            _prevSibling = null;
        }
        else {
            _prevSibling = new WeakReference<Element>(v);
        }
    }

    /**
     * 追加子节点
     * @param element
     * @return
     */
    public Element append(Element element) {

        element.remove();

        if(_lastChild == null) {
            _lastChild = element;
            _firstChild = element;
            element.setParentElement(this);

            sendEvent(new ElementAddedEvent(element,this));
        }
        else {
            _lastChild._nextSibling = element;
            element.setPrevSibling(_lastChild);
            _lastChild = element;
            element.setParentElement(this);

            sendEvent(new ElementAddedEvent(element,this));

        }

        return this;
    }

    /**
     * 追加到父级节点
     * @param element
     * @return
     */
    public Element appendTo(Element element) {
        element.append(this);
        return this;
    }

    /**
     * 前面插入节点
     * @param element
     * @return
     */
    public Element before(Element element) {

        element.remove();

        Element v = prevSibling();

        if(v != null) {

            v.setNextSibling(element);
            element.setPrevSibling(v);
            element.setNextSibling(this);
            setPrevSibling(element);
            element.setParentElement(parentElement());

            sendEvent(new ElementAddedEvent(element,parentElement()));
        }
        else if((v = parentElement()) != null) {
            v.setFirstChild(element);
            element.setNextSibling(this);
            setPrevSibling(element);
            element.setParentElement(v);

            sendEvent(new ElementAddedEvent(element,parentElement()));
        }

        return this;
    }

    /**
     * 插入到节点前
     * @param element
     * @return
     */
    public Element beforeTo(Element element) {
        element.before(this);
        return this;
    }

    /**
     * 后面插入节点
     * @param element
     * @return
     */
    public Element after(Element element) {

        element.remove();

        Element v = parentElement();

        if(v != null) {
            element.setParentElement(v);
            element.setNextSibling(_nextSibling);

            if(_nextSibling != null) {
                _nextSibling.setPrevSibling(element);
            }
            else {
                v.setLastChild(element);
            }
            setNextSibling(element);
            element.setPrevSibling(this);

            sendEvent(new ElementAddedEvent(element,v));
        }

        return this;
    }

    /**
     * 插入到节点后面
     * @param element
     * @return
     */
    public Element afterTo(Element element) {
        element.after(this);
        return this;
    }

    /**
     * 从父级节点移除
     * @return
     */
    public Element remove() {

        Element p = parentElement();

        if(p != null) {

            ElementRemovedEvent event = new ElementRemovedEvent(this,p);

            Element v = prevSibling();

            if (v != null) {
                v.setNextSibling(_nextSibling);
                if (_nextSibling != null) {
                    _nextSibling.setPrevSibling(v);
                } else {
                    parentElement().setLastChild(v);
                }
            } else if ((v = parentElement()) != null) {
                v.setFirstChild( _nextSibling);
                if (_nextSibling != null) {
                    _nextSibling.setPrevSibling(null);
                } else {
                    v.setLastChild(null);
                }
            }

            _parentElement = null;
            _prevSibling = null;
            _nextSibling = null;

            p.sendEvent(event);

            dispatchEvent(event);
        }

        return this;
    }

    private final String _name;
    private final int _elementId;
    private final Document _document;

    /**
     * 节点名称
     * @return
     */
    public String name() {
        return _name;
    }

    /**
     * 节点ID
     * @return
     */
    public int elementId() {
        return _elementId;
    }

    /**
     * 所属文档
     * @return
     */
    public Document document() {
        return _document;
    }

    public Element(Document document,String name,int elementId) {
        _document = document;
        _name = name;
        _elementId = elementId;
    }

    private String _text ;

    /**
     * 节点文本
     * @return
     */
    public String text() {
        return _text;
    }

    /**
     * 设置节点文本
     * @param text
     */
    public void setText(String text) {
        _text = text;
    }


    protected EventDispatcher dispatchChildrenEvent(Element element,Event event) {
        return element.dispatchEvent(event);
    }

    /**
     * 派发事件
     * @param event
     * @return
     */
    @Override
    public EventDispatcher dispatchEvent(Event event) {

        if(event instanceof ElementEmitEvent) {

            emit(event);

            Element p = _lastChild;

            while(p != null) {

                dispatchChildrenEvent(p,event);

                p = p.prevSibling();
            }

            return null;
        }

        if( event instanceof ElementRemovedEvent
                || event instanceof ElementStyleChangedEvent
                || event instanceof StyleSheet.StyleStyleChangedEvent) {

            _style = null;

            Element p = _lastChild;

            while(p != null) {

                dispatchChildrenEvent(p,event);

                p = p.prevSibling();
            }

            return this;
        }

        EventDispatcher r = this;

        Element p = _lastChild;

        while(p != null) {

            r = dispatchChildrenEvent(p,event);

            if(r != null) {
                return r;
            }

            p = p.prevSibling();
        }

        return r;
    }

    /**
     * 发送事件
     * @param event
     */
    public void sendEvent(Event event) {
        super.sendEvent(event);
        if(! event.isCancelBubble()) {
            Element v = parentElement();
            if(v != null) {
                v.sendEvent(event);
            }
            else if(_document != null) {
                _document.sendEvent(event);
            }
        }
    }

    private ComputedStyle _style;

    protected void computeStyle(ComputedStyle style,StyleSheet styleSheet,String name) {

        if(styleSheet != null) {
            styleSheet.compute(style,name);
        }

        Element p = parentElement();

        if(p != null) {

            ComputedStyle pStyle = p.style();

            for(Style v : pStyle.dependencies()) {
                if(v instanceof StyleSheet) {
                    ((StyleSheet)v).compute(style,name);
                }
            }
        }
    }

    public ComputedStyle style() {

        if(_style == null) {

            _style = new ComputedStyle();

            String v = attr("class");

            StyleSheet styleSheet = document().styleSheet();

            computeStyle(_style,styleSheet,name());

            if(v != null) {

                String[] vs = v.split(" ");

                for(String vv : vs) {
                    if(vv.length() > 0) {
                        computeStyle(_style,styleSheet,"." + vv);
                    }
                }
            }

        }

        return _style;
    }

    public String status() {
        String v = attr("status");
        if(v == null){
            Element p = parentElement();
            if(p != null) {
                return p.status();
            }
            return "";
        }
        return v;
    }

    public String stringValue(String key, String defaultValue) {

        String v = attr(key);

        if(v == null) {
            v = style().attrInStatus(key,this.status());
        }

        if(v == null) {
            return defaultValue;
        }

        return v;
    }

    public int intValue(String key, int defaultValue) {
        return Value.intValue(stringValue(key,null),defaultValue);
    }

    public int intValue(String key, int baseValue,int defaultValue) {
        return Value.intValue(stringValue(key,null),baseValue,defaultValue);
    }

    public float floatValue(String key, float defaultValue) {
        return (float) Value.doubleValue(stringValue(key,null),defaultValue);
    }

    public double doubleValue(String key, double defaultValue) {
        return Value.doubleValue(stringValue(key,null),defaultValue);
    }

    public Color colorValue(String key, Color defaultValue) {
        return Value.colorValue(stringValue(key,null),defaultValue);
    }

    public boolean booleanValue(String key,boolean defaultValue) {
        return Value.booleanValue(stringValue(key,null),defaultValue);
    }

    public Font fontValue(String key, Font defaultValue) {
        return Value.fontValue(stringValue(key,null),defaultValue);
    }


    public static class ElementChangedEvent extends Event {

        public final Element element;
        public final Set<String> keys;

        public ElementChangedEvent(Element element,Set<String> keys) {
            super("element.changed");
            this.element = element;
            this.keys = keys;
        }

    }

    public static class ElementStyleChangedEvent extends Event {

        public final Element element;

        public ElementStyleChangedEvent(Element element) {
            super("element.style.changed");
            this.element = element;
        }
    }

    public static class ElementRemovedEvent extends Event {
        public final Element element;
        public final Element parentElement;

        public ElementRemovedEvent(Element element,Element parentElement) {
            super("element.removed");
            this.element = element;
            this.parentElement = parentElement;
        }
    }

    public static class ElementAddedEvent extends Event {
        public final Element element;
        public final Element parentElement;

        public ElementAddedEvent(Element element,Element parentElement) {
            super("element.added");
            this.element = element;
            this.parentElement = parentElement;
        }
    }

    public static class ElementEmitEvent extends Event {

        /**
         * 创建事件
         *
         * @param name
         */
        public ElementEmitEvent(String name) {
            super(name);
        }

    }

    /**
     * 取消事件
     */
    public static class ElementCanceledEvent extends ElementEmitEvent {

        public final Element element;
        public final Event event;

        public ElementCanceledEvent(Element element,Event event) {
            super("element.canceled");
            this.element = element;
            this.event = event;
        }

    }

    public Element elementClone() {

        Element v = _document.createElement(_name);

        for(String key : keys()) {
            v.attr(key,attr(key));
        }

        Element p = firstChild();

        while(p != null) {
            v.append(p.elementClone());
            p = p.nextSibling();
        }

        v.setText(_text);

        return v;
    }

}
