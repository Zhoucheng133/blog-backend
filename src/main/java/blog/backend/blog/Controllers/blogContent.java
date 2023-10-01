package blog.backend.blog.Controllers;

import java.sql.Timestamp;

public class blogContent {
    String title;
    String tag;
    Timestamp date;
    String cata;

    public blogContent(String title, String tag, Timestamp date, String cata) {
        this.title = title;
        this.tag = tag;
        this.date = date;
        this.cata = cata;
    }
}
