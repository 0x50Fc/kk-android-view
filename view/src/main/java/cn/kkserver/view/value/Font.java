package cn.kkserver.view.value;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class Font {

    public final String name;
    public final int size;
    public final boolean bold;
    public final boolean italic;

    public Font(String name,int size,boolean bold,boolean italic) {
        this.name = name;
        this.size = size;
        this.bold = bold;
        this.italic = italic;
    }

    public Font(int size) {
        this(null,size,false,false);
    }

    public final static Font defaultFont = new Font(14);

}
