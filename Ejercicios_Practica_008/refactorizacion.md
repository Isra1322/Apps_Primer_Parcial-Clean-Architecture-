1. Estructura de paquetes antes y después

Antes (estructura monolítica)

twittersearches/
├── MainActivity.java          ← Toda la lógica concentrada aquí
├── SearchesAdapter.java
└── ItemDivider.java

Toda la responsabilidad persistencia, lógica de negocio y presentación vivía dentro de `MainActivity.java`. No existía separación de capas ni contratos abstractos.

Después (Clean Architecture por capas)

twittersearches/
├── data/
│   └── repository/
│       └── SearchRepositoryImpl.java   ← Implementación concreta (SharedPreferences)
├── domain/
│   ├── model/
│   │   └── Search.java                 ← Entidad de dominio inmutable
│   └── repository/
│       └── SearchRepository.java       ← Contrato abstracto (interfaz)
├── ui/
│   ├── MainActivity.java               ← Solo presentación y coordinación de vistas
│   ├── SearchesAdapter.java            ← Adaptador del RecyclerView
│   └── ItemDivider.java                ← Decorador visual del RecyclerView
└── utils/
    └── UrlBuilder.java                 ← Utilidad sin estado para construir URLs


2. Cambios por principio SOLID

2.1 SRP — Principio de Responsabilidad Única
"Una clase debe tener una sola razón para cambiar."

| Clase | Antes | Después |
|---|---|---|
| `MainActivity` | Mezclaba persistencia, lógica de negocio y UI en un solo archivo | Se limita a inicializar vistas, escuchar eventos y delegar acciones al repositorio |
| `SearchesAdapter` | Manejaba clicks directamente con lógica ad-hoc | Solo enlaza datos al ViewHolder; el ViewHolder solo dibuja datos con `bind()` |
| `ItemDivider` | Estaba ubicado en el paquete raíz mezclado con otras clases | Movido al paquete `ui` donde pertenece conceptualmente |
| `UrlBuilder` | Lógica de construcción de URL duplicada en varios métodos de la Activity | Clase utilitaria final y sin estado, con un único método estático `buildSearchUrl()` |

Evidencia en código:

// MainActivity.java — ahora delega toda la persistencia:
searchRepository.saveSearch(new Search(tag, query));  // No toca SharedPreferences
searchRepository.deleteSearch(tag);
searchRepository.getQuery(tag);

2.2 OCP — Principio Abierto/Cerrado
"Las entidades deben estar abiertas para extensión, pero cerradas para modificación."

El repositorio se diseñó como una interfaz en la capa de dominio. Si en el futuro se necesita migrar de SharedPreferences a Room, a Retrofit o a cualquier otra fuente de datos, se crea una nueva implementación sin tocar ningún otro archivo del proyecto.

Evidencia en código:
// domain/repository/SearchRepository.java — contrato estable
public interface SearchRepository {
    void saveSearch(Search search);
    void deleteSearch(String tag);
    String getQuery(String tag);
    List<String> getAllTags();
}

// data/repository/SearchRepositoryImpl.java — implementación intercambiable
public class SearchRepositoryImpl implements SearchRepository {
    // Usa SharedPreferences HOY; mañana puede ser Room sin cambiar la interfaz
}


2.3 LSP — Principio de Sustitución de Liskov
"Los subtipos deben poder sustituir a sus tipos base sin alterar el comportamiento del programa."

`SearchRepositoryImpl` implementa fielmente **todos** los métodos declarados en `SearchRepository`. `MainActivity` trabaja únicamente con la referencia de tipo `SearchRepository`, de modo que en tests o en producción se puede inyectar cualquier implementación conforme sin romper la actividad.

Evidencia en código:
```java
// MainActivity.java — referencia siempre al tipo abstracto
private SearchRepository searchRepository;

// En onCreate: se asigna la implementación concreta (podría ser un mock en tests)
searchRepository = new SearchRepositoryImpl(this);


2.4 ISP — Principio de Segregación de Interfaces
"Los clientes no deben depender de interfaces que no usan."

En lugar de usar los listeners genéricos de Android (`View.OnClickListener`, `AdapterView.OnItemClickListener`), se definió una interfaz propia y específica `OnSearchInteractionListener` con exactamente las dos operaciones que la `Activity` necesita conocer sobre la lista.

Evidencia en código:
// SearchesAdapter.java — interfaz segregada con solo los métodos necesarios
public interface OnSearchInteractionListener {
    void onSearchClick(String tag);
    void onSearchLongClick(String tag);
}

// MainActivity.java — implementa únicamente esta interfaz específica
public class MainActivity extends AppCompatActivity
        implements SearchesAdapter.OnSearchInteractionListener { ... }


2.5 DIP — Principio de Inversión de Dependencias
"Los módulos de alto nivel no deben depender de módulos de bajo nivel; ambos deben depender de abstracciones."

`MainActivity` (módulo de alto nivel) **no importa** `SharedPreferences` ni conoce detalles de almacenamiento. Depende únicamente de la interfaz `SearchRepository` definida en la capa de dominio.

Evidencia en código:
// MainActivity.java — solo importa la interfaz del dominio
import fisei.uta.edu.ec.twittersearches.domain.repository.SearchRepository;
// NO importa SharedPreferences ni SearchRepositoryImpl (salvo para instanciarla una vez)

private SearchRepository searchRepository; // Tipo abstracto, no concreto


3. Cambios hacia Clean Architecture

3.1 Capa de Dominio (`domain/`)
Contiene las reglas de negocio puras, sin dependencias de Android.

Archivo | Rol | Cambio realizado
|---|---|---|
| `Search.java` | Entidad de dominio | **Creado nuevo.** Objeto de valor inmutable (`final` fields, solo getters). Reemplaza el uso de pares sueltos `(String tag, String query)` dispersos en la Activity. |
| `SearchRepository.java` | Puerto de salida | **Creado nuevo.** Define el contrato que deben cumplir las fuentes de datos. |

`Search.java` — inmutabilidad:

public class Search {
    private final String tag;    // final → inmutable
    private final String query;  // final → inmutable

    public Search(String tag, String query) {
        this.tag = tag;
        this.query = query;
    }
    // Solo getters, sin setters
}
```

