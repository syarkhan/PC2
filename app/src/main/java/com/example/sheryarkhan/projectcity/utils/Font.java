package com.example.sheryarkhan.projectcity.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Sheryar Khan on 10/21/2017.
 */

public class Font {

    private Typeface RobotoFont;
    private String FontType;

    public Font() {

    }

    public Font(String fontType, Context context) {

        switch (fontType){
            case "Bold" : RobotoFont = Typeface.createFromAsset( context.getAssets(), "fonts/Roboto-Bold.ttf");
                break;
            case "Thin" : RobotoFont = Typeface.createFromAsset( context.getAssets(), "fonts/Roboto-Thin.ttf");
                break;
            case "Light" : RobotoFont = Typeface.createFromAsset( context.getAssets(), "fonts/Roboto-Light.ttf");
                break;
            case "Regular" : RobotoFont = Typeface.createFromAsset( context.getAssets(), "fonts/Roboto-Regular.ttf");
                break;
        }
        FontType = fontType;
    }


    public Typeface getRobotoFont() {
        return RobotoFont;
    }

    public void setRoboto(Typeface robotoFont) {
        RobotoFont = robotoFont;
    }


    public String getFontType() {
        return FontType;
    }

    public void setFontType(String fontType) {
        FontType = fontType;
    }
}
