package lip6.graph;

import java.util.*;

public class Graph  {
    private Map<String, GraphNode> nodes;
    private List<GraphEdge> edges;
    private List<ClassUse> classUses;
    private String packageName;
    private List<String> imports;
    public Graph() {
        nodes = new HashMap<String, GraphNode>();
        edges = new ArrayList<GraphEdge>();
        imports = new ArrayList<>();
        classUses = new ArrayList<>();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public void setImports(List<String> imports) {
        this.imports = imports;
    }
    public void addImport(String importName) {
        imports.add(importName);
    }

   	public void addNode(String nodeName, String nodeType, String parentNodeName) {
        GraphNode graphNode = new GraphNode(nodeName, nodeType, parentNodeName);
        if (graphNode.getType().equals("class_declaration")) {
        }
        this.nodes.putIfAbsent(nodeName, graphNode);
    }
    public List<String> getClassDeclarationNames(){
        List<String> classDeclarationNames = new ArrayList<>();
        for (GraphNode graphNode : nodes.values()) {
            if (graphNode.getType().equals("class_declaration")) {
                classDeclarationNames.add(graphNode.getName());
            }
        }
        return classDeclarationNames;
    }
    public void addClassUse(String className, String parentName) {
        List<String> javaTypes = List.of("Integer", "String", "Double", "Float", "Boolean", "Character", "Long", "Short", "Byte");
        if (!javaTypes.contains(className)) {
            classUses.add(new ClassUse(className, parentName, packageName, imports));
        }
    }
    public List<ClassUse> getClassUses() {
        return classUses;
    }
    public void addUseEdges() {
        List<String> classDeclarationNames = getClassDeclarationNames();
//        System.out.println("classDeclarationNames : "+classDeclarationNames);
//        System.out.println("classUses : "+classUses.size());
//        for (ClassUse classUse : classUses) {
//            System.out.println(classUse.getClassName());
//        }
        for (String classDeclarationName : classDeclarationNames) {
            for (ClassUse classUse : classUses) {
                if (classUse.isUsed(classDeclarationName)) {
                    addEdge(classUse.getParentName(), classDeclarationName,"use");
                }
            }
        }
//        System.out.println("Use Edges : "+edges.stream().filter(e -> e.getType().equals("use")).count());
        List<GraphEdge> useEdges = edges.stream().filter(e -> e.getType().equals("use")).toList();
//        for (GraphEdge edge : useEdges) {
//            System.out.println(edge);
//        }
    }
    public Map<String, List<String>> getNodesByType() {
        Map<String, List<String>> nodesByType = new HashMap<>();
        for (GraphNode graphNode : nodes.values()) {
            String type = graphNode.getType();
            if (!nodesByType.containsKey(type)) {
                nodesByType.put(type, new ArrayList<>());
            }
            nodesByType.get(type).add(graphNode.getName());
        }
        return nodesByType;
    }
    public String formatNodesByType() {
        Map<String, List<String>> nodesByType = getNodesByType();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : nodesByType.entrySet()) {
            sb.append(entry.getKey()).append(" : ");
            List<String> nodeNames = entry.getValue();
            for (int i = 0; i < nodeNames.size(); i++) {
                sb.append(nodeNames.get(i));
                if (i < nodeNames.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Map<String, GraphNode> getNodes() {
        return nodes;
    }
    public void addEdge(String source, String target,String edgeType) {
        // check if the graph edge already exists


        edges.add(new GraphEdge(source, target,edgeType));
    }
    public List<GraphEdge> getEdges() {
        return edges;
    }
    public String getJSONForCytoscape() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"nodes\": [\n");
        boolean firstNode = true;
        for (GraphNode nodeData : nodes.values()) {
            if (!firstNode) {
                sb.append(",\n");
            }
            sb.append("    { \"data\": { \"id\": \"").append(nodeData.getName()).append("\", \"type\": \"").append(nodeData.getType()).append("\" } }");
            firstNode = false;
        }
        sb.append("\n  ],\n");
        sb.append("  \"edges\": [\n");
        boolean firstEdge = true;
        for (GraphEdge edge : edges) {
            if (!firstEdge) {
                sb.append(",\n");
            }
            sb.append("    { \"data\": { \"source\": \"").append(edge.getSource()).append("\", \"target\": \"").append(edge.getTarget()).append("\" }, \"classes\": \"").append(edge.getType()).append("\" }");
            firstEdge = false;
        }
        sb.append("\n  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String nn : nodes.keySet()) {
            GraphNode nodeData = nodes.get(nn);
            sb.append(nodeData.getName()).append(" : ");
            sb.append(nodeData);
            sb.append("\n");


        }
        return sb.toString();
    }
    
   
}
