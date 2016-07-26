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
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class BRElement extends Element {

    public BRElement(Document document, String name, int elementId) {
        super(document, name, elementId);
    }
}
