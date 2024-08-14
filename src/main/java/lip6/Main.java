package lip6;

import lip6.graph.Graph;
import org.kohsuke.github.GHContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TreeSitterJava;
import org.treesitter.TSTree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        String code = """
                

    protected FigSingleLineTextWithNotation createFigText(Object owner,
            Rectangle bounds,
            @SuppressWarnings("unused") DiagramSettings settings,
            NotationProvider np) {

        FigSingleLineTextWithNotation comp = createFigText(
                    bounds.x,
                    bounds.y,
                    bounds.width,
                    bounds.height,
                    this.getBigPort(),
                    np);
        comp.setOwner(owner);
        return comp;
    } 
                """;
        Graph g = new Graph();
        TSTree tree = getTree(null);
        TSNode rootNode = tree.getRootNode();
        g.setImports(new ArrayList<>());
        g.setPackageName(null);
        List<String> declarationTypes = List.of("package_declaration", "class_declaration");
        addNodeAndChildrenToGraph(g, rootNode, null,declarationTypes);
        g.addUseEdges();
        System.out.println(g.getJSONForCytoscape());
//        TSTree tree = getTree();
//        TSNode rootNode = tree.getRootNode();
//        printNodes(rootNode, 0);

//        Graph g = buildGraph(tree);
////        System.out.println(g);
//        String repoUrl = "RyRii/TreeSitter";
//        String token = "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";
//        GitHubFileProcessor gitHubFileProcessor;
//
//        String directoryPath = "ressources/argo";
//        FileProcessor fileProcessor = new FileProcessor(directoryPath);
//        Graph g = new Graph();
//
//        try {
//            gitHubFileProcessor = new GitHubFileProcessor(repoUrl, token);
//            List<GHContent> javaFiles = gitHubFileProcessor.listJavaFiles();
//            System.out.println("javaFiles : " + javaFiles.size());
//            for (GHContent file : javaFiles) {
////            List<Path> filePaths = fileProcessor.listFiles();
////            System.out.println("filePaths : " + filePaths.size());
////            List<Path> subFilePaths = filePaths.subList(0, filePaths.size());
////            for (Path filePath : subFilePaths) {
////                String code = fileProcessor.readFileContent(filePath);
//                String code = gitHubFileProcessor.readFileContent(file);
//                TSTree tree = getTree(code);
//                TSNode rootNode = tree.getRootNode();
//                g.setImports(new ArrayList<>());
//                g.setPackageName(null);
//                addNodeAndChildrenToGraph(g, rootNode, null);
////                System.out.println("Nodes Added Successfully");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        System.out.println(g.formatNodesByType());
////        System.out.println(g.getEdges());
//        System.out.println(g.getJSONForCytoscape());
////        System.out.println("classes : " + g.getClassDeclarationNames());
////        System.out.println("class uses : " + g.getClassUses());
////        System.out.println("check class uses : ");
//        g.addUseEdges();
//        System.out.println("Use Edges Added Successfully");
    }
    public static String getJsonFromPath(List<String> codeList, List<String> declarationTypes) {
    Graph g = new Graph();

    int count = 0;
    int nbFiles = codeList.size();
    for (String code : codeList) {
        count++;
        System.out.println("Processing file " + count + " out of " + nbFiles);
        TSTree tree = getTree(code);
        TSNode rootNode = tree.getRootNode();
        g.setImports(new ArrayList<>());
        g.setPackageName(null);
        addNodeAndChildrenToGraph(g, rootNode, null, declarationTypes);
    }

    System.out.println("Adding Use Edges");
    g.addUseEdges();
    System.out.println("Use Edges Added Successfully");
    String json = g.getJSONForCytoscape();
    System.out.println(json.length());
    return json;
}
    public static ResponseEntity<String> getJsonFromGitHub(String repoUrl, String token,List<String> declarationTypes) {
        GitHubFileProcessor gitHubFileProcessor;
        Graph g = new Graph();
//        List <String> declarationTypes = List.of("package_declaration", "class_declaration", "constructor_declaration", "method_declaration", "field_declaration", "local_variable_declaration");
        try {
            gitHubFileProcessor = new GitHubFileProcessor(repoUrl, token);
            List<GHContent> javaFiles = gitHubFileProcessor.listJavaFiles();
            int count = 0;
            int nbFiles = javaFiles.size();
            for (GHContent file : javaFiles) {
                count++;
                System.out.println("Processing file " + count + " out of " + nbFiles);
                String code = gitHubFileProcessor.readFileContent(file);
                TSTree tree = getTree(code);
                TSNode rootNode = tree.getRootNode();
                g.setImports(new ArrayList<>());
                g.setPackageName(null);
                addNodeAndChildrenToGraph(g, rootNode, null,declarationTypes);
            }
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
        System.out.println("Adding Use Edges");
        g.addUseEdges();
        System.out.println("Use Edges Added Successfully");
        return ResponseEntity.ok(g.getJSONForCytoscape());
    }
    public static TSTree getTree(String code1) {
        TSParser parser = new TSParser();
        TSLanguage java = new TreeSitterJava();
        parser.setLanguage(java);
        code1= code1 !=null ? code1 :  """
                package pckg_a;
                    class class_A{
                        int field_a;
                        public cstrct_A(){
                        }
                        public void method_a(int arg_a, int arg_b){
                        }
                        public void method_b(int arg_a, int arg_b){
                        }
                    }
                    class class_B{
                        class_A field_b;
                        public cstrct_B(){
                        }
                        public void method_c(int arg_c, int arg_d){
                        }
                        public void method_d(int arg_e, int arg_f){
                        }
                    }
                }
                """;
        SourceCode.setCode(code1);
        String code = SourceCode.getCode();
        return parser.parseString(null, code);
    }
    public static Graph buildGraph(TSTree tree) {
        Graph g = new Graph();
        TSNode rootNode = tree.getRootNode();
        List <String> declarationTypes = List.of("package_declaration", "class_declaration", "constructor_declaration", "method_declaration", "field_declaration", "local_variable_declaration");
        addNodeAndChildrenToGraph(g, rootNode, null,declarationTypes);
        return g;
    }
    private static boolean isDeclarationNode(TSNode node) {
        List <String> declarationTypes = List.of("package_declaration", "class_declaration", "constructor_declaration", "method_declaration", "field_declaration", "local_variable_declaration");
        String type = node.getType();
        return declarationTypes.contains(type);
    }
    public static void addNodeAndChildrenToGraph(Graph g, TSNode node, String parentNodeName,List<String> declarationTypes) {
        String NodeName = SourceCode.getName(node);
        String nodeQName = (parentNodeName == null) ? NodeName :
                parentNodeName + '.' + NodeName;
        String nodeType = node.getType();
//        System.out.println("Node start Character : " + node.getStartByte());
//        System.out.println("Node end Character : " + node.getEndByte());
//
//        System.out.println("Node start column : " + node.getStartPoint().getColumn());
//        System.out.println("Node start line : " + node.getStartPoint().getRow());
//        System.out.println("Node end column : " + node.getEndPoint().getColumn());
//        System.out.println("Node end line : " + node.getEndPoint());
        if(nodeType.equals("type_identifier") && isDeclarationNode(node.getParent()) ){
            g.addClassUse(NodeName, parentNodeName);
        }
        if(nodeType.equals("import_declaration")){
//            System.out.println("Import Declaration : " + SourceCode.getName(node));
            g.addImport(NodeName);
        }
        if(declarationTypes.contains(nodeType)){
            g.addNode(nodeQName, nodeType , parentNodeName);
            if (parentNodeName != null) {
                g.addEdge(parentNodeName, nodeQName,"contain");
            }
            parentNodeName = nodeQName;
        }

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TSNode child = node.getChild(i);
            if(child.getType().equals("package_declaration")){
                parentNodeName = SourceCode.getName(child);
                addNodeAndChildrenToGraph(g, child, null,declarationTypes);
                g.setPackageName(parentNodeName);
            }
            else{
                addNodeAndChildrenToGraph(g, child, parentNodeName,declarationTypes);
            }
        }
    }

    // Recursive method to print nodes and their children
    private static void printNodes(TSNode node, int depth) {
        // Print the current node with indentation based on depth
        System.out.println(" ".repeat(depth * 2) + node.toString());

        // Traverse all children of the current node
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TSNode child = node.getChild(i);
            printNodes(child, depth + 1);
        }
    }
}