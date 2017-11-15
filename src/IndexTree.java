import java.io.File;
import java.io.Serializable;
import java.util.*;

public class IndexTree implements Serializable {
    private TreeMap<IndexKey, IndexNode> treeMap;

    public IndexTree() {
        treeMap = new TreeMap<>();
    }

    public TreeMap<IndexKey, IndexNode> getTreeMap() {
        return treeMap;
    }

    public void setTreeMap(TreeMap<IndexKey, IndexNode> treeMap) {
        this.treeMap = treeMap;
    }

    public List<IndexNode> find(Relationship relationship, IndexKey condition) {
        List<IndexNode> indexNodeList = new ArrayList<>();
        Map<IndexKey, IndexNode> indexNodeMap = null;
        switch (relationship) {
            case LESS_THAN:
                //此方法获得小于key的映射
                indexNodeMap = treeMap.headMap(condition);
                if (null != indexNodeMap) {
                    for (IndexNode node : indexNodeMap.values()) {
                        indexNodeList.add(node);
                    }
                }
                /*for (IndexNode node : indexNodeMap.values()) {
                    indexNodeList.add(node);
                }*/
                break;
            case EQUAL_TO:
                IndexNode indexNode = treeMap.get(condition);
                if (null != indexNode) {
                    indexNodeList.add(indexNode);
                }
                break;
            case MORE_THAN:
                //此方法获得大于等于key的映射，如果有等于那么要去掉等于key的映射
                indexNodeMap = treeMap.tailMap(condition);
                if (null != indexNodeMap) {
                    if (indexNodeMap.containsKey(condition)) {
                        indexNodeMap.remove(condition);
                    }
                    for (IndexNode node : indexNodeMap.values()) {
                        indexNodeList.add(node);
                    }
                }
                break;
            default:
                try {
                    throw new Exception("条件限定不匹配");
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
        return indexNodeList;
    }

    public Set<File> getFiles(Relationship relationship, IndexKey condition) {
        Set<File> fileSet = new HashSet<>();
        List<IndexNode> indexNodes = this.find(relationship, condition);
        for (IndexNode indexNode : indexNodes) {
            fileSet.addAll(indexNode.getFiles());
        }
        return fileSet;
    }

    public void put(IndexKey indexKey, IndexNode indexNode) {
        treeMap.put(indexKey, indexNode);
    }

    public void putIndex(IndexKey indexKey, String filePath, int lineNum) {
        IndexNode indexNode = treeMap.get(indexKey);
        //如果没有此节点，添加此节点
        if (null == indexNode) {
            treeMap.put(indexKey, new IndexNode());
            //将indexNode从新引用到此节点
            indexNode = treeMap.get(indexKey);
        }
        Index index = new Index(filePath, lineNum);
        indexNode.addIndex(index);
    }

    /*public void buildIndex() {
        for (String fieldName : fieldMap.keySet()) {

        }
        File[] dataFiles=new File(folder, "data").listFiles();
        for (File dataFile : dataFiles) {
            List<Map<String,String>> datas=readDatas(dataFile);
            for (Map<String, String> data : datas) {

            }
        }
    }*/
}
