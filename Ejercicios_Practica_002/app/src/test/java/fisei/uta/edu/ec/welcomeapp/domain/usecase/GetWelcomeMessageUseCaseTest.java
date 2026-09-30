package fisei.uta.edu.ec.welcomeapp.domain.usecase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;
import fisei.uta.edu.ec.welcomeapp.domain.repository.WelcomeRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link GetWelcomeMessageUseCase} (Capa Domain).
 * Verifica que las reglas del caso de uso se cumplan de manera aislada con Mockito.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetWelcomeMessageUseCaseTest {

    @Mock
    private WelcomeRepository welcomeRepository;

    private GetWelcomeMessageUseCase getWelcomeMessageUseCase;

    @Before
    public void setUp() {
        getWelcomeMessageUseCase = new GetWelcomeMessageUseCase(welcomeRepository);
    }

    @Test
    public void execute_returnsMessageFromRepository_whenRepositoryProvidesData() {
        // Arrange
        WelcomeMessage expectedMessage = new WelcomeMessage("Test Welcome to Android!");
        when(welcomeRepository.getWelcomeMessage()).thenReturn(expectedMessage);

        // Act
        WelcomeMessage actualMessage = getWelcomeMessageUseCase.execute();

        // Assert
        assertNotNull(actualMessage);
        assertEquals(expectedMessage.getMessage(), actualMessage.getMessage());
        verify(welcomeRepository).getWelcomeMessage();
    }

    @Test
    public void execute_returnsDefaultFallbackMessage_whenRepositoryReturnsNull() {
        // Arrange
        when(welcomeRepository.getWelcomeMessage()).thenReturn(null);

        // Act
        WelcomeMessage fallbackMessage = getWelcomeMessageUseCase.execute();

        // Assert
        assertNotNull(fallbackMessage);
        assertEquals("Welcome to Android!", fallbackMessage.getMessage());
        verify(welcomeRepository).getWelcomeMessage();
    }
}
