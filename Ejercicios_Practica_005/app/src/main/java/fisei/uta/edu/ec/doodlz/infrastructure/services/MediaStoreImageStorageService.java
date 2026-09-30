package fisei.uta.edu.ec.doodlz.infrastructure.services;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImageStorageService;

/**
 * Concrete implementation of ImageStorageService using Android's MediaStore.
 */
@Singleton
public final class MediaStoreImageStorageService implements ImageStorageService {
    private static final String TAG = "MediaStoreImageStorage";
    private final Context context;

    @Inject
    public MediaStoreImageStorageService(@ApplicationContext Context context) {
        this.context = context;
    }

    @Override
    public boolean saveImage(Bitmap bitmap, String displayName) {
        if (bitmap == null) {
            return false;
        }

        String fileName = (displayName != null && !displayName.trim().isEmpty())
                ? displayName
                : "Doodlz_" + System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Doodlz");
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream out = resolver.openOutputStream(uri)) {
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error saving bitmap to MediaStore: " + e.getMessage(), e);
            }
        }

        return false;
    }
}
