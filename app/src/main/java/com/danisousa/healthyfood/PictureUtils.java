package com.danisousa.healthyfood;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PictureUtils {

    public static Bitmap createThumbnail(File photo) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        bitmap = BitmapFactory.decodeFile(photo.getPath(), options);
        return PictureUtils.RotateBitmap(photo, bitmap);
    }

    public static Bitmap RotateBitmap(File photo, Bitmap source) {
        float rotate = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photo.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return source;
        }
    }

    public static byte[] FileToBytes(File file) throws IOException {
        InputStream is = null;
        DataInputStream dis = null;
        byte[] buffer = null;

        try {
            is = new FileInputStream(file);
            dis = new DataInputStream(is);

            int length = dis.available();
            buffer = new byte[length];

            // read the full data into the buffer
            dis.readFully(buffer);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            // releases all system resources from the streams
            if(is != null)
                is.close();
            if(dis != null)
                dis.close();
        }
        return buffer;
    }

    public static void compressBitmap(File file) throws IOException {
        Bitmap bmp = PictureUtils.createThumbnail(file);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        try(OutputStream outputStream = new FileOutputStream(file)) {
            bytes.writeTo(outputStream);
        }
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();

        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}
