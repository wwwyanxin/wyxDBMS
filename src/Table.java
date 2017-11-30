import java.io.*;
import java.util.*;

public class Table {
    private String name;//表名
    private File folder;//表所在的文件夹
    private File dictFile;//数据字典
    private LinkedHashSet<File> dataFileSet;
    private File indexFile;//索引文件
    private Map<String, Field> fieldMap;//字段映射集
    //存放对所有字段的索引树
    private Map<String, IndexTree> indexMap;
    private static String userName;//用户姓名，切换或修改用户时修改
    private static String dbName;//数据库dataBase名，切换时修改

    //控制文件行数
    private static long lineNumConfine = 10;

    /**
     * 只能静态创建，所以构造函数私有
     */
    private Table(String name) {
        this.name = name;
        this.fieldMap = new LinkedHashMap();
        this.folder = new File("dir" + "/" + userName + "/" + dbName + "/" + name);
        this.dictFile = new File(folder, name + ".dict");
        this.dataFileSet = new LinkedHashSet<>();
        //this.dataFile = new File(folder + "/data", 1 + ".data");
        this.indexFile = new File(folder, this.name + ".index");
        this.indexMap = new HashMap<>();
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
        if (existTable(name)) {
            return "创建表失败，因为已经存在表:" + name;
        }
        Table table = new Table(name);


        table.dictFile.getParentFile().mkdirs();//创建真实目录

        table.addDict(fields);
        return "success";
    }


