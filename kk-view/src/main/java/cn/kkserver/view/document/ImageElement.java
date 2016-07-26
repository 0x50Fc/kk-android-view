package cn.kkserver.view.document;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import cn.kkserver.view.io.IResource;
import cn.kkserver.view.io.Resource;
import cn.kkserver.view.value.Edge;
import cn.kkserver.view.value.Rect;
import cn.kkserver.view.value.Size;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/17.
 */
public class ImageElement extends CanvasElement {

    public ImageElement(Document document, String name, int elementId) {
        super(document, name, elementId);
        setEnabledTouch(false);
    }

    private Drawable _defaultImage;
    private ImageCallback _defaultCallback;

    public Drawable defaultImage() {

        if(_defaultImage == null && _defaultCallback ==null) {

            String v = stringValue("default-src",null);

            if(v != null) {

                if(_defaultCallback == null) {

                    _defaultCallback = new ImageCallback(this) {

                        @Override
                        public void onLoaded(Drawable object) {
                            ImageElement v = _ref.get();
                            if(v != null ){
                                v.setDefaultImage(object);
                            }
                        }

                        @Override
                        public void onFail(Throwable ex) {

                        }

                        @Override
                        public void onProcess(long loadBytes, long totalBytes) {

                        }
                    };
                }

                Drawable img =Resource.peek().getImage(v,_defaultCallback);
                if(img != null && _image == null) {
                    _defaultImage = img;
                }
            }

        }
        return _defaultImage;
    }

    public void setDefaultImage(Drawable image) {
        if(_defaultImage != image) {
            _defaultImage = image;
            _defaultCallback = null;
            if(_image == null && _imageCallback == null) {
                setNeedsDisplay();
            }
        }
    }

    private Drawable _image;
    private ImageCallback _imageCallback;

    public Drawable image() {

        if(_image == null && _imageCallback == null) {

            String v = stringValue("src",null);

            if(v != null) {

                if(_imageCallback == null) {

                    _imageCallback = new ImageCallback(this) {

                        @Override
                        public void onLoaded(Drawable object) {
                            ImageElement v = _ref.get();
                            if(v != null) {
                                v.setImage(object);
                            }
                        }

                        @Override
                        public void onFail(Throwable ex) {

                        }

                        @Override
                        public void onProcess(long loadBytes, long totalBytes) {

                        }
                    };

                }

                Drawable img =Resource.peek().getImage(v,_imageCallback);
                if(img != null && _image == null) {
                    _image = img;
                }
            }
        }

        return _image;
    }

    public void setImage(Drawable image) {
        if(_image != image) {
            _image = image;
            _imageCallback = null;
            setNeedsDisplay();
        }
    }


    @Override
    protected void onChangeKey(String key) {
        super.onChangeKey(key);

        if(key.equals("default-src")) {
            _defaultImage = null;
            _defaultCallback = null;
            setNeedsDisplay();
        }
        else if(key.equals("src")) {
            _image = null;
            _imageCallback = null;
            setNeedsDisplay();
        }

    }

