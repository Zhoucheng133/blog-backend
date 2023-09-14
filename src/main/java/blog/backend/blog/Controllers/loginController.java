package blog.backend.blog.Controllers;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import blog.backend.blog.MariaDB.operations;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Data
class loginFeedback{
    Boolean ok;
    String token;

    public loginFeedback(Boolean ok, String token) {
        this.ok = ok;
        this.token = token;
    }
}
@Controller
@ResponseBody
// 注意临时允许跨域
@CrossOrigin
public class loginController {

    private final String key="blogUserKey";

    @RequestMapping("/login")
    loginFeedback login(@RequestParam String name, @RequestParam String pass){
        if(operations.Check2("user", "name", name, "password", pass)!=0){

            String token = JWT.create()
                .withIssuer("userData")
                .withClaim("name", name)
                .sign(Algorithm.HMAC256(key));

            return new loginFeedback(true, token);
        }else{
            return new loginFeedback(false, "");
        }
    }

    @RequestMapping("/checkLogin")
    Boolean checkLogin(@RequestHeader("token") String token, @RequestHeader("name") String name){
        return checkToken(token, name);
    }

    // 在执行操作之前，先检查token!
    public Boolean checkToken(String token, String name){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key))
                    .withIssuer("userData")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String username= String.valueOf(decodedJWT.getClaim("name"));
            return username.equals(name);
        } catch (JWTVerificationException exception){
            return false;
        }
    }

}
