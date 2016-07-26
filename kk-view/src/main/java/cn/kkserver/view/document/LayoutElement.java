package cn.kkserver.view.document;

import android.util.Log;
import android.view.View;

import java.util.Set;

import cn.kkserver.view.event.Event;
import cn.kkserver.view.value.Edge;
import cn.kkserver.view.value.Rect;
import cn.kkserver.view.value.Size;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class LayoutElement extends Element {


    private Rect _frame;
    private Size _contentSize;

    public Rect frame() {
        if(_frame == null) {
            _frame = new Rect(intValue("left",0),intValue("right",0),intValue("width",0),intValue("height",0));
        }
        return _frame;
    }

    public void setFrame(Rect frame) {
        _frame = frame;
    }

    public void setContentSize(Size contentSize) {
        _contentSize = contentSize;
    }

    public Size contentSize() {
        if(_contentSize == null) {
            _contentSize = new Size(0,0);
        }
        return _contentSize;
    }

    private Edge _padding;

    private Edge _margin;

    public Edge padding() {

        if(_padding == null){
            int v = intValue("padding",0);
            _padding = new Edge(intValue("padding-left",v),intValue("padding-top",v),intValue("padding-right",v),intValue("padding-bottom",v));
        }
        return _padding;
    }

    public Edge margin() {
        if(_margin == null){
            int v = intValue("margin",0);
            _margin = new Edge(intValue("margin-left",v),intValue("margin-top",v),intValue("margin-right",v),intValue("margin-bottom",v));
        }
        return _margin;
    }

    public LayoutElement(Document document, String name, int elementId) {
        super(document, name, elementId);
    }

    @Override
    protected void onChangeKey(String key) {
        super.onChangeKey(key);

        if(key.startsWith("padding")) {
            _padding = null;
        }
        else if(key.startsWith("margin")) {
            _margin = null;
        }

    }

    public Size layoutChildren(Edge padding) {

        Rect frame = this.frame();
        Size size = new Size(0,0);
        Size insetSize = new Size(frame.width - padding.left - padding.right
                , frame.height - padding.top - padding.bottom);

        String layout = stringValue("layout",null);

        if("flow".equals(layout)){

            int x = padding.left;
            int y = padding.top;
            int lineHeight = 0;
            int width = padding.left + padding.right;
            int maxWidth = frame.width;
            boolean nowarp = booleanValue("nowarp",false);

            if(maxWidth == Integer.MAX_VALUE){
                maxWidth = intValue("max-width",maxWidth);
            }

            Element p = firstChild();

            while(p != null) {

                if(p instanceof LayoutElement) {

                    LayoutElement element = (LayoutElement) p;

                    Edge margin = element.margin();

                    element.layout(new Size(insetSize.width - margin.left - margin.right,insetSize.height - margin.top - margin.bottom));

                    String display = element.stringValue("display",null);

                    if("none".equals(display)) {

                    }
                    else {

                        Rect r = element.frame();

                        if (nowarp || (x + r.width + margin.left + margin.right <= maxWidth - padding.right)) {

                            r.x = x + margin.left;
                            r.y = y + margin.top;

                            x += r.width + margin.left + margin.right;

                            if (lineHeight < r.height + margin.top + margin.bottom) {
                                lineHeight = r.height + margin.top + margin.bottom;
                            }
                            if (width < x + padding.right) {
                                width = x + padding.right;
                            }
                        } else {
                            x = padding.left;
                            y += lineHeight;
                            lineHeight = r.height + margin.top + margin.bottom;
                            r.x = x + margin.left;
                            r.y = y + margin.top;
                            x += r.width + margin.left + margin.right;
                            if (width < x + padding.right) {
                                width = x + padding.right;
                            }
                        }

                    }

                }
                else if(p instanceof BRElement){
                    x = padding.left;
                    y += lineHeight;
                    lineHeight = 0;
                }

                p = p.nextSibling();
            }

            size = new Size(width, y + lineHeight + padding.bottom);

        }
        else{

            size.width = padding.left + padding.right;
            size.height = padding.top + padding.bottom;

            Element p = firstChild();

            while(p != null) {

                if(p instanceof LayoutElement) {

                    LayoutElement element = (LayoutElement) p;

                    element.layout(insetSize);

                    Rect r = element.frame();

                    String left = element.stringValue("left",null);
                    String right = element.stringValue("right",null);
                    String top = element.stringValue("top",null);
                    String bottom = element.stringValue("bottom",null);

                    if("auto".equals(left)){
                        if("auto".equals(right)){
                            r.x = (int) Math.ceil((double) (frame.width - r.width) * 0.5);
                        }
                        else{
                            r.x = (int) Math.ceil(frame.width - r.width - padding.right - Value.doubleValue(right,0));
                        }
                    }
                    else{
                        r.x = padding.left + Value.intValue(left,0);
                    }

                    if("auto".equals(top)){
                        if("auto".equals(bottom)){
                            r.y = (int) Math.ceil((double)(frame.height - r.height) * 0.5);
                        }
                        else{
                            r.y = (int) Math.ceil(frame.height - r.height - padding.bottom - Value.doubleValue(bottom,0));
                        }
                    }
                    else{
                        r.y = padding.top + Value.intValue(top,0);
                    }

                    if(r.x + r.width + padding.right > size.height){
                        size.width = r.x +r.width +padding.right;
                    }

                    if(r.y + r.height + padding.bottom > size.height){
                        size.height = r.y +r.height + padding.bottom;
                    }

                }

                p = p.nextSibling();
            }

        }

        setContentSize(size);

        return size;
    }

    public Size layout(Size size) {

        Edge insets = padding();

        Rect frame = _frame;

        if(frame == null) {
            frame = new Rect(0, 0, intValue("width", size.width, 0), intValue("height", size.height, 0));
        }
        else {
            frame.width = intValue("width", size.width, 0);
            frame.height = intValue("height", size.height, 0);
        }

        setFrame(frame);

        if(frame.width == Integer.MAX_VALUE || frame.height == Integer.MAX_VALUE){

            Size contentSize = layoutChildren(insets);

            if(frame.width == Integer.MAX_VALUE){

                frame.width = contentSize.width;

                int max = intValue("max-width",size.width,frame.width);
                int min = intValue("min-width",size.width,frame.width);

                if(frame.width > max){
                    frame.width = max;
                }
                if(frame.width < min){
                    frame.width = min;
                }
            }

            if(frame.height == Integer.MAX_VALUE){

                frame.height = contentSize.height;

                int max = intValue("max-height",size.height,frame.height);
                int min = intValue("min-height",size.height,frame.height);

                if(frame.height > max){
                    frame.height = max;
                }

                if(frame.height < min){
                    frame.height = min;
                }

            }

            setFrame(frame);

            return contentSize;
        }
        else{
            return layoutChildren(insets);
        }
    }

    public Size layout() {
        return layoutChildren(padding());
    }

    public boolean isLayouted(){
        return _frame != null;
    }

    public static class ElementLayoutedEvent extends ElementEmitEvent {

        public final LayoutElement element;

        public ElementLayoutedEvent(LayoutElement element) {
            super("element.layouted");
            this.element = element;
        }

    }

}
