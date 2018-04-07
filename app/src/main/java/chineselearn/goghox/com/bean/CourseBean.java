package chineselearn.goghox.com.bean;

/**
 * Created by GogHox on 2018/2/14.
 */

public class CourseBean {
    /**
     * id : 1
     * title : 回不去的旧时光
     * img_url : https://upload-images.jianshu.io/upload_images/6266669-c56042cc65b8b7aa.jpg
     * course_content_id : 1
     */

    private int id;
    private String title;
    private String img_url;
    private int course_content_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getCourse_content_id() {
        return course_content_id;
    }

    public void setCourse_content_id(int course_content_id) {
        this.course_content_id = course_content_id;
    }

    @Override
    public String toString() {
        return "" + getTitle() + getId() + getImg_url() + getCourse_content_id();
    }

    /*// 图片、标题、描述、内容id
    public String id;
    public String title;
    public String description;
    public @DrawableRes int imgRes;

    public CourseBean() {}

    public CourseBean(String id, String title, String description, int imgRes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgRes = imgRes;
    }*/
}
