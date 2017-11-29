import java.util.List;

public class JoinCondition {
    private String tableName1;
    private String tableName2;
    private Field field1;
    private Field field2;
    private String relationshipName;


    public JoinCondition(String tableName1, String tableName2, Field field1, Field field2, String relationshipName) {
        this.tableName1 = tableName1;
        this.tableName2 = tableName2;
        this.field1 = field1;
        this.field2 = field2;
        this.relationshipName = relationshipName;
    }


    /**
     * 从joinList中查找匹配的join，如果表匹配但是位置相反，那么将位置交换
     * @param tableName1 表1的名字
     * @param tableName2 表2的名字
     * @param joinConditionList join列表
     * @return 匹配的join,如果没找到返回null;
     */
    public static JoinCondition getConditionJoin(String tableName1, String tableName2, List<JoinCondition> joinConditionList) {
        for (JoinCondition joinCondition : joinConditionList) {
            if (joinCondition.tableName1.equals(tableName1) && joinCondition.tableName2.equals(tableName2)) {
                return joinCondition;
            } else if (joinCondition.tableName1.equals(tableName2) && joinCondition.tableName2.equals(tableName1)) {
                //如果匹配到连接条件，但是位置相反，那么将此join的tableName,field交换，将relationshipName符号取反
                String tmpStr= joinCondition.tableName1;
                joinCondition.tableName1= joinCondition.tableName2;
                joinCondition.tableName2=tmpStr;

                Field tmpField = joinCondition.field1;
                joinCondition.field1 = joinCondition.field2;
                joinCondition.field2=tmpField;

                if (">".equals(joinCondition.relationshipName)) {
                    joinCondition.relationshipName = "<";
                } else if ("<".equals(joinCondition.relationshipName)) {
                    joinCondition.relationshipName = ">";
                }

                return joinCondition;
            }
        }
        return null;
    }

    /**
     * 判断行有没有这个表的数据，如果包含tableName.field那么就有
     * @param joinConditionList
     * @param tableName
     * @return
     */
    public static boolean containsTable(List<JoinCondition> joinConditionList, String tableName) {
        for (JoinCondition joinCondition : joinConditionList) {
            if (tableName.contains(joinCondition.tableName1 + ".")||tableName.contains(joinCondition.tableName2+".")) {
                return true;
            }
        }
        return false;
    }

    public String getTableName1() {
        return tableName1;
    }

    public String getTableName2() {
        return tableName2;
    }

    public Field getField1() {
        return field1;
    }

    public Field getField2() {
        return field2;
    }

    public String getRelationshipName() {
        return relationshipName;
    }
}
