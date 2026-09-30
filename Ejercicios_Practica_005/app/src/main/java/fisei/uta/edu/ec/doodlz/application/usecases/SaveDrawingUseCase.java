package fisei.uta.edu.ec.doodlz.application.usecases;

import android.graphics.Bitmap;

import fisei.uta.edu.ec.doodlz.application.contracts.services.ImageStorageService;

/**
 * Use case to save a drawing to device storage.
 */
public final class SaveDrawingUseCase {
    private final ImageStorageService storageService;

    public SaveDrawingUseCase(ImageStorageService storageService) {
        this.storageService = storageService;
    }

    public boolean execute(Bitmap bitmap, String displayName) {
        if (bitmap == null) {
            return false;
        }
        return storageService.saveImage(bitmap, displayName);
    }
}
