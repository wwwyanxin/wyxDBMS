import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test2 {
    public static void main(String[] args) {

        User user = new User("user1", "abc");
        //默认进入user1用户文件夹
        File userFolder = new File("/Users/ouhikoshin/IdeaProjects/wyxDBMS/dir", user.getName());

        //默认进入user1的默认数据库db1
        File dbFolder = new File(userFolder, "db1");



        Table.init(user.getName(), dbFolder.getName());

        Scanner sc = new Scanner(System.in);
        String cmd;
        while (!"exit".equals(cmd = sc.nextLine())) {
            /*Pattern patternInsert = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
            Matcher matcherInsert = patternInsert.matcher(cmd);

            Pattern patternDelete = Pattern.compile("delete\\sfrom\\s(\\w+)(\\swhere\\s(\\w+)\\s?([<=>])\\s?([^\\s\\;]+))?(\\s(and|or)\\s(\\w+)\\s?([<=>])\\s?([^\\s\\;]+))*;?");
            Matcher matcherDelete = patternDelete.matcher(cmd);
*/
            Pattern pattern = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
            Matcher matcherInsert = pattern.matcher(cmd);

            while (matcherInsert.find()) {
                String tableName = matcherInsert.group(1);
              //  Table table = Table.getTable(tableName);
//                Map dictMap = table.getFieldMap();
               // Map<String, String> data = new HashMap<>();

                System.out.println(tableName);
            }
        }
    }
}
