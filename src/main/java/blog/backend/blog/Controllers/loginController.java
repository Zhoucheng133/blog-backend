package blog.backend.blog.Controllers;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import blog.backend.blog.MariaDB.operations;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Data
class checkTokenFeedback{
    Boolean ok;
    String username;

    public checkTokenFeedback(Boolean ok, String username) {
        this.ok = ok;
        this.username = username;
    }
}

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

    @RequestMapping("/checkToken")
    checkTokenFeedback checkToken(@RequestParam String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key))
                    .withIssuer("userData")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String username= String.valueOf(decodedJWT.getClaim("name"));
            return new checkTokenFeedback(true, username.substring(1, username.length()-1));
        } catch (JWTVerificationException exception){
            return new checkTokenFeedback(false, "");
        }
    }

}
