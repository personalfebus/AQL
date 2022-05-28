package database.field;

public class Fields {
    public static String getIntClazz() {
        return int.class.getName();
    }

    public static String getLongClazz() {
        return long.class.getName();
    }

    public static String getDoubleClazz() {
        return double.class.getName();
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
        }

        throw new IncompatibleClassChangeError("Cannot compare " + c1 + " and " + c2);
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
}
