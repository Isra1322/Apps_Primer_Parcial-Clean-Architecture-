package fisei.uta.edu.ec.doodlz.application.usecases;

import android.graphics.Bitmap;

import fisei.uta.edu.ec.doodlz.application.contracts.services.ImagePrintService;

/**
 * Use case to export/print a drawing as a PDF document.
 */
public final class PrintDrawingUseCase {
    private final ImagePrintService printService;

    public PrintDrawingUseCase(ImagePrintService printService) {
        this.printService = printService;
    }

    public boolean execute(String jobName, Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        return printService.printImage(jobName, bitmap);
    }
}