3.2 Capa de Datos (`data/`)
Adaptadores de infraestructura que satisfacen los contratos del dominio.

| Archivo | Rol | Cambio realizado |
|---|---|---|
| `SearchRepositoryImpl.java` | Adaptador de datos | **Creado nuevo.** Encapsula toda la interacción con `SharedPreferences`. La lista de tags se devuelve ordenada alfabéticamente (sin distinción de mayúsculas/minúsculas). |

3.3 Capa de Presentación (`ui/`)
Solo responsable de mostrar datos y capturar eventos del usuario.

| Archivo | Cambio realizado |
|---|---|
| `MainActivity.java` | Toda la lógica de persistencia fue extraída. Ahora delega en `SearchRepository`. Se organizó en secciones claras (Lifecycle, Setup, Input helpers, Save flow, Dialog, Actions, Utility) usando separadores de comentario. |
| `SearchesAdapter.java` | Se introdujo `OnSearchInteractionListener` (ISP). El `ViewHolder` ahora expone un método `bind()` limpio. Se reemplazó `notifyDataSetChanged()` por `notifyItemInserted()` / `notifyItemRemoved()` para mejor rendimiento. |
| `ItemDivider.java` | Reubicado del paquete raíz al paquete `ui`. |

3.4 Utilidades (`utils/`)
| Archivo | Cambio realizado |
|---|---|
| `UrlBuilder.java` | **Creado nuevo.** Clase `final` no instanciable con constructor privado. Centraliza la construcción de la URL de búsqueda y elimina la duplicación que existía entre `onSearchClick()` y `shareSearch()` en la Activity. |


4. Otros principios de Clean Code aplicados

| Principio | Aplicación concreta |
|---|---|
| **DRY** (Don't Repeat Yourself) | `UrlBuilder.buildSearchUrl()` evita que la URL se construya dos veces en `MainActivity` (`onSearchClick` y `shareSearch`). |
| **Inmutabilidad** | `Search` usa campos `final` y no expone setters. |
| **Nombres significativos** | Métodos como `setupRecyclerView()`, `onSaveButtonClicked()`, `confirmDeleteSearch()` describen su intención sin necesidad de comentarios adicionales. |
| **Funciones pequeñas** | `MainActivity` fue dividida en métodos de una sola responsabilidad: `setupInputViews()`, `setupSaveButton()`, `clearInputFields()`, `hideKeyboard()`, `buildUrl()`, etc. |
| **Separación de concerns** | `ItemDivider` solo se ocupa de dibujar divisores; `SearchesAdapter` solo de enlazar datos; `MainActivity` solo de coordinar. |
| **Clase utilitaria no instanciable** | `UrlBuilder` tiene constructor privado y la clase es `final`, siguiendo el patrón de clases de utilidad estáticas. |

---

## 5. Diagrama de dependencias (Clean Architecture)

```
┌─────────────────────────────────────────────────┐
│                   ui (Presentation)             │
│  MainActivity · SearchesAdapter · ItemDivider   │
└────────────────────┬────────────────────────────┘
                     │ depende de
┌────────────────────▼────────────────────────────┐
│                domain (Business Logic)          │
│       Search (Model) · SearchRepository (Port)  │
└────────────────────▲────────────────────────────┘
                     │ implementa
┌────────────────────┴────────────────────────────┐
│                data (Infrastructure)            │
│            SearchRepositoryImpl                 │
│         (SharedPreferences adapter)             │
└─────────────────────────────────────────────────┘

utils/ → UrlBuilder  (sin dependencias de capa, solo Android SDK)
```

> **Regla de dependencia de Clean Architecture:** Las flechas apuntan siempre **hacia adentro**. El dominio no conoce ni importa nada de `data` ni de `ui`.

---

## 6. Resumen de archivos creados / modificados

| Acción | Archivo | Motivo |
|---|---|---|
| Creado | `domain/model/Search.java` | Entidad de dominio inmutable |
| Creado | `domain/repository/SearchRepository.java` | Contrato abstracto (DIP, OCP) |
| Creado | `data/repository/SearchRepositoryImpl.java` | Implementación con SharedPreferences |
| Creado | `utils/UrlBuilder.java` | Eliminación de duplicación (DRY) |
| Refactorizado | `ui/MainActivity.java` | Extracción de lógica, uso de abstracciones |
| Refactorizado | `ui/SearchesAdapter.java` | ISP + ViewHolder limpio + notificaciones granulares |
| Movido | `ui/ItemDivider.java` | Reubicado al paquete `ui` |
| Actualizado | `AndroidManifest.xml` | `android:name` actualizado a `.ui.MainActivity` |
