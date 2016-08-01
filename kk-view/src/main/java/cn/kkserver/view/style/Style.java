package cn.kkserver.view.style;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.kkserver.view.event.Event;
import cn.kkserver.view.event.EventEmitter;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class Style extends EventEmitter {

    private final Map<String,String> _attributes;
    private final boolean _readonly;

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
        if(_attributes.containsKey(key)) {
            return _attributes.get(key);
        }
        return null;
    }

    /**
     * 设置属性值
     * @param key
     * @return
     */
    public Style attr(String key,String value) {
        if(!_readonly) {
            _attributes.put(key, value);
            if(_edittingKeys != null) {
                _edittingKeys.add(key);
            }
        }
        return this;
    }

    /**
     * 是否只读
     * @return
     */
    public boolean isReadonly() {
        return _readonly;
    }

    private Set<String> _edittingKeys;

    public boolean isEditting() {
        return _edittingKeys != null;
    }

    public boolean hasEditting() {
        return _edittingKeys != null && _edittingKeys.size() > 0;
    }

    public Style beginEditting() {
        _edittingKeys = new TreeSet<>();
        return this;
    }

    public Style commitEditting() {
        if(_edittingKeys != null && _edittingKeys.size() > 0) {
            this.emit(new StyleChangedEvent(_edittingKeys));
        }
        _edittingKeys = null;
        return this;
    }

    public Style cancelEditting() {
        _edittingKeys = null;
        return this;
    }

    private final String _name;

    /**
     * 样式名称
     * @return
     */
    public String name() {
        return _name;
    }

    private final String _status;

    public String status() {
        return _status;
    }

    public Style(String name,String status,Map<String,String> attributes,boolean readonly) {
        _name = name;
        _status = status;
        _attributes = attributes;
        _readonly = readonly;
    }

    public Style() {
        this("","",new TreeMap<String,String>(),false);
    }

    public static Map<String,String> loadCSSAttribute(String css) {

        Map<String,String> attributes = new TreeMap<String,String>();

        if(css != null) {
            String[] vs = css.split(";");

            for (String v : vs) {
                String[] vv = v.split(":");
                String key = vv[0].trim();
                String value = vv.length > 1 ? vv[1].trim() : "";
                if(! key.isEmpty()) {
                    attributes.put(key, value);
                }
            }
        }

        return attributes;

    }

    public static class StyleChangedEvent extends Event {

        public final Set<String> keys;

        public StyleChangedEvent(Set<String> keys) {
            super("style.change");
            this.keys = keys;
        }
    }

}
