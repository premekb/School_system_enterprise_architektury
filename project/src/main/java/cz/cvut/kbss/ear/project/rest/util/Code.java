package cz.cvut.kbss.ear.project.rest.util;

/**
 * /api/course/kos takes code in post mapping,
 * if we were to use just string, then the whole json document gets saved in the string instead of
 * just the code
 */
public class Code {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
