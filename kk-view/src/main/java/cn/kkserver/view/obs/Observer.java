package cn.kkserver.view.obs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhanghailong on 16/7/13.
 */
public class Observer extends ObsObject implements IObserver {

    private final KeyObserver _keyObserver = new KeyObserver();

    public Observer(Object object) {
        super(object);
    }

    public Observer() {
        this(null);
    }

    @Override
    protected void onChangedKeys(String[] keys) {

        int idx = 0;
        KeyObserver obs = _keyObserver;

        while(idx < keys.length) {

            String key = keys[idx];

            obs = obs.keyObserver(key);

            idx ++;

            if (idx == keys.length) {
                obs.change(this,keys);
            }
        }

    }

    @Override
    public IObserver change(String[] keys) {

        if(keys == null) {
            _keyObserver.change(this,keys);
        }
        else {
            onChangedKeys(keys);
        }

        return this;
    }

    public IWithObserver with(String[] keys) {
        return new WithObserver(keys);
    }

    public IWithObserver with(String[] keys,Object value) {
        return new WithObserver(keys,value);
    }

    @Override
    public IObserver on(Listener listener,String[] keys) {

        _keyObserver.on(listener);

        if(keys != null) {

            int idx = 0;
            KeyObserver obs = _keyObserver;

            while(idx < keys.length) {

                String key = keys[idx];

                obs = obs.keyObserver(key);

                obs.on(listener);

                idx ++;
            }

        }

        return this;
    }

    @Override
    public IObserver off(Listener listener,String[] keys) {

        _keyObserver.off(listener);

        if(keys != null) {

            int idx = 0;
            KeyObserver obs = _keyObserver;

            while(idx < keys.length) {

                String key = keys[idx];

                obs = obs.keyObserver(key);

                obs.off(listener);

                idx ++;
            }
        }

        return this;
    }

    private static class KeyObserver {

        private final List<Listener> _listeners = new LinkedList<>();
        private final Map<String,KeyObserver> _observers = new TreeMap<String,KeyObserver>();

        public void on(Listener listener) {
            _listeners.add(listener);
        }

        public void off(Listener listener) {
            _listeners.remove(listener);
        }

        public void change(Observer observer,String[] keys) {

            List<Listener> ls = new ArrayList<>(_listeners);

            for(Listener v : ls) {
                v.onChanged(observer,keys);
            }
        }

        public KeyObserver keyObserver(String key) {
            if(_observers.containsKey(key)) {
                return _observers.get(key);
            }
            KeyObserver v = new KeyObserver();
            _observers.put(key,v);
            return v;
        }

    }

    private class WithObserver extends WithObject implements IObserver,IWithObserver {

        public WithObserver(String[] baseKeys) {
            super(Observer.this, baseKeys);
        }


        public WithObserver(String[] baseKeys,Object value) {
            super(Observer.this, baseKeys,value);
        }

        @Override
        protected void onChangedKeys(String[] keys) {
            Observer.this.onChangedKeys(ObsObject.join(_baseKeys,keys));
        }

        @Override
        public IObserver change(String[] keys) {
            Observer.this.change(ObsObject.join(_baseKeys,keys));
            return this;
        }

        @Override
        public IObserver on(Listener listener, String[] keys) {
            Observer.this.on(listener,ObsObject.join(_baseKeys,keys));
            return this;
        }

        @Override
        public IObserver off(Listener listener, String[] keys) {
            Observer.this.on(listener,ObsObject.join(_baseKeys,keys));
            return this;
        }

        @Override
        public IWithObserver with(String[] keys) {
            return Observer.this.with(ObsObject.join(_baseKeys,keys));
        }

        @Override
        public IWithObserver with(String[] keys, Object value) {
            return Observer.this.with(ObsObject.join(_baseKeys,keys),value);
        }

    }
}
