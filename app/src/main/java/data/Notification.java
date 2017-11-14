package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sheryar Khan on 11/1/2017.
 */




public class Notification {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("NotificationType")
    @Expose
    private String notificationType;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("PostId")
    @Expose
    private String postId;
    @SerializedName("Read")
    @Expose
    private Boolean read;
    @SerializedName("CommentInfo")
    @Expose
    private CommentInfo commentInfo;
    @SerializedName("PostInfo")
    @Expose
    private PostInfo postInfo;
    @SerializedName("UserInfo")
    @Expose
    private UserInfo userInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public CommentInfo getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }

    public PostInfo getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(PostInfo postInfo) {
        this.postInfo = postInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class CommentInfo {

        @SerializedName("CommentText")
        @Expose
        private String commentText;
        @SerializedName("LikesCount")
        @Expose
        private Integer likesCount;

        public String getCommentText() {
            return commentText;
        }

        public void setCommentText(String commentText) {
            this.commentText = commentText;
        }

        public Integer getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(Integer likesCount) {
            this.likesCount = likesCount;
        }

    }

    public static class PostInfo {

        @SerializedName("PostText")
        @Expose
        private String postText;

        public String getPostText() {
            return postText;
        }

        public void setPostText(String postText) {
            this.postText = postText;
        }

    }

}


