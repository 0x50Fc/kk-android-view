package cn.kkserver.view.value;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class Size {

    public int width;
    public int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Size Zero = new Size(0,0);

}
