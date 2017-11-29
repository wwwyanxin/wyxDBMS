import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public enum Relationship {
    LESS_THAN,
    MORE_THAN,
    EQUAL_TO;

    private final static Map<String,Class> TYPE_MAP =new HashMap<String,Class>();

    static {
        TYPE_MAP.put("int",Integer.class);
        TYPE_MAP.put("double", Double.class);
        TYPE_MAP.put("varchar", String.class);
    }

    public static Relationship parseRel(String relationshipName) {
        switch (relationshipName) {
            case "<":
                return Relationship.LESS_THAN;
            case "=":
                return Relationship.EQUAL_TO;
            case ">":
                return Relationship.MORE_THAN;
            default:
                try {
                    throw new Exception("条件错误");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
        }
    }

    private static boolean compareResult(Relationship relationship, int result) {
        switch (relationship) {
            case LESS_THAN:
                if (result < 0) {
                    return true;
                } else {
                    return false;
                }

            case EQUAL_TO:
                if (result == 0) {
                    return true;
                } else {
                    return false;
                }

            case MORE_THAN:
                if (result > 0) {
                    return true;
                } else {
                    return false;
                }

            default:
                try {
                    throw new Exception("条件限定不匹配");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
        }
    }

    /**
     * 根据提供的数据，数据字典，关系，比较数据 返回匹配信息
     *
     * @param srcData      提供的数据
     * @param field        数据的字典
     * @param relationship 大小关系
     * @param condition    比较的数据
     * @return 是否匹配
     */
    public static boolean matchCondition(Map<String, String> srcData, Field field, Relationship relationship, String condition) {
        if (null == srcData.get(field.getName()) || "[NULL]".equals(srcData.get(field.getName()))) {
            return false;
        }
        String srcDataValue = srcData.get(field.getName());
        Integer result = null;

        //获得类型
        Class typeClass=TYPE_MAP.get(field.getType());
        if (null == typeClass) {
            return false;
        }
        try {
            //将类型实例化，并且调用实例的compareTo方法比较
            Method method_compareTo = typeClass.getMethod("compareTo", typeClass);
            Constructor constructor1 = typeClass.getDeclaredConstructor(String.class);
            Constructor constructor2 = typeClass.getDeclaredConstructor(String.class);

            Object o1 = constructor1.newInstance(srcDataValue);
            Object o2 = constructor2.newInstance(condition);

            result= (Integer) method_compareTo.invoke(o1,o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* switch (field.getType()) {
            case "int":
                result = Integer.valueOf(srcDataValue).compareTo(Integer.valueOf(condition));
                break;
            case "double":
                result=Double.valueOf(srcDataValue).compareTo(Double.valueOf(condition));
                break;
            case "varchar":
                result=srcDataValue.compareTo(condition);
                break;
            default:
                try {
                    throw new Exception("条件限定不匹配");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }*/
        return compareResult(relationship, result);
    }

    /**
     * 根据提供的数据，数据字典，关系，比较数据 返回匹配信息
     * @param data1 数据1 tableName1.data1型式
     * @param data2 数据2 tableName2.data2型式
     * @param joinCondition 连接关系
     * @return
     */
    public static boolean matchJionCondition(Map<String, String> data1,Map<String, String> data2,JoinCondition joinCondition) {
        String tableName1 = joinCondition.getTableName1();
        String tableName2 = joinCondition.getTableName2();

        Field field1=joinCondition.getField1();
        Field field2=joinCondition.getField2();

        String fieldName1 = field1.getName();
        String fieldName2 = field2.getName();

        String dataValue1 = data1.get(tableName1+"."+fieldName1);
        String dataValue2 = data2.get(tableName2+"."+fieldName2);

        if (null == dataValue1 || "[NULL]".equals(dataValue1)
                || null == dataValue2 || "[NULL]".equals(dataValue2)) {
            return false;
        }

       /* String dataValue1=dataStr1.split("\\.")[1];
        String dataValue2=dataStr2.split("\\.")[1];*/

        Integer result = null;

       /* String dataValue1 = data1.get(field1.getName());
        String dataValue2 = data2.get(field2.getName());*/
        //获得类型
        Class typeClass1=TYPE_MAP.get(field1.getType());
        Class typeClass2=TYPE_MAP.get(field2.getType());
        if (null == typeClass1||null==typeClass2) {
            return false;
        }
        try {
            //将类型实例化，并且调用实例的compareTo方法比较
            Method method_compareTo = typeClass1.getMethod("compareTo", typeClass1);
            Constructor constructor1 = typeClass1.getDeclaredConstructor(String.class);
            Constructor constructor2 = typeClass2.getDeclaredConstructor(String.class);
            Object o1 = constructor1.newInstance(dataValue1);
            Object o2 = constructor2.newInstance(dataValue2);

            result= (Integer) method_compareTo.invoke(o1,o2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //解析关系
        Relationship relationship = Relationship.parseRel(joinCondition.getRelationshipName());
        //比较关系
        return compareResult(relationship, result);
    }
}
