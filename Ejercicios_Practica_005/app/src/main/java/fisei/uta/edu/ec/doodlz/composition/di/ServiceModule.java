package fisei.uta.edu.ec.doodlz.composition.di;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImagePrintService;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImageStorageService;
import fisei.uta.edu.ec.doodlz.infrastructure.services.AndroidImagePrintService;
import fisei.uta.edu.ec.doodlz.infrastructure.services.MediaStoreImageStorageService;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ServiceModule {
    @Binds
    @Singleton
    public abstract ImageStorageService bindImageStorageService(
            MediaStoreImageStorageService service);

    @Binds
    @Singleton
    public abstract ImagePrintService bindImagePrintService(
            AndroidImagePrintService service);
}
