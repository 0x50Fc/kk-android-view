package cn.kkserver.view.obs;

/**
 * Created by zhanghailong on 16/7/26.
 */
public interface IWithObject extends IObject {

    public Object value();

    public void setValue(Object value);

    public String[] baseKeys();

}
