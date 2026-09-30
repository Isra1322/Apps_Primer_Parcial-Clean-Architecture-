package fisei.uta.edu.ec.welcomeapp.di;

import fisei.uta.edu.ec.welcomeapp.data.datasource.MockWelcomeDataSource;
import fisei.uta.edu.ec.welcomeapp.data.datasource.WelcomeDataSource;
import fisei.uta.edu.ec.welcomeapp.data.repository.WelcomeRepositoryImpl;
import fisei.uta.edu.ec.welcomeapp.domain.repository.WelcomeRepository;
import fisei.uta.edu.ec.welcomeapp.domain.usecase.GetWelcomeMessageUseCase;
import fisei.uta.edu.ec.welcomeapp.presentation.viewmodel.MainViewModelFactory;

/**
 * Contenedor de Inyección de Dependencias manual (Dependency Inversion Principle - DIP).
 * Centraliza la instanciación y ensamblado de los componentes de las diferentes capas
 * (DataSource -> Repository -> UseCase -> ViewModelFactory).
 */
public final class DependencyInjector {

    private DependencyInjector() {
        // Constructor privado para evitar instanciación
    }

    public static WelcomeDataSource provideWelcomeDataSource() {
        return new MockWelcomeDataSource();
    }

    public static WelcomeRepository provideWelcomeRepository() {
        return new WelcomeRepositoryImpl(provideWelcomeDataSource());
    }

    public static GetWelcomeMessageUseCase provideGetWelcomeMessageUseCase() {
        return new GetWelcomeMessageUseCase(provideWelcomeRepository());
    }

    public static MainViewModelFactory provideMainViewModelFactory() {
        return new MainViewModelFactory(provideGetWelcomeMessageUseCase());
    }
}
