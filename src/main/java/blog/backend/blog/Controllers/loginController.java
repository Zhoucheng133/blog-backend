package blog.backend.blog.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class loginController {
    @RequestMapping("/login")
    String login(@RequestParam String name, @RequestParam String pass){
        return name+":"+pass;
    }
}
