package cn.kkserver.view.value;

/**
 * Created by zhanghailong on 16/7/6.
 */
public class Color {

    private int _value;

    public Color(double r, double g, double b, double a) {
        _value = ((int) (a * 0x0ff) << 24) | ((int) (r * 0x0ff) << 16) | ((int) (g * 0x0ff) << 8) | ((int) (b * 0x0ff));
    }

    public Color(double r, double g, double b) {
        _value = (0x0ff000000) | ((int) (r * 0x0ff) << 16) | ((int) (g * 0x0ff) << 8) | ((int) (b * 0x0ff));
    }

    public Color(int value) {
        _value = value;
    }

    public Color(String value) {
        _value = 0;

        if(value != null && value.startsWith("#")) {
            String[] vs = value.split(" ");
            String v = vs[0];
            if(v.length() == 9) {
                _value = (Integer.valueOf(v.substring(1,3),16) << 24)
                        | (Integer.valueOf(v.substring(3,5),16) << 16)
                        | (Integer.valueOf(v.substring(5,7),16) << 8)
                        | (Integer.valueOf(v.substring(7,9),16));
            }
            else if(v.length() == 7) {
                _value = 0x0ff000000
                        | (Integer.valueOf(v.substring(1,3),16) << 16)
                        | (Integer.valueOf(v.substring(3,5),16) << 8)
                        | (Integer.valueOf(v.substring(5,7),16));
            }
            else if(v.length() == 4) {
                _value = 0x0ff000000
                        | (Integer.valueOf(v.substring(1,2) + v.substring(1,2),16) << 16)
                        | (Integer.valueOf(v.substring(2,3) + v.substring(2,3),16) << 8)
                        | (Integer.valueOf(v.substring(3,4) + v.substring(3,4) ,16));
            }
            if(vs.length > 1) {
                _value = _value | ((int) (Double.valueOf(vs[1]) * 0x0ff) << 24);
            }
        }

    }

    public int intValue() {
        return _value;
    }

    public int r() {
        return (_value & 0x0ff0000) >> 16;
    }

    public int g() {
        return (_value & 0x0ff00) >> 8;
    }

    public int b() {
        return (_value & 0x0ff);
    }

    public int a() {
        return (_value & 0x0ff000000) >> 24;
    }

    public double alpha() {
        return (double) a() / 255.0;
    }

    @Override
    public String toString() {
        if(_value == 0) {
            return "clear";
        }
        else if((_value & 0x0ff000000) == 0xff) {
            return String.format("#%02x%02x%02x",r(),g(),b());
        }
        else {
            return String.format("#%02x%02x%02x%02x",a(),r(),g(),b());
        }
    }

    public final static Color clearColor = new Color(0);

    public final static Color blackColor = new Color(0xff000000);
}
