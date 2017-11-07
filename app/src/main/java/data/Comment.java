package data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sheryar Khan on 10/25/2017.
 */

public class Comment {

    private String commentid;
    private String commenttext;
    private Map<String,Boolean> likes = new HashMap<>();
    private int likesCount=0;
    private Long timestamp;
    private String userid;
    private String username;
    private String profilepicture;

    public Comment() {
    }

    public Comment(String commenttext, Map<String, Boolean> likes, int likesCount, Long timestamp, String userid) {
        this.commenttext = commenttext;
        this.likes = likes;
        this.likesCount = likesCount;
        this.timestamp = timestamp;
        this.userid = userid;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("commentid", commentid);
        result.put("commenttext", commenttext);
        result.put("likes", likes);
        result.put("likesCount", likesCount);
        result.put("timestamp", timestamp);
        result.put("userid", userid);
        result.put("username", username);
        result.put("profilepicture", profilepicture);


        return result;
    }

    public Comment(String commentid, String commenttext, Map<String, Boolean> likes, int likesCount, Long timestamp, String userid, String username, String profilepicture) {
        this.commentid = commentid;
        this.commenttext = commenttext;
        this.likes = likes;
        this.likesCount = likesCount;
        this.timestamp = timestamp;
        this.userid = userid;
        this.username = username;
        this.profilepicture = profilepicture;
    }

    public String getCommentid() {
        return commentid;
    }

    public String getCommenttext() {
        return commenttext;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public void setCommenttext(String commenttext) {
        this.commenttext = commenttext;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentid='" + commentid + '\'' +
                ", commenttext='" + commenttext + '\'' +
                ", likes=" + likes +
                ", likesCount=" + likesCount +
                ", timestamp=" + timestamp +
                ", userid='" + userid + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }
}
