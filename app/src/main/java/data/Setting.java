package data;

/**
 * Created by Sheryar Khan on 11/1/2017.
 */

public class Setting {


    private String imgViewSettings;
    private String txtSettings;

    public Setting(String imgViewSettings, String txtSettings) {
        this.imgViewSettings = imgViewSettings;
        this.txtSettings = txtSettings;
    }

    public Setting() {
    }

    public String getImgViewSettings() {
        return imgViewSettings;
    }

    public void setImgViewSettings(String imgViewSettings) {
        this.imgViewSettings = imgViewSettings;
    }

    public String getTxtSettings() {
        return txtSettings;
    }

    public void setTxtSettings(String txtSettings) {
        this.txtSettings = txtSettings;
    }



}
