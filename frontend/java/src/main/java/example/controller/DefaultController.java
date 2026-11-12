package example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

  @GetMapping("/")
  public String index() {
      return "forward:/index.html";
  }
  @GetMapping("/language")
  public String language() {
      return "forward:/language.html";
  }
  @GetMapping("/samples")
  public String samples() {
      return "forward:/samples.html";
  }
}