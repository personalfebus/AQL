package database.field;

import database.exception.TypeMismatchException;
import parser.ast.value.*;

public class Fields {
    public static String getIntClazz() {
        return "int";
    }

    public static String getLongClazz() {
        return "long";
    }

    public static String getDoubleClazz() {
        return "double";
    }

    public static String getStringClazz() {
        return "string";
    }

    public static String getCharClazz() {
        return "char";
    }

    public static int compare(Field o1, Field o2) {
        String c1 = o1.getClazz();
        String c2 = o2.getClazz();

        if (c1.equals(getIntClazz())) {
            if (c2.equals(getIntClazz())) {
                return compareIntAndInt((IntField) o1, (IntField) o2);
            } else if (c2.equals(getLongClazz())) {
                return compareIntAndLong((IntField) o1, (LongField) o2);
            } else if (c2.equals(getDoubleClazz())) {
                return compareIntAndDouble((IntField) o1, (DoubleField) o2);
            }
        } else if (c1.equals(getLongClazz())) {
            if (c2.equals(getIntClazz())) {
                return -compareIntAndLong((IntField) o2, (LongField) o1);
            } else if (c2.equals(getLongClazz())) {
                return compareLongAndLong((LongField) o1, (LongField) o2);
            } else if (c2.equals(getDoubleClazz())) {
                return compareLongAndDouble((LongField) o1, (DoubleField) o2);
            }
        } else if (c1.equals(getDoubleClazz())) {
            if (c2.equals(getIntClazz())) {
                return -compareIntAndDouble((IntField) o2, (DoubleField) o1);
            } else if (c2.equals(getLongClazz())) {
                return -compareLongAndDouble((LongField) o2, (DoubleField) o1);
            } else if (c2.equals(getDoubleClazz())) {
                return compareDoubleAndDouble((DoubleField) o1, (DoubleField) o2);
            }
        } else if (c1.equals(getStringClazz())) {
            if (c2.equals(getStringClazz())) {
                return compareStringAndString((StringField) o1, (StringField) o2);
            }
        } else if (c1.equals(getCharClazz())) {
            //maybe comparable to int?
            if (c2.equals(getCharClazz())) {
                return compareCharAndChar((CharField) o1, (CharField) o2);
            }
        }

        throw new IncompatibleClassChangeError("Cannot compare " + c1 + " and " + c2);
    }

    public static int compareCharAndChar(CharField o1, CharField o2) {
        return o1.getValue() - o2.getValue();
    }

    public static int compareStringAndString(StringField o1, StringField o2) {
        return o1.getValue().compareTo(o2.getValue());
    }

    public static int compareIntAndInt(IntField o1, IntField o2) {
        return Integer.compare(o1.getValue(), o2.getValue());
    }

    public static int compareIntAndLong(IntField o1, LongField o2) {
        return Long.compare(o1.getLongValue(), o2.getValue());
    }

    public static int compareIntAndDouble(IntField o1, DoubleField o2) {
        return Double.compare(o1.getDoubleValue(), o2.getValue());
    }

    public static int compareLongAndLong(LongField o1, LongField o2) {
        return Long.compare(o1.getValue(), o2.getValue());
    }

    public static int compareLongAndDouble(LongField o1, DoubleField o2) {
        return Double.compare(o1.getDoubleValue(), o2.getValue());
    }

    public static int compareDoubleAndDouble(DoubleField o1, DoubleField o2) {
        return Double.compare(o1.getValue(), o2.getValue());
    }

    public static Field astValueToField(String desiredClass, AstValue value) throws TypeMismatchException {
        if (value.getType().equalsIgnoreCase(desiredClass)) {
            if (value.getType().equals(getIntClazz())) {
                AstIntegerNumberValue tmp = (AstIntegerNumberValue) value;
                return new IntField((int)tmp.getValue());
            } else if (value.getType().equals(getLongClazz())) {
                AstIntegerNumberValue tmp = (AstIntegerNumberValue) value;
                return new LongField(tmp.getValue());
            } else if (value.getType().equals(getDoubleClazz())) {
                AstFloatingNumberValue tmp = (AstFloatingNumberValue) value;
                return new DoubleField(tmp.getValue());
            } else if (value.getType().equals(getStringClazz())) {
                AstStringValue tmp = (AstStringValue) value;
                return new StringField(tmp.getValue());
            } else if (value.getType().equals(getCharClazz())) {
                AstSymbolValue tmp = (AstSymbolValue) value;
                return new CharField(tmp.getValue());
            } else {
                throw new TypeMismatchException();
            }
        } else {
            if (desiredClass.equals("int") && value.getType().equals("long")) {
                AstIntegerNumberValue tmp = (AstIntegerNumberValue) value;
                return new IntField((int)tmp.getValue());
            } else {
                throw new TypeMismatchException();
            }
        }
    }
}
