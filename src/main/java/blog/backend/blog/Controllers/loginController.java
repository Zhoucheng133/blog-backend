package blog.backend.blog.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import blog.backend.blog.MariaDB.operations;


@Controller
@ResponseBody
// 注意临时允许跨域
@CrossOrigin
public class loginController {

    @RequestMapping("/login")
    Boolean login(@RequestParam String name, @RequestParam String pass){
        if(operations.Check2("user", "name", name, "password", pass)!=0){
            return true;
        }else{
            return false;
        }
    }
}
