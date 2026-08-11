# Technical Report: Rho Studio UI
An Android Jetpack Compose app.

[![Android CI/CD Rho.Studio®](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/android.yml/badge.svg)](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/android.yml)
[![Android Release Rho.Studio®](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/release.yml/badge.svg)](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/release.yml)

> _Document Version: 2.0 Last Updated: August 10, 2026_
## Enterprise-Grade Android Architecture with Jetpack Compose

This document provides a comprehensive technical overview of the **Rho Studio UI** application. It serves as the primary architectural reference for developers, outlining the system's design, layer responsibilities, and technical standards.

<img width="320" height="693" alt="App Screenshot" src="https://github.com/user-attachments/assets/1a9febc6-edde-42ce-9239-3b20b8b50665" />

---

## 1. Executive Summary
Rho Studio UI is the **base application template** designed to establish and enforce the **Rho Studio Android App Standards**. It provides a robust foundation for building secure, authenticated mobile experiences within the Rho Studio ecosystem.

The application is a pure **Jetpack Compose** implementation following a **Single-Activity Architecture**, leveraging a reactive **MVVM (Model-View-ViewModel)** pattern to ensure a clean separation of concerns, testability, and a fluid user experience driven by **Unidirectional Data Flow (UDF)**. This architectural foundation ensures a focus on **Fluid UX**, **Transactional Integrity**, and **Decoupled Business Logic**.

### Core Features:
- **Secure Authentication**: Robust login flow with real-time validation and session lifecycle management, following corporate security protocols.
- **Adaptive Home Experience**: A responsive home interface that dynamically adjusts to different service modules and device form factors.
- **Brand Consistency**: A centralized design system leveraging Material 3 to reflect the Rho Studio corporate identity across all derived applications.

---

## 2. Architectural Framework
The application follows a **Single-Activity Architecture** and is structured according to **Clean Architecture** principles. It utilizes a **Feature-Layered Modularization** strategy to ensure scalability and maintainability.

### 2.1 Layered Structure
The system is divided into three primary logical layers, enforcing a strict unidirectional dependency flow: **UI → Domain ← Data**.

```mermaid
graph TD
    subgraph "UI Layer (Presentation)"
        UI[Jetpack Compose Screens]
        VM[ViewModels]
        Nav[Navigation / NavHost]
    end

    subgraph "Domain Layer (Business Logic)"
        UC[Use Cases / Interactors]
        Entities[Domain Entities]
        Int[Repository Interfaces]
    end

    subgraph "Data Layer (Infrastructure)"
        Repo[Repository Implementations]
        SM[Session Manager]
        Local[Local / Network Data Sources]
    end

    UI --> VM
    VM --> UC
    UC --> Entities
    UC --> Int
    Repo -.-> Int
    Repo --> SM
    Repo --> Local
```

### 2.2 Multi-Module Topology
We have moved away from a monolithic `:app` structure to a **Feature-Layered Modularization** strategy. This optimizes build parallelization and enforces strict dependency inversion.
```mermaid
flowchart TD
    APP[":app<br/>MainActivity, NavHost"]
    
    AUTH[":features:auth<br/>LoginScreen, LoginViewModel"]
    HOME[":features:home<br/>HomeScreen, HomeViewModel"]
    
    UI_CORE[":core:ui<br/>Theme, Common Composables"]
    DOMAIN[":core:domain<br/>Use Cases, Models, Contracts"]
    DATA[":core:data<br/>Repositories, SessionManager"]
    
    APP --> AUTH
    APP --> HOME
    
    AUTH --> UI_CORE
    AUTH --> DOMAIN
    HOME --> UI_CORE
    HOME --> DOMAIN
    
    UI_CORE --> DOMAIN
    
    DOMAIN -.->|"implemented by"| DATA
    
    style APP fill:#e94560,stroke:#c62828,color:#ffffff
    style AUTH fill:#1a1a2e,stroke:#e94560,color:#ffffff
    style HOME fill:#1a1a2e,stroke:#e94560,color:#ffffff
    style UI_CORE fill:#16213e,stroke:#0f3460,color:#ffffff
    style DOMAIN fill:#0f3460,stroke:#16213e,color:#ffffff
    style DATA fill:#1a1a2e,stroke:#e94560,color:#ffffff
```
> **Key Principle**: `:features` depend only on `:core` modules (`:core:domain`, `:core:ui`), preventing circular dependencies. Feature-specific models remain within their respective feature modules, adhering to the Interface Segregation Principle.

---

## 3. Layer Detail & Responsibilities

