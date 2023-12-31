package blog.backend.blog.Controllers;

import lombok.Data;

import java.util.Date;

@Data
public class blog {
    int id;
    String title;
    Date date;
    String path;
    Boolean top;
    String tag;
    String cata;

    public blog(int id, String title, Date date, String path, Boolean top, String tag, String cata) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.path = path;
        this.top = top;
        this.tag = tag;
        this.cata = cata;
    }
}
