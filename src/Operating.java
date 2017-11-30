import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operating {
    private static final Pattern PATTERN_INSERT = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
    private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
    private static final Pattern PATTERN_ALTER_TABLE_ADD = Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;");
    private static final Pattern PATTERN_DELETE = Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern PATTERN_UPDATE = Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("drop\\stable\\s(\\w+);");
    private static final Pattern PATTERN_SELECT = Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+\\s?;))?");
    private static final Pattern PATTERN_DELETE_INDEX = Pattern.compile("delete\\sindex\\s(\\w+)\\s?;");
    private static final Pattern PATTERN_GRANT_ADMIN = Pattern.compile("grant\\sadmin\\sto\\s([^;\\s]+)\\s?;");
    private static final Pattern PATTERN_REVOKE_ADMIN = Pattern.compile("revoke\\sadmin\\sfrom\\s([^;\\s]+)\\s?;");


    public void dbms() {
        //User user = new User("user1", "abc");
        User user = User.getUser("user1", "abc");
        if (null == user) {
            System.out.println("已退出dbms");
            return;
        } else {
            System.out.println(user.getName() + "登陆成功!");
        }
        //User.grant(user.getName(), User.READ_ONLY);
        //user.grant(User.READ_ONLY);

        //默认进入user1用户文件夹
        File userFolder = new File("dir", user.getName());

        //默认进入user1的默认数据库db1
        File dbFolder = new File(userFolder, "db1");


        Table.init(user.getName(), dbFolder.getName());


        Scanner sc = new Scanner(System.in);
        String cmd;
        while (!"exit".equals(cmd = sc.nextLine())) {
            Matcher matcherGrantAdmin = PATTERN_GRANT_ADMIN.matcher(cmd);
            Matcher matcherRevokeAdmin = PATTERN_REVOKE_ADMIN.matcher(cmd);
            Matcher matcherInsert = PATTERN_INSERT.matcher(cmd);
            Matcher matcherCreateTable = PATTERN_CREATE_TABLE.matcher(cmd);
            Matcher matcherAlterTable_add = PATTERN_ALTER_TABLE_ADD.matcher(cmd);
            Matcher matcherDelete = PATTERN_DELETE.matcher(cmd);
            Matcher matcherUpdate = PATTERN_UPDATE.matcher(cmd);
            Matcher matcherDropTable = PATTERN_DROP_TABLE.matcher(cmd);
            Matcher matcherSelect = PATTERN_SELECT.matcher(cmd);
            Matcher matcherDeleteIndex = PATTERN_DELETE_INDEX.matcher(cmd);

            while (matcherGrantAdmin.find()) {
                User grantUser = User.getUser(matcherGrantAdmin.group(1));
                if (null == grantUser) {
                    System.out.println("授权失败！");
                } else if (user.getName().equals(grantUser.getName())) {
                    //如果是当前操作的用户，就直接更改当前用户权限
                    user.grant(User.ADMIN);
                    System.out.println("用户:" + user.getName() + "授权成功！");
                } else {
                    grantUser.grant(User.ADMIN);
                    System.out.println("用户:" + grantUser.getName() + "授权成功!");
                }
            }

            while (matcherRevokeAdmin.find()) {
                User revokeUser = User.getUser(matcherRevokeAdmin.group(1));
                if (null == revokeUser) {
                    System.out.println("取消授权失败!");
                }
                if (user.getName().equals(revokeUser.getName())) {
                    //如果是当前操作的用户，就直接更改当前用户权限
                    user.grant(User.READ_ONLY);
                    System.out.println("用户:" + user.getName() + "已取消授权！");
                } else {
                    revokeUser.grant(User.READ_ONLY);
                    System.out.println("用户:" + revokeUser.getName() + "已取消授权！");
                }
            }

            while (matcherAlterTable_add.find()) {
                if (user.getLevel() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                alterTableAdd(matcherAlterTable_add);
            }

            while (matcherDropTable.find()) {
                if (user.getLevel() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                dropTable(matcherDropTable);
            }


            while (matcherCreateTable.find()) {
                if (user.getLevel() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                createTable(matcherCreateTable);
            }

            while (matcherDelete.find()) {
                if (user.getLevel() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                delete(matcherDelete);
            }

            while (matcherUpdate.find()) {
                if (user.getLevel() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                update(matcherUpdate);
            }

            while (matcherInsert.find()) {
                if (user.getLevel() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                insert(matcherInsert);
            }

            while (matcherSelect.find()) {
                select(matcherSelect);
            }

            while (matcherDeleteIndex.find()) {
                if (user.getLevel() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                deleteIndex(matcherDeleteIndex);
            }
        }

    }

    private void deleteIndex(Matcher matcherDeleteIndex) {
        String tableName = matcherDeleteIndex.group(1);
        Table table = Table.getTable(tableName);
        System.out.println(table.deleteIndex());
    }

    private void select(Matcher matcherSelect) {
        //将读到的所有数据放到tableDatasMap中
        Map<String, List<Map<String, String>>> tableDatasMap = new LinkedHashMap<>();

        //将投影放在Map<String,List<String>> projectionMap中
        Map<String, List<String>> projectionMap = new LinkedHashMap<>();


        List<String> tableNames = StringUtil.parseFrom(matcherSelect.group(2));

        String whereStr = matcherSelect.group(3);

        //将tableName和table.fieldMap放入
        Map<String, Map<String, Field>> fieldMaps = new HashMap();

        for (String tableName : tableNames) {
            Table table = Table.getTable(tableName);
            if (null == table) {
                System.out.println("未找到表：" + tableName);
                return;
            }
            Map<String, Field> fieldMap = table.getFieldMap();
            fieldMaps.put(tableName, fieldMap);

            //解析选择
            List<SingleFilter> singleFilters = new ArrayList<>();
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr, tableName, fieldMap);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));

                singleFilters.add(singleFilter);
            }

            //解析最终投影
            List<String> projections = StringUtil.parseProjection(matcherSelect.group(1), tableName, fieldMap);
            projectionMap.put(tableName, projections);


            //读取数据并进行选择操作
            List<Map<String, String>> srcDatas = table.read(singleFilters);
            List<Map<String, String>> datas = associatedTableName(tableName, srcDatas);

            tableDatasMap.put(tableName, datas);
        }


        //解析连接条件，并创建连接对象jion
        List<Map<String, String>> joinConditionMapList = StringUtil.parseWhere_join(whereStr, fieldMaps);
        List<JoinCondition> joinConditionList = new LinkedList<>();
        for (Map<String, String> joinMap : joinConditionMapList) {
            String tableName1 = joinMap.get("tableName1");
            String tableName2 = joinMap.get("tableName2");
            String fieldName1 = joinMap.get("field1");
            String fieldName2 = joinMap.get("field2");
            Field field1 = fieldMaps.get(tableName1).get(fieldName1);
            Field field2 = fieldMaps.get(tableName2).get(fieldName2);
            String relationshipName = joinMap.get("relationshipName");
            JoinCondition joinCondition = new JoinCondition(tableName1, tableName2, field1, field2, relationshipName);

            joinConditionList.add(joinCondition);

            //将连接条件的字段加入投影中
            projectionMap.get(tableName1).add(fieldName1);
            projectionMap.get(tableName2).add(fieldName2);
        }

        List<Map<String, String>> resultDatas = Join.joinData(tableDatasMap, joinConditionList, projectionMap);
        //System.out.println(resultDatas);

        //将需要显示的字段名按table.filed的型式存入dataNameList
        List<String> dataNameList = new LinkedList<>();
        for (Map.Entry<String, List<String>> projectionEntry : projectionMap.entrySet()) {
            String projectionKey = projectionEntry.getKey();
            List<String> projectionValues = projectionEntry.getValue();
            for (String projectionValue : projectionValues) {
                dataNameList.add(projectionKey + "." + projectionValue);
            }

        }

        //计算名字长度，用来对齐数据
        int[] lengh = new int[dataNameList.size()];
        Iterator<String> dataNames = dataNameList.iterator();
        for (int i = 0; i < dataNameList.size(); i++) {
            String dataName = dataNames.next();
            lengh[i] = dataName.length();
            System.out.printf("|%s", dataName);
        }

        System.out.println("|");
        for (int ls : lengh) {
            for (int l = 0; l <= ls; l++) {
                System.out.printf("-");
            }
        }
        System.out.println("|");

        for (Map<String, String> line : resultDatas) {
            Iterator<String> valueIter = line.values().iterator();
            for (int i = 0; i < lengh.length; i++) {
                String value = valueIter.next();
                System.out.printf("|%s", value);
                for (int j = 0; j < lengh[i] - value.length(); j++) {
                    System.out.printf(" ");
                }
            }
            System.out.println("|");
        }
    }

    private void insert(Matcher matcherInsert) {
        String tableName = matcherInsert.group(1);
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return;
        }
        Map dictMap = table.getFieldMap();
        Map<String, String> data = new HashMap<>();

        String[] fieldValues = matcherInsert.group(5).trim().split(",");
        //如果插入指定的字段
        if (null != matcherInsert.group(2)) {
            String[] fieldNames = matcherInsert.group(3).trim().split(",");
            //如果insert的名值数量不相等，错误
            if (fieldNames.length != fieldValues.length) {
                return;
            }
            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i].trim();
                String fieldValue = fieldValues[i].trim();
                //如果在数据字典中未发现这个字段，返回错误
                if (!dictMap.containsKey(fieldName)) {
                    return;
                }
                data.put(fieldName, fieldValue);
            }
        } else {//否则插入全部字段
            Set<String> fieldNames = dictMap.keySet();
            int i = 0;
            for (String fieldName : fieldNames) {
                String fieldValue = fieldValues[i].trim();

                data.put(fieldName, fieldValue);

                i++;
            }
        }
        table.insert(data);
    }

    private void update(Matcher matcherUpdate) {
        String tableName = matcherUpdate.group(1);
        String setStr = matcherUpdate.group(2);
        String whereStr = matcherUpdate.group(3);

        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return;
        }
        Map<String, Field> fieldMap = table.getFieldMap();
        Map<String, String> data = StringUtil.parseUpdateSet(setStr);


        List<SingleFilter> singleFilters = new ArrayList<>();
        if (null == whereStr) {
            table.update(data, singleFilters);
        } else {
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));

                singleFilters.add(singleFilter);
            }
            table.update(data, singleFilters);
        }
    }

    private void delete(Matcher matcherDelete) {
        String tableName = matcherDelete.group(1);
        String whereStr = matcherDelete.group(2);
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return;
        }

        Map<String, Field> fieldMap = table.getFieldMap();

        List<SingleFilter> singleFilters = new ArrayList<>();
        if (null == whereStr) {
            table.delete(singleFilters);
        } else {
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));

                singleFilters.add(singleFilter);
            }
            table.delete(singleFilters);
        }
    }

    private void createTable(Matcher matcherCreateTable) {
        String tableName = matcherCreateTable.group(1);
        String propertys = matcherCreateTable.group(2);
        Map<String, Field> fieldMap = StringUtil.parseCreateTable(propertys);
        System.out.println(Table.createTable(tableName, fieldMap));
    }

    private void dropTable(Matcher matcherDropTable) {
        String tableName = matcherDropTable.group(1);
        System.out.println(Table.dropTable(tableName));
    }

    private void alterTableAdd(Matcher matcherAlterTable_add) {
        String tableName = matcherAlterTable_add.group(1);
        String propertys = matcherAlterTable_add.group(2);
        Map<String, Field> fieldMap = StringUtil.parseCreateTable(propertys);
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return;
        }
        System.out.println(table.addDict(fieldMap));

    }

    /**
     * 将数据整理成tableName.fieldName dataValue的型式
     *
     * @param tableName 表名
     * @param srcDatas  原数据
     * @return 添加表名后的数据
     */
    private List<Map<String, String>> associatedTableName(String tableName, List<Map<String, String>> srcDatas) {
        List<Map<String, String>> destDatas = new ArrayList<>();
        for (Map<String, String> srcData : srcDatas) {
            Map<String, String> destData = new LinkedHashMap<>();
            for (Map.Entry<String, String> data : srcData.entrySet()) {
                destData.put(tableName + "." + data.getKey(), data.getValue());
            }
            destDatas.add(destData);
        }
        return destDatas;
    }

}

