package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sheryar Khan on 10/25/2017.
 */


public class Comment {

    @SerializedName("_id")
    @Expose
    private String commentId;


    @SerializedName("PostId")
    @Expose
    private String postId;

    @SerializedName("CommentText")
    @Expose
    private String commentText;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("LikesCount")
    @Expose
    private int likesCount;

    @SerializedName("Likes")
    @Expose
    private List<String> likes = null;

    @SerializedName("UserInfo")
    @Expose
    private UserInfo userInfo;

    public Comment(String commentId, String postId, String commentText, Long timestamp, String userId, int likesCount, List<String> likes, UserInfo userInfo) {
        this.commentId = commentId;
        this.postId = postId;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.userId = userId;
        this.likesCount = likesCount;
        this.likes = likes;
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String id) {
        this.commentId = id;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }



}





//public class Comment {
//
//    private String commentid;
//    private String commenttext;
//    private Map<String,Boolean> likes = new HashMap<>();
//    private int likesCount=0;
//    private Long timestamp;
//    private String userid;
//    private String username;
//    private String profilepicture;
//
//    public Comment() {
//    }
//
//    public Comment(String commenttext, Map<String, Boolean> likes, int likesCount, Long timestamp, String userid) {
//        this.commenttext = commenttext;
//        this.likes = likes;
//        this.likesCount = likesCount;
//        this.timestamp = timestamp;
//        this.userid = userid;
//    }
//
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("commentid", commentid);
//        result.put("commenttext", commenttext);
//        result.put("likes", likes);
//        result.put("likesCount", likesCount);
//        result.put("timestamp", timestamp);
//        result.put("userid", userid);
//        result.put("username", username);
//        result.put("profilepicture", profilepicture);
//
//
//        return result;
//    }
//
//    public Comment(String commentid, String commenttext, Map<String, Boolean> likes, int likesCount, Long timestamp, String userid, String username, String profilepicture) {
//        this.commentid = commentid;
//        this.commenttext = commenttext;
//        this.likes = likes;
//        this.likesCount = likesCount;
//        this.timestamp = timestamp;
//        this.userid = userid;
//        this.username = username;
//        this.profilepicture = profilepicture;
//    }
//
//    public String getCommentid() {
//        return commentid;
//    }
//
//    public String getCommenttext() {
//        return commenttext;
//    }
//
//    public Map<String, Boolean> getLikes() {
//        return likes;
//    }
//
//    public int getLikesCount() {
//        return likesCount;
//    }
//
//    public Long getTimestamp() {
//        return timestamp;
//    }
//
//    public String getUserid() {
//        return userid;
//    }
//
//    public void setCommentid(String commentid) {
//        this.commentid = commentid;
//    }
//
//    public void setCommenttext(String commenttext) {
//        this.commenttext = commenttext;
//    }
//
//    public void setLikes(Map<String, Boolean> likes) {
//        this.likes = likes;
//    }
//
//    public void setLikesCount(int likesCount) {
//        this.likesCount = likesCount;
//    }
//
//    public void setTimestamp(Long timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public void setUserid(String userid) {
//        this.userid = userid;
//    }
//
//    @Override
//    public String toString() {
//        return "Comment{" +
//                "commentid='" + commentid + '\'' +
//                ", commenttext='" + commenttext + '\'' +
//                ", likes=" + likes +
//                ", likesCount=" + likesCount +
//                ", timestamp=" + timestamp +
//                ", userid='" + userid + '\'' +
//                '}';
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getProfilepicture() {
//        return profilepicture;
//    }
//
//    public void setProfilepicture(String profilepicture) {
//        this.profilepicture = profilepicture;
//    }
//}
