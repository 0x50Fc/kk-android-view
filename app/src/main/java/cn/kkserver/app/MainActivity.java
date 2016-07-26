package cn.kkserver.app;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.regex.Pattern;

import cn.kkserver.view.ElementObserver;
import cn.kkserver.view.ElementView;
import cn.kkserver.view.document.Document;
import cn.kkserver.view.document.Element;
import cn.kkserver.view.document.XMLReader;
import cn.kkserver.view.event.Event;
import cn.kkserver.view.io.Resource;
import cn.kkserver.view.obs.Observer;
import cn.kkserver.view.style.StyleSheet;
import cn.kkserver.view.value.Unit;
import cn.kkserver.view.value.Value;

public class MainActivity extends AppCompatActivity {

    private Document _document;
    private StyleSheet _styleSheet;
    private Observer _observer;
    private ElementObserver _elementObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _observer = new Observer();

        ElementView elementView = (ElementView) findViewById(R.id.elementView);

        _styleSheet = new StyleSheet();

        Unit unit = new Unit();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        unit.displayScale = metrics.density;
        unit.dp = 1;

        Value.push(unit);

        Resource.push(new Resource(this){

            @Override
            public Drawable getImage(final String uri, final Callback<Drawable> callback) {

                if(uri.startsWith("http://")) {

                    Picasso.with(MainActivity.this).load(uri).into(new Target() {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            callback.onLoaded(new BitmapDrawable(MainActivity.this.getResources(),bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            callback.onFail(new Exception("image fail: " + uri));
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                    return null;
                }
                else {
                    return super.getImage(uri,callback);
                }
            }

        });

        InputStream in = getResources().openRawResource(R.raw.style);

        try {
            _styleSheet.loadCSS(new InputStreamReader(in, "utf-8"));
        }
        catch(Throwable ex) {

        }
        finally {
            try {
                in.close();
            }
            catch(Throwable ex){}
        }


        _document = new Document();
        _document.setStyleSheet(_styleSheet);

        _document.on(Pattern.compile("^element\\.action$"), new Event.WeakCallback<Observer>(_observer) {

            @Override
            public boolean onEvent(Event event) {
                Observer observer = object();
                if(observer != null) {
                    _observer.set(new String[]{"items"},new Object[]{new TreeMap<String,Object>()});
                }
                return false;
            }
        });

        try {
            XMLReader reader = new XMLReader(_document);
            Element element = reader.read(getResources().getXml(R.xml.demo));
            _document.setRootElement(element);
            _elementObserver = new ElementObserver(element,_observer);
            elementView.setElement(element);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        _observer.set(new String[]{"items"},new Object[]{new TreeMap<String,Object>(),new TreeMap<String,Object>(),new TreeMap<String,Object>()});

    }
}
