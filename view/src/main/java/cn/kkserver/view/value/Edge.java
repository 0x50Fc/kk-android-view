package cn.kkserver.view.value;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class Edge {

    public final int left;
    public final int right;
    public final int top;
    public final int bottom;

    public Edge(int left,int top,int right,int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

}
