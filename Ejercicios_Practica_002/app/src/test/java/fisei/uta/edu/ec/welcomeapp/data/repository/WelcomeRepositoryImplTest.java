package fisei.uta.edu.ec.welcomeapp.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fisei.uta.edu.ec.welcomeapp.data.datasource.WelcomeDataSource;
import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link WelcomeRepositoryImpl} (Capa Data).
 * Valida la delegación y obtención de datos desde la abstracción {@link WelcomeDataSource}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WelcomeRepositoryImplTest {

    @Mock
    private WelcomeDataSource welcomeDataSource;

    private WelcomeRepositoryImpl welcomeRepository;

    @Before
    public void setUp() {
        welcomeRepository = new WelcomeRepositoryImpl(welcomeDataSource);
    }

    @Test
    public void getWelcomeMessage_delegatesToDataSource_andReturnsResult() {
        // Arrange
        WelcomeMessage expected = new WelcomeMessage("Hello Data Layer Welcome");
        when(welcomeDataSource.fetchWelcomeMessage()).thenReturn(expected);

        // Act
        WelcomeMessage actual = welcomeRepository.getWelcomeMessage();

        // Assert
        assertNotNull(actual);
        assertEquals(expected.getMessage(), actual.getMessage());
        verify(welcomeDataSource).fetchWelcomeMessage();
    }
}
