import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    //匹配表的选择关系
    private final static Pattern SINGLE_REL_PATTERN = Pattern.compile("(\\w+(?:\\.\\w+)?)\\s?([<=>])\\s?([^\\s\\;\\.]+)[\\s;]");
    //匹配多个表的连接关系
    private final static Pattern JOIN_CONNECTION_REL_PATTERN = Pattern.compile("(\\w+(?:\\.\\w+)?)\\s?([<=>])\\s?(\\w+\\.\\w+)");
    // private final static Pattern updateSetPattern=Pattern.compile("(\\w+)\\s?=\\s?([^\\s\\;]+)")

    /**
     * 解析投影
     *
     * @param str       解析字符串
     * @param tableName 表名
     * @param fieldMap  此表字段
     * @return 投影的字段集
     */
    public static List<String> parseProjection(String str, String tableName, Map<String, Field> fieldMap) {
        List<String> projectionList = new LinkedList<>();
        //如果是 * 那么投影所有字段
        if ("*".equals(str)) {
            for (String key : fieldMap.keySet()) {
                projectionList.add(key);
            }
        }
        String[] projectionNames = str.trim().split(",");
        for (String projectionName : projectionNames) {
            projectionName = projectionName.trim();
            //如果包含table.id这样的型式，将table名进行匹配，如果不匹配则跳过
            if (projectionName.contains(".")) {
                String[] projection = projectionName.split("\\.");
                //如果不匹配就跳过
                if (!tableName.equals(projection[0])) {
                    continue;
                } else {
                    //匹配
                    projectionName = projection[1];
                }
            }
            Field field = fieldMap.get(projectionName);
            if (null != field) {
                projectionList.add(projectionName);
            }
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

    /**
     * 解析单表选择
     *
     * @param str
     * @return
     */
    public static List<Map<String, String>> parseWhere(String str) {

        List<Map<String, String>> filtList = new LinkedList<>();
        //修改了正则规则，需要末尾加;或空格才能匹配
        Matcher singleMatcher = SINGLE_REL_PATTERN.matcher(str + ";");
        while (singleMatcher.find()) {
            Map<String, String> filtMap = new LinkedHashMap<>();
            //singleMatcher.find();
            filtMap.put("fieldName", singleMatcher.group(1));
            filtMap.put("relationshipName", singleMatcher.group(2));
            filtMap.put("condition", singleMatcher.group(3));

            filtList.add(filtMap);
        }
        return filtList;
    }

    /**
     * 解析多表选择
     *
     * @param str       解析字符串
     * @param tableName 表名
     * @param fieldMap  表中字段
     * @return
     */
    public static List<Map<String, String>> parseWhere(String str, String tableName, Map<String, Field> fieldMap) {

        List<Map<String, String>> filtList = new LinkedList<>();
        if (null == str) {
            return filtList;
        }
        Matcher singleMatcher = SINGLE_REL_PATTERN.matcher(str);
        while (singleMatcher.find()) {
            String fieldName = singleMatcher.group(1);
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
                Map<String, String> filtMap = new LinkedHashMap<>();
                filtMap.put("fieldName", fieldName);
                filtMap.put("relationshipName", singleMatcher.group(2));
                filtMap.put("condition", singleMatcher.group(3));

                filtList.add(filtMap);
            }
        }
        return filtList;
    }


    /**
     * 解析多表连接条件
     *
     * @param str       where语句
     * @param fieldMaps 连接的所有表的字段集合
     * @return
     */
    public static List<Map<String, String>> parseWhere_join(String str, Map<String, Map<String, Field>> fieldMaps) {

        List<Map<String, String>> joinConditionList = new LinkedList<>();

        if (null == str) {
            return joinConditionList;
        }
        Matcher joinMatcher = JOIN_CONNECTION_REL_PATTERN.matcher(str);
        while (joinMatcher.find()) {
            //连接关系
            Map<String, String> connRel = new LinkedHashMap<>();
            String leftStr = joinMatcher.group(1);
            String relationshipName = joinMatcher.group(2);
            String rightStr = joinMatcher.group(3);

            String[] leftRel = leftStr.split("\\.");
            String[] rightRel = rightStr.split("\\.");

            if (null != fieldMaps.get(leftRel[0]) && null != fieldMaps.get(leftRel[0]).get(leftRel[1])
                    && null != fieldMaps.get(rightRel[0]) && null != fieldMaps.get(rightRel[0]).get(rightRel[1])) {

                connRel.put("tableName1", leftRel[0]);
                connRel.put("field1", leftRel[1]);
                connRel.put("relationshipName", relationshipName);
                connRel.put("tableName2", rightRel[0]);
                connRel.put("field2", leftRel[1]);

                joinConditionList.add(connRel);
            }
        }
        return joinConditionList;
    }

    /**
     * 解析创建表的字段语句
     *
     * @param fieldsStr
     * @return
     */
    public static Map<String, Field> parseCreateTable(String fieldsStr) {
        String[] lines = fieldsStr.trim().split(",");
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        for (String line : lines) {
            String[] property = line.trim().split(" ");

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
            //修改了正则规则，需要末尾加;或空格才能匹配
            Matcher relMatcher = SINGLE_REL_PATTERN.matcher(setStr + ";");
            relMatcher.find();
            //将组1做为key，组3作为value
            dataMap.put(relMatcher.group(1), relMatcher.group(3));
        }
        return dataMap;
    }
}
