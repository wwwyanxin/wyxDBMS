import java.io.*;
import java.util.*;

public class Table {
    private String name;
    private File dictFile;//数据字典
    private File dataFile;//数据
    private Map<String, Field> fieldMap;//字段映射集
    private static String userName;//用户姓名，切换或修改用户时修改
    private static String dbName;//数据库dataBase名，切换时修改

    /**
     * 只能静态创建，所以构造函数私有
     */
    private Table(String name) {
        this.name = name;
        this.fieldMap = new LinkedHashMap();
        this.dictFile = new File("/Users/ouhikoshin/IdeaProjects/wyxDBMS/dir" +"/"+ userName +"/"+ dbName , name + ".dict");
        this.dataFile = new File("/Users/ouhikoshin/IdeaProjects/wyxDBMS/dir" + userName +"/"+ dbName , name + ".data");
    }


    /**
     * 初始化表信息，包括用户和数据库
     *
     * @param userName 用户名
     * @param dbName   数据库名
     */
    public static void init(String userName, String dbName) {
        Table.userName = userName;
        Table.dbName = dbName;
    }

    /**
     * 创建一个新的表文件
     *
     * @param name 表名
     * @return 如果表存在返回失败的信息，否则返回success
     */
    public static String createTable(String name, Map<String, Field> fields) {
        if (exist(name)) {
            return "创建表失败，因为已经存在表:" + name;
        }
        Table table = new Table(name);


        table.dataFile.getParentFile().mkdirs();//创建真实目录

        table.addDict(fields);
        return "success";
    }

    /**
     * 在字典文件中写入创建的字段信息,然后将新增的字段map追加到this.fieldMap
     *
     * @param fields 字段列表，其中map的name为列名，type为数据类型，primaryKey为是否作为主键
     * @return
     */
    public String addDict(Map<String, Field> fields) {
        Set<String> keys=fields.keySet();
        for (String key : keys) {
            if (fieldMap.containsKey(key)) {
                return "错误：存在重复添加的字段:"+key;
            }
        }
        try (
                FileWriter fw = new FileWriter(dictFile, true);
                PrintWriter pw = new PrintWriter(fw)
        ) {
            //for (Map.Entry<String, Field> fieldEntry : fields.entrySet()) {
            for (Field field : fields.values()) {
                String name = field.getName();
                String type = field.getType();
                //如果是主键字段后面加*
                if (field.isPrimaryKey()) {
                    pw.println(name + " " + type + " " + "*");
                } else {
                    pw.println(name + " " + type);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fieldMap.putAll(fields);

        return "success";
    }

    /**
     * 根据表名获取表
     *
     * @param name 表名
     * @return 如果不存在此表返回null, 否则返回对应Table对象
     */
    public static Table getTable(String name) {
        if (!exist(name)) {
            return null;
        }
        Table table = new Table(name);
        try (
                FileReader fr = new FileReader(table.dictFile);
                BufferedReader br = new BufferedReader(fr)
        ) {

            String line=null;
            //读到末尾是NULL
            while (null != (line= br.readLine())) {
                String[] fieldValues=line.split(" ");//用空格产拆分字段
                Field field=new Field();
                field.setName(fieldValues[0]);
                field.setType(fieldValues[1]);
                //如果长度为3说明此字段是主键
                if (3 == fieldValues.length && "*".equals(fieldValues[2])) {
                    field.setPrimaryKey(true);
                } else {
                    field.setPrimaryKey(false);
                }
                //将字段的名字作为key
                table.fieldMap.put(fieldValues[0], field);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 判断表是否存在
     *
     * @param name 表名
     * @return 存在与否
     */
    public static boolean exist(String name) {
        File file = new File("/Users/ouhikoshin/IdeaProjects/wyxDBMS/dir" +"/"+ userName +"/"+ dbName , name + ".dict");
        return file.exists();
    }


    //public String alterDict()
}