### 3.1 UI Layer (Presentation)
**Goal**: Transform application state into a visual interface and handle user interactions.
- **Jetpack Compose**: All UI is declarative, using stateless composables for maximum testability.
- **MVVM Pattern**: ViewModels manage UI state using `StateFlow`, exposing it to the UI in a lifecycle-aware manner.
- **UDF (Unidirectional Data Flow)**: User actions trigger events in the ViewModel, which updates the state, triggering a UI recomposition.
- **Side-Effect Orchestration**: `MainActivity` uses `LaunchedEffect` keyed to authentication state, transforming state changes into one-time navigation events.
- **Key Components**:
    - `MainActivity.kt`: The entry point and navigation orchestrator.
    - `LoginViewModel.kt` & `HomeViewModel.kt`: Feature-specific state holders.
    - `BaseViewModel.kt`: Provides shared logic for loading states, error handling, and navigation side-effects.
    - `HeaderViewModel.kt`: Bridges `SessionManager` state to common UI components
```mermaid
flowchart TB
    subgraph Navigation["Navigation Orchestration"]
        MA["MainActivity.kt<br/>- NavHost<br/>- Session-based routing"]
    end
    
    subgraph Shared["Shared UI Components"]
        PV["BaseViewModel.kt<br/>- Loading states<br/>- Error handling"]
        HV["HeaderViewModel.kt<br/>- Session state bridging"]
        PH["PageHeader.kt"]
        PF["PageFooter.kt"]
    end
    
    subgraph Auth["Authentication Feature"]
        LS["LoginScreen.kt"]
        LVM["LoginViewModel.kt<br/>- Form state<br/>- Debounced validation"]
    end
    
    subgraph Home["Home Feature"]
        HS["HomeScreen.kt"]
        HVM["HomeViewModel.kt<br/>- Home state<br/>- Session termination"]
    end
    
    MA --> LS
    MA --> HS
    LS --> LVM
    HS --> HVM
    LVM --> PV
    HVM --> PV
    HV --> PV
    
    style Navigation fill:#e94560,stroke:#c62828,color:#ffffff
    style Shared fill:#16213e,stroke:#0f3460,color:#ffffff
    style Auth fill:#1a1a2e,stroke:#e94560,color:#ffffff
    style Home fill:#1a1a2e,stroke:#e94560,color:#ffffff
```

### 3.2 Domain Layer (Business Logic)
**Goal**: House the platform-agnostic business rules and "truth" of the application.
- **Pure Kotlin**: This layer has zero dependencies on the Android Framework (no `Context`, no `Parcelable`).
- **Use Cases (Interactors)**: Each business action is encapsulated in a dedicated Use Case (e.g., `LoginUseCase`). This promotes the Single Responsibility Principle and makes logic reusable across ViewModels.
- **Entities**: Data classes like `User` and `Credentials` represent the core business models.
- **Key Components**:
    - `BaseUseCase<P, R>`: Standardizes execution context (Coroutines) and error handling.
    - `SessionManagerInterface`: Defines the contract for session operations without revealing implementation details.
    - `LoginUseCase`: Encapsulates the authentication transaction. 
    - `LogoutUseCase`: Orchestrates atomic session teardown.

### 3.3 Data Layer (Infrastructure)
**Goal**: Manage data acquisition, persistence, and external service coordination.
- **Repository Pattern**: Acts as a mediator between different data sources (Network, Database) and the Domain Layer.
- **Session Management**: `SessionManager` serves as the Single Source of Truth (SSOT) for the user's authentication state, exposing `StateFlow<AuthState>` for the UI to observe.
- **Current Implementation**: Uses `SharedPreferences` with `Gson` serialization for persistence and mock authentication for development.
- **Key Components**:
    - `SessionManager.kt`: Singleton coordinator for authentication state and user profile.
    - `SessionRepository.kt`: Coordinates data retrieval strategies.
    - `SessionRepositoryImpl.kt`: Manages persistent storage using SharedPreferences. Migrated to Room Database in the future.
    - `AuthRepositoryImpl.kt`: Mock implementation (**temporary**) simulating network delay and user creation. Replaced by Firebase Auth in the future.
```mermaid
flowchart TB
    subgraph SSOT["Single Source of Truth"]
        SM["SessionManager.kt<br/>- AuthState Flow<br/>- updateSession()<br/>- clearSession()"]
    end
    
    subgraph Repos["Repository Implementations"]
        ARI["AuthRepositoryImpl<br/>- Mock login()"]
        SRI["SessionRepositoryImpl<br/>- SharedPreferences"]
    end
    
    subgraph Sources["Data Sources (Planned)"]
        Remote["Remote API<br/>- Firebase Auth"]
        Local["Local Storage<br/>- Room Database"]
    end
    
    SM --> SRI
    ARI --> Remote
    SRI --> Local
    
    style SSOT fill:#e94560,stroke:#c62828,color:#ffffff
    style Repos fill:#1a1a2e,stroke:#e94560,color:#ffffff
    style Sources fill:#0f3460,stroke:#16213e,color:#ffffff
```
---

