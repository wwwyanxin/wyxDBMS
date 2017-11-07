import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IndexKey implements Comparable,Serializable {
    private String value;
    private String type;

    public IndexKey(String value, String type) {
        this.value = value;
        this.type = type;
    }



    @Override
    public int compareTo(Object ohterValue) {
        String keyValue=((IndexKey)ohterValue).getValue();
        switch (type) {
            case "int":
                return Integer.valueOf(value).compareTo(Integer.valueOf(keyValue));
            case "double":
                return Double.valueOf(value).compareTo(Double.valueOf(keyValue));
            case "varchar":
                return value.compareTo(String.valueOf(keyValue));
            default:
                try {
                    throw new Exception("条件限定不匹配");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IndexKey indexKey = (IndexKey) o;

        return value != null ? value.equals(indexKey.value) : indexKey.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
