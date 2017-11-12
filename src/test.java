import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    private static final Pattern patternInsert=Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
    private static final Pattern patternCreateTable=Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
    private static final Pattern patternAlterTable_add=Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;");
    private static final Pattern patternDelete=Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern patternUpdate=Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern patternDropTable=Pattern.compile("drop\\stable\\s(\\w+);");
    private static final Pattern patternSelect=Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+))?\\s?;");

    public static void main(String[] args) {
        User user = new User("user1", "abc");
        //默认进入user1用户文件夹
        File userFolder = new File("dir", user.getName());

        //默认进入user1的默认数据库db1
        File dbFolder = new File(userFolder, "db1");



        Table.init(user.getName(), dbFolder.getName());





        Scanner sc = new Scanner(System.in);
        String cmd;
        while (!"exit".equals(cmd = sc.nextLine())) {
            Matcher matcherInsert = patternInsert.matcher(cmd);
            Matcher matcherCreateTable = patternCreateTable.matcher(cmd);
            Matcher matcherAlterTable_add = patternAlterTable_add.matcher(cmd);
            Matcher matcherDelete = patternDelete.matcher(cmd);
            Matcher matcherUpdate = patternUpdate.matcher(cmd);
            Matcher matcherDropTable = patternDropTable.matcher(cmd);
            Matcher matcherSelect = patternSelect.matcher(cmd);


            while (matcherAlterTable_add.find()) {
                String tableName = matcherAlterTable_add.group(1);
                String propertys = matcherAlterTable_add.group(2);
                Map<String,Field> fieldMap=StringUtil.parseCreateTable(propertys);
                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表："+tableName);
                    break;
                }
                System.out.println(table.addDict(fieldMap));
            }

            while (matcherDropTable.find()) {
                String tableName = matcherDropTable.group(1);
                System.out.println(Table.dropTable(tableName));
            }


            while (matcherCreateTable.find()) {
                String tableName = matcherCreateTable.group(1);
                String propertys = matcherCreateTable.group(2);
                Map<String,Field> fieldMap=StringUtil.parseCreateTable(propertys);
                System.out.println(Table.createTable(tableName,fieldMap));
            }

            while (matcherDelete.find()) {
                String tableName=matcherDelete.group(1);
                String whereStr = matcherDelete.group(2);
                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表："+tableName);
                    break;
                }

                Map<String,Field> fieldMap = table.getFieldMap();

                List<SingleFilter> singleFilters = new ArrayList<>();
                if (null == whereStr) {
                    table.delete(singleFilters);
                }else {
                    List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
                    for (Map<String, String> filtMap : filtList) {
                        SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                                , filtMap.get("relationshipName"), filtMap.get("condition"));

                        singleFilters.add(singleFilter);
                    }
                    table.delete(singleFilters);
                }
            }

            while (matcherUpdate.find()) {
                String tableName = matcherUpdate.group(1);
                String setStr = matcherUpdate.group(2);
                String whereStr = matcherUpdate.group(3);

                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表："+tableName);
                    break;
                }
                Map<String,Field> fieldMap = table.getFieldMap();
                Map<String, String> data = StringUtil.parseUpdateSet(setStr);


                List<SingleFilter> singleFilters = new ArrayList<>();
                if (null == whereStr) {
                    table.update(data,singleFilters);
                }else{
                    List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
                    for (Map<String, String> filtMap : filtList) {
                        SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                                , filtMap.get("relationshipName"), filtMap.get("condition"));

                        singleFilters.add(singleFilter);
                    }
                    table.update(data,singleFilters);
                }
            }

            while (matcherInsert.find()) {
                String tableName=matcherInsert.group(1);
                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表："+tableName);
                    break;
                }
                Map dictMap = table.getFieldMap();
                Map<String, String> data = new HashMap<>();

                String[] fieldValues=matcherInsert.group(5).trim().split(",");
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
                    Set<String> fieldNames=dictMap.keySet();
                    int i=0;
                    for (String fieldName : fieldNames) {
                        String fieldValue = fieldValues[i].trim();

                        data.put(fieldName, fieldValue);

                        i++;
                    }
                }
                table.insert(data);
            }

            while (matcherSelect.find()) {
                if ("*".equals(matcherSelect.group(1))&&null==matcherSelect.group(3)) {
                    //暂定一个表并且没有条件，之后重写
                    String tableName = matcherSelect.group(2);
                    Table table = Table.getTable(tableName);
                    if (null == table) {
                        System.out.println("未找到表："+tableName);
                        break;
                    }
                    List<Map<String, String>> datas = table.read();
                    Map<String, Field> fieldMap = table.getFieldMap();
                    for (String fieldName : fieldMap.keySet()) {
                        System.out.printf("\t|\t%s", fieldName);
                    }
                    System.out.println();
                    for (Map<String, String> data : datas) {
                        for (String fieldValue : data.values()) {
                            System.out.printf("\t|\t%s",fieldValue);
                        }
                        System.out.println();
                    }
                }
            }
        }

        /*Table table1 = Table.getTable("table1");
        table1.delete("id","<","2");*/
    }

}

