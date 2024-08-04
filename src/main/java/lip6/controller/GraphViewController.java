package lip6.controller;

import lip6.Main;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lip6.Main.getJsonFromGitHub;

@Controller
public class GraphViewController {

    @GetMapping("/graphView")
    public String viewGraph(Model model) {
        return "graphView";
    }
}