import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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
                {"a", "varchar"},
                {"b", "varchar"}
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

        String result=Table.createTable("table1", fieldMap);
        System.out.println(result);
        Table table1 = Table.getTable("table1");
        result = table1.addDict(fieldMap);
        System.out.println(result);
    }
}

