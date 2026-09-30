package fisei.uta.edu.ec.doodlz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;

import org.junit.Test;

import fisei.uta.edu.ec.doodlz.application.contracts.repositories.DrawingSettingsRepository;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImagePrintService;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImageStorageService;
import fisei.uta.edu.ec.doodlz.application.usecases.DetectShakeUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.GetDrawingSettingsUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.PrintDrawingUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.SaveDrawingUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateColorUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateLineWidthUseCase;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingColor;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingSettings;
import fisei.uta.edu.ec.doodlz.domain.entities.LineWidth;

public class UseCaseTest {

    @Test
    public void drawingColorCalculatesArgbAndClampsComponents() {
        DrawingColor color = DrawingColor.fromRgba(255, 100, 150, 200);
        assertEquals(255, color.getAlpha());
        assertEquals(100, color.getRed());
        assertEquals(150, color.getGreen());
        assertEquals(200, color.getBlue());

        int expectedArgb = (255 << 24) | (100 << 16) | (150 << 8) | 200;
        assertEquals(expectedArgb, color.getArgb());

        DrawingColor clamped = DrawingColor.fromRgba(300, -20, 500, 128);
        assertEquals(255, clamped.getAlpha());
        assertEquals(0, clamped.getRed());
        assertEquals(255, clamped.getGreen());
        assertEquals(128, clamped.getBlue());
    }

    @Test
    public void lineWidthEnforcesBounds() {
        LineWidth widthValid = LineWidth.of(15);
        assertEquals(15, widthValid.getValue());

        LineWidth widthTooLow = LineWidth.of(0);
        assertEquals(LineWidth.MIN_WIDTH, widthTooLow.getValue());

        LineWidth widthTooHigh = LineWidth.of(100);
        assertEquals(LineWidth.MAX_WIDTH, widthTooHigh.getValue());
    }

    @Test
    public void getAndModifyDrawingSettingsDelegatesToRepository() {
        FakeDrawingSettingsRepository repository = new FakeDrawingSettingsRepository();
        GetDrawingSettingsUseCase getUseCase = new GetDrawingSettingsUseCase(repository);
        UpdateColorUseCase updateColorUseCase = new UpdateColorUseCase(repository);
        UpdateLineWidthUseCase updateWidthUseCase = new UpdateLineWidthUseCase(repository);

        DrawingSettings initial = getUseCase.execute();
        assertEquals(DrawingColor.defaultBlack(), initial.getColor());
        assertEquals(LineWidth.DEFAULT_WIDTH, initial.getLineWidth().getValue());

        DrawingColor newColor = DrawingColor.fromRgba(255, 255, 0, 0);
        updateColorUseCase.execute(newColor);
        assertEquals(newColor, repository.getSettings().getColor());

        LineWidth newWidth = LineWidth.of(20);
        updateWidthUseCase.execute(newWidth);
        assertEquals(newWidth, repository.getSettings().getLineWidth());
    }

    @Test
    public void detectShakeUseCaseTriggersOnExceedingThreshold() {
        DetectShakeUseCase shakeUseCase = new DetectShakeUseCase(4.0f);

        // Gentle movement should not trigger shake
        boolean gentle = shakeUseCase.evaluate(0f, 9.8f, 0f);
        assertFalse(gentle);

        // Rapid acceleration change exceeding threshold triggers shake
        boolean shake = shakeUseCase.evaluate(30f, 30f, 30f);
        assertTrue(shake);

        // After triggering, acceleration resets to 0
        assertEquals(0.0f, shakeUseCase.getAcceleration(), 0.001f);
    }

    @Test
    public void saveDrawingUseCaseReturnsFalseOnNullBitmap() {
        FakeImageStorageService storageService = new FakeImageStorageService();
        SaveDrawingUseCase saveUseCase = new SaveDrawingUseCase(storageService);

        boolean result = saveUseCase.execute(null, "test.jpg");
        assertFalse(result);
    }

    @Test
    public void printDrawingUseCaseReturnsFalseOnNullBitmap() {
        FakeImagePrintService printService = new FakeImagePrintService();
        PrintDrawingUseCase printUseCase = new PrintDrawingUseCase(printService);

        boolean result = printUseCase.execute("test_job", null);
        assertFalse(result);
    }

    private static final class FakeDrawingSettingsRepository implements DrawingSettingsRepository {
        private DrawingSettings settings = DrawingSettings.createDefault();

        @Override
        public DrawingSettings getSettings() {
            return settings;
        }

        @Override
        public void updateColor(DrawingColor color) {
            this.settings = settings.withColor(color);
        }

        @Override
        public void updateLineWidth(LineWidth lineWidth) {
            this.settings = settings.withLineWidth(lineWidth);
        }
    }

    private static final class FakeImageStorageService implements ImageStorageService {
        private Bitmap lastSavedBitmap;
        private String lastDisplayName;

        @Override
        public boolean saveImage(Bitmap bitmap, String displayName) {
            this.lastSavedBitmap = bitmap;
            this.lastDisplayName = displayName;
            return true;
        }
    }

    private static final class FakeImagePrintService implements ImagePrintService {
        private String lastJobName;
        private Bitmap lastPrintedBitmap;

        @Override
        public boolean printImage(String jobName, Bitmap bitmap) {
            this.lastJobName = jobName;
            this.lastPrintedBitmap = bitmap;
            return bitmap != null;
        }
    }
}
