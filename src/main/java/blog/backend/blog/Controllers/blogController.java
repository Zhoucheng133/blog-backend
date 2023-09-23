package blog.backend.blog.Controllers;

import blog.backend.blog.MariaDB.operations;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

import static blog.backend.blog.MariaDB.operations.delBlog;
import static blog.backend.blog.MariaDB.operations.getAllTitles;

@Data
class normalResponse{
    Boolean ok;
    String reason;

    public normalResponse(Boolean ok, String reason) {
        this.ok = ok;
        this.reason = reason;
    }
}

@Data
class contentResponse{
    String title;
    String tag;
    Timestamp date;
    String content;

    public contentResponse(String title, String tag, Timestamp date, String content) {
        this.title = title;
        this.tag = tag;
        this.date = date;
        this.content = content;
    }
}

@Data
class fileResponse{
    Boolean ok;
    ArrayList<file> files;

    public fileResponse(Boolean ok, ArrayList<file> files) {
        this.ok = ok;
        this.files = files;
    }
}

@Data
class file{
    Boolean isFile;
    String name;

    public file(Boolean isFile, String name) {
        this.isFile = isFile;
        this.name = name;
    }
}

@Data
class searchPath{
    Boolean ok;
    String path;

    public searchPath(Boolean ok, String path) {
        this.ok = ok;
        this.path = path;
    }
}

@Controller
@ResponseBody
@CrossOrigin
public class blogController {
    @RequestMapping("/delFile")
    normalResponse delFile(@RequestHeader("token") String token, @RequestHeader("name") String name, @RequestParam("fileName") String fileName, @RequestParam("path") String path){
        if(!loginController.checkToken(token, name)){
            return new normalResponse(false, "登录失败或者Token过期");
        }
        if(!delBlog(fileName)){
            return new normalResponse(false, "移除数据库数据失败");
        }
        String filePath="blogs"+path+"/"+fileName+".md";
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();

            if (deleted) {
                return new normalResponse(true, "");
            } else {
                return new normalResponse(false, "权限问题，删除失败");
            }
        } else {
            return new normalResponse(false, "需要删除的文件不存在");
        }
    }
    @RequestMapping("/newFolder")
    normalResponse newFolder(@RequestHeader("token") String token, @RequestHeader("name") String name, @RequestParam("folderName") String folderName, @RequestParam("path") String path){
        if(!loginController.checkToken(token, name)){
            return new normalResponse(false, "登录失败或者Token过期");
        }

        path="blogs"+path+"/"+folderName;
        File folder = new File(path);
        boolean success = folder.mkdir();

        if(success){
            return new normalResponse(true, "");
        }else{
            return new normalResponse(false, "创建文件夹失败");
        }
    }
    @RequestMapping("/fileManage")
    fileResponse fileManage(@RequestHeader("token") String token, @RequestHeader("name") String name, @RequestParam("path") String path){
        if(!loginController.checkToken(token, name)){
            return new fileResponse(false, null);
        }

        path="blogs"+path;

        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            return new fileResponse(false, null);
        }

        File[] files = directory.listFiles();
        ArrayList<file> fileInfos = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                boolean isFile = file.isFile();
                String filename = file.getName();
                fileInfos.add(new file(isFile, filename));
            }
        }

        return new fileResponse(true, fileInfos);
    }
    @RequestMapping("/upload")
    normalResponse upload(@RequestParam("file") MultipartFile file, @RequestHeader("token") String token, @RequestHeader("name") String name, @RequestParam("title") String title, @RequestParam("path") String savePath, @RequestParam("tag") String tag, @RequestParam("top") Boolean top) throws IOException {
        // 登录失败
        if(!loginController.checkToken(token, name)){
            return new normalResponse(false, "登录失败或者Token过期");
        }

        ArrayList<String> titles=getAllTitles();
        if (titles != null && titles.contains(title)) {
            return new normalResponse(false, "重复命名");
        }

        // 判断文件是否为空
        if(!file.isEmpty()){

            byte[] bytes = file.getBytes();
            // 在项目中创建的文件夹
//            String uploadDir = savePath;
            // 创建路径
            Path path = Path.of("blogs/"+savePath + File.separator + title + ".md");

            // 创建目录
            Files.createDirectories(path.getParent());
            // 存储文件
            Files.write(path, bytes);
            operations.Insert(title, top ?1:0, tag);
            return new normalResponse(true, "");
        }
        return new normalResponse(false, "存储出错");
    }

    searchPath pathResponse(File directory, String fileName) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        searchPath subResult = pathResponse(file, fileName);
                        if (subResult.ok) {
                            return subResult; // 在子目录中找到文件，返回结果
                        }
                    } else if (file.getName().equals(fileName)) {
                        return new searchPath(true, file.getPath()); // 找到文件后返回结果
                    }
                }
            }
        }
        return new searchPath(false, ""); // 文件未找到
    }

    @RequestMapping("/getContentByName")
    contentResponse getContentByName(@RequestHeader("token") String token, @RequestHeader("name") String name, @RequestParam("fileName") String fileName) throws IOException {
        if(!loginController.checkToken(token, name)){
            return new contentResponse("", "", null, "");
        }
        blogContent info=operations.getBlogByName(fileName);
        fileName=fileName+".md";
        String path="blogs/";

        searchPath tmp = pathResponse(new File(path), fileName);

        File file = new File(tmp.path);
        if (!file.exists()) {
            return new contentResponse("", "", null, "");
        }
        StringBuilder contentBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine()).append("\n");
            }
        }
        return new contentResponse(info.title, info.tag, info.date, contentBuilder.toString());
    }

    @RequestMapping("/blog/content/{id}")
    contentResponse getTxtFile(@PathVariable int id) throws IOException {
        blogContent info=operations.getBlogById(id);

        String fileName=info.title+".md";
        String path="blogs/";

        searchPath tmp = pathResponse(new File(path), fileName);

        File file = new File(tmp.path);
        if (!file.exists()) {
            return new contentResponse("", "", null, "");
        }
        StringBuilder contentBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine()).append("\n");
            }
        }
        return new contentResponse(info.title, info.tag, info.date, contentBuilder.toString());
    }

    @RequestMapping("/blog/getAll")
    ArrayList<blog> getAll(){
        return operations.getAllBlogs();
    }
}
