package fisei.uta.edu.ec.doodlz.composition.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import fisei.uta.edu.ec.doodlz.application.contracts.repositories.DrawingSettingsRepository;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImagePrintService;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImageStorageService;
import fisei.uta.edu.ec.doodlz.application.usecases.DetectShakeUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.GetDrawingSettingsUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.PrintDrawingUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.SaveDrawingUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateColorUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateLineWidthUseCase;

@Module
@InstallIn(SingletonComponent.class)
public final class UseCaseModule {
    private UseCaseModule() {
    }

    @Provides
    public static GetDrawingSettingsUseCase provideGetDrawingSettingsUseCase(
            DrawingSettingsRepository repository) {
        return new GetDrawingSettingsUseCase(repository);
    }

    @Provides
    public static UpdateColorUseCase provideUpdateColorUseCase(
            DrawingSettingsRepository repository) {
        return new UpdateColorUseCase(repository);
    }

    @Provides
    public static UpdateLineWidthUseCase provideUpdateLineWidthUseCase(
            DrawingSettingsRepository repository) {
        return new UpdateLineWidthUseCase(repository);
    }

    @Provides
    public static SaveDrawingUseCase provideSaveDrawingUseCase(
            ImageStorageService service) {
        return new SaveDrawingUseCase(service);
    }

    @Provides
    public static PrintDrawingUseCase providePrintDrawingUseCase(
            ImagePrintService service) {
        return new PrintDrawingUseCase(service);
    }

    @Provides
    public static DetectShakeUseCase provideDetectShakeUseCase() {
        return new DetectShakeUseCase();
    }
}
