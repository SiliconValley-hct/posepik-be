package hackthevalley.posepik_be.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class testController {
  @GetMapping("/test")
  @ResponseBody
  public String test() {
    return "connection success";
  }
}
