import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operating {
    private static final Pattern patternInsert = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
    private static final Pattern patternCreateTable = Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
    private static final Pattern patternAlterTable_add = Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;");
    private static final Pattern patternDelete = Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern patternUpdate = Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern patternDropTable = Pattern.compile("drop\\stable\\s(\\w+);");
    private static final Pattern patternSelect = Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+))?\\s?;");
    private static final Pattern patternDeleteIndex = Pattern.compile("delete\\sindex\\s(\\w+)\\s?;");


    public  void dbms() {
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
            Matcher matcherDeleteIndex = patternDeleteIndex.matcher(cmd);


            while (matcherAlterTable_add.find()) {
                alterTableAdd(matcherAlterTable_add);
            }

            while (matcherDropTable.find()) {
                dropTable(matcherDropTable);
            }


            while (matcherCreateTable.find()) {
                createTable(matcherCreateTable);
            }

            while (matcherDelete.find()) {
                delete(matcherDelete);
            }

            while (matcherUpdate.find()) {
                update(matcherUpdate);
            }

            while (matcherInsert.find()) {
                insert(matcherInsert);
            }

            while (matcherSelect.find()) {
                select(matcherSelect);


            }

            while (matcherDeleteIndex.find()) {
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
        Map<String, List<Map<String, String>>> talbeDatasMap = new LinkedHashMap<>();
        //将需要显示的字段名按table.filed的型式存入dataNameList
        List<String> dataNameList = new ArrayList<>();

        // select * from table1,table2;
        if ("*".equals(matcherSelect.group(1)) && null == matcherSelect.group(3)) {

            List<String> tableNames = StringUtil.parseFrom(matcherSelect.group(2));
            for (String tableName : tableNames) {
                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表：" + tableName);
                    break;
                }
                Map<String, Field> fieldMap = table.getFieldMap();
                //读取数据
                List<Map<String, String>> datas = table.read();
                for (String fieldName : fieldMap.keySet()) {
                    dataNameList.add(tableName+"."+fieldName);
                }
                if (0 != datas.size()) {
                    talbeDatasMap.put(tableName, datas);
                }
            }
        } else if (!"*".equals(matcherSelect.group(1)) && null == matcherSelect.group(3)) {
            // select table.id name from table1,table2;
            List<String> tableNames = StringUtil.parseFrom(matcherSelect.group(2));
            for (String tableName : tableNames) {
                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表：" + tableName);
                    break;
                }

                Map<String, Field> fieldMap = table.getFieldMap();
                //解析投影
                Set<String> projection = StringUtil.parseProjection(matcherSelect.group(1), tableName, fieldMap);

                //读取数据
                List<Map<String, String>> datas=new ArrayList<>();
                //如果存在此表的投影项
                if (0 != projection.size()) {
                     datas= table.read(projection);
                    for (String projectionName : projection) {
                        dataNameList.add(tableName+"."+projectionName);
                    }
                } /*else {
                    datas = table.read();
                    for (String fieldName : fieldMap.keySet()) {
                        dataNameList.add(tableName+"."+fieldName);
                    }
                }*/
                if (0 != datas.size()) {
                    talbeDatasMap.put(tableName, datas);
                }
            }
        } else if ("*".equals(matcherSelect.group(1)) && null != matcherSelect.group(3)) {
            // select * from table1,table2 where table1.id>10 and height<1.8
            List<String> tableNames = StringUtil.parseFrom(matcherSelect.group(2));
            for (String tableName : tableNames) {
                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表：" + tableName);
                    break;
                }

                Map<String, Field> fieldMap = table.getFieldMap();
                //解析选择
                List<SingleFilter> singleFilters = new ArrayList<>();
                List<Map<String, String>> filtList = StringUtil.parseWhere(matcherSelect.group(3),tableName,fieldMap);
                for (Map<String, String> filtMap : filtList) {
                    SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                            , filtMap.get("relationshipName"), filtMap.get("condition"));

                    singleFilters.add(singleFilter);
                }


                //读取数据
                List<Map<String, String>> datas;
                //如果存在此表的选择项
                if (0 != singleFilters.size()) {
                    datas= table.read(singleFilters);
                    for (String fieldName : fieldMap.keySet()) {
                        dataNameList.add(tableName+"."+fieldName);
                    }
                } else {
                    datas = table.read();
                    for (String fieldName : fieldMap.keySet()) {
                        dataNameList.add(tableName+"."+fieldName);
                    }
                }
                if (0 != datas.size()) {
                    talbeDatasMap.put(tableName, datas);
                }
            }
        }else if (!"*".equals(matcherSelect.group(1)) && null != matcherSelect.group(3)) {
            // select table.id,height from table1,table2 where table1.id>10 and height<1.8
            List<String> tableNames = StringUtil.parseFrom(matcherSelect.group(2));
            for (String tableName : tableNames) {
                Table table = Table.getTable(tableName);
                if (null == table) {
                    System.out.println("未找到表：" + tableName);
                    break;
                }

                Map<String, Field> fieldMap = table.getFieldMap();
                //解析投影
                Set<String> projection = StringUtil.parseProjection(matcherSelect.group(1), tableName, fieldMap);
                //解析选择
                List<SingleFilter> singleFilters = new ArrayList<>();
                List<Map<String, String>> filtList = StringUtil.parseWhere(matcherSelect.group(3),tableName,fieldMap);
                for (Map<String, String> filtMap : filtList) {
                    SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                            , filtMap.get("relationshipName"), filtMap.get("condition"));

                    singleFilters.add(singleFilter);
                }


                //读取数据
                List<Map<String, String>> datas=new ArrayList<>();
                //如果存在此表的投影项和选择项
                if (0 != projection.size()&&0!=singleFilters.size()) {
                    datas= table.read(singleFilters,projection);
                    for (String projectionName : projection) {
                        dataNameList.add(tableName+"."+projectionName);
                    }
                } else if (0 != projection.size()) {
                    datas = table.read(projection);
                    for (String projectionName : projection) {
                        dataNameList.add(tableName+"."+projectionName);
                    }
                } else if (0 != singleFilters.size()) {
                    datas = table.read(singleFilters);
                    /*for (String fieldName : fieldMap.keySet()) {
                        dataNameList.add(tableName+"."+fieldName);
                    }*/
                } /*else {
                    datas = table.read();
                    for (String fieldName : fieldMap.keySet()) {
                        dataNameList.add(tableName+"."+fieldName);
                    }
                }*/
                if (0 != datas.size()) {
                    talbeDatasMap.put(tableName, datas);
                }
            }
        }

        //笛卡尔积
        List<List<String>> cartesianProduct = new ArrayList<>();
        Iterator<List<Map<String, String>>> tableIterator = talbeDatasMap.values().iterator();
        if (tableIterator.hasNext()) {
            //当前表
            List<Map<String, String>> datasList1 = tableIterator.next();

            for (Map<String, String> dataMap : datasList1) {
                //将表的一行数据加入到dataLine
                List<String> dataLine = new ArrayList<>();
                for (String value : dataMap.values()) {
                    dataLine.add(value);
                }
                cartesianProduct.add(dataLine);
            }
            //遍历剩下的表做笛卡尔积
            while (tableIterator.hasNext()) {
                //下一张表
                List<Map<String, String>> datasList2 = tableIterator.next();

                //深拷贝到newCarPro;
                List<List<String>> newCarPro = new ArrayList<>();
                File cloneFile = new File("clone");
                try (
                        FileOutputStream fos = new FileOutputStream(cloneFile);
                        ObjectOutputStream oos = new ObjectOutputStream(fos)
                ) {
                    oos.writeObject(cartesianProduct);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //清空cartesianProduct
                cartesianProduct.clear();
                for (Map<String, String> dataMap : datasList2) {
                    //将表的一行数据加入到dataLine
                    List<String> dataLine = new ArrayList<>();
                    for (String value : dataMap.values()) {
                        dataLine.add(value);
                    }

                    try (
                            FileInputStream fis = new FileInputStream(cloneFile);
                            ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                        newCarPro = (List<List<String>>) ois.readObject();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    for (List<String> newCarProLine : newCarPro) {
                        newCarProLine.addAll(dataLine);
                        //carProLine.addAll(dataLine);
                    }
                    cartesianProduct.addAll(newCarPro);
                }

            }
            //计算名字长度，用来对齐数据
            int[] lengh = new int[dataNameList.size()];
            Iterator<String> dataNames = dataNameList.iterator();
            for (int i = 0; i < dataNameList.size(); i++) {
                String dataName=dataNames.next();
                lengh[i] = dataName.length();
                System.out.printf("|%s", dataName);
            }

            System.out.println("|");
            for (int ls : lengh) {
                for (int l = 0; l <=ls ; l++) {
                    System.out.printf("-");
                }
            }
            System.out.println();
            for (List<String> carProLine : cartesianProduct) {
                Iterator<String> carProDatas = carProLine.iterator();
                for (int i = 0; i < carProLine.size(); i++) {
                    String carProData = carProDatas.next();
                    System.out.printf("|%s",carProData);
                    for (int j = 0; j < lengh[i]-carProData.length(); j++) {
                        System.out.printf(" ");
                    }
                }
                System.out.println("|");
            }

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

}

