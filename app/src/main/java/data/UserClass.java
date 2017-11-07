package data;

import java.security.Timestamp;
import java.util.Map;

/**
 * Created by ibrahim-01 on 7/17/2017. s
 */

public class UserClass {

    private long dateofjoining;
    private String profilepicture;
    private String bio;
    private boolean status;
    private String primarylocation;
    private String city;
    private String town;
    private Map<String,Boolean> secondarylocations;
    private String username;
    private String email;

    public UserClass(long dateofjoining, String profilepicture, String bio, boolean status, String primarylocation, String city, String town, Map<String, Boolean> secondarylocations, String username, String email) {
        this.dateofjoining = dateofjoining;
        this.profilepicture = profilepicture;
        this.bio = bio;
        this.status = status;
        this.primarylocation = primarylocation;
        this.city = city;
        this.town = town;
        this.secondarylocations = secondarylocations;
        this.username = username;
        this.email = email;
    }

    public UserClass() {
    }

    public long getDateofjoining() {
        return dateofjoining;
    }

    public void setDateofjoining(long dateofjoining) {
        this.dateofjoining = dateofjoining;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPrimarylocation() {
        return primarylocation;
    }

    public void setPrimarylocation(String primarylocation) {
        this.primarylocation = primarylocation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSuburb() {
        return town;
    }

    public void setSuburb(String town) {
        this.town = town;
    }

    public Map<String, Boolean> getSecondarylocations() {
        return secondarylocations;
    }

    public void setSecondarylocations(Map<String, Boolean> secondarylocations) {
        this.secondarylocations = secondarylocations;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserClass{" +
                "dateofjoining=" + dateofjoining +
                ", profilepicture='" + profilepicture + '\'' +
                ", bio='" + bio + '\'' +
                ", status=" + status +
                ", primarylocation='" + primarylocation + '\'' +
                ", city='" + city + '\'' +
                ", town='" + town + '\'' +
                ", secondarylocations=" + secondarylocations +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
