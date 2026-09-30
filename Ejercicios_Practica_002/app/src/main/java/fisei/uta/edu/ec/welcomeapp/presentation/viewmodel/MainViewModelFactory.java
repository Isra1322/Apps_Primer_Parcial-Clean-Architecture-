package fisei.uta.edu.ec.welcomeapp.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import fisei.uta.edu.ec.welcomeapp.domain.usecase.GetWelcomeMessageUseCase;

import java.util.Objects;

/**
 * Fábrica para la creación de {@link MainViewModel} con inyección de dependencias.
 * Cumple con el principio de Inversión de Dependencias (DIP).
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final GetWelcomeMessageUseCase getWelcomeMessageUseCase;

    public MainViewModelFactory(GetWelcomeMessageUseCase getWelcomeMessageUseCase) {
        this.getWelcomeMessageUseCase = Objects.requireNonNull(getWelcomeMessageUseCase, "useCase cannot be null");
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(getWelcomeMessageUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
