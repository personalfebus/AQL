package database.field;

//todo
public class IntField implements Field {
    private int value;

    public static final String TYPE = "INT";

    @Override
    public String getType() {
        return TYPE;
    }
}
