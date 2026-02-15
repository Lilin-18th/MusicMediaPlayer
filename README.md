# MusicMediaPlayer
音楽再生アプリ

![Version](https://img.shields.io/badge/Version-0.1.0-blue)

![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-7F52FF?logo=kotlin&logoColor=white) 
![Jetpack Compose](https://img.shields.io/badge/UI-Compose%20M3-4285F4?logo=jetpackcompose&logoColor=white) 
[![Metro](https://img.shields.io/badge/DI-Metro-orange?logoColor=white)](https://zacsweers.github.io/metro/latest/)

## Architecture
Clean Architecture + MVVM
GameLibraryはClean ArchitectureとMVVM（Model-View-ViewModel）パターンを組み合わせた設計を採用している。
Data、Domain、UIの3層に明確に分離することで、テスタビリティ、保守性、スケーラビリティを実現。


## 📁 Project Structure
本プロジェクトは、関心の分離と高い保守性を実現するため、以下のパッケージ構造を採用している

<details>
<summary>📁 プロジェクト構造の詳細を表示 (クリックで開閉)</summary>

```text
app/
├── src/
│   └── main/
│       ├── kotlin/com/lilin/musicmediaplayer/
│       │   ├── MusicMediaPlayerApplication.kt
│       │   │
│       │   ├── data/
│       │   │   ├── di/
│       │   │   │   ├── factory/           # ViewModel Factory
│       │   │   │   │   └── ViewModelFactory.kt
│       │   │   │   └── AppGraph.kt        # DI container (Metro)
│       │   │   └── service/               # Media Session Service
│       │   │       └── MusicMediaSessionService.kt
│       │   │
│       │   ├── data/                      # Data Layer
│       │   │   ├── datasource/            # Data Store
│       │   │   │   └── MusicDataSource.kt
│       │   │   ├── entity/                # Data Transfer Objects
│       │   │   │   └── MusicEntity.kt
│       │   │   ├── mapper/                # DTO ↔ Entity mappers
│       │   │   │   └── MusicEntityMapper.kt
│       │   │   └── repository/            # Repository implementations
│       │   │       ├── MusicPlayerRepositoryImpl.kt
│       │   │       └── MusicRepositoryImpl.kt
│       │   │
│       │   ├── domain/                    # Domain Layer
│       │   │   ├── model/                 # Business entities
│       │   │   │   ├── Music.kt
│       │   │   │   └── PlayerState.kt
│       │   │   ├── repository/            # Repository interfaces
│       │   │   │   ├── MusicPlayerRepository.kt
│       │   │   │   └── MusicRepository.kt
│       │   │   └── usecase/               # Business logic
│       │   │       └── GetMusicListUseCase.kt
│       │   │
│       │   ├── feature/                   # Feature modules (UI Layer)
│       │   │   ├── play/                  # Player screen
│       │   │   │   ├── MusicPlayScreen.kt
│       │   │   │   ├── MusicPlayUiState.kt
│       │   │   │   └── MusicPlayViewModel.kt
│       │   │   └── playlist/              # Music list screen
│       │   │       ├── MusicPlayListScreen.kt
│       │   │       ├── PlayListUiState.kt
│       │   │       └── MusicPlayListViewModel.kt
│       │   │
│       │   ├── ui/                        # Shared UI components
│       │   │   ├── component/             # Reusable components
│       │   │   │   ├── MusicListCard.kt
│       │   │   │   ├── MusicPlayComponents.kt
│       │   │   │   └── TopAppBar.kt
│       │   │   ├── theme/                 # App theme & styling
│       │   │   │   ├── Color.kt
│       │   │   │   ├── Theme.kt
│       │   │   │   └── Type.kt
│       │   │   └── util/
│       │   │       └── Formatter.kt
│       │   │
│       │   ├── MainActivity.kt
│       │   └── MusicMediaPlayerApplication.kt
│       │
│       └── res/                           # Resources
│           ├── values/
│           ├── drawable/
│           └── ...
│
├── build.gradle.kts
└── ...
```
</details>

#### Key Directories

| Directory | Purpose |
| :--- | :--- |
| `data/` | **データアクセス層** - API通信、DB操作、Repository実装 |
| `domain/` | **ビジネスロジック層** - Entity、UseCase、Repository interface |
| `feature/` | **画面単位のUI実装** - Screen、ViewModel、UiState |
| `ui/component/` | **再利用可能なUIコンポーネント** |
| `ui/theme/` | **アプリ全体のテーマとデザインシステム** |
| `navigation/` | **画面遷移の定義** |
| `di/` | **Hiltによる依存性注入の設定** |

## Tech Stack（技術スタック）
- UI: Jetpack Compose (Material 3), Coil 3
- Dependency Injection: [Metro](https://zacsweers.github.io/metro/latest/)
- Navigation: Nav3
- Async: Kotlin Coroutines, Flow (StateFlow / SharedFlow)
