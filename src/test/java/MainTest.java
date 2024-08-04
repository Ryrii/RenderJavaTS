import lip6.Main;
import lip6.SourceCode;
import lip6.graph.Graph;
import lip6.graph.GraphNode;
import org.junit.jupiter.api.Test;
import org.treesitter.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

class MainTest {

    @Test
    void testBuildGraph() {
        TSParser parser = new TSParser();
        TSLanguage java = new TreeSitterJava();
        parser.setLanguage(java);
        String code = """
                package pckg_a;
                    class class_A{
                    int field_a;
                    public cstrct_A(){
                    }
                    public void method_a(int arg_a, int arg_b){
                    }
                    }
                }
                """;
        SourceCode.setCode(code);
        TSTree tree = parser.parseString(null, code);
        Main main = new Main();

        Graph graph = main.buildGraph(tree);


        Map<String, GraphNode> nodes = graph.getNodes();
        GraphNode node = nodes.get("pckg_a");

        assertThat(nodes).hasSize(5);

        assertThat(nodes).containsOnlyKeys("pckg_a", "pckg_a.class_A","pckg_a.class_A.method_a(int,int)",
                "pckg_a.class_A.cstrct_A()",
                "pckg_a.class_A.field_a");

        assertThat(node)
                .extracting(GraphNode::getName,GraphNode::getType,GraphNode::getParentName)
                .containsExactly("pckg_a", "package_declaration", null);

        assertThat(nodes.get("pckg_a.class_A"))
                .extracting(GraphNode::getName,GraphNode::getType,GraphNode::getParentName)
                .containsExactly("pckg_a.class_A", "class_declaration", "pckg_a");

        assertThat(nodes.get("pckg_a.class_A.method_a(int,int)"))
                .extracting(GraphNode::getName,GraphNode::getType,GraphNode::getParentName)
                .containsExactly("pckg_a.class_A.method_a(int,int)", "method_declaration", "pckg_a.class_A");

        assertThat(nodes.get("pckg_a.class_A.cstrct_A()"))
                .extracting(GraphNode::getName,GraphNode::getType,GraphNode::getParentName)
                .containsExactly("pckg_a.class_A.cstrct_A()", "constructor_declaration", "pckg_a.class_A");

        assertThat(nodes.get("pckg_a.class_A.field_a"))
                .extracting(GraphNode::getName,GraphNode::getType,GraphNode::getParentName)
                .containsExactly("pckg_a.class_A.field_a", "field_declaration", "pckg_a.class_A");
    }


}