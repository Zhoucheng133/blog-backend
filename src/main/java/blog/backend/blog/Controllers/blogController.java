package blog.backend.blog.Controllers;

import blog.backend.blog.MariaDB.operations;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

@Data
class uploadResponse{
    Boolean ok;
    String reason;

    public uploadResponse(Boolean ok, String reason) {
        this.ok = ok;
        this.reason = reason;
    }
}


@Controller
@ResponseBody
@CrossOrigin
public class blogController {
    @RequestMapping("/upload")
    uploadResponse upload(@RequestParam("file") MultipartFile file, @RequestHeader("token") String token, @RequestHeader("name") String name, @RequestParam("title") String title, @RequestParam("tag") String tag, @RequestParam("top") Boolean top) throws IOException {
        System.out.println(tag+":"+top);
        // 登录失败
        if(!loginController.checkToken(token, name)){
            return new uploadResponse(false, "TokenErr");
        }
        // 重复命名
        ArrayList<String> allTitles= operations.getAllTitle();
        if(allTitles.contains(title)){
            return new uploadResponse(false, "duplicateName");
        }

        // 判断文件是否为空
        if(!file.isEmpty()){
            byte[] bytes = file.getBytes();
            // 在项目中创建的文件夹
            String uploadDir = "blogs";
            // 创建路径
            Path path = Path.of(uploadDir + File.separator + title + ".md");
            // 创建目录
            Files.createDirectories(path.getParent());
            // 存储文件
            Files.write(path, bytes);
            operations.Insert(title, top ?1:0, tag);
            return new uploadResponse(true, "");
        }
        return new uploadResponse(false, "存储出错");
    }

    @RequestMapping("/blog/content/{id}")
    public String getTxtFile(@PathVariable int id) throws IOException {
        String name=operations.getTitle(id);

        File file = new File("blogs/"+name+".md");
        if (!file.exists()) {
            return "";
        }
        StringBuilder contentBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine()).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    @RequestMapping("/blog/getAll")
    ArrayList<blog> getAll(){
        return operations.getAllBlogs();
    }
}
