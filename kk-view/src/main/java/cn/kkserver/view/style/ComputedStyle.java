package cn.kkserver.view.style;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

import cn.kkserver.view.event.Event;

/**
 * Created by zhanghailong on 16/7/8.
 */
public class ComputedStyle extends Style {

    private final static Pattern PATTERN_CHANGED = Pattern.compile("^style\\.change$");

    private List<Style> _dependencies = new LinkedList<Style>();

    private Event.WeakCallback<ComputedStyle> _changed = new Event.WeakCallback<ComputedStyle>(this) {

        @Override
        public boolean onEvent(Event event) {

            ComputedStyle v = object();

            if(v != null) {

                v.emit(event);

            }

            return true;
        }
    };

    public List<Style> dependencies() {
        return _dependencies;
    }

    public ComputedStyle addDependence(Style style) {
        style.on(PATTERN_CHANGED,_changed);
        _dependencies.add(style);
        return this;
    }

    public ComputedStyle clearDependence(Style style) {
        for(Style v : _dependencies) {
            v.off(PATTERN_CHANGED,_changed);
        }
        _dependencies.remove(style);
        return this;
    }

    public ComputedStyle removeDependence(Style style) {
        style.off(PATTERN_CHANGED,_changed);
        _dependencies.remove(style);
        return this;
    }

    @Override
    public String attr(String key) {
        return attrInStatus(key,"");
    }

    public String attrInStatus(String key ,String status) {

        String v = super.attr(key);

        if(v == null) {

            ListIterator<Style> i = _dependencies.listIterator(_dependencies.size());

            while(i.hasPrevious()) {

                Style s = i.previous();

                if(status.equals(s.status())
                        && (v = s.attr(key)) != null) {
                    break;
                }

            }
        }

        if(v == null && !"".equals(status) ) {

            ListIterator<Style> i = _dependencies.listIterator(_dependencies.size());

            while(i.hasPrevious()) {

                Style s = i.previous();

                if("".equals(s.status())
                        && (v = s.attr(key)) != null) {
                    break;
                }

            }
        }

        return v;
    }
}