## 4. Technical Implementation Standards

### 4.1 Reactive Orchestration
The application uses **Kotlin Coroutines and Flow** for all asynchronous operations.
- **State-Driven Navigation**: `MainActivity` observes `SessionManager.isAuthenticated`; state changes trigger navigation transitions via `LaunchedEffect`.
- **Debounced Validation**: Login inputs are validated using a 300ms debounce to optimize performance.
- **State Pushing**: ViewModels push immutable state objects to the UI, ensuring that recompositions are predictable and efficient.
```mermaid
sequenceDiagram
    participant UI as MainActivity
    participant SM as SessionManager
    participant Nav as NavController
    
    UI->>SM: collectAsState()
    SM-->>UI: AuthState (Unauthenticated)
    UI->>Nav: navigate to Login
    
    Note over UI,Nav: User clicks Login
    UI->>LoginViewModel: onLoginClicked()
    LoginViewModel->>LoginUseCase: login(email, password)
    LoginUseCase->>AuthRepository: login(credentials)
    AuthRepository-->>LoginUseCase: User
    LoginUseCase->>SessionManager: updateSession(user)
    
    SM-->>UI: AuthState (Authenticated)
    UI->>Nav: navigate to Home
```

### 4.2 Modularization Strategy
The project is split into granular Gradle modules to improve build times and enforce architectural boundaries:
- `:app`: The main coordinator and DI root.
- `:features:*`: Feature-specific UI and ViewModels (e.g., `:features:auth`, `:features:home`).
- `:core:ui`: Shared design system components and theming.
- `:core:domain`: The platform-agnostic business layer.
- `:core:data`: Implementation details for data and external services.

### 4.3 Design System
Located in `:core:ui`, the design system defines the application's visual language:
- **Typography**: Custom typeface integration.
- **Color Palette**: Strict adherence to the Rho Studio brand (`RhoRed`, `RhoStrongGray`).
- **Components**: A library of reusable, styleable components (Buttons, Inputs, Cards).

---

## 5. Roadmap & Evolution: Strategic Phases

The application is transitioning from a modular prototype to a production-hardened system. The evolution is structured into three strategic phases:

### Phase I: Dependency Orchestration & Decoupling
- **Dagger Migration**: Implementation of **Dagger 2** to replace manual Service Locators.
    - Define `@Component` and `@Module` boundaries for `:core` and `:features`.
    - Implement `@Inject` for UseCase and ViewModel construction to ensure compile-time dependency safety.
- **Interface Segregation**: Strict enforcement of domain-defined interfaces to further isolate the Data Layer from Business Logic.

### Phase II: Transactional Integrity & persistence
- **Advanced Token Management**:
    - Implementation of an atomic token refresh mechanism within the Data Layer.
    - Securing critical transaction flows by validating session integrity before high-stakes domain executions.
    - Complete token lifecycle: Acquisition → Persistence → Validation → Refresh → Recovery → Invalidation.
- **Offline-First with Room**:
    - Integration of **Room Database** as the local cache for service modules.
    - Implementation of a "Source of Truth" strategy in Repositories to handle network-to-local synchronization.
```mermaid
flowchart TD
    A[1. Acquisition<br/>LoginUseCase --> AuthRepository.login]
    B[2. Persistence<br/>SessionRepository.saveToken]
    C[3. Validation<br/>ValidateTokenUseCase]
    D[4. Refresh<br/>RefreshTokenUseCase]
    E[5. Recovery<br/>SessionManager.initializeSession]
    F[6. Invalidation<br/>LogoutUseCase]
    
    A --> B --> C
    C -->|"Valid"| G[Use Access Token]
    C -->|"Expired"| D --> B
    E --> C
    F --> H[Reset AuthState]
    
    style A fill:#e94560,stroke:#c62828,color:#ffffff
    style B fill:#16213e,stroke:#0f3460,color:#ffffff
    style C fill:#1a1a2e,stroke:#e94560,color:#ffffff
    style D fill:#0f3460,stroke:#16213e,color:#ffffff
    style E fill:#16213e,stroke:#0f3460,color:#ffffff
    style F fill:#e94560,stroke:#c62828,color:#ffffff
    style G fill:#0f3460,stroke:#16213e,color:#ffffff
    style H fill:#1a1a2e,stroke:#e94560,color:#ffffff
```

### Phase III: Verification & Quality Engineering
- **Domain Test Suite**: Achieving 90%+ coverage for `:core:domain` logic using JUnit 5 and MockK.
- **UI & Regression Testing**:
    - Implementation of **Compose UI Tests** for critical user journeys (Login, Home navigation).
    - Integration of **Screenshot Testing** to ensure visual consistency across the Rho Studio design system.
