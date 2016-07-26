package cn.kkserver.view.io;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.TreeMap;

import cn.kkserver.view.document.Document;
import cn.kkserver.view.document.Element;
import cn.kkserver.view.document.XMLReader;
import cn.kkserver.view.value.Pool;

/**
 * Created by zhanghailong on 16/7/17.
 */
public class Resource implements IResource {

    private final Context _context;
    private final Handler _handler;

    public Resource(Context context) {
        _context = context;
        _handler = new Handler();
    }

    private Map<String,WeakReference<Drawable>> _images = new TreeMap<String,WeakReference<Drawable>>();

    protected void setImage(String uri,Drawable image) {
        _images.put(uri ,new WeakReference<Drawable>(image));
    }

    @Override
    public Drawable getImage(final String uri, Callback<Drawable> callback) {

        if(_images.containsKey(uri)) {
            WeakReference<Drawable> ref = _images.get(uri);
            Drawable v = ref.get();
            if(v == null) {
                _images.remove(uri);
            }
            else {
                return v;
            }
        }

        int i = uri.indexOf("R.drawable.");

        if(i == 0) {
            try {
                Class<?> clazz = Class.forName(_context.getPackageName() + ".R$drawable");
                java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 11));
                int id = fd.getInt(null);
                Drawable v = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    v = _context.getDrawable(id);
                }
                else {
                    v = _context.getResources().getDrawable(id);
                }
                _images.put(uri,new WeakReference<Drawable>(v));
                return v;
            }
            catch (final Throwable ex) {
                if(callback != null) {
                    final WeakReference<Callback<Drawable>> ref = new WeakReference<Callback<Drawable>>(callback);
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Callback<Drawable> v = ref.get();
                            if(v != null) {
                                v.onFail(ex);
                            }
                        }
                    });
                }
                Log.d("kk-view",ex.getMessage(),ex);
            }
        }
        else if(i > 0) {
            try {
                Class<?> clazz = Class.forName(uri.substring(0,i) + "R$drawable");
                java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 11));
                int id = fd.getInt(null);
                Drawable v = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    v = _context.getDrawable(id);
                }
                else {
                    v = _context.getResources().getDrawable(id);
                }
                _images.put(uri,new WeakReference<Drawable>(v));
                return v;
            }
            catch (final Throwable ex) {
                if(callback != null) {
                    final WeakReference<Callback<Drawable>> ref = new WeakReference<Callback<Drawable>>(callback);
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Callback<Drawable> v = ref.get();
                            if(v != null) {
                                v.onFail(ex);
                            }
                        }
                    });
                }
                Log.d("kk-view",ex.getMessage(),ex);
            }
        }
        else if(uri.startsWith("assets://")){

            if(callback != null) {
                final AssetManager assets = _context.getAssets();
                final String name = uri.substring(9);
                final WeakReference<Callback<Drawable>> ref = new WeakReference<Callback<Drawable>>(callback);

                IO.current().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            InputStream in = assets.open(name) ;

                            try {

                                final Drawable image = Drawable.createFromStream(in,name);

                                _handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        _images.put(uri,new WeakReference<Drawable>(image));
                                        Callback<Drawable> v = ref.get();
                                        if(v != null) {
                                            v.onLoaded(image);
                                        }
                                    }
                                });
                            }
                            finally {
                                in.close();
                            }

                        } catch (final Throwable ex) {

                            _handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Callback<Drawable> v = ref.get();
                                    if(v != null) {
                                        v.onFail(ex);
                                    }
                                }
                            });

                        }
                    }
                });
            }

        }

        return null;
    }

    @Override
    public Element getElement(String uri, final Document document, final Callback<Element> callback) {

        int i = uri.indexOf("R.xml.");

        XmlPullParser parser = null;
        InputStream in = null;

        if(i == 0) {
            try {
                Class<?> clazz = Class.forName(_context.getPackageName() + ".R$xml");
                java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 6));
                int id = fd.getInt(null);
                parser = _context.getResources().getXml(id);
            }
            catch (final Throwable ex) {
                if(callback != null) {
                    final WeakReference<Callback<Element>> ref = new WeakReference<Callback<Element>>(callback);
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Callback<Element> v = ref.get();
                            if(v != null) {
                                v.onFail(ex);
                            }
                        }
                    });
                }
                Log.d("kk-view",ex.getMessage(),ex);
            }
        }
        else if(i > 0) {
            try {
                Class<?> clazz = Class.forName(uri.substring(0,i) + "R$drawable");
                java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 6));
                int id = fd.getInt(null);
                parser = _context.getResources().getXml(id);
            }
            catch (final Throwable ex) {
                if(callback != null) {
                    final WeakReference<Callback<Element>> ref = new WeakReference<Callback<Element>>(callback);
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Callback<Element> v = ref.get();
                            if(v != null) {
                                v.onFail(ex);
                            }
                        }
                    });
                }
                Log.d("kk-view",ex.getMessage(),ex);
            }
        }
        else if(uri.startsWith("assets://")){

            if(callback != null) {
                final AssetManager assets = _context.getAssets();
                final String name = uri.substring(9);
                final WeakReference<Callback<Element>> ref = new WeakReference<Callback<Element>>(callback);

                try {

                    in = assets.open(name) ;

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    parser = factory.newPullParser();
                    parser.setInput(in,"utf-8");

                } catch (final Throwable ex) {

                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Callback<Element> v = ref.get();
                            if(v != null) {
                                v.onFail(ex);
                            }
                        }
                    });

                }
            }

        }

        if(parser != null) {

            try {
                XMLReader reader = new XMLReader(document);
                return reader.read(parser);
            }
            catch (final Throwable ex) {
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(ex);
                    }
                });
            }
            finally {
                if(in != null) {
                    try {
                        in.close();
                    }
                    catch (final Throwable ex) {
                        _handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFail(ex);
                            }
                        });
                    }
                }
            }
        }
        else if(in != null) {
            try {
                in.close();
            }
            catch (final Throwable ex) {
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(ex);
                    }
                });
            }
        }

        return null;
    }

    private static Pool<IResource> _pool = new Pool<IResource>();

    public static void push(IResource resource) {
        _pool.push(resource);
    }

    public static IResource peek() {
        return _pool.peek();
    }

    public static IResource pop() {
        return _pool.pop();
    }

}
