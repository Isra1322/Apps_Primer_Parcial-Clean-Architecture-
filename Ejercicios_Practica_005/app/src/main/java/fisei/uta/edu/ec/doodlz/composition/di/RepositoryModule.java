package fisei.uta.edu.ec.doodlz.composition.di;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import fisei.uta.edu.ec.doodlz.application.contracts.repositories.DrawingSettingsRepository;
import fisei.uta.edu.ec.doodlz.infrastructure.repositories.InMemoryDrawingSettingsRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {
    @Binds
    @Singleton
    public abstract DrawingSettingsRepository bindDrawingSettingsRepository(
            InMemoryDrawingSettingsRepository repository);
}
