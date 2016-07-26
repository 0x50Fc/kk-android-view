package cn.kkserver.view.document;

import android.graphics.Canvas;
import android.graphics.Paint;

import cn.kkserver.view.value.Color;
import cn.kkserver.view.value.Edge;
import cn.kkserver.view.value.Font;
import cn.kkserver.view.value.Rect;
import cn.kkserver.view.value.Size;
import cn.kkserver.view.value.Value;

public class TextElement extends CanvasElement {

	public TextElement(Document document, String name, int elementId) {
		super(document, name, elementId);
		setEnabledTouch(false);
	}

	public Font font(){
		Font v = fontValue("font",null);
		if(v == null){
			v = new Font(intValue("font-size",14));
		}
		return v;
	}

	public Color textColor(){
		return colorValue("color", Color.blackColor);
	}

	@Override
	protected void onDrawElement(Canvas canvas){
		super.onDrawElement(canvas);
		
		String text = this.text();
		
		if(text != null && text.length() > 0){

			float displayScale = Value.peek().displayScale;


			Font font = this.font();
	        Color textColor = this.textColor();
			
	        Paint paint = new Paint();
	        
	        paint.setAntiAlias(true);
	        paint.setTextSize(font.size * displayScale);
	        paint.setFakeBoldText(font.bold);
	        paint.setColor(textColor.intValue() & 0x0ffffff);
	        paint.setAlpha(textColor.a());
	        
	        Rect r = frame();
	        
	        float width = r.width ;
	        float height = r.height ;
	        float maxWidth = intValue("max-width",r.width);
	        float maxHeight = intValue("max-height",r.height);
	        
	        Size textSize = getTextSize(text,paint,maxWidth);
	        
	        float w = textSize.width;
	        float h = textSize.height;
	        
	        if(h > maxHeight){
	        	h = maxHeight;
	        }

	        float dy = 0.0f;
	        
        	String align = stringValue("vertical-align","top");
    		
    		if("middle".equals(align)){
    			dy = ( height - h ) * 0.5f;
    		}
    		else if("bottom".equals(align)){
    			dy = ( height - h ) ;
    		}
    		
			align = stringValue("text-align","left");
    		
    		if("center".equals(align)){
    			
    			{
    				
    				float[] widths = new float[1];
    				
    				int start = 0;
    				int end = text.length();
    				int len;
    				
    				while(start < end ){
    					
    					len = paint.breakText(text, start, end, false, w, widths);

    					canvas.drawText(text, start, start + len, (width - widths[0]) / 2.0f, dy - paint.ascent(), paint);
    					
    					dy +=  - paint.ascent() + paint.descent();
    					
    					start += len;
    					
    				}
    			}

    		}
    		else if("right".equals("align")){
    			
    			{
    				
    				float[] widths = new float[1];
    				
    				int start = 0;
    				int end = text.length();
    				int len;
    				
    				while(start < end ){
    					
    					len = paint.breakText(text, start, end, false, w, widths);

    					canvas.drawText(text, start, start +  len, width - widths[0], dy - paint.ascent(), paint);
    					
    					dy +=  - paint.ascent() + paint.descent();
    					
    					start += len;
    					
    				}
    			}

    		}
    		else {
    			{
    				
    				float[] widths = new float[1];
    				
    				int start = 0;
    				int end = text.length();
    				int len;
    				
    				while(start < end ){
    					
    					len = paint.breakText(text, start, end, false, w, widths);

    					canvas.drawText(text, start, start +  len, 0.0f, dy - paint.ascent(), paint);
    					
    					dy +=  - paint.ascent() + paint.descent();
    					
    					start += len;
    					
    				}
    			}
    		}

	        
		}
		
	}
	
	public Size getTextSize(String text,Paint paint,float maxWidth){
		
		Size size = new Size(0,0);

		float[] widths = new float[1];

		int start = 0;
		int end = text.length();
		int len;

		while(start < end ){

			len = paint.breakText(text, start, end, false, maxWidth, widths);

			if(widths[0] > size.width){
				size.width = (int) Math.ceil( widths[0]);
			}

			size.height = (int) Math.ceil(size.height - paint.ascent() + paint.descent());

			start += len;
		}

		return size;
		
	}

	public Size layoutChildren(Edge padding){
		
		Rect r = frame();
	    
	    if(r.width == Integer.MAX_VALUE || r.height == Integer.MAX_VALUE){

	    	String text = this.text();

	        if(text != null && text.length() > 0){

				float displayScale = Value.peek().displayScale;

				Font font = this.font();
	            
	            Paint paint = new Paint();
	            
	            paint.setTextSize(font.size * displayScale);
	            paint.setFakeBoldText(font.bold);
	      
	            paint.breakText(text, 0, 0, false, 0, null);
	            
		        float maxWidth = intValue("max-width",r.width);
		        float maxHeight = intValue("max-height",r.height);
		        

		        Size textSize = getTextSize(text,paint,maxWidth);
	            
		        float w = textSize.width ;
		        float h = textSize.height;
		        
		        if(h > maxHeight){
		        	h = maxHeight;
		        }
		        
	            if(r.width == Integer.MAX_VALUE){
	                r.width = (int) Math.ceil(w + padding.left + padding.right);
	                int max = intValue("max-width",r.width);
	                int min = intValue("min-width",r.width);
	                if(r.width > max){
	                    r.width = max;
	                }
	                if(r.width <  min){
	                    r.width = min;
	                }
	            }
	            
	            if(r.height == Integer.MAX_VALUE){

	            	r.height = (int) Math.ceil(h + padding.top + padding.bottom);

	            	int max = intValue("max-height",r.height);
	                int min = intValue("min-height",r.height);

	                if(r.height > max){
	                    r.height = max;
	                }
	                if(r.height <  min){
	                    r.height = min;
	                }

	            }
	        }
	        else{
	            if(r.width == Integer.MAX_VALUE){
	                r.width = intValue("min-width",0);
	            }
	            
	            if(r.height == Integer.MAX_VALUE){
	                r.height = intValue("min-height",0);
	            }
	        }
	        
	    }
	    return new Size(r.width,r.height);
	}

}
