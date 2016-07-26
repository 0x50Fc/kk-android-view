package cn.kkserver.view;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.kkserver.view.document.Element;
import cn.kkserver.view.obs.IObserver;
import cn.kkserver.view.obs.IWithObserver;
import cn.kkserver.view.obs.ObsObject;
import cn.kkserver.view.obs.WeakListener;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/25.
 */
public class ElementObserver {

    private final IObserver _observer;
    private final List<ElementListener> _listeners;
    private final Element _element;

    public ElementObserver(Element element,IObserver observer) {
        _element = element;
        _observer = observer;
        _listeners = new LinkedList<ElementListener>();

        Element p = _element.firstChild();
        while(p != null) {
            add(p);
            p = p.nextSibling();
        }
    }


    public Element element() {
        return _element;
    }

    public IObserver observer() {
        return _observer;
    }

    public void change() {
        Iterator<ElementListener> v = _listeners.iterator();
        while(v.hasNext()) {
            ElementListener l = v.next();
            l.onChanged(_observer,null);
        }
    }

    protected void add(Element element) {

        String v = element.stringValue("kk-view",null);

        if(v != null && !v.isEmpty() ) {

            TextElementListener l = new TextElementListener(element);

            String[] keys = ObsObject.keys(v);

            l.keys.add(keys);

            _listeners.add(l);

            _observer.on(l,keys);

            return;
        }

        v = element.text();

        if(v != null && ! v.isEmpty()) {

            FormatElementListener l = new FormatElementListener(element,v);

            Matcher matcher =  FormatElementListener.pattern.matcher(v);

            while(matcher.find()) {

                String key = v.substring(matcher.start(1),matcher.end(1));

                if(!key.isEmpty()) {
                    String[] keys = ObsObject.keys(key);
                    l.keys.add(keys);
                    _observer.on(l, keys);
                }

            }

            if(l.keys.size() > 0) {
                _listeners.add(l);
            }

            return ;
        }


        v = element.stringValue("kk-each",null);

        if(v != null && ! v.isEmpty()) {

            String[] keys = ObsObject.keys(v);

            EachElementListener l = new EachElementListener(element);

            l.keys.add(keys);

            _listeners.add(l);

            _observer.on(l,keys);

            return ;
        }

        for(String key : element.keys()) {

            if(key.startsWith("kk-")) {

                v = element.attr(key);

                if(v != null && ! v.isEmpty()) {
                    String[] keys = ObsObject.keys(v);
                    AttrElementListener l = new AttrElementListener(element,key.substring(3));
                    l.keys.add(keys);
                    _listeners.add(l);
                    _observer.on(l,keys);
                }

            }
        }

        Element p = element.firstChild();

        while(p != null) {
            add(p);
            p = p.nextSibling();
        }

    }

    public void off() {

        Iterator<ElementListener> p = _listeners.iterator();

        while(p.hasNext()) {
            ElementListener e = p.next();
            for(String[] key : e.keys) {
                _observer.off(e,key);
            }
            e.off();
            p.remove();
        }

    }

    @Override
    protected void finalize() throws Throwable {

        off();

        super.finalize();
    }

    private static abstract class ElementListener extends WeakListener<Element> {

        public final List<String[]> keys = new LinkedList<String[]>();

        public ElementListener(Element object) {
            super(object);
        }

        public void off() {

        }
    }

    private static class TextElementListener extends ElementListener{

        public TextElementListener(Element object) {
            super(object);
        }


        @Override
        protected void onChanged(IObserver observer, String[] keys, Element v) {
            if(v != null) {

                for(String[] key : this.keys) {
                    v.setText(Value.stringValue(observer.get(key),""));
                    break;
                }

            }
        }
    }

    private static class EachElementListener extends ElementListener{

        private final List<ElementObserver> _elementObservers = new LinkedList<ElementObserver>();

        public EachElementListener(Element object) {
            super(object);
            object.attr("display","none");
        }

        @Override
        public void off() {

            Iterator<ElementObserver> v = _elementObservers.iterator();

            while(v.hasNext()) {
                ElementObserver obs = v.next();
                obs.element().remove();
                obs.off();
                v.remove();
            }
        }

        @Override
        protected void finalize() throws Throwable {

            off();

            super.finalize();
        }

        protected Iterator<ElementObserver> each(IObserver observer,Object object,Iterator<ElementObserver> iterator,String[] baseKey,Element element) {

            Iterator<ElementObserver> r = null;

            ElementObserver obs;
            IWithObserver with;

            if(iterator != null && iterator.hasNext()) {
                obs = iterator.next();
                with = (IWithObserver) obs.observer();
                with.setValue(object);
                r = iterator;
            }
            else {
                Element el = element.elementClone();
                el.removeAttr("display");
                element.before(el);
                with = observer.with(baseKey,object);
                obs = new ElementObserver(el,with);
                _elementObservers.add(obs);
            }

            obs.change();

            return r;
        }

        @Override
        protected void onChanged(IObserver observer, String[] keys, Element v) {
            if(v != null) {

                for(String[] key : this.keys) {

                    Iterator<ElementObserver> iterator = _elementObservers.iterator();

                    Object object = observer.get(keys);

                    if(object != null) {

                        if(object instanceof List) {

                            int i=0;

                            for(Object vv : (List) object) {
                                iterator = each(observer,vv,iterator,ObsObject.join(key,new String[]{String.valueOf(i ++)}),v);
                            }

                        }
                        else if(object.getClass().isArray()) {
                            int c = Array.getLength(object);
                            for(int i=0;i<c;i++) {
                                iterator = each(observer,Array.get(object,i),iterator,ObsObject.join(key,new String[]{String.valueOf(i)}),v);
                            }
                        }
                    }

                    while(iterator != null && iterator.hasNext()) {
                        ElementObserver obs = iterator.next();
                        obs.element().remove();
                        obs.off();
                        iterator.remove();
                    }

                    break;
                }

            }
        }
    }

    private static class AttrElementListener extends ElementListener{

        private final String _attr;

        public AttrElementListener(Element object,String attr) {
            super(object);
            _attr = attr;
        }


        @Override
        protected void onChanged(IObserver observer, String[] keys, Element v) {
            if(v != null) {

                for(String[] key : this.keys) {
                    v.attr(_attr,Value.stringValue(observer.get(key),""));
                    break;
                }

            }
        }
    }



    private static class FormatElementListener extends ElementListener{

        private final String _format;

        public FormatElementListener(Element object,String format) {
            super(object);
            _format = format;
        }

        private final static Pattern pattern = Pattern.compile("\\{([^\\{\\}]*?)\\}");

        @Override
        protected void onChanged(IObserver observer, String[] keys, Element v) {
            if(v != null) {

                StringBuilder sb = new StringBuilder();

                Matcher matcher = pattern.matcher(_format);
                int idx =0;

                while(matcher.find()) {
                    sb.append(_format,idx,matcher.start(0) - idx);
                    String[] key = ObsObject.keys(_format.substring(matcher.start(1),matcher.end(1)));
                    sb.append(Value.stringValue(observer.get(key),""));
                    idx = matcher.end(0);
                }

                if(idx < _format.length()) {
                    sb.append(_format,idx,_format.length() - idx);
                }

                v.setText(sb.toString());

            }
        }
    }

}
