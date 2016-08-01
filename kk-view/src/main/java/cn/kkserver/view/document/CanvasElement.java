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
import cn.kkserver.view.style.ComputedStyle;
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

		Paint paint = new Paint();

		Path path = new Path();

		paint.setAntiAlias(true);

		{
			Color v = colorValue("color", Color.clearColor);
			paint.setColor(v.intValue() & 0x0ffffff);
			paint.setAlpha(v.a());
		}

		Rect frame  = frame();

		Element p = firstChild();

		while(p != null) {

			if(p instanceof PaintElement) {

				String name = p.name();

				if("color".equals(name)) {
					{
						Color v = p.colorValue("value", Color.clearColor);
						paint.setColor(v.intValue() & 0x0ffffff);
						paint.setAlpha(v.a());
					}
				}
				else if("move".equals(name)) {
					path.moveTo((float) p.intValue("x",frame.width,0),(float) p.intValue("y",frame.height,0));
				}
				else if("line".equals(name)) {
					path.lineTo((float) p.intValue("x",frame.width,0),(float) p.intValue("y",frame.height,0));
				}
				else if("arc".equals(name)) {
					path.arcTo(
							new RectF((float) p.intValue("left",frame.width,0)
									,(float) p.intValue("top",frame.height,0)
									,(float) p.intValue("right",frame.width,0)
									,(float) p.intValue("bottom",frame.height,0))

							,(float) (p.floatValue("start",0) * Math.PI / 180.0f)
							,(float) (p.floatValue("end",0) * Math.PI / 180.0f)
							,true);
				}
				else if("circle".equals(name)) {
					path.addCircle((float) p.intValue("x",frame.width,0)
							,(float) p.intValue("y",frame.height,0)
							,(float) p.intValue("radius",frame.width,0), Path.Direction.CW);
				}
				else if("width".equals(name)) {
					paint.setStrokeWidth(p.intValue("value",frame.width,0));
				}
				else if("draw".equals(name)) {

					boolean fill = p.booleanValue("fill",false);
					boolean stroke = p.booleanValue("stroke",false);

					if(fill && stroke) {
						paint.setStyle(Style.FILL_AND_STROKE);
					}
					else if(fill) {
						paint.setStyle(Style.FILL);
					}
					else if(stroke) {
						paint.setStyle(Style.STROKE);
					}

					canvas.drawPath(path,paint);

					path = new Path();
				}

			}

			p = p.nextSibling();
		}

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
