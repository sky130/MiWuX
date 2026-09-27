# Compose app architecture

Application code under `com.github.miwu` is split into four layers:

- `domain`: Pure Kotlin models, repository contracts, gateways, and use cases.
- `data`: Repository implementations, remote orchestration, DataStore, and settings storage.
- `ui` (`screen` and `route`): Composables, ViewModels, UI state, and navigation keys.
- `di`: The composition root that connects implementations to domain contracts.

Dependencies point toward domain contracts:

```text
ui --------> domain <-------- data

di --------> all layers
```

## Rules

1. `domain` must not import Compose, Android, `data`, `di`, or UI packages.
2. Repository interfaces describe business capabilities and expose immutable `Flow` or `StateFlow`.
3. Repository implementations own data synchronization, caching, and dispatcher selection.
4. One-shot repository writes are main-safe suspend functions; callers own their lifecycle.
5. ViewModels use constructor injection and depend only on domain contracts and use cases.
6. Feature code must not implement `KoinComponent` or use Koin as a service locator.
7. Routes are serializable navigation keys containing only minimal identifiers.
8. Composable destination mapping is centralized in the application navigation host.
