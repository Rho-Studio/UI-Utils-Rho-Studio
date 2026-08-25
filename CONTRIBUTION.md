# Architecture & Contribution Guide: Rho Studio UI

## 1. Project Vision & Architecture
Rho Studio UI is a modern Android application built with **Jetpack Compose** and **MVVM** following a **Single-Activity Architecture**. 

To achieve scalability, implement **Domain-Driven Design (DDD)** and **Clean Architecture** principles. This ensures a clear separation of concerns, framework independence, and high testability.

---

## 2. Feature Implementation Workflow (Step-by-Step)

When adding a new feature (e.g., "Settings", "Profile"), follow this **Inside-Out** sequence to ensure architectural integrity:

### Step 1: Domain Layer (The Logic)
1.  **Define Models**: Create pure Kotlin data classes in `core:domain` (e.g., `Settings.kt`).
2.  **Define Repository Interface**: Add an interface in `core:domain` describing the data contract.
3.  **Create Interactor (UseCase)**: Implement the business logic by inheriting from `BaseUseCase<P, R>`.
    *   **P (Parameters)**: Use a `data class` for multiple inputs or `Unit` for none.
    *   **R (Return)**: The raw data type (Dagger/BaseUseCase will wrap it in `Result<R>`).
    *   **Rule**: Must be a pure Kotlin class without Android dependencies.
    *   **Rule**: Must be testable with MockK/JUnit 5.

Example:
```kotlin
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseUseCase<Credentials, User>() {
    override suspend fun execute(parameters: Credentials): User {
        return repository.login(parameters)
    }
}
```

### Step 2: Data Layer (The Infrastructure)
1.  **Implement Repository**: Create the implementation in `core:data` using Firebase, Retrofit, or DataStore.
2.  **Dagger Binding**: Add a `@Binds` method in `CoreModule.kt` to link the interface to the implementation.

### Step 3: UI Layer (The Presentation)
1.  **Create ViewModel**: Inherit from `BaseViewModel`.
    *   Use `launchSafe` or `launchWithLoading` for all coroutines.
    *   Expose UI state via `StateFlow`.
2.  **Dagger Multibinding**: 
    *   Create a Dagger `@Module` for the feature.
    *   Use `@Binds @IntoMap @ViewModelKey(MyViewModel::class)` to register it.
    *   Add the module to the appropriate component (`AppComponent` for public, `UserComponent` for authenticated).
3.  **Build Composables**: Create stateless Compose functions. Observe the ViewModel state in the screen-level Composable.

---

## 3. Dependency Injection Standards (Dagger 2)

We use a **Multi-Tiered Dependency Graph**. Developers must respect scope boundaries:

*   **@Singleton**: For infrastructure (Network, Firebase, SessionManager). Lives in `CoreComponent`.
*   **@AppScope**: For public/login logic. Lives in `AppComponent`.
*   **@UserScope**: For authenticated user data. Lives in `UserComponent`.

**CRITICAL**: Never attempt to inject a `@UserScope` dependency into a `@Singleton` class. This will cause a memory leak or a crash.

---

## 4. UI Standards & Base Classes

### 4.1 BaseViewModel
Every ViewModel **must** extend `BaseViewModel`. This provides:
- `isLoading`: A built-in StateFlow for progress bars.
- `launchSafe { ... }`: Automatic error handling and crash prevention.
- `handleError(e)`: Standardized toast and error state management.

### 4.2 Stateless Composables
Divide your UI into two parts:
1.  **Screen Composable**: "Stateful." Injects the ViewModel and passes data down.
2.  **Component Composables**: "Stateless." Take raw data and lambdas (e.g., `onClick: () -> Unit`). This makes them previewable and testable.

---

## 5. Security & PII
- **PII**: Any Personal Identifiable Information must be stored in the **Encrypted DataStore**.
- **Session**: Global session state is managed by `SessionManager`. Use it to reactively hide/show UI elements based on authentication.

---

## 6. Testing Requirements
- **Domain**: 90%+ coverage for UseCases.
- **ViewModels**: Test state transitions using `Dispatcher.Main` delegation.
- **Data**: Mock external SDKs (Firebase) using MockK.

---
**[Rho.Studio®](https://rho.studio/) - Engineering Department** - Contact [alexis.tercero@rho.studio](mailto:alexis.tercero@rho.studio)
