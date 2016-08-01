package cn.kkserver.view.document;

import android.graphics.Canvas;
import android.graphics.Paint;

import cn.kkserver.view.value.Color;
import cn.kkserver.view.value.Edge;
import cn.kkserver.view.value.Font;
import cn.kkserver.view.value.Rect;
import cn.kkserver.view.value.Size;

public class LinkElement extends TextElement {

	public LinkElement(Document document, String name, int elementId) {
		super(document, name, elementId);
		setEnabledTouch(true);
	}

}
