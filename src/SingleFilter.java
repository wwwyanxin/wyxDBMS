import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SingleFilter {
    private Field field;
    private String relationshipName;
    private String condition;

    public SingleFilter(Field field, String relationshipName, String condition) {
        this.field = field;
        this.relationshipName = relationshipName;
        this.condition = condition;
    }

    /**
     * @param srcDatas 原数据
     * @return 过滤后的数据
     */
    public List<Map<String, String>> singleFiltData(List<Map<String, String>> srcDatas) {
        // Field field, Relationship relationship, String condition
        Relationship relationship = Relationship.parseRel(relationshipName);
        List<Map<String, String>> datas = new ArrayList<>();
        //如果没有限定条件，返回原始列表
        if (null == field || null == relationship) {
            //Collections.copy(datas, srcDatas);
            return srcDatas;
        }
        for (Map<String, String> srcData : srcDatas) {
            //如果条件匹配成功,则新的列表存储此条数据
            if (Relationship.matchCondition(srcData, field, relationship, condition)) {
                datas.add(srcData);
            } else {
                continue;
            }
        }
        return datas;
    }

    public Field getField() {
        return field;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public String getCondition() {
        return condition;
    }

    public Relationship getRelationship() {
        Relationship relationship = Relationship.parseRel(relationshipName);
        return relationship;
    }
}
