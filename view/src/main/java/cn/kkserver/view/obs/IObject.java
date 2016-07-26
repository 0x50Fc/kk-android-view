package cn.kkserver.view.obs;

/**
 * Created by zhanghailong on 16/7/26.
 */
public interface IObject extends IGetter,ISetter {

    public Object get(String[] keys);

    public void set(String[] keys, Object value);

    public void remove(String[] keys);

}
