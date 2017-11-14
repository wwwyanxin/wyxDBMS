import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private final static Pattern relPattern =Pattern.compile("(\\w+(?:\\.\\w+)?)\\s?([<=>])\\s?([^\\s\\;]+)");
   // private final static Pattern updateSetPattern=Pattern.compile("(\\w+)\\s?=\\s?([^\\s\\;]+)")

    //解析投影
    public static List<String> parseProjection(String str) {
        List<String> projectionList = new ArrayList<>();
        String[] projectionNames = str.trim().split(",");
        for (String projectionName : projectionNames) {
            projectionList.add(projectionName.trim());
        }
        return projectionList;
    }

    public static List<String> parseFrom(String str) {
        String[] tableNames = str.trim().split(",");
        List<String> tableNameList = new ArrayList<>();
        for (String tableName : tableNames) {
            tableNameList.add(tableName.trim());
        }
        return tableNameList;
    }

    public static List<Map<String, String>> parseWhere(String str) {
//        Matcher relMatcher = relPattern.matcher(str);

        List<Map<String, String>> filtList = new LinkedList<>();
        /*//如果没有and返回空过滤器
        if (!str.matches("and")) {
            return filtList;
        }*/
        String[] filtStrs = str.trim().split("and");
        for (String filtStr : filtStrs) {
            Map<String, String> filtMap = new LinkedHashMap<>();
            Matcher relMatcher = relPattern.matcher(filtStr);
            relMatcher.find();
            //String[] filt = filtStr.split(relPattern);
            filtMap.put("fieldName", relMatcher.group(1));
            filtMap.put("relationshipName", relMatcher.group(2));
            filtMap.put("condition", relMatcher.group(3));

            filtList.add(filtMap);
        }
        return filtList;
    }

    public static Map<String, Field> parseCreateTable(String fieldsStr) {
        String[] lines = fieldsStr.trim().split(",");
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        for (String line : lines) {
            String property[]=line.trim().split(" ");

            Field field = new Field();

            field.setName(property[0]);
            field.setType(property[1]);
            //如果是主键字段后面加*
            if (3 == property.length && "*".equals(property[2])) {
                field.setPrimaryKey(true);
            } else {
                field.setPrimaryKey(false);
            }
            fieldMap.put(property[0], field);
        }

        return fieldMap;
    }

    public static Map<String, String> parseUpdateSet(String str) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        String[] setStrs = str.trim().split(",");
        for (String setStr : setStrs) {
            Matcher relMatcher = relPattern.matcher(setStr);
            relMatcher.find();
            //将组1做为key，组3作为value
            dataMap.put(relMatcher.group(1), relMatcher.group(3));
        }
        return dataMap;
    }
}
