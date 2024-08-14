package lip6.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lip6.Main.*;

@RestController
@RequestMapping(value = "/graph", produces = "application/json")
@CrossOrigin(origins = "http://127.0.0.1:3001")
public class GraphController {
//    List<String> declarationTypes = List.of("package_declaration", "class_declaration", "constructor_declaration", "method_declaration", "field_declaration", "local_variable_declaration");
@PostMapping("/local")
public ResponseEntity<String> getLocalGraphJSON(@RequestBody CodeRequest codeRequest) {
    System.out.println(codeRequest);
    String json = getJsonFromPath(codeRequest.getCodeList(), codeRequest.getDeclarationTypes());
    return ResponseEntity.ok(json);
}
    public static class CodeRequest {
        private List<String> codeList;
        private List<String> declarationTypes;

        // Getters and setters
        public List<String> getCodeList() {
            return codeList;
        }

        public void setCodeList(List<String> codeList) {
            this.codeList = codeList;
        }

        public List<String> getDeclarationTypes() {
            return declarationTypes;
        }

        public void setDeclarationTypes(List<String> declarationTypes) {
            this.declarationTypes = declarationTypes;
        }
    }
    @GetMapping("/github")
    public ResponseEntity<String> getGraphJSONFromJson(@RequestParam String user, @RequestParam String project, @RequestParam String token,@RequestParam List<String> declarationTypes) {
        String repoUrl = user + "/" + project;

        ResponseEntity<String> jsonGraph = getJsonFromGitHub(repoUrl, token,declarationTypes);
//        if (!jsonGraph.getStatusCode().equals(HttpStatus.OK)) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Invalid user, project or token");
//        }
//            System.out.println(jsonGraph.getStatusCode().equals(HttpStatus.OK));
//            System.out.println(jsonGraph.getBody());
        return jsonGraph;
    }

}