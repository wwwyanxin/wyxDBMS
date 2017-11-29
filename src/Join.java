import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Join {

    /**
     * 将数据进行连接并去掉对应的投影，如果匹配到条件做条件连接，没有匹配到就做笛卡尔积
     * @param tableDatasMap 所有数据
     * @param joinConditionList 连接条件列表
     * @param projectionMap 投影表
     * @return 最终数据
     */
    public static List<Map<String, String>> joinData(Map<String, List<Map<String, String>>> tableDatasMap, List<JoinCondition> joinConditionList, Map<String, List<String>> projectionMap) {
        List<Map<String, String>> resultProduct = new LinkedList<>();
        for (Map.Entry<String, List<Map<String, String>>> datasEntry : tableDatasMap.entrySet()) {

            String tableName = datasEntry.getKey();
            List<Map<String, String>>datas=datasEntry.getValue();
            //连接前进行一次投影
            datas=projection(datas, projectionMap);

            //在上次乘积中找存在连接条件的连接表
            String leftTableName = findTableName(resultProduct, joinConditionList);
            //如果没找到，做笛卡尔积
            if (null == leftTableName) {
                resultProduct = cartesianProduct(resultProduct, datas);
            } else {
                JoinCondition joinCondition = JoinCondition.getConditionJoin(leftTableName, tableName, joinConditionList);
                //如果只在上次乘积有连接条件，此表没有连接条件，做笛卡尔积
                if (null == joinCondition) {
                    resultProduct = cartesianProduct(resultProduct, datas);
                } else {
                    //做条件连接
                    resultProduct = joinProductByCondition(resultProduct, datas, joinCondition);
                    //将用于连接条件的投影去掉
                    List<String> projectionList1=projectionMap.get(joinCondition.getTableName1());
                    List<String> projectionList2=projectionMap.get(joinCondition.getTableName2());

                    String fieldName1 = joinCondition.getField1().getName();
                    String fieldName2 = joinCondition.getField2().getName();

                    //因为投影是从前往后加，所以去掉投影应该从后往前，以此保持顺序
                    projectionList1.remove(projectionList1.lastIndexOf(fieldName1));
                    projectionList2.remove(projectionList2.lastIndexOf(fieldName2));

                    //连接后进行一次投影
                    resultProduct=projection(resultProduct, projectionMap);
                }
            }
        }
        return resultProduct;
    }

    /**
     * 做笛卡尔积
     * @param srcProduct 上次的乘积
     * @param joinProduct 相乘表
     * @return 笛卡尔积结果
     */
    private static List<Map<String, String>> cartesianProduct(List<Map<String, String>> srcProduct, List<Map<String, String>> joinProduct) {
        List<Map<String, String>> result = new LinkedList<>();
        if (0 == srcProduct.size()) {
            return joinProduct;
        }
        for (Map<String, String> srcLine : srcProduct) {
            for (Map<String, String> joinLine : joinProduct) {
                Map<String, String> newLine = new LinkedHashMap<>();
                newLine.putAll(srcLine);
                newLine.putAll(joinLine);
//                srcLine.putAll(joinLine);
                result.add(newLine);
            }
        }
        return result;
    }

    /**
     * 做条件连接
     * @param srcProduct 上次乘积
     * @param joinProduct 连接的数据表
     * @param joinCondition 连接条件
     * @return 连接结果
     */
    private static List<Map<String, String>> joinProductByCondition(List<Map<String, String>> srcProduct, List<Map<String, String>> joinProduct,JoinCondition joinCondition) {
        List<Map<String, String>> result = new LinkedList<>();

        for (Map<String, String> srcLine : srcProduct) {
            for (Map<String, String> joinLine : joinProduct) {
                //如果与条件匹配,添加此行
                if(Relationship.matchJionCondition(srcLine, joinLine, joinCondition)) {
                    Map<String, String> newLine = new LinkedHashMap<>();
                    newLine.putAll(srcLine);
                    newLine.putAll(joinLine);
                    // srcLine.putAll(joinLine);
                    result.add(newLine);
                }
            }
        }
        return result;
    }

    private static List<Map<String, String>> projection(List<Map<String, String>> srcProduct,Map<String, List<String>> projectionMap) {
        List<Map<String, String>> result = new LinkedList<>();
        for (Map<String, String> productLine : srcProduct) {
            Map<String, String> resultLine = new LinkedHashMap<>();
            for (Map.Entry<String, List<String>> projectionEntry : projectionMap.entrySet()) {
                String tableName = projectionEntry.getKey();
                List<String> projections = projectionEntry.getValue();
                    for (String projection : projections) {
                        String projectionStr = tableName + "." + projection;
                        //如果投中，添加到结果集
                        String value=productLine.get(projectionStr);
                        if (null !=value) {
                            resultLine.put(projectionStr, value);
                        }
                    }

            }
            result.add(resultLine);
        }
        return result;
    }

    /**
     * 根据给出的乘积结果和连接条件，找出乘积结果中存在连接条件中的tableName
     * @param resultProduct 上次的乘积
     * @param joinConditionList 连接条件表
     * @return 找到的tableName或null
     */
    private static String findTableName(List<Map<String, String>> resultProduct, List<JoinCondition> joinConditionList) {
        if (null == resultProduct || null == joinConditionList || 0 == resultProduct.size() || 0 == joinConditionList.size()) {
            return null;
        }
        //获得第一行
        Map<String, String> resultFirstLine = resultProduct.get(0);

        for (String resultKey : resultFirstLine.keySet()) {
            //判断是否有连接条件
            if (JoinCondition.containsTable(joinConditionList,resultKey)) {
                //只返回tableName
                String tableName=resultKey.split("\\.")[0];
                return tableName;
            }
        }
        return null;
    }
}
