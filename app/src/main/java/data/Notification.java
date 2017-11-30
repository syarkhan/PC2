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
    private Long timestamp;
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

    public TownNotification getTownNotification() {
        return townNotification;
    }

    public void setTownNotification(TownNotification townNotification) {
        this.townNotification = townNotification;
    }

    @SerializedName("TownNotification")
    @Expose
    private TownNotification townNotification;

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
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

        @SerializedName("_id")
        @Expose
        private String commentId;

        @SerializedName("CommentText")
        @Expose
        private String commentText;
        @SerializedName("LikesCount")
        @Expose
        private Integer likesCount;


        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

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

    public class TownNotification {
        @SerializedName("_id")
        @Expose
        private String TownNotificationId;

        @SerializedName("NotificationId")
        @Expose
        private String notificationId;
        @SerializedName("ToUserId")
        @Expose
        private String toUserId;

        @SerializedName("Read")
        @Expose
        private boolean read;

        public String getTownNotificationId() {
            return TownNotificationId;
        }

        public void setTownNotificationId(String townNotificationId) {
            TownNotificationId = townNotificationId;
        }

        public String getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(String notificationId) {
            this.notificationId = notificationId;
        }

        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }
    }
}


