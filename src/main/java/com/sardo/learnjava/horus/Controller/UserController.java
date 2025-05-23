package com.sardo.learnjava.horus.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class UserController {
    @RequestMapping("/")  // ルートへこのメソッドをマップする
    public String index(Model model) {
        return "index";
    }
    @RequestMapping("/hello")
    public String hello(@RequestParam(value="name", required=false, defaultValue="masa") String name, Model model) {
        model.addAttribute("name", name);
        return "hello";
    }
}