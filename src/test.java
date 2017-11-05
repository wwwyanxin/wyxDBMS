import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        User user = new User("user1", "abc");
        //默认进入user1用户文件夹
        File userFolder = new File("/Users/ouhikoshin/IdeaProjects/wyxDBMS/dir", user.getName());

        //默认进入user1的默认数据库db1
        File dbFolder = new File(userFolder, "db1");



        Table.init(user.getName(), dbFolder.getName());

/*      String[][] lines = {
                {"id", "int", "*"},
                {"name", "varchar"},
                {"height", "double"},
                {"sex","varchar"}
        };
        String[][] newLines = {
                {"age", "int"},
                {"weight", "double"},
               // {"height", "double"},
               // {"sex","varchar"}
        };
        //Map<String, Field> fieldMap = new LinkedHashMap();
        Map<String, Field> newFieldMap = new LinkedHashMap<>();
        for (String[] line : newLines) {
            Field field = new Field();

            field.setName(line[0]);
            field.setType(line[1]);
            //如果是主键字段后面加*
            if (3 == line.length && "*".equals(line[2])) {
                field.setPrimaryKey(true);
            } else {
                field.setPrimaryKey(false);
            }
            newFieldMap.put(line[0], field);
        }
        Table table1 = Table.getTable("table1");
        table1.addDict(newFieldMap);

       String result="";

        //result = Table.dropTable("table1");
        System.out.println(result);
        result=Table.createTable("table1", fieldMap);
        System.out.println(result);
        Table table1 = Table.getTable("table1");
        //result = table1.addDict(fieldMap);
        //System.out.println(result);
        //result = table1.deleteDict("b");
        //System.out.println(result);

        //String[] testStr = "aa bb &null; &null;".split(" ");
        //System.out.println(Arrays.toString(testStr));

        String[][] insertStrs = {
                {"1", "张三", "1.7", "men"},
                {"2", "李四", "women"},
                {"3", "王二", "men"},
                {"4", "大黑", "1"},
                {}
        };
        Map<String, Field> dictMap = table1.getFieldMap();

        List<Map<String, String> > strs = new ArrayList();
        for (String[] insertStr : insertStrs) {
            Iterator<String> fieldIterator = dictMap.keySet().iterator();
            Map<String, String> data = new LinkedHashMap<>();
            for (String fieldStr : insertStr) {
                String fieldKey=fieldIterator.next();
                data.put(fieldKey, fieldStr);
            }
            strs.add(data);
        }
        for (Map<String, String> insertMap : strs) {
            result=table1.insert(insertMap);
            System.out.println(result);
        }

*/


        Scanner sc = new Scanner(System.in);
        String cmd;
        while (!"exit".equals(cmd = sc.nextLine())) {
            Pattern patternInsert=Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
            Matcher matcherInsert = patternInsert.matcher(cmd);

            Pattern patternCreateTable=Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
            Matcher matcherCreateTable = patternCreateTable.matcher(cmd);

            Pattern patternAlterTable_add=Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;");
            Matcher matcherAlterTable_add = patternAlterTable_add.matcher(cmd);

            //Pattern patternDelete=Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+)\\s?([<=>])\\s?([^\\s\\;]+))?((?:\\s(?:and|or)\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*)?;?");
            Pattern patternDelete=Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
            Matcher matcherDelete = patternDelete.matcher(cmd);

            Pattern patternDropTable=Pattern.compile("drop\\stable\\s(\\w+);");
            Matcher matcherDropTable = patternDropTable.matcher(cmd);

            while (matcherAlterTable_add.find()) {
                String tableName = matcherAlterTable_add.group(1);
                String propertys = matcherAlterTable_add.group(2);
                Map<String,Field> fieldMap=StringUtil.parseCreateTable(propertys);
                Table table = Table.getTable(tableName);
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
            while (matcherInsert.find()) {
                String tableName=matcherInsert.group(1);
                Table table = Table.getTable(tableName);
                Map dictMap = table.getFieldMap();
                Map<String, String> data = new HashMap<>();

                String[] fieldValues=matcherInsert.group(5).split(",");
                //如果插入指定的字段
                if (null != matcherInsert.group(2)) {
                    String[] fieldNames = matcherInsert.group(3).split(",");
                    //如果insert的名值数量不相等，错误
                    if (fieldNames.length != fieldValues.length) {
                        return;
                    }
                    for (int i = 0; i < fieldNames.length; i++) {
                        String fieldName = fieldNames[i];
                        String fieldValue = fieldValues[i];
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
                        String fieldValue = fieldValues[i];

                        data.put(fieldName, fieldValue);

                        i++;
                    }
                }
                table.insert(data);
            }
        }

        /*Table table1 = Table.getTable("table1");
        table1.delete("id","<","2");*/
    }

}