- **Performance Profiling**: Regular benchmarking of recomposition counts and memory allocation in high-density feature screens.
```mermaid
flowchart LR
    subgraph Current["Current Flow"]
        C1[UI] --> C2[ViewModel] --> C3[UseCase] --> C4[Repository] --> C5[SharedPreferences/Mock Auth]
    end
    
    subgraph Planned["Planned Flow"]
        P1[UI] --> P2[ViewModel] --> P3[UseCase] --> P4[Repository]
        P4 --> P5[Local: Room Database]
        P4 --> P6[Remote: Retrofit/Firebase]
    end
    
    Current -.->|"Evolution"| Planned
    
    style Current fill:#1a1a2e,stroke:#e94560,color:#ffffff
    style Planned fill:#0f3460,stroke:#16213e,color:#ffffff
```
---

## 6. Verification & Quality Assurance
- **CI/CD**: GitHub Actions pipeline verifies every commit against build and test suites.
- **Static Analysis**: Automated linting and ASCII metadata headers enforce code style and legal standards.

## 7. File Registry and responsibilities
Here is the updated table based on the file structure provided:

| File / Module                     | Layer        | Responsibility                       | Status |
|:----------------------------------|:-------------|:-------------------------------------|:-------|
| `MainActivity.kt`                 | UI Layer     | Navigation orchestration             | ✅      |
| `BaseViewModel.kt`                | UI Layer     | Loading/Error state management       | ✅      |
| `HeaderViewModel.kt`              | UI Layer     | Session state bridging               | ✅      |
| `PageHeader.kt` / `PageFooter.kt` | UI Layer     | Shared UI components                 | ✅      |
| `LoginScreen.kt`                  | UI Layer     | Login UI entry point                 | ✅      |
| `LoginViewModel.kt`               | UI Layer     | Form state & validation              | ✅      |
| `LoginEmailField.kt`              | UI Layer     | Email input with validation          | ✅      |
| `LoginPasswordField.kt`           | UI Layer     | Password input with security         | ✅      |
| `LoginButton.kt`                  | UI Layer     | Login action button                  | ✅      |
| `HomeScreen.kt`                   | UI Layer     | Home UI entry point                  | ✅      |
| `HomeViewModel.kt`                | UI Layer     | Home state & session termination     | ✅      |
| `ServiceList.kt`                  | UI Layer     | Service list grid component          | ✅      |
| `ServiceItem.kt`                  | UI Layer     | Individual service item component    | ✅      |
| `ServiceModule.kt`                | UI Layer     | Feature-specific model (Home)        | ✅      |
| `BaseUseCase.kt`                  | Domain Layer | Standardized UseCase abstraction     | ✅      |
| `LoginUseCase.kt`                 | Domain Layer | Atomic authentication transaction    | ✅      |
| `LogoutUseCase.kt`                | Domain Layer | Session teardown orchestration       | ✅      |
| `SessionManagerInterface.kt`      | Domain Layer | Session operations contract          | ✅      |
| `AuthRepository.kt`               | Domain Layer | Authentication contract              | ✅      |
| `SessionRepository.kt`            | Domain Layer | Session persistence contract         | ✅      |
| `User.kt` / `Credentials.kt`      | Domain Layer | Pure Kotlin Entities                 | ✅      |
| `SessionManager.kt`               | Data Layer   | SSOT for authentication              | ✅      |
| `AuthRepositoryImpl.kt`           | Data Layer   | Mock auth (Firebase **planned**)     | ⚠️     |
| `SessionRepositoryImpl.kt`        | Data Layer   | SharedPreferences (Room **planned**) | ⚠️     |
| `RefreshTokenUseCase.kt`          | Domain Layer | Token refresh (**planned**)          | 📅     |
| `Dagger Components`               | App Root     | DI setup (**planned**)               | 📅     |
## 8. References & Standards
- **MAD (Modern Android Development)**: Adhering to official [Android Architecture Guidelines](https://developer.android.com/topic/architecture).
- **Jetpack Compose Best Practices**: Following UDF ([Unidirectional Data Flow](https://developer.android.com/develop/ui/compose/architecture#udf)) principles for state management.
- **Multi-Module Topology**: Following [Guide to App Modularization](https://developer.android.com/topic/modularization).
- **Dependency Injection**: [Dagger Documentation](https://dagger.dev/).
- **Secure Token Management**: [Android Security Best Practices](https://developer.android.com/privacy-and-security/security-best-practices).


---
**[Rho.Studio®](https://rho.studio/) - Engineering Department** - Contact [alexis.tercero@rho.studio](mailto:alexis.tercero@rho.studio)