    @Override
    protected void onDrawElement(Canvas canvas){
        super.onDrawElement(canvas);

        Drawable image = this.image();

        if(image == null){
            image = defaultImage();
        }

        if(image != null){

            Rect r = frame();
            float imageWidth = image.getIntrinsicWidth();
            float imageHeight = image.getIntrinsicHeight();
            float width = r.width;
            float height = r.height;

            float radius = borderRadius();
            float tx = 0,ty = 0,rx = 1.0f,ry = 1.0f;

            String gravity = stringValue("gravity","aspect-fill");

            if("center".equals(gravity)){
                float dx = (imageWidth - width) / 2.0f;
                float dy = (imageHeight - height) / 2.0f;
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("resize".equals(gravity)){
                rx = width / imageWidth;
                ry =  height / imageHeight;
                image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
            }
            else if("top".equals(gravity)){
                float dx = (imageWidth - width) / 2.0f;
                float dy = 0;
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("bottom".equals(gravity)){
                float dx = (imageWidth - width) / 2.0f;
                float dy = (imageHeight - height);
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("left".equals(gravity)){
                float dx =0;
                float dy = (imageHeight - height) / 2.0f;
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("right".equals(gravity)){
                float dx = (imageWidth - width) ;
                float dy = (imageHeight - height) / 2.0f;
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("topleft".equals(gravity)){
                float dx = 0 ;
                float dy = 0;
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("topright".equals(gravity)){
                float dx = (imageWidth - width) ;
                float dy = 0;
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("bottomleft".equals(gravity)){
                float dx = 0 ;
                float dy = (imageHeight - height);
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("bottomright".equals(gravity)){
                float dx = (imageWidth - width) ;
                float dy = (imageHeight - height);
                image.setBounds((int) (dx  ), (int) (dy  )
                        , (int) ( width  ), (int) (height  ));
            }
            else if("aspect".equals(gravity)){
                float r0 = imageWidth / imageHeight;
                float r1 = width / height;
                if(r0 == r1){
                    image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
                else if(r0 > r1){

                    rx = width / imageWidth;
                    ry = width / imageWidth;

                    imageHeight = width / r0;
                    imageWidth = width;

                    image.setBounds(0, (int) (imageHeight - height), (int) ( width  ), (int) (height  ));
                }
                else if(r0 < r1){

                    rx = height / imageHeight;
                    ry = height / imageHeight;

                    imageWidth = height * r0;
                    imageHeight = height;

                    image.setBounds((int) (imageWidth - width),0, (int) ( width  ), (int) (height  ));
                }
            }
            else if("aspect-top".equals(gravity)){
                float r0 = imageWidth / imageHeight;
                float r1 = width / height;
                if(r0 == r1){
                    rx = width / imageWidth;
                    ry = height / imageHeight;
                    image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
                else if(r0 < r1){

                    rx = width / imageWidth;
                    ry = width / imageWidth;

                    imageHeight = width / r0;
                    imageWidth = width;

                    image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
                else if(r0 > r1){

                    rx = height / imageHeight;
                    ry = height / imageHeight;

                    imageWidth = height * r0;
                    imageHeight = height;
                    image.setBounds(0,0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
            }
            else if("aspect-bottom".equals(gravity)){

                float r0 = imageWidth / imageHeight;
                float r1 = width / height;
                if(r0 == r1){
                    rx = width / imageWidth;
                    ry = height / imageHeight;
                    image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
                else if(r0 < r1){

                    rx = width / imageWidth;
                    ry = width / imageWidth;

                    imageHeight = width / r0;
                    imageWidth = width;

                    ty = (height - imageHeight)  ;

                    image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
                else if(r0 > r1){


                    rx = height / imageHeight;
                    ry = height / imageHeight;

                    imageWidth = height * r0;
                    imageHeight = height;

                    tx = (width - height)  ;

                    image.setBounds(0 , 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }

            }
            else {

                float r0 = imageWidth / imageHeight;
                float r1 = width / height;
                if(r0 == r1){
                    rx = width / imageWidth;
                    ry = height / imageHeight;
                    image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
                else if(r0 < r1){

                    rx = width / imageWidth;
                    ry = width / imageWidth;

                    ty = (height - width / r0)   * 0.5f;

                    image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }
                else if(r0 > r1){

                    rx = height / imageHeight;
                    ry = height / imageHeight;

                    tx = (width - height * r0)   * 0.5f;

                    image.setBounds(0 , 0, (int) ( imageWidth  ), (int) (imageHeight  ));
                }

            }

            if(radius != 0.0f && image instanceof BitmapDrawable){

                int x = (int) (width );
                int y = (int) (height );
                float[] mOuter = new float[] { radius, radius, radius, radius,
                        radius, radius, radius, radius };


                // 新建一个矩形
                RectF outerRect = new RectF(0, 0, x, y);

                Paint paint = new Paint();

                paint.setAntiAlias(true);
                paint.setColor(0xffffffff);

                canvas.saveLayer(outerRect, paint, Canvas.CLIP_SAVE_FLAG);

                Path mPath = new Path();
                // 创建一个圆角矩形路径
                mPath.addRoundRect(outerRect, mOuter, Path.Direction.CW);

                canvas.clipPath(mPath);

                canvas.drawRoundRect(outerRect, radius, radius, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                canvas.translate(tx, ty);
                canvas.scale(rx, ry);

                canvas.drawBitmap(((BitmapDrawable) image).getBitmap(), 0, 0, paint);

                canvas.restore();

            }
            else{
                canvas.translate(tx, ty);
                canvas.scale(rx, ry);
                image.draw(canvas);
            }
        }

    }

    @Override
    public Size layoutChildren(Edge padding){

        Rect r = this.frame();

        if(r.width == Integer.MAX_VALUE || r.height == Integer.MAX_VALUE){

            Drawable image = this.image();

            if(image != null ){

                float displayScale = Value.peek().displayScale;

                float width = image.getIntrinsicWidth();
                float height = image.getIntrinsicHeight();

                if(r.width == Integer.MAX_VALUE){
                    r.width = (int) (width / displayScale);
                }

                if(r.height == Integer.MAX_VALUE){
                    r.height = (int) (height / displayScale);
                }

            }
            else{
                if(r.width == Integer.MAX_VALUE){
                    r.width = intValue("min-width",0);
                }

                if(r.height == Integer.MAX_VALUE){
                    r.height = intValue("min-height",0);;
                }
            }

        }
        return new Size(r.width,r.height);
    }

    private abstract static class ImageCallback implements IResource.Callback<Drawable> {

        protected WeakReference<ImageElement> _ref;

        public ImageCallback(ImageElement element) {
            _ref = new WeakReference<ImageElement>(element);
        }

    }
}
