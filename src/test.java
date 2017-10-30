import java.io.File;
import java.util.*;

public class test {
    public static void main(String[] args) {
        User user = new User("user1", "abc");
        //默认进入user1用户文件夹
        File userFolder = new File("/Users/ouhikoshin/IdeaProjects/wyxDBMS/dir", user.getName());
        //默认进入user1的默认数据库db1
        File dbFolder = new File(userFolder, "db1");

 /*       Scanner sc = new Scanner(System.in);
        String cmd;
        while (!"exit".equals(cmd = sc.nextLine())) {

        }*/

        Table.init(user.getName(), dbFolder.getName());

        String[][] lines = {
                {"id", "int", "*"},
                {"name", "varchar"},
                {"height", "double"},
                {"sex","varchar"}
        };
        Map<String, Field> fieldMap = new LinkedHashMap();
        for (String[] line : lines) {
            Field field = new Field();

            field.setName(line[0]);
            field.setType(line[1]);
            //如果是主键字段后面加*
            if (3 == line.length && "*".equals(line[2])) {
                field.setPrimaryKey(true);
            } else {
                field.setPrimaryKey(false);
            }
            fieldMap.put(line[0], field);
        }

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
    }
}

