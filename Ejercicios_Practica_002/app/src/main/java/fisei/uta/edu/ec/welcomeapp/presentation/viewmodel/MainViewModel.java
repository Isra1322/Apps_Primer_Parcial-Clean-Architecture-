package fisei.uta.edu.ec.welcomeapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;
import fisei.uta.edu.ec.welcomeapp.domain.usecase.GetWelcomeMessageUseCase;

import java.util.Objects;

/**
 * ViewModel para la pantalla principal (MVVM + Clean Architecture).
 * Aplica el principio de Responsabilidad Única (SRP): gestiona y emite el estado
 * de la interfaz de usuario sin mantener referencias directas a vistas o contexto de Android.
 */
public class MainViewModel extends ViewModel {

    private final GetWelcomeMessageUseCase getWelcomeMessageUseCase;

    private final MutableLiveData<WelcomeMessage> welcomeMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    public MainViewModel(GetWelcomeMessageUseCase getWelcomeMessageUseCase) {
        this.getWelcomeMessageUseCase = Objects.requireNonNull(getWelcomeMessageUseCase, "useCase cannot be null");
    }

    /**
     * Expone el mensaje de bienvenida de forma inmutable para ser observado por la vista.
     */
    public LiveData<WelcomeMessage> getWelcomeMessage() {
        return welcomeMessageLiveData;
    }

    /**
     * Expone el estado de carga para la vista.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    /**
     * Ejecuta el caso de uso y actualiza el estado de la UI.
     */
    public void loadWelcomeMessage() {
        isLoadingLiveData.setValue(true);
        WelcomeMessage message = getWelcomeMessageUseCase.execute();
        welcomeMessageLiveData.setValue(message);
        isLoadingLiveData.setValue(false);
    }
}
