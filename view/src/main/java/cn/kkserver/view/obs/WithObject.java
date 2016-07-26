package cn.kkserver.view.obs;

import java.util.TreeMap;

/**
 * Created by zhanghailong on 16/7/26.
 */
public class WithObject implements IObject ,IWithObject {

    protected final IObject _object;
    protected final String[] _baseKeys;
    private Object _value;

    public WithObject(IObject object,String[] baseKeys,Object value) {
        _object = object;
        _baseKeys = baseKeys;
        _value = value;
    }

    public WithObject(IObject object,String[] baseKeys) {
        this(object,baseKeys,object.get(baseKeys));
    }

    @Override
    public Object get(String[] keys) {
        return ObsObject.get(_value,keys);
    }

    @Override
    public void set(String[] keys, Object value) {
        if(_value == null) {
            _value = new TreeMap<String,Object>();
            _object.set(_baseKeys,_value);
        }
        ObsObject.set(_value,keys,value);
        onChangedKeys(keys);
    }

    @Override
    public void remove(String[] keys) {
        ObsObject.remove(_value,keys);
        onChangedKeys(keys);
    }

    @Override
    public Object get(String key) {
        return ObsObject.get(_value,key);
    }

    @Override
    public void set(String key, Object value) {
        if(_value == null) {
            _value = new TreeMap<String,Object>();
            _object.set(_baseKeys,_value);
        }
        ObsObject.set(_value,key,value);
        onChangedKeys(new String[]{key});
    }

    @Override
    public void remove(String key) {
        ObsObject.remove(_value,key);
        onChangedKeys(new String[]{key});
    }

    protected void onChangedKeys(String[] keys) {

    }

    @Override
    public Object value() {
        return _value;
    }

    @Override
    public void setValue(Object value) {
        _value = value;
    }

    @Override
    public String[] baseKeys() {
        return _baseKeys;
    }
}
