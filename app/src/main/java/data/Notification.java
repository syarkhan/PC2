package data;

/**
 * Created by Sheryar Khan on 11/1/2017.
 */

public class Notification {

    private String postid;
    private String profilepic;
    private String notification;
    private Long timestamp;

    public Notification(String postid, String profilepic, String notification, Long timestamp) {
        this.postid = postid;
        this.profilepic = profilepic;
        this.notification = notification;
        this.timestamp = timestamp;
    }

    public Notification() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
