package lip6.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static lip6.Main.*;

@RestController
@RequestMapping(value = "/graph", produces = "application/json")
@CrossOrigin(origins = "http://127.0.0.1:3001")
public class GraphController {

    @GetMapping("/local")
    public String getLocalGraphJSON(@RequestParam String directoryPath) {
        return getJsonFromPath(directoryPath);
    }
    @GetMapping("/github")
    public ResponseEntity<String> getGraphJSONFromJson(@RequestParam String user, @RequestParam String project, @RequestParam String token) {
        String repoUrl = user + "/" + project;
        ResponseEntity<String> jsonGraph = getJsonFromGitHub(repoUrl, token);
//        if (!jsonGraph.getStatusCode().equals(HttpStatus.OK)) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Invalid user, project or token");
//        }
//            System.out.println(jsonGraph.getStatusCode().equals(HttpStatus.OK));
//            System.out.println(jsonGraph.getBody());
        return jsonGraph;
    }

}