package cn.kkserver.view.io;

import android.graphics.drawable.Drawable;
import android.telecom.Call;

import org.xmlpull.v1.XmlPullParser;

import cn.kkserver.view.document.Document;
import cn.kkserver.view.document.Element;

/**
 * Created by zhanghailong on 16/7/17.
 */
public interface IResource {

    public Drawable getImage(String uri, Callback<Drawable> callback);

    public Element getElement(String uri, Document document,Callback<Element> callback);

    public static interface Callback<T> {

        public void onLoaded(T object);

        public void onFail(Throwable ex);

        public void onProcess(long loadBytes,long totalBytes);

    }

}
