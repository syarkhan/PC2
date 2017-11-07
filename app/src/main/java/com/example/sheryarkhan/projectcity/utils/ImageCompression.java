package com.example.sheryarkhan.projectcity.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Sheryar Khan on 10/1/2017.
 */

public class ImageCompression {

    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final String TAG = "ImagePicker";
    private static final String TEMP_IMAGE_NAME = "tempImage";

    private static Bitmap actuallyUsableBitmap = null;

    public static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;

    public static Bitmap getImageFromResult(Context context,
                                            String filePath) {
        //Log.d(TAG, "getImageFromResult, resultCode: " + resultCode);
        Bitmap bm;
        File imageFile = getTempFile(context);
            Uri selectedImage=null;
            boolean isCamera = false;
            //String filePath=null;
            if (isCamera) {     /** CAMERA **/
                selectedImage = Uri.fromFile(imageFile);
            } else {            /** ALBUM **/

                //HashMap<Integer, String> hashMap = (HashMap<Integer, String>) imageReturnedIntent.getSerializableExtra("hashMap");
                selectedImage = Uri.parse(filePath);
                //filePath = hashMap.get(0);


                        //Uri.fromFile(new File(hashMap.get(0))); // imageReturnedIntent.getData()
            }
            Log.d(TAG, "selectedImage: " + selectedImage);

            bm = getImageResized(context, selectedImage);
            int rotation = getRotation(context, selectedImage, isCamera);
            //bm = rotate(bm, rotation);
//            long l = bm.getByteCount();
//            long b = bm.getRowBytes()*bm.getHeight();
//
//            long w = bm.getWidth();
//            long h = bm.getHeight();
//            int i =0;
//            i++;

        return bm;
    }


    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 6;
        options.inJustDecodeBounds = true;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(Uri.fromFile(new File(theUri.toString())), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);


        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        if (options.outHeight > options.outWidth) {
            options.inSampleSize = calculateInSampleSize(options, 640, 960);
        } else {
            options.inSampleSize = calculateInSampleSize(options, 960, 640);
        }
        //options.inSampleSize=7;
//        int scale=1;
//        final int REQUIRED_SIZE=75;
//        while(options.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                options.outHeight / scale / 2 >= REQUIRED_SIZE) {
//            scale *= 2;
//        }

        options.inJustDecodeBounds = false;


//        Bitmap actuallyUsableBitmap2 = BitmapFactory.decodeFileDescriptor(
//                fileDescriptor.getFileDescriptor(), null, options);

        //Bitmap actuallyUsableBitmap2 = BitmapFactory.decodeFile(theUri,options);

        //Log.d(TAG, options.inSampleSize + " sample method bitmap ... " +
          //      actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

        //selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

        return BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm = null;

//        AssetFileDescriptor fileDescriptor = null;
//        try {
//            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(selectedImage, "r");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap m = BitmapFactory.decodeFileDescriptor(
//                fileDescriptor.getFileDescriptor(), null, options);
//        long l = m.getByteCount();
//        long k = m.getRowBytes()*m.getHeight()/1024;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        int width;
//        m.recycle();
//        m=null;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            Log.d(TAG, "resizer: new bitmap width = " + bm.getWidth());
            i++;
            width = bm.getWidth();
        } while (width < minWidthQuality && i < sampleSizes.length);
        return bm;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to
            // the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation;
        if (isCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }
        Log.d(TAG, "Image rotation: " + rotation);
        return rotation;
    }

    private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {

            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private static int getRotationFromGallery(Context context, Uri imageUri) {

        String[] columns = {MediaStore.Images.Media.SIZE,MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
        if (cursor == null) return 0;

        cursor.moveToFirst();

        int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
        int rotation = cursor.getInt(orientationColumnIndex);

        int index = cursor.getColumnIndex(columns[1]);
        String j = cursor.getString(index);
        cursor.close();
        return rotation;

    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        }
        return bm;
    }


    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

}
