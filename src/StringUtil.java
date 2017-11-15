import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private final static Pattern relPattern =Pattern.compile("(\\w+(?:\\.\\w+)?)\\s?([<=>])\\s?([^\\s\\;]+)");
   // private final static Pattern updateSetPattern=Pattern.compile("(\\w+)\\s?=\\s?([^\\s\\;]+)")

    /**
     * 解析投影
     * @param str 解析字符串
     * @param tableName 表名
     * @param fieldMap 此表字段
     * @return 投影的字段集
     */
    public static Set<String> parseProjection(String str,String tableName,Map<String,Field> fieldMap) {
        Set<String> projectionSet = new LinkedHashSet<>();
        String[] projectionNames = str.trim().split(",");
        for (String projectionName : projectionNames) {
            projectionName = projectionName.trim();
            //如果包含table.id这样的型式，将table名进行匹配，如果不匹配则跳过
            if (projectionName.contains(".")) {
                String[] projection = projectionName.split("\\.");
                //如果不匹配就跳过
                if (!tableName .equals(projection[0]) ) {
                    continue;
                } else {
                    //匹配
                    projectionName=projection[1];
                }
            }
            Field field = fieldMap.get(projectionName);
            if (null != field) {
                projectionSet.add(projectionName);
            }
        }
        return projectionSet;
    }


    public static List<String> parseFrom(String str) {
        String[] tableNames = str.trim().split(",");
        List<String> tableNameList = new ArrayList<>();
        for (String tableName : tableNames) {
            tableNameList.add(tableName.trim());
        }
        return tableNameList;
    }

    /**
     * 解析单表选择
     * @param str
     * @return
     */
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

    /**
     * 解析多表选择
     * @param str 解析字符串
     * @param tableName 表名
     * @param fieldMap 表中字段
     * @return
     */
    public static List<Map<String, String>> parseWhere(String str,String tableName,Map<String,Field> fieldMap) {
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

            String fieldName = relMatcher.group(1);
            //如果包含table.id这样的型式，将table名进行匹配，如果不匹配则跳过
            if (fieldName.contains(".")) {
                String[] field = fieldName.split("\\.");
                //如果不匹配就跳过
                if (!tableName.equals(field[0])) {
                    continue;
                } else {
                    //匹配
                    fieldName = field[1];
                }
            }
            Field field = fieldMap.get(fieldName);
            if (null != field) {
            filtMap.put("fieldName", fieldName);
            filtMap.put("relationshipName", relMatcher.group(2));
            filtMap.put("condition", relMatcher.group(3));

            filtList.add(filtMap);
            }
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
