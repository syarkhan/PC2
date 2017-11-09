package data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Posts {

    @SerializedName("_id")
    @Expose
    private String postId;
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("PostText")
    @Expose
    private String postText;
    @SerializedName("Location")
    @Expose
    private String location;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("Town")
    @Expose
    private String town;
    @SerializedName("ContentPost")
    @Expose
    private List<String> contentPost = null;

    @SerializedName("Username")
    @Expose
    private String username;

    @SerializedName("ProfilePicture")
    @Expose
    private String profilePicture;

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

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    @SerializedName("LikesCount")
    @Expose
    private int likesCount;

    @SerializedName("Likes")
    @Expose
    private List<String> likes = null;

    @SerializedName("CommentsCount")
    @Expose
    private int commentsCount;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public List<String> getContentPost() {
        return contentPost;
    }

    public void setContentPost(List<String> contentPost) {
        this.contentPost = contentPost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "postid='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", postText='" + postText + '\'' +
                ", location='" + location + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", town='" + town + '\'' +
                ", contentPost=" + contentPost +
                '}';
    }
}
