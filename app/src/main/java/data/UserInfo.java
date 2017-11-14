package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sheryar Khan on 11/14/2017.
 */

public class UserInfo {

    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("ProfilePicture")
    @Expose
    private String profilePicture;

    public UserInfo(String username, String profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
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

}
