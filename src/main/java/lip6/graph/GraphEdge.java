package lip6.graph;

public class GraphEdge {
    private String source;
    private String target;
    private String type;

    public GraphEdge(String source, String target, String type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getType() {
        return type;
    }



    @Override
    public String toString() {
        return "GraphEdge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
