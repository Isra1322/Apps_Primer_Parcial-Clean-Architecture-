package fisei.uta.edu.ec.welcomeapp.presentation.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import fisei.uta.edu.ec.welcomeapp.databinding.ActivityMainBinding;
import fisei.uta.edu.ec.welcomeapp.di.DependencyInjector;
import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;
import fisei.uta.edu.ec.welcomeapp.presentation.viewmodel.MainViewModel;
import fisei.uta.edu.ec.welcomeapp.presentation.viewmodel.MainViewModelFactory;

/**
 * Vista principal de la aplicación (capa Presentation).
 * Cumple con el principio de Responsabilidad Única (SRP): su única responsabilidad
 * es inflar y vincular la vista mediante ViewBinding, escuchar cambios del ViewModel
 * y renderizar el estado del mensaje de bienvenida en la UI original.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Inicialización de ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Manejo de Insets (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.welcomeLinearLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inyección de dependencias y obtención del ViewModel mediante Factory
        MainViewModelFactory factory = DependencyInjector.provideMainViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        // Observación reactiva de los estados
        setupObservers();

        // Carga inicial del mensaje de bienvenida
        viewModel.loadWelcomeMessage();
    }

    private void setupObservers() {
        viewModel.getWelcomeMessage().observe(this, this::renderWelcomeMessage);
    }

    private void renderWelcomeMessage(WelcomeMessage message) {
        if (message != null) {
            binding.welcomeTextView.setText(message.getMessage());
        }
    }
}
