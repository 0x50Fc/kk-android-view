package cn.kkserver.view.document;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by zhanghailong on 16/7/14.
 */
public class XMLReader {

    private final Document _document;
    private final org.xml.sax.XMLReader _reader;

    public XMLReader(Document document) throws Throwable{
        _document = document;
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser newSAXParser = saxParserFactory.newSAXParser();
        _reader = newSAXParser.getXMLReader();
    }

    public Element read(Reader reader) throws Throwable {

        XMLContentHandler handler = new XMLContentHandler(_document);

        _reader.setContentHandler(handler);
        _reader.parse(new InputSource(reader));

        return handler.rootElement();

    }

    public Element read(XmlPullParser parser) throws Throwable {

        Element rootElement = null;
        Element element = null;
        StringBuilder text = null;

        int type = parser.next();

        while(type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                {
                    Element el = _document.createElement(parser.getName());

                    for(int i=0;i<parser.getAttributeCount();i++) {
                        el.attr(parser.getAttributeName(i),parser.getAttributeValue(i));
                    }

                    if(rootElement == null) {
                        rootElement = el;
                    }

                    if(element == null) {
                        element = el;
                    }
                    else {
                        element.append(el);
                        element = el;
                    }

                    if(text != null) {
                        text.delete(0,text.length());
                    }
                }
                    break;
                case XmlPullParser.END_TAG:
                {
                    if(element != null) {

                        if(element.firstChild() == null && text != null) {
                            element.setText(text.toString());
                        }

                        element = element.parentElement();

                    }

                    if(text != null) {
                        text.delete(0,text.length());
                    }
                }
                    break;
                case XmlPullParser.TEXT:
                {
                    if(text == null) {
                        text = new StringBuilder();
                    }
                    text.append(parser.getText());
                }
                    break;
            }

            type = parser.next();
        }

        return rootElement;
    }

    private static class XMLContentHandler implements ContentHandler {

        private final Document _document;
        private Element _rootElement;
        private Element _element;
        private StringBuilder _text;

        public Element rootElement() {
            return _rootElement;
        }

        public XMLContentHandler(Document document) {
            _document = document;
        }

        @Override
        public void setDocumentLocator(Locator locator) {

        }

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void endDocument() throws SAXException {

        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

            Element element = _document.createElement(localName);

            for(int i=0;i<atts.getLength();i++) {
                element.attr(atts.getLocalName(i),atts.getValue(i));
            }

            if(_rootElement == null) {
                _rootElement = element;
            }

            if(_element == null) {
                _element = element;
            }
            else {
                _element.append(element);
                _element = element;
            }

            if(_text != null) {
                _text.delete(0,_text.length());
            }

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if(_element != null) {

                if(_element.firstChild() == null && _text != null) {
                    _element.setText(_text.toString());
                }

                _element = _element.parentElement();

            }

            if(_text != null) {
                _text.delete(0,_text.length());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {

            if(_text == null) {
                _text = new StringBuilder();
            }

            _text.append(ch,start,length);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {

        }

        @Override
        public void skippedEntity(String name) throws SAXException {

        }
    }
}
