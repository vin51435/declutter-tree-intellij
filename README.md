# Project View Bottom Files - IntelliJ Platform Plugin

An ultra-efficient, lightweight IntelliJ IDEA plugin that customizes the Project View tree structure so that designated files (such as `package-info.java`) are always positioned at the very bottom of their parent directory.

---

## ⚡ Architectural Highlights & Performance Guarantees

Because this code executes in the Project View tree rendering and node evaluation loops:
1. **Zero UI Stutter & EDT Protection**:
   - **Fast-Path 0-Allocations**: For 99%+ of folders that do not contain target files, the provider scans nodes and returns the original collection immediately without allocating `ArrayList`s or wrapper nodes.
   - **No Disk or PSI I/O**: File name matching reads from `virtualFile.name` (a fast in-memory string pointer) without loading syntax trees, resolving AST nodes, or acquiring heavy PSI read locks.
2. **$O(1)$ Hash Lookups**:
   - Uses a pre-compiled `Set<String>` for immediate lookup instead of runtime string transformations or regex matching.
3. **Dual-Mode Sorting Guarantee**:
   - **"Folders Always on Top" & Type Sorting**: `BottomPsiFileNode` returns `BOTTOM_SORT_WEIGHT = 1000` via `getTypeSortWeight()`, ensuring directories stay on top, standard files stay in the middle, and bottom files sink to the end.
   - **Pure Alphabetical Sorting**: `getSortKey()` returns `\uFFFF + filename`. `\uFFFF` is the terminal character of the Unicode Basic Multilingual Plane (BMP), guaranteeing that natural string comparison places the node after any ASCII or UTF-8 file name.
4. **Stable Selection & VCS State**:
   - Subclasses `PsiFileNode` and preserves identity equality with the underlying `PsiFile`. File selection, cursor positioning, keyboard navigation, Git badges, and context menus remain 100% stable.
5. **Dumb-Mode Resilience**:
   - Implements `DumbAware` so tree rendering is instantaneous and does not block while the IDE indexes project files.

---

## 📂 Project Structure

```text
project-view-bottom-files/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── kotlin/com/plugin/projectview/bottomfiles/
│   │   │   ├── BottomFileConstants.kt
│   │   │   ├── BottomPsiFileNode.kt
│   │   │   └── BottomFilesTreeStructureProvider.kt
│   │   └── resources/META-INF/
│   │       └── plugin.xml
│   └── test/
│       └── kotlin/com/plugin/projectview/bottomfiles/
│           └── BottomFileConstantsTest.kt
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Building & Running

### Requirements
- **JDK 21** (Required by IntelliJ Platform 2024.2+)
- Gradle 8.10+ (managed via included `./gradlew`)

### Run Tests
```bash
./gradlew test
```

### Launch Development IDE with Plugin
```bash
./gradlew runIde
```

### Build Distribution Plugin ZIP
```bash
./gradlew buildPlugin
```
The deployable plugin artifact will be generated in `build/distributions/project-view-bottom-files-1.0.0.zip`.
