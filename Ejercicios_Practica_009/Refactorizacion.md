Refactorización – AddressBook
Principios SOLID & Clean Architecture

---

¿Qué se cambió?

El proyecto original tenía toda la lógica mezclada dentro de los Fragments (UI + datos + lógica de negocio en un solo lugar).

Se reorganizó aplicando Clean Architectur con tres capas bien definidas:

```
presentation/   ->  Fragments + ViewModels (solo UI)
domain/         ->  Entidades + Contratos (lógica de negocio)
data/           ->  Implementación de repositorios (acceso a datos)
```

---

Cambios por capa

Capa Domain

| Archivo | Qué es |
|---|---|
| entity/Contact.java | Entidad pura de negocio (sin Android) |
| repository/ContactRepository.java | Interfaz que define las operaciones CRUD |

Capa Data

| Archivo | Qué es |
|---|---|
| repository/ContactRepositoryImpl.java | Implementación concreta del repositorio usando ContentProvider |

Capa Presentation

| Archivo | Qué es |
|---|---|
| viewmodel/ContactsViewModel.java | Lógica de lista de contactos |
| viewmodel/AddEditViewModel.java | Lógica de creación/edición |
| viewmodel/DetailViewModel.java | Lógica de detalle y eliminación |

---

Principios SOLID aplicados

| Principio | Cómo se aplicó |
|---|---|
| S – Single Responsibility | Cada clase tiene una sola responsabilidad: Fragment = UI, ViewModel = lógica, Repository = datos |
| O – Open/Closed | Se puede cambiar la fuente de datos sin tocar la lógica de negocio |
| L – Liskov Substitution | ContactRepositoryImpl sustituye a ContactRepository sin romper nada |
| I – Interface Segregation | La interfaz ContactRepository solo expone lo necesario |
| D – Dependency Inversion | Los ViewModels dependen de la interfaz, no de la implementación concreta |

---

Antes vs Después

```
ANTES                          DESPUES
ContactsFragment.java          presentation/viewmodel/ContactsViewModel.java
  - Cursor directo              domain/repository/ContactRepository.java (interfaz)
  - Lógica de negocio           domain/entity/Contact.java
  - Manipulación de UI          data/repository/ContactRepositoryImpl.java
```

---

Beneficios obtenidos

- Testeable – Los ViewModels y el dominio se pueden probar sin Android
- Mantenible – Cambiar la BD solo afecta la capa data
- Escalable – Agregar features no rompe lo existente
- Legible – Cada archivo tiene un propósito claro