    /**
     * 根据表名获取表
     *
     * @param name 表名
     * @return 如果不存在此表返回null, 否则返回对应Table对象
     */
    public static Table getTable(String name) {
        if (!existTable(name)) {
            return null;
        }
        Table table = new Table(name);
        try (
                FileReader fr = new FileReader(table.dictFile);
                BufferedReader br = new BufferedReader(fr)
        ) {

            String line = null;
            //读到末尾是NULL
            while (null != (line = br.readLine())) {
                String[] fieldValues = line.split(" ");//用空格产拆分字段
                Field field = new Field();
                field.setName(fieldValues[0]);
                field.setType(fieldValues[1]);
                //如果长度为3说明此字段是主键
                if ("*".equals(fieldValues[2])) {
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
        File[] dataFiles = new File(table.folder, "data").listFiles();
        if (null != dataFiles && 0 != dataFiles.length) {
            for (int i = 1; i <= dataFiles.length; i++) {
                File dataFile = new File(table.folder + "/data", i + ".data");
                table.dataFileSet.add(dataFile);
            }
        }

        if (table.indexFile.exists()) {
            table.readIndex();
        } /*else {
            table.buildIndex();
            //table.writeIndex();
        }*/

        return table;
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }


    private static void deleteFolder(File file) {
        if (file.isFile()) {//判断是否是文件
            file.delete();//删除文件
        } else if (file.isDirectory()) {//否则如果它是一个目录
            File[] files = file.listFiles();//声明目录下所有的文件 files[];
            for (int i = 0; i < files.length; i++) {//遍历目录下所有的文件
                deleteFolder(files[i]);//把每个文件用这个方法进行迭代
            }
            file.delete();//删除文件夹
        }
    }


    public static String dropTable(String name) {
        if (!existTable(name)) {
            return "错误：不存在表:" + name;
        }
        File folder = new File("dir" + "/" + userName + "/" + dbName + "/" + name);
        deleteFolder(folder);
        return "success";

    }

    /**
     * 判断表是否存在
     *
     * @param name 表名
     * @return 存在与否
     */
    private static boolean existTable(String name) {
        File folder = new File("dir" + "/" + userName + "/" + dbName + "/" + name);
        return folder.exists();
    }

    /**
     * 在字典文件中写入创建的字段信息,然后将新增的字段map追加到this.fieldMap
     *
     * @param fields 字段列表，其中map的name为列名，type为数据类型，primaryKey为是否作为主键
     * @return
     */
    public String addDict(Map<String, Field> fields) {
        Set<String> keys = fields.keySet();
        for (String key : keys) {
            if (fieldMap.containsKey(key)) {
                return "错误：存在重复添加的字段:" + key;
            }
        }
        writeDict(fields, true);

        fieldMap.putAll(fields);

        return "success";
    }

    /**
     * 在数据文件没有此字段的数据的前提下，可以删除此字段
     *
     * @param fieldName 字段名
     * @return
     */
    public String deleteDict(String fieldName) {
        if (!fieldMap.containsKey(fieldName)) {
            return "错误：不存字段：" + fieldName;
        }
        fieldMap.remove(fieldName);
       /* //如果删除了最后一条字段，则删除整个表文件
        if (0 == fieldMap.size()) {
            dropTable(this.name);
        } else {
        }*/
        writeDict(fieldMap, false);

        return "success";
    }

    /**
     * 提供一组字段写入文件
     *
     * @param fields 字段映射集
     * @param append 是否在文件结尾追加
     */
    private void writeDict(Map<String, Field> fields, boolean append) {
        try (
                FileWriter fw = new FileWriter(dictFile, append);
                PrintWriter pw = new PrintWriter(fw)
        ) {
            //for (Map.Entry<String, Field> fieldEntry : fields.entrySet()) {
            for (Field field : fields.values()) {
                String name = field.getName();
                String type = field.getType();
                //如果是主键字段后面加*
                if (field.isPrimaryKey()) {
                    pw.println(name + " " + type + " " + "*");
                } else {//非主键^
                    pw.println(name + " " + type + " " + "^");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对空位填充fillStr,填充后的字段按照数据字段顺序排序
     *
     * @param fillStr 要填充的字符串
     * @param data    原始数据
     * @return 填充后的数据
     */
    private Map<String, String> fillData(Map<String, String> data, String fillStr) {
        //fillData是真正写入文件的集合，空位补fillStr;
        Map<String, String> fillData = new LinkedHashMap<>();
        //遍历数据字典
        for (Map.Entry<String, Field> fieldEntry : fieldMap.entrySet()) {
            String fieldKey = fieldEntry.getKey();
            if (null == data.get(fieldKey)) {
                fillData.put(fieldKey, fillStr);
            } else {
                fillData.put(fieldKey, data.get(fieldKey));
            }
        }
        return fillData;
    }

    /**
     * 利用正则表达式判断data类型是否与数据字典相符
     *
     * @param data
     * @return
     */
    private boolean checkType(Map<String, String> data) {
        //如果长度不一致，返回false
        if (data.size() != fieldMap.size()) {
            return false;
        }

        //遍历data.value和field.type,逐个对比类型
        //Iterator<String> dataIter = data.values().iterator();
        Iterator<Field> fieldIter = fieldMap.values().iterator();

        while (fieldIter.hasNext()) {
            //String dataValue = dataIter.next();
            Field field = fieldIter.next();
            String dataValue = data.get(field.getName());
            //如果是[NULL]则跳过类型检查
            if ("[NULL]".equals(dataValue)) {
                continue;
            }

            switch (field.getType()) {
                case "int":
                    if (!dataValue.matches("^(-|\\+)?\\d+$")) {
                        return false;
                    }
                    break;
                case "double":
                    if (!dataValue.matches("^(-|\\+)?\\d*\\.?\\d+$")) {
                        return false;
                    }
                    break;
                case "varchar":
                    break;
                default:
                    return false;
            }
        }
        return true;
    }


    /**
     * 插入数据到最后一个数据文件，如果数据行数超过限定值，写入下一个文件中
     *
     * @param srcData
     * @return
     */
    public String insert(Map<String, String> srcData) {
        File lastFile = null;
        int lineNum = 0;
        int fileNum = 0;
        for (File file : dataFileSet) {
            fileNum++;
            lastFile = file;
            lineNum = fileLineNum(lastFile);
        }
        //如果没有一个文件，新建1.data
        if (null == lastFile || 0 == fileNum) {
            lastFile = new File(folder + "/data", 1 + ".data");
            dataFileSet.add(lastFile);
            lineNum = 0;
        } else if (lineNumConfine <= fileLineNum(lastFile)) {
            //如果最后一个文件大于行数限制，新建数据文件
            lastFile = new File(folder + "/data", fileNum + 1 + ".data");
            dataFileSet.add(lastFile);
            lineNum = 0;
        }
        //添加索引
        for (Map.Entry<String, Field> fieldEntry : fieldMap.entrySet()) {
            String dataName = fieldEntry.getKey();
            String dataValue = srcData.get(dataName);
            //如果发现此数据为空，不添加到索引树中
            if (null == dataValue || "[NULL]".equals(dataValue)) {
                continue;
            }
            String dataType = fieldEntry.getValue().getType();

            IndexTree indexTree = indexMap.get(dataName);
            if (null == indexTree) {
                indexMap.put(dataName, new IndexTree());
                indexTree = indexMap.get(dataName);
            }
            IndexKey indexKey = new IndexKey(dataValue, dataType);
            indexTree.putIndex(indexKey, lastFile.getAbsolutePath(), lineNum);
        }
        writeIndex();
        return insertData(lastFile, srcData);
    }

    /**
     * 在插入时，对语法进行检查，并对空位填充[NULL]
     *
     * @param srcData 未处理的原始数据
     * @return
     */
    private String insertData(File file, Map<String, String> srcData) {
        if (srcData.size() > fieldMap.size() || 0 == srcData.size()) {
            return "错误：插入数据失败，请检查语法";
        }

        //遍历数据字典,查看主键是否为空
        for (Map.Entry<String, Field> fieldEntry : fieldMap.entrySet()) {
            String fieldKey = fieldEntry.getKey();
            Field field = fieldEntry.getValue();
            //如果此字段是主键,不可以为null
            if (field.isPrimaryKey()) {
                if (null == srcData.get(fieldKey) || "[NULL]".equals(srcData.get(fieldKey))) {
                    return "错误：字段:" + fieldKey + "是主键，不能为空";
                }
            }
        }
        Map<String, String> insertData = fillData(srcData, "[NULL]");
        if (!checkType(insertData)) {
            return "错误：检查插入的类型";
        }

        file.getParentFile().mkdirs();
        try (
                FileWriter fw = new FileWriter(file, true);
                PrintWriter pw = new PrintWriter(fw)
        ) {
            StringBuilder line = new StringBuilder();
            for (String value : insertData.values()) {
                line.append(value).append(" ");
            }
            pw.println(line.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return "写入异常";
        }

        buildIndex();
        writeIndex();
        return "success";
    }


    private int fileLineNum(File file) {
        int num = 0;
        try (
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr)
        ) {
            while (null != br.readLine()) {
                num++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 读取指定文件的所有数据
     *
     * @param dataFile 数据文件
     * @return 数据列表
     */
    private List<Map<String, String>> readDatas(File dataFile) {
        List<Map<String, String>> dataMapList = new ArrayList<>();

        try (
                FileReader fr = new FileReader(dataFile);
                BufferedReader br = new BufferedReader(fr)
        ) {

            String line = null;
            while (null != (line = br.readLine())) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                String[] datas = line.split(" ");
                Iterator<String> fieldNames = getFieldMap().keySet().iterator();
                for (String data : datas) {
                    String dataName = fieldNames.next();
                    dataMap.put(dataName, data);
                }
                dataMapList.add(dataMap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataMapList;
    }

    /**
     * 读取指定文件的所有数据加行号
     *
     * @param dataFile 数据文件
     * @return 数据列表
     */
    private List<Map<String, String>> readDatasAndLineNum(File dataFile) {
        List<Map<String, String>> dataMapList = new ArrayList<>();

        try (
                FileReader fr = new FileReader(dataFile);
                BufferedReader br = new BufferedReader(fr)
        ) {

            String line = null;
            long lineNum = 1;
            while (null != (line = br.readLine())) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                String[] datas = line.split(" ");
                Iterator<String> fieldNames = getFieldMap().keySet().iterator();
                for (String data : datas) {
                    String dataName = fieldNames.next();
                    dataMap.put(dataName, data);
                }
                dataMap.put("[lineNum]", String.valueOf(lineNum));
                dataMapList.add(dataMap);
                lineNum++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataMapList;
    }


    /**
     * 读取对应索引文件的数据
     *
     * @return
     */
    private List<Map<String, String>> read() {
        //索引文件***
        List<Map<String, String>> datas = new ArrayList<>();
        //Set<File> fileSet = findFileSet(singleFilters);
        for (File file : dataFileSet) {
            datas.addAll(readDatas(file));
            //datas.addAll(readDatas(file));
        }
        return datas;
    }



    /**
     * 读取对应索引文件的数据并过滤
     *@param singleFilters 过滤器
     * @return
     */
    public List<Map<String, String>> read(List<SingleFilter> singleFilters) {
        //索引文件***
        List<Map<String, String>> datas = new ArrayList<>();
        if (null != singleFilters && 0 != singleFilters.size()) {
            Set<File> fileSet = findFileSet(singleFilters);
            for (File file : fileSet) {
                datas.addAll(readFilter(file, singleFilters));
                //datas.addAll(readDatas(file));
            }
        } else {
            datas = read();
        }

        return datas;
    }



    /**
     * 读取指定文件的数据并用where规则过滤
     *
     * @param file
     * @param singleFilters
     * @return
     */
    private List<Map<String, String>> readFilter(File file, List<SingleFilter> singleFilters) {
        //读取数据文件
        List<Map<String, String>> srcDatas = readDatas(file);
        List<Map<String, String>> filtDatas = new ArrayList<>(srcDatas);
        //循环过滤
        for (SingleFilter singleFilter : singleFilters) {
            filtDatas = singleFilter.singleFiltData(filtDatas);
        }
        //将过滤的数据返回
        return filtDatas;
    }

    /**
     * 将数据写入对应的文件
     *
     * @param dataFile
     * @param datas
     */
    private void writeDatas(File dataFile, List<Map<String, String>> datas) {
        if (dataFile.exists()) {
            dataFile.delete();
        }
        for (Map<String, String> data : datas) {
            insertData(dataFile, data);
        }
    }


    /**
     * 根据给定的过滤器组，查找索引，将指定的文件数据删除
     *
     * @param singleFilters 过滤器组
     */
    public void delete(List<SingleFilter> singleFilters) {
        //此处查找索引
        //deleteData(this.dataFile, singleFilters);
        Set<File> fileSet = findFileSet(singleFilters);
        for (File file : fileSet) {
            deleteData(file, singleFilters);
        }
        buildIndex();
        writeIndex();
    }

    /**
     * 读取给定文件，读取数据并使用过滤器组过滤，将过滤后的写入文件
     *
     * @param file          数据文件
     * @param singleFilters 过滤器组
     */
    private void deleteData(File file, List<SingleFilter> singleFilters) {
        //读取数据文件
        List<Map<String, String>> srcDatas = readDatas(file);
        List<Map<String, String>> filtDatas = new ArrayList<>(srcDatas);
        //Collections.copy(filtDatas, srcDatas);
        for (SingleFilter singleFilter : singleFilters) {
            filtDatas = singleFilter.singleFiltData(filtDatas);
        }
        srcDatas.removeAll(filtDatas);
        writeDatas(file, srcDatas);
    }

    /**
     * 根据给定的过滤器组，查找索引，将指定的文件数据更新
     *
     * @param updateDatas   更新的数据
     * @param singleFilters 过滤器组
     */
    public void update(Map<String, String> updateDatas, List<SingleFilter> singleFilters) {
        Set<File> fileSet = findFileSet(singleFilters);
        for (File file : fileSet) {
            updateData(file, updateDatas, singleFilters);
        }
        buildIndex();
        writeIndex();
    }

    /**
     * 读取给定文件，读取数据并使用过滤器组过滤，将过滤出的数据更新并写入文件
     *
     * @param file          数据文件
     * @param updateDatas   更新的数据
     * @param singleFilters 过滤器组
     */
    private void updateData(File file, Map<String, String> updateDatas, List<SingleFilter> singleFilters) {
        //读取数据文件
        List<Map<String, String>> srcDatas = readDatas(file);
        List<Map<String, String>> filtDatas = new ArrayList<>(srcDatas);
        //Collections.copy(filtDatas, srcDatas);
        //循环过滤
        for (SingleFilter singleFilter : singleFilters) {
            filtDatas = singleFilter.singleFiltData(filtDatas);
        }
        //将过滤的数据遍历，将数据的值更新为updateDatas对应的数据
        for (Map<String, String> filtData : filtDatas) {
            for (Map.Entry<String, String> setData : updateDatas.entrySet()) {
                filtData.put(setData.getKey(), setData.getValue());
            }
        }
//        srcDatas.removeAll(filtDatas);
        writeDatas(file, srcDatas);
    }

    /**
     * 将索引对象从索引文件读取
     */
    private void readIndex() {
        if (!indexFile.exists()) {
            return;
        }
        try (
                FileInputStream fis = new FileInputStream(indexFile);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            indexMap = (Map<String, IndexTree>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将索引对象写入索引文件
     */
    private void writeIndex() {
        try (
                FileOutputStream fos = new FileOutputStream(indexFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(indexMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除建立的索引
     */
    public String deleteIndex() {
        if (indexFile.exists()) {
            indexFile.delete();
            return "success";
        }
        return "失败";
    }

    /**
     * 为每个属性建立索引树，如果此属性值为[NULL]索引树将排除此条字段
     */
    private void buildIndex() {
        indexMap = new HashMap<>();
        File[] dataFiles = new File(folder, "data").listFiles();
        //每个文件
        for (File dataFile : dataFiles) {
            List<Map<String, String>> datas = readDatasAndLineNum(dataFile);
            //每个元组
            for (Map<String, String> data : datas) {
                //每个数据字段
                for (Map.Entry<String, Field> fieldEntry : fieldMap.entrySet()) {
                    String dataName = fieldEntry.getKey();
                    String dataValue = data.get(dataName);
                    //如果发现此数据为空，不添加到索引树中
                    if ("[NULL]".equals(dataValue)) {
                        continue;
                    }
                    String dataType = fieldEntry.getValue().getType();
                    int lineNum = Integer.valueOf(data.get("[lineNum]"));


                    IndexTree indexTree = indexMap.get(dataName);
                    if (null == indexTree) {
                        indexMap.put(dataName, new IndexTree());
                        indexTree = indexMap.get(dataName);
                    }
                    IndexKey indexKey = new IndexKey(dataValue, dataType);
                    indexTree.putIndex(indexKey, dataFile.getAbsolutePath(), lineNum);
                }
            }
        }

        //重新填充dataFileSet
        if (null != dataFiles && 0 != dataFiles.length) {
            for (int i = 1; i <= dataFiles.length; i++) {
                File dataFile = new File(folder + "/data", i + ".data");
                dataFileSet.add(dataFile);
            }
        }
    }

    private Set<File> findFileSet(List<SingleFilter> singleFilters) {
        Set<File> fileSet = new HashSet<>();
        //此处查找索引
        for (SingleFilter singleFilter : singleFilters) {
            String fieldName = singleFilter.getField().getName();
            String fieldType = singleFilter.getField().getType();
            Relationship relationship = singleFilter.getRelationship();
            String condition = singleFilter.getCondition();

            IndexKey indexKey = new IndexKey(condition, fieldType);
            IndexTree indexTree = indexMap.get(fieldName);
            fileSet.addAll(indexTree.getFiles(relationship, indexKey));
        }
        return fileSet;
    }

}
