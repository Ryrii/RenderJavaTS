package lip6.graph;

public class GraphNode {
    private String name;
    private String type;
    private String parentName;
    public GraphNode(String nodeName, String nodeType, String parentNodeName) {
        this.parentName = parentNodeName;
        this.name = nodeName;
        this.type = nodeType;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getParentName() {
        return parentName;
    }

    public void setName(String nodeName){
        if(parentName == null){
            this.name = nodeName;
            return;
        }
        this.name = this.parentName + "." + nodeName;
    }
    @Override
    public String toString() {
        return  "{" +
                "\n name: " + this.name
                + "\n type: " + this.type
                + "\n parent: " + this.parentName
                + "\n}";
    }
}


