package cn.kkserver.view.document;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

import java.util.Set;
import java.util.TreeSet;

import cn.kkserver.view.event.Event;
import cn.kkserver.view.value.Color;
import cn.kkserver.view.value.Rect;
import cn.kkserver.view.value.Unit;
import cn.kkserver.view.value.Value;

public class CanvasElement extends TouchElement{


	public CanvasElement(Document document, String name, int elementId) {
		super(document, name, elementId);
	}

	public void draw(Canvas canvas) {
		
		drawBackground(canvas);

		onDrawElement(canvas);
		
		drawBorder(canvas);
		
	}
	
	protected void onDrawElement(Canvas canvas){
		
	}
	
	public Color getBackgroundColor(){
		return colorValue("background-color", Color.clearColor);
	}
	
	protected void drawBackground(Canvas canvas){
		
		Color backgroundColor = getBackgroundColor();
		
		if(backgroundColor.a() != 0 ){

			float displayScale = Value.peek().displayScale;

			Rect r = frame();
			
			Paint paint = new Paint();
			
			paint.setAntiAlias(true);
			paint.setColor(backgroundColor.intValue() & 0x0ffffff);
			paint.setAlpha(backgroundColor.a());
			paint.setStyle(Style.FILL);
	
			int radius = borderRadius();
			float borderWidth =  intValue("border-width",0) * displayScale;

			if(radius == 0){
				canvas.drawRect(borderWidth * 0.5f,borderWidth * 0.5f,r.width - borderWidth * 0.5f,r.height - borderWidth * 0.5f,paint);
			}
			else {
				canvas.drawRoundRect(new RectF(borderWidth * 0.5f,borderWidth * 0.5f,r.width- borderWidth * 0.5f,r.height- borderWidth * 0.5f)
						,radius * displayScale,radius * displayScale ,paint);
			}
			
		}
	}

	protected void drawBorder(Canvas canvas){
		
		Color borderColor = colorValue("border-color",Color.clearColor);
		
		float borderWidth =  intValue("border-width",0);
		
		if(borderWidth > 0 && borderColor.a() != 0){

			float displayScale = Value.peek().displayScale;

			borderWidth = borderWidth * displayScale;

			Rect r = frame();
			
			int radius = borderRadius();
			
			Paint paint = new Paint();
			
			paint.setAntiAlias(true);
			paint.setColor(borderColor.intValue() & 0x0ffffff);
			paint.setAlpha(borderColor.a());
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(borderWidth);


			if(radius == 0){
				canvas.drawRect(borderWidth * 0.5f,borderWidth * 0.5f,r.width - borderWidth * 0.5f ,r.height - borderWidth * 0.5f,paint);
			}
			else {
				canvas.drawRoundRect(new RectF(borderWidth * 0.5f,borderWidth * 0.5f,r.width - borderWidth * 0.5f,r.height - borderWidth * 0.5f )
						,radius * displayScale,radius * displayScale,paint);
			}
			
		}
		
	}

	public boolean isHidden() {
		return booleanValue("hidden",false) || ! booleanValue("visible",true);
	}

	public int borderRadius() {
		return intValue("border-radius",0);
	}

	@Override
	public void setText(String text){
		super.setText(text);
		setNeedsDisplay();
	}

	private static final Set<String> _changedKeys;

	static  {
		_changedKeys = new TreeSet<String>();
		_changedKeys.add("hidden");
		_changedKeys.add("visible");
		_changedKeys.add("border-radius");
		_changedKeys.add("class");
		_changedKeys.add("status");
	}

	@Override
	protected void onChangeKey(String key) {
		super.onChangeKey(key);

		if(_changedKeys.contains(key)) {
			setNeedsDisplay();
		}

	}


	public void setNeedsDisplay() {

		sendEvent(new CanvasNeedsDisplay(this));

	}

	public static class CanvasNeedsDisplay extends Event {

		public final CanvasElement element;

		public CanvasNeedsDisplay(CanvasElement element) {
			super("element.canvas.NeedsDisplay");
			this.element = element;
		}

	}

}
