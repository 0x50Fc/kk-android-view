package cn.kkserver.view.value;


/**
 * Created by zhanghailong on 16/7/7.
 */
public class Value {

    private static Pool<Unit> _unit = new Pool<Unit>();

    public static void push(Unit unit) {
        _unit.push(unit);
    }

    public static Unit peek() {
        return _unit.peek();
    }

    public static Unit pop() {
        return _unit.pop();
    }

    public static double doubleValue(String v,double defaultValue) {

        if(v == null) {
            return defaultValue;
        }

        if(v.endsWith("dp")) {
            try {
                return Double.valueOf(v.substring(0,v.length() - 2)) * peek().dp;
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else if(v.endsWith("px")) {
            try {
                return Double.valueOf(v.substring(0,v.length() - 2));
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else {
            try {
                return Double.valueOf(v);
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }

    }

    public static int intValue(String v, int defaultValue) {

        if(v == null) {
            return defaultValue;
        }

        if(v.equals("auto")) {
            return Integer.MAX_VALUE;
        }

        if(v.endsWith("dp")) {
            try {
                return (int) (Integer.valueOf(v.substring(0,v.length() - 2)) * peek().dp);
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else if(v.endsWith("px")) {
            try {
                return Integer.valueOf(v.substring(0,v.length() - 2));
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else {
            try {
                return Integer.valueOf(v);
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }

    }

    public static long longValue(String v, long defaultValue) {

        if(v == null) {
            return defaultValue;
        }

        if("auto".equals(v)) {
            return Integer.MAX_VALUE;
        }

        if(v.endsWith("dp")) {
            try {
                return (long) (Long.valueOf(v.substring(0,v.length() - 2)) * peek().dp);
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else if(v.endsWith("px")) {
            try {
                return Long.valueOf(v.substring(0,v.length() - 2));
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else {
            try {
                return Long.valueOf(v);
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
    }

    public static double doubleValue(String v, double baseValue,double defaultValue) {

        if(v == null) {
            return defaultValue;
        }

        if("auto".equals(v)) {
            return Integer.MAX_VALUE;
        }

        double vv = 0;

        int i = v.indexOf("%");

        if(i >= 0) {

            try {

                vv = Double.valueOf(v.substring(0,i)) * baseValue * 0.01;

                if(i + 1 < v.length()) {
                    v = v.substring(i + 1);
                }
                else {
                    return vv;
                }
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }

        if(v.endsWith("dp")) {
            try {
                return vv + Double.valueOf(v.substring(0,v.length() - 2)) * peek().dp;
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else if(v.endsWith("px")) {
            try {
                return vv + Double.valueOf(v.substring(0,v.length() - 2));
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
        else {
            try {
                return vv + Double.valueOf(v);
            }
            catch (Throwable e) {
                return defaultValue;
            }
        }
    }

    public static int intValue(String v, int baseValue,int defaultValue) {
        return (int) Math.ceil(doubleValue(v,(double) baseValue,(double) defaultValue));
    }

    public static boolean booleanValue(String v, boolean defaultValue) {

        if( "true".equals(v) || "yes".equals(v)) {
            return true;
        }

        if( "false".equals(v) || "no".equals(v)) {
            return false;
        }

        return defaultValue;
    }

    public static Color colorValue(String v, Color defaultValue) {
        if(v == null) {
            return defaultValue;
        }
        try {
            return new Color(v);
        }
        catch (Throwable e) {
            return defaultValue;
        }
    }

    public static Font fontValue(String v, Font defaultValue) {

        if(v == null) {
            return defaultValue;
        }

        String[] vs = v.split(" ");

        String name = null;
        int size = 14;
        boolean bold = false;
        boolean italic = false;

        for(String vv : vs) {
            if(vv.endsWith("dp")) {
                size = intValue(vv,0);
            }
            else if(vv.endsWith("px")) {
                size = intValue(vv,0);
            }
            else if(vv.equals("bold")) {
                bold = true;
            }
            else if(vv.equals("italic")) {
                italic = true;
            }
            else {
                name = vv;
            }
        }

        try {
            return new Font(name,size,bold,italic);
        }
        catch (Throwable e) {
            return defaultValue;
        }
    }

    public static String stringValue(Object value,String defaultValue) {

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof  String) {
            return (String) value;
        }

        return value.toString();
    }
}
