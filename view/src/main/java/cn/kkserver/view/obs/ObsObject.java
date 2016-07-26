package cn.kkserver.view.obs;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhanghailong on 16/7/26.
 */
public class ObsObject implements IObject {

    public static String[] join(String[] baseKeys,String[] keys) {

        if(baseKeys == null && keys == null) {
            return null;
        }

        if(baseKeys == null) {
            return keys;
        }

        if(keys == null) {
            return baseKeys;
        }

        String[] vs = new String[baseKeys.length + keys.length];
        int i = 0;
        for(String v : baseKeys) {
            vs[i ++] = v;
        }
        for(String v : keys) {
            vs[i ++] = v;
        }
        return vs;
    }

    public static String[] keys(String key) {
        return key.split("\\.");
    }


    public static Object get(Object v, String key) {

        if(v != null) {

            if(v instanceof Map) {
                Map<String,Object> m = (Map<String,Object>)v;
                if(m.containsKey(key)) {
                    v = m.get(key);
                }
                else {
                    v = null;
                }
            }
            else if(v instanceof List) {
                List<Object> l = (List<Object>) v;
                try {
                    int i = Integer.valueOf(key);
                    if(i >=0 && i < l.size()) {
                        v = l.get(i);
                    }
                    else {
                        v = null;
                    }
                }
                catch (Throwable e) {
                    v = null;
                }
            }
            else if(v instanceof IGetter) {
                v =((IGetter) v).get(key);
            }
            else {
                try {
                    Field fd = v.getClass().getField(key);
                    v = fd.get(v);
                } catch (Throwable e) {
                    v = null;
                }
            }

        }

        return v;
    }

    public static void set(Object v , String key, Object value) {

        if(v != null) {

            if(v instanceof Map) {
                ((Map<String, Object>) v).put(key,value);
            }
            else if(v instanceof List) {
                List<Object> l = (List<Object>) v;
                try {
                    int i = Integer.valueOf(key);
                    if(i >=0 && i < l.size()) {
                        l.set(i,value);
                    }
                    else if(i == l.size()) {
                        l.add(value);
                    }
                }
                catch (Throwable e) {}
            }
            else if(v instanceof ISetter) {
                ((ISetter) v).set(key,value);
            }
            else {
                try {
                    Field fd = v.getClass().getField(key);
                    fd.set(v,value);
                } catch (Throwable e) {
                }
            }
        }
    }

    public static void remove(Object v , String key) {

        if(v != null) {

            if(v instanceof Map) {
                ((Map<String, Object>) v).remove(key);
            }
            else if(v instanceof List) {
                List<Object> l = (List<Object>) v;
                try {
                    int i = Integer.valueOf(key);
                    if(i >=0 && i < l.size()) {
                        l.remove(i);
                    }
                }
                catch (Throwable e) {}
            }
            else if(v instanceof ISetter) {
                ((ISetter) v).remove(key);
            }
            else {
                try {
                    Field fd = v.getClass().getField(key);
                    fd.set(v,null);
                } catch (Throwable e) {
                }
            }
        }
    }

    public static Object get(Object v, String[] keys) {

        int idx = 0;

        while(idx < keys.length) {

            String key = keys[idx];

            if(v != null) {

                v = get(v,key);

            }
            else {
                break;
            }

            idx ++ ;
        }

        return v;
    }

    public static void set(Object v, String[] keys, Object value) {

        int idx = 0;

        while(idx < keys.length) {

            String key = keys[idx];

            if(idx + 1 < keys.length) {
                Object vv = get(v,key);
                if(vv == null) {
                    vv = new TreeMap<String,Object>();
                    set(v,key,vv);
                }
                v = vv;
            }
            else {
                set(v,key,value);
                break;
            }

            idx ++;
        }

    }

    public static void remove(Object v,String[] keys) {

        int idx = 0;

        while(v != null && idx < keys.length) {

            String key = keys[idx];

            if(idx + 1 < keys.length) {
                v = get(v,key);
            }
            else {
                remove(v,key);
                break;
            }

            idx ++;
        }
    }


    private Object _object;

    public ObsObject(Object object) {
        _object = object;
    }

    @Override
    public Object get(String[] keys) {
        return get(_object,keys);
    }

    @Override
    public void set(String[] keys, Object value) {

        if(_object == null) {
            _object = new TreeMap<String,Object>();
        }

        set(_object,keys,value);

        onChangedKeys(keys);
    }

    @Override
    public void remove(String[] keys) {
        remove(_object,keys);
        onChangedKeys(keys);
    }

    @Override
    public Object get(String key) {
        return get(_object,key);
    }

    @Override
    public void set(String key, Object value) {
        if (_object == null) {
            _object = new TreeMap<String,Object>();
        }
        set(_object,key,value);
        onChangedKeys(new String[]{key});
    }

    @Override
    public void remove(String key) {
        remove(_object,key);
        onChangedKeys(new String[]{key});
    }

    protected void onChangedKeys(String[] keys) {

    }
}
