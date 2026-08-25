# Technical Report: Rho Studio UI
An Android Jetpack Compose app.

[![Android CI/CD Rho.Studio®](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/android.yml/badge.svg)](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/android.yml)
[![Android Release Rho.Studio®](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/release.yml/badge.svg)](https://github.com/Rho-Studio/UI-Utils-Rho-Studio/actions/workflows/release.yml)

> _Document Version: 3.0 Last Updated: August 24, 2026_
## Enterprise-Grade Android Architecture with Jetpack Compose

This document provides a comprehensive technical overview of the **Rho Studio UI** application. It serves as the primary architectural reference for developers, outlining the system's design, layer responsibilities, and technical standards.

<img width="320" height="693" alt="App Screenshot" src="https://github.com/user-attachments/assets/1a9febc6-edde-42ce-9239-3b20b8b50665" />

---

## 1. Executive Summary
Rho Studio UI is the **base application template** designed to establish and enforce the **Rho Studio Android App Standards**. It provides a robust foundation for building secure, authenticated mobile experiences within the Rho Studio ecosystem.

The application is a **Jetpack Compose** implementation following a **Single-Activity Architecture**, leveraging a reactive **MVVM (Model-View-ViewModel)** pattern, implementing a Multi-Tier Dagger Hierarchy and a fluid user experience driven by **Unidirectional Data Flow (UDF)**. This architectural foundation ensures a focus on **Fluid UX**, **Transactional Integrity**, and **Decoupled Business Logic**.

**Contribution**: [See CONTRIBUTION.md](./CONTRIBUTION.md).

### Core Features:
- **Authentication Orchestration**: Implements a production flow using Firebase Auth with a reactive session lifecycle. It ensures transactional security by synchronizing remote authentication states with automated, state-driven navigation transitions.
- **Architectural Boundaries**: A high-performance Multi-Tier Dagger Hierarchy that enforces strict data isolation between core, app, and user scopes. Sensitive user information is isolated within a dedicated "User Tier" that is physically purged from memory upon logout to prevent data leakage.
- **Reactive Data Layer and State Integrity**: Leverages Jetpack DataStore for atomic persistence and a Sealed State Machine for global orchestration. This creates a thread-safe, non-blocking "stream of truth" that guarantees the UI remains a perfect reflection of the underlying data.
- **Stateless Presentation Layer**: A fully decoupled UI built with Jetpack Compose following Unidirectional Data Flow (UDF) principles. This enables the presentation layer to scale dynamically across diverse device form factors while ensuring high testability and visual consistency.
- **Domain-Driven Design (DDD)**: Every business operation is encapsulated in a dedicated UseCase (Interactor) within a framework-independent domain layer. By strictly isolating the business rules from the Android framework, ensure testability, logic reusability, and architectural resilience against framework changes.

---

## 2. Architectural Framework
The application follows a **Single-Activity Architecture** and is structured according to **Clean Architecture** principles. It utilizes a **Feature-Layered Modularization** strategy to ensure scalability and maintainability.

### 2.1 Layered Structure
The system follows the three layers Google's recommendations:

```mermaid
graph TD
    subgraph UI["UI Layer (Presentation)"]
        UI_Screens[Jetpack Compose Screens]
        VM[ViewModels]
        Nav[Navigation / NavHost]
    end

    subgraph Domain["Domain Layer (Business Logic)"]
        UC[Use Cases / Interactors]
        Entities[Domain Entities]
        Int[Repository Interfaces]
    end

    subgraph Data["Data Layer (Infrastructure)"]
        Repo[Repository Implementations]
        SM[Session Manager / SSOT]
        Local[Local / Network Data Sources]
    end

    UI_Screens --> VM
    VM --> UC
    UC --> Entities
    UC --> Int
    Repo -.-> Int
    Repo --> SM
    Repo --> Local

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef uiNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    classDef domainNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef dataNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class UI_Screens,VM,Nav uiNode
    class UC,Entities,Int domainNode
    class Repo,SM,Local dataNode

    style UI fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style Domain fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style Data fill:#D3D3D3,stroke:#D32F2F,color:#000000
```

### 2.2 Multi-Module Topology
The project is split into granular Gradle modules to improve build parallelization and enforce boundaries.
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

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef appNode fill:#D32F2F,stroke:#FFFFFF,color:#FFFFFF
    classDef featureNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    classDef coreNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef domainNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef dataNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class APP appNode
    class AUTH,HOME featureNode
    class UI_CORE coreNode
    class DOMAIN domainNode
    class DATA dataNode

    style APP fill:#D32F2F,stroke:#FFFFFF,color:#FFFFFF
    style AUTH fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    style HOME fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    style UI_CORE fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style DOMAIN fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style DATA fill:#D3D3D3,stroke:#D32F2F,color:#000000
```
> **Key Principle**: `:features` depend only on `:core` modules (`:core:domain`, `:core:ui`), preventing circular dependencies. Feature-specific models remain within their respective feature modules.

### 2.3 Multi-Tier Dependency Injection (Dagger 2 + KSP)
We utilize a high-performance Directed Acyclic Graph (DAG) generated at compile-time using KSP to ensure zero runtime overhead. The graph is organized into three tiers to mirror the application lifecycle:

#### DI Conceptual Framework

| Concept | Implementation | Purpose |
| :--- | :--- | :--- |
| **The Request** | `@Inject` constructor | Objects request dependencies via constructor injection, ensuring loose coupling and testability |
| **The Recipe** | `@Module` with `@Provides` / `@Binds` | Dagger Modules define how to provide complex objects, interfaces, or library classes |
| **The Manager** | `@Component` | Bridge between providers (Modules) and consumers (Activities/ViewModels). Validates graph at compile-time |
| **The Lifecycle** | `@Scope` (e.g., `@Singleton`, `@UserScope`) | Ensures objects live exactly as long as their context (App lifecycle vs. User session) |
| **Annotation Retention** | `@Retention(AnnotationRetention.RUNTIME)` | Ensures annotation metadata is available to Dagger compiler and at runtime |
| **Multibinding Keys** | `@MapKey` | Identifies which class type to use as a Key in Dagger's internal Maps |
| **Lazy Provisioning** | `Provider<T>` | Defers actual creation of dependencies until requested, saving memory and startup time |
| **Kotlin Interop** | `@JvmSuppressWildcards` | Handles Kotlin's generic covariance in Dagger's Java-based compiler |

#### Multi-Tier Component Dependency Architecture

```mermaid
flowchart TB
    subgraph Core["CoreComponent (@Singleton)"]
        direction TB
        CTX["Context (Application)"]
        DS["DataStore"]
        SM["SessionManager"]
        FA["FirebaseAuth"]
        FAN["FirebaseAnalytics"]
    end
    
    subgraph App["AppComponent (@AppScope)"]
        direction TB
        AM["AuthModule"]
        UM["UIModule"]
        LoginVM["LoginViewModel"]
    end
    
    subgraph User["UserComponent (@UserScope)"]
        direction TB
        HM["HomeModule"]
        UM2["UIModule (Reused)"]
        HomeVM["HomeViewModel"]
        HeaderVM["HeaderViewModel"]
    end
    
    Core -->|"Component Dependency"| App
    Core -->|"Component Dependency"| User
    
    AM -->|"Contains"| LoginVM
    AM -->|"Contains"| UM
    
    HM -->|"Contains"| HomeVM
    HM -->|"Contains"| HeaderVM
    HM -->|"Contains"| UM2

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef coreNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    classDef appNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef userNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class CTX,DS,SM,FA,FAN coreNode
    class AM,UM,LoginVM,HeaderVM appNode
    class HM,UM2,HomeVM,HeaderVM2 userNode

    style Core fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style App fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style User fill:#D3D3D3,stroke:#D32F2F,color:#000000
```

**Core Tier** (`CoreComponent`):

- **Scope**: `@Singleton`.
- **Responsibility**: Infrastructure foundation (Firebase, DataStore, Threading).
- **Hierarchy**: The foundation; does not depend on other components.

**App Tier** (`AppComponent`):

- **Scope**: @AppScope.
- **Responsibility**: Public lifecycle (Authentication feature, Shared UI).
- **Hierarchy**: Depends on CoreComponent. Orchestrates AuthModule (Pre-Login ViewModels) and UIModule (Shared ViewModels).

**User Tier** (`UserComponent`):

- **Scope**: `@UserScope`.
- **Responsibility**: Authenticated session (Home feature, Profile).
- **Hierarchy**: Depends on CoreComponent. Orchestrates `HomeModule` (Post-Login ViewModels) and `UIModule`.
- **Isolation**: Created dynamically upon login and binary-purged from memory upon logout to ensure session security.

### 2.4 ViewModel Multibinding Strategy

To decouple the UI from DI wiring, we implement a centralized registry using `@IntoMap`:

```mermaid
flowchart TB
    subgraph Contribution["Step 1: Contribution"]
        M1["AuthModule"] --> B1["@Binds LoginViewModel"]
        M2["UIModule"] --> B2["@Binds HeaderViewModel"]
        M3["HomeModule"] --> B3["@Binds HomeViewModel"]
    end
    
    subgraph Aggregation["Step 2: Aggregation"]
        M1 --> Map["Internal Map<br/>Map<Class, Provider<ViewModel>>"]
        M2 --> Map
        M3 --> Map
    end
    
    subgraph Resolution["Step 3: Resolution"]
        Map --> Factory["DaggerViewModelFactory"]
        Factory --> UI["UI requests ViewModel by Class"]
    end
    
    Contribution --> Aggregation --> Resolution
    
    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef contributionNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    classDef aggregationNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef resolutionNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class M1,B1,M2,B2,M3,B3 contributionNode
    class Map aggregationNode
    class Factory,UI resolutionNode

    style Contribution fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style Aggregation fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style Resolution fill:#D3D3D3,stroke:#D32F2F,color:#000000
```

#### Key Principles:

- **Registry**: Feature modules contribute ViewModels via a custom @ViewModelKey.
- **Factory**: A single DaggerViewModelFactory resolves instances on-demand, adhering to the Open/Closed Principle.
- **Isolation**: Each component builds unique internal Maps, ensuring strict data and logic isolation based on user state.

| Component | Modules Included | Resulting Internal Map |
| :--- | :--- | :--- |
| **AppComponent** | `AuthModule`, `UIModule` | `{LoginViewModel, HeaderViewModel}` |
| **UserComponent** | `HomeModule`, `UIModule` | `{HomeViewModel, HeaderViewModel}` |

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
        PV["BaseViewModel.kt<br/>- launchSafe<br/>- isLoading state"]
        HV["HeaderViewModel.kt<br/>- Session state bridging"]
        PH["PageHeader.kt"]
    end
    
    subgraph Auth["Authentication Feature"]
        LS["LoginScreen.kt"]
        LVM["LoginViewModel.kt"]
    end
    
    subgraph Home["Home Feature"]
        HS["HomeScreen.kt"]
        HVM["HomeViewModel.kt"]
    end
    
    MA --> LS
    MA --> HS
    LS --> LVM
    HS --> HVM
    LVM --> PV
    HVM --> PV
    HV --> PV

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef navNode fill:#D32F2F,stroke:#FFFFFF,color:#FFFFFF
    classDef sharedNode fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    classDef authNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    classDef homeNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF

    class MA navNode
    class PV,HV,PH sharedNode
    class LS,LVM authNode
    class HS,HVM homeNode

    style Navigation fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style Shared fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style Auth fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style Home fill:#333333,stroke:#D32F2F,color:#FFFFFF
```

### 3.2 Domain Layer (Business Logic)
**Goal**: House the platform-agnostic business rules and "truth" of the application.
- **Pure Kotlin**: This layer has zero dependencies on the Android Framework (no `Context`, no `Parcelable`).
- **Entities**: Data classes like `User` and `Credentials` represent the core business models.
- **Use Cases (Interactors)**: Each business action is encapsulated in a dedicated Use Case (e.g., `LoginUseCase`). This promotes the Single Responsibility Principle and makes logic reusable across ViewModels.
- **BaseUseCase Pattern**: All interactors inherit from `BaseUseCase<P, R>`. This architectural anchor standardizes:
    - **Thread Safety**: Automatic execution on `Dispatchers.IO`.
    - **Result Wrapping**: Consistent use of the `Result<T>` sealed class for Success/Error states.
    - **Functional Invocation**: Use cases are invoked as functions using the invoke operator.

- **Key Components**:
    - `BaseUseCase<P, R>`: Standardizes execution context (Coroutines) and error handling.
    - `SessionManagerInterface`: Defines the contract for session operations without revealing implementation details.
    - `LoginUseCase`: Encapsulates the authentication transaction. 
    - `LogoutUseCase`: Orchestrates atomic session teardown.

```mermaid
flowchart TB
    subgraph UseCases["Use Cases (Interactors)"]
        LU["LoginUseCase<br/>- Validate Credentials<br/>- Authenticate via Firebase<br/>- Commit to SSOT"]
        LogU["LogoutUseCase<br/>- Clear session<br/>- Reset global state"]
        VCU["ValidateCredentialsUseCase"]
        RTU["RefreshTokenUseCase<br/>(Planned)"]
    end
    
    subgraph Models["Domain Models"]
        U["User.kt"]
        C["Credentials.kt"]
        AT["AuthToken.kt"]
    end
    
    subgraph Contracts["Repository Contracts"]
        AR["AuthRepository"]
        SR["SessionRepository"]
    end
    
    UseCases --> Models
    UseCases --> Contracts

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef useCaseNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef modelNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef contractNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class LU,LogU,VCU,RTU useCaseNode
    class U,C,AT modelNode
    class AR,SR contractNode

    style UseCases fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style Models fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style Contracts fill:#D3D3D3,stroke:#D32F2F,color:#000000
```

### 3.3 Data Layer (Infrastructure)
**Goal**: Manage data acquisition, persistence, and external service coordination.
- **Repository Pattern**: Acts as a mediator between different data sources (Network, Database) and the Domain Layer.
- **Session Management**: `SessionManager` serves as the Single Source of Truth (SSOT) for the user's authentication state, exposing `StateFlow<AuthState>` for the UI to observe.
- **Production Sources**: Production-grade implementation using Firebase Auth and Jetpack DataStore.
- **Telemetry**: Integrated Firebase Analytics for automated journey tracking.
- **Dependency Inversion**: Repository interfaces defined in Domain; implementations in Data.
```mermaid
flowchart TB
    subgraph SSOT["Single Source of Truth"]
        SM["SessionManager.kt<br/>- AuthState Flow<br/>- updateSession()<br/>- clearSession()"]
    end
    
    subgraph Repos["Repository Implementations"]
        ARI["AuthRepositoryImpl<br/>- Firebase Auth"]
        SRI["SessionRepositoryImpl<br/>- Jetpack DataStore"]
    end
    
    subgraph Sources["Production Infrastructure"]
        Remote["Firebase Auth SDK"]
        Local["Jetpack DataStore PII"]
    end
    
    SM --> SRI
    ARI --> Remote
    SRI --> Local

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef ssotNode fill:#D32F2F,stroke:#FFFFFF,color:#FFFFFF
    classDef repoNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    classDef sourceNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class SM ssotNode
    class ARI,SRI repoNode
    class Remote,Local sourceNode

    style SSOT fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style Repos fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style Sources fill:#D3D3D3,stroke:#D32F2F,color:#000000
```
#### Session State Machine

```mermaid
flowchart LR
    INIT["Uninitialized"] -->|"initialize()"| CHECK["Checking"]
    CHECK -->|"Valid token found"| AUTH["Authenticated"]
    CHECK -->|"No token / expired"| GUEST["Guest"]
    AUTH -->|"logout()"| GUEST
    GUEST -->|"login()"| AUTH

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef stateNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class INIT,CHECK,AUTH,GUEST stateNode
```
#### Authentication Flow Architecture
```mermaid
flowchart TB
    subgraph UI["UI Layer"]
        LS["LoginScreen"]
        LVM["LoginViewModel"]
    end
    
    subgraph Domain["Domain Layer"]
        LU["LoginUseCase"]
        AR["AuthRepository<br/>(Interface)"]
    end
    
    subgraph Data["Data Layer"]
        ARI["AuthRepositoryImpl"]
        FRD["FirebaseRemoteDataSource"]
        ARD["AnalyticsRemoteDataSource"]
        SM["SessionManager"]
    end
    
    subgraph Firebase["Firebase SDK"]
        FA["FirebaseAuth"]
        FAN["FirebaseAnalytics"]
    end
    
    LS --> LVM
    LVM --> LU
    LU --> AR
    AR -.->|"implements"| ARI
    ARI --> FRD
    ARI --> ARD
    ARI --> SM
    FRD --> FA
    ARD --> FAN

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef uiNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF
    classDef domainNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef dataNode fill:#333333,stroke:#D32F2F,color:#FFFFFF
    classDef firebaseNode fill:#4A4A4A,stroke:#D32F2F,color:#FFFFFF

    class LS,LVM uiNode
    class LU,AR domainNode
    class ARI,FRD,ARD,SM dataNode
    class FA,FAN firebaseNode

    style UI fill:#333333,stroke:#D32F2F,color:#FFFFFF
    style Domain fill:#4A4A4A,stroke:#333333,color:#FFFFFF
    style Data fill:#D3D3D3,stroke:#D32F2F,color:#000000
    style Firebase fill:#333333,stroke:#D32F2F,color:#FFFFFF
```
#### Telemetry and Analytics Integration

```mermaid
flowchart LR
    USER["User Login"] --> AUTH["Authentication Success"]
    AUTH --> ANALYTICS["AnalyticsRemoteDataSource"]
    ANALYTICS --> FIREBASE["FirebaseAnalytics.logEvent()"]
    FIREBASE --> DEBUG["Visible in Firebase DebugView"]

    linkStyle default stroke:#D32F2F,stroke-width:2px

    classDef telemetryNode fill:#333333,stroke:#D32F2F,color:#FFFFFF

    class USER,AUTH,ANALYTICS,FIREBASE,DEBUG telemetryNode
```

---

## 4. Technical Implementation Standards

Rho Studio UI is engineered for sensitive information (fintech) environments

### 4.1 Session Isolation

```mermaid
sequenceDiagram
    participant UI as MainActivity
    participant SM as SessionManager
    participant CM as ComponentManager
    participant FC as FeatureComponent
    
    UI->>SM: collectAsState()
    SM-->>UI: SessionState (Guest)
    UI->>CM: getAppComponent()
    CM-->>UI: AppComponent
    
    Note over UI,FC: User logs in
    UI->>SM: updateSession(User)
    SM-->>UI: SessionState (Authenticated)
    UI->>CM: getUserComponent()
    CM-->>UI: UserComponent
    
    Note over UI,FC: User logs out
    UI->>SM: clearSession()
    SM-->>UI: SessionState (Guest)
    UI->>CM: releaseUserComponent()
    Note over CM: @UserScope objects<br/>binary-purged from memory
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
