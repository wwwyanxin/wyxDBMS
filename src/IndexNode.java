import java.io.File;
import java.io.Serializable;
import java.util.*;

public class IndexNode implements Serializable{

    private List<Index> indexList;

    public IndexNode() {
        this.indexList = new ArrayList<>();
    }

    public void addIndex(Index index) {
        indexList.add(index);
    }

    public Iterator<Index> indexIterator() {
        return indexList.iterator();
    }

    public Set<File> getFiles() {
        Set<File> fileSet = new HashSet<>();
        Iterator<Index> indexIterator = indexIterator();
        for (Index index : indexList) {
            File file = new File(index.getFilePath());
            fileSet.add(file);
        }
        return fileSet;
    }
}
