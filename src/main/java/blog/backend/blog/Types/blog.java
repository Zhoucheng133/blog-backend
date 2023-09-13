package blog.backend.blog.Types;

import java.sql.Date;

import lombok.Data;

@Data
public class blog {
    int id;
    String name;
    Date createDate;
    String path;
    /**
     * @param id
     * @param name
     * @param createDate
     * @param path
     */
    public blog(int id, String name, Date createDate, String path) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.path = path;
    }
}
