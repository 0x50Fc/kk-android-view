package cn.kkserver.view.value;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class Rect {

    public int x;
    public int y;
    public int width;
    public int height;

    public Rect(int x,int y,int width,int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public int left() {
        return x;
    }

    public int top() {
        return y;
    }

}
