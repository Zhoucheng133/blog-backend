package blog.backend.blog.Controllers;

import java.sql.Timestamp;

public class blogContent {
    String title;
    String tag;
    Timestamp date;

    public blogContent(String title, String tag, Timestamp date) {
        this.title = title;
        this.tag = tag;
        this.date = date;
    }
}
