package blog.backend.blog.Types;

import lombok.Data;

@Data
public class user {
    int id;
    String name;
    String pass;
    /**
     * @param id
     * @param name
     * @param pass
     */
    public user(int id, String name, String pass) {
        this.id = id;
        this.name = name;
        this.pass = pass;
    }
}
