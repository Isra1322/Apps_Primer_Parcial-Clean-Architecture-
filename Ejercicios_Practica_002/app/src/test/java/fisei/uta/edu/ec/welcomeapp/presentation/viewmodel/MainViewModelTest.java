package fisei.uta.edu.ec.welcomeapp.presentation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;
import fisei.uta.edu.ec.welcomeapp.domain.usecase.GetWelcomeMessageUseCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link MainViewModel} (Capa Presentation).
 * Usa {@link InstantTaskExecutorRule} para asegurar la ejecución síncrona de LiveData en el hilo de pruebas.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private GetWelcomeMessageUseCase getWelcomeMessageUseCase;

    private MainViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new MainViewModel(getWelcomeMessageUseCase);
    }

    @Test
    public void loadWelcomeMessage_executesUseCase_andUpdatesLiveData() {
        // Arrange
        WelcomeMessage message = new WelcomeMessage("Welcome to Android!");
        when(getWelcomeMessageUseCase.execute()).thenReturn(message);

        List<WelcomeMessage> observedMessages = new ArrayList<>();
        List<Boolean> observedLoadingStates = new ArrayList<>();

        Observer<WelcomeMessage> messageObserver = observedMessages::add;
        Observer<Boolean> loadingObserver = observedLoadingStates::add;

        viewModel.getWelcomeMessage().observeForever(messageObserver);
        viewModel.getIsLoading().observeForever(loadingObserver);

        try {
            // Act
            viewModel.loadWelcomeMessage();

            // Assert
            verify(getWelcomeMessageUseCase).execute();

            // Verificar LiveData del mensaje
            assertNotNull(viewModel.getWelcomeMessage().getValue());
            assertEquals("Welcome to Android!", viewModel.getWelcomeMessage().getValue().getMessage());
            assertEquals(1, observedMessages.size());
            assertEquals(message, observedMessages.get(0));

            // Verificar transiciones del estado de carga (false -> true -> false)
            assertTrue(observedLoadingStates.contains(true));
            assertFalse(viewModel.getIsLoading().getValue());
        } finally {
            viewModel.getWelcomeMessage().removeObserver(messageObserver);
            viewModel.getIsLoading().removeObserver(loadingObserver);
        }
    }
}
