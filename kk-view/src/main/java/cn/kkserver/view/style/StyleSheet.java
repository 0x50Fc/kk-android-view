package cn.kkserver.view.style;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import cn.kkserver.view.event.Event;
import cn.kkserver.view.value.Pool;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class StyleSheet extends Style {

    private List<Style> _children = new LinkedList<Style>();

    public StyleSheet(String name,String status,Map<String,String> attributes,boolean readonly) {
        super(name,status,attributes,readonly);
    }

    public StyleSheet() {
        super();
    }

    public List<Style> get(String name) {

        List<Style> vs = new LinkedList<Style>();

        if(name == null) {
            vs.addAll(_children);
        }
        else {
            for(Style v  : _children ) {
                if(name.equals(v.name())) {
                    vs.add(v);
                }
            }
        }

        return vs;
    }

    private Map<String,String> _values = null;

    public String get(String name,String key) {

        String k = name + "_" + key;

        if(_values != null && _values.containsKey(k)) {
            return _values.get(k);
        }

        ListIterator<Style> p = _children.listIterator(_children.size());

        while(p.hasPrevious()) {
            Style s = p.previous();
            if(name == null || name.equals(s.name())) {
                String v = s.attr(key);
                if(v != null) {
                    if(_values == null) {
                        _values = new TreeMap<String,String>();
                    }
                    _values.put(k,v);
                    return v;
                }
            }

        }

        return null;
    }

    public StyleSheet addStyle(Style style) {
        _children.add(style);
        _hasEditting = true;
        _values = null;
        return this;
    }

    public StyleSheet clearStyle() {
        _children.clear();
        _hasEditting = true;
        _values = null;
        return this;
    }

    public StyleSheet removeStyle(Style style) {
        _children.remove(style);
        _hasEditting = true;
        _values = null;
        return this;
    }

    /**
     * 加载CSS
     * @param css
     */
    public void loadCSS(String css) {

        String[] vs = css.split("\\}");

        for(String v : vs) {

            String[] nv = v.split("\\{");

            Map<String,String> attributes = loadCSSAttribute(nv.length > 1 ? nv[1] : null);

            String[] ns = nv[0].trim().split(" ");

            StyleSheet ss = this;

            for(String n : ns) {

                if(n.length() > 0){
                    String[] nn = n.split(":");
                    StyleSheet s = new StyleSheet(nn[0],nn.length > 1 ? nn[1] : "",attributes,true);
                    ss.addStyle(s);
                    ss = s;
                }

            }

        }

    }

    public void loadCSS(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] data = new char[20480];
        int length;
        while((length = reader.read(data)) >0) {
            sb.append(data,0,length);
        }
        loadCSS(sb.toString());
    }

    private boolean _hasEditting;

    public static class StyleStyleChangedEvent extends Event {

        public StyleStyleChangedEvent() {
            super("styleStyle.change");
        }
    }

    @Override
    public Style beginEditting() {
        _hasEditting = false;
        return super.beginEditting();
    }

    @Override
    public Style commitEditting() {
        if(_hasEditting) {
            this.emit(new StyleStyleChangedEvent());
            _hasEditting = false;
        }
        return super.commitEditting();
    }

    @Override
    public Style cancelEditting() {
        _hasEditting = false;
        return super.cancelEditting();
    }

    /**
     * 计算样式
     * @param style
     * @param name
     */
    public void compute(ComputedStyle style,String name) {

        for(Style v : _children) {
            if(name.equals(v.name())) {
                style.addDependence(v);
            }
        }
    }

    private static  final Pool<StyleSheet> _pool = new Pool<StyleSheet>();

    public static void push(StyleSheet styleSheet) {
        _pool.push(styleSheet);
    }

    public static StyleSheet peek() {
        return _pool.peek();
    }

    public static  StyleSheet pop() {
        return _pool.pop();
    }
}
