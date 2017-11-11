package data;

/**
 * Created by Sheryar Khan on 11/8/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("DateOfJoining")
    @Expose
    private String dateOfJoining;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Town")
    @Expose
    private String town;
    @SerializedName("ProfilePicture")
    @Expose
    private String profilePicture;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Bio")
    @Expose
    private String bio;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("TotalNumberOfPosts")
    @Expose
    private String totalNumberOfPosts;
    @SerializedName("Points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTotalNumberOfPosts() {
        return totalNumberOfPosts;
    }

    public void setTotalNumberOfPosts(String totalNumberOfPosts) {
        this.totalNumberOfPosts = totalNumberOfPosts;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfjoining) {
        this.dateOfJoining = dateOfjoining;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", dateOfJoining='" + dateOfJoining + '\'' +
                ", email='" + email + '\'' +
                ", town='" + town + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", status='" + status + '\'' +
                ", bio='" + bio + '\'' +
                ", city='" + city + '\'' +
                ", numberOfPosts='" + totalNumberOfPosts + '\'' +
                '}';
    }
}
