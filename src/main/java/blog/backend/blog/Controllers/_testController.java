package blog.backend.blog.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class _testController {
    @RequestMapping("/test")
    String test(){
        return "Hello world!";
    }
}
