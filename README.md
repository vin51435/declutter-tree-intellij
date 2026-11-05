# Declutter Tree

Intelligent Project View file organization for IntelliJ IDEA. Groups Java elements by declaration type and positions auxiliary metadata files (such as `package-info.java`) at the bottom of directories.

---

## Overview

In standard IntelliJ IDEA projects, files are sorted strictly alphabetically. Auxiliary documentation files like `package-info.java` often sit in the middle of domain logic, while interfaces, entities, records, and enums are mixed together purely by name.

Declutter Tree provides a clean, predictable sorting hierarchy:
1. **Directories on top:** Folder structure remains untouched at the top of the directory.
2. **Type-based grouping:** Java files are grouped by declaration kind:
   - Interfaces
   - Abstract Classes
   - Classes
   - Final Classes
   - Records
   - Enums
   - Annotations
3. **Alphabetical sorting:** Elements within each group remain sorted A-Z.
4. **Bottom file anchoring:** Auxiliary files like `package-info.java` are pushed to the bottom.
5. **Configurable settings:** Custom bottom files and sorting rules can be configured in `Settings -> Appearance & Behavior -> Declutter Tree`.

---

## Visual Example

### Default IntelliJ Sorting
```text
com.example.order/
├── service/
├── Order.java                    (Class)
├── OrderCancelledEvent.java      (Record)
├── OrderDTO.java                 (Class)
├── OrderRepository.java          (Interface)
├── OrderStatus.java              (Enum)
├── package-info.java             (Metadata file in the middle)
└── ValidOrder.java               (Annotation)
```

### With Declutter Tree
```text
com.example.order/
├── service/                   (Directories on top)
├── OrderRepository.java       (1. Interfaces)
├── Order.java                 (2. Classes)
├── OrderDTO.java              (2. Classes, sorted A-Z)
├── OrderCancelledEvent.java   (3. Records)
├── OrderStatus.java           (4. Enums)
├── ValidOrder.java            (5. Annotations)
└── package-info.java          (6. Anchored to the bottom)
```

---

## Performance Considerations

Because this logic runs inside the Project Tool Window tree rendering loop, the plugin is built for minimal overhead:

- **Fast-path evaluation:** Scans children in memory first. Folders without matching files return the original collection immediately without extra object allocations.
- **Stub-based type resolution:** Reads Java declaration kinds using compiled index stubs (`PsiJavaFile.classes`), avoiding full AST parsing or disk reads during tree rendering.
- **DumbAware:** Runs smoothly during background project indexing without blocking the UI.
- **Tree state preservation:** Extends standard `PsiFileNode` and delegates equality checks to the underlying `PsiFile`. File selection, Git status colors, bookmarks, and breadcrumbs function normally.

---

## Installation

### From JetBrains Marketplace
Search for **Declutter Tree** in `Settings -> Plugins -> Marketplace` and click **Install**.

### From Disk (.zip)
1. Download `declutter-tree-intellij-x.x.x.zip` from [Releases](https://github.com/vin51435/declutter-tree-intellij/releases).
2. In IntelliJ IDEA, go to `Settings (Ctrl+Alt+S) -> Plugins`.
3. Click the gear icon next to "Installed" and select **Install Plugin from Disk...**.
4. Choose the zip file and restart the IDE.

---

## Development

### Requirements
- Java 21 LTS
- IntelliJ IDEA (2024.2+ or 2025.x)

### Commands
```bash
# Clone the repository
git clone https://github.com/vin51435/declutter-tree-intellij.git
cd declutter-tree-intellij

# Run unit tests
./gradlew test

# Run isolated sandbox IDE with the plugin installed
./gradlew runIde

# Build the distributable plugin ZIP
./gradlew buildPlugin
```

The output zip will be placed in `build/distributions/`.

---

## Security and Governance

This repository uses GitHub Code Owners (`.github/CODEOWNERS`):
- All pull requests modifying build scripts, plugin descriptors, or workflows require review from [@vin51435](https://github.com/vin51435).
- GitHub Actions CI checks build health, runs unit tests, and validates plugin compatibility against IntelliJ platform APIs on every push and PR.

---

## License

Apache License 2.0. Copyright (c) Vin (https://github.com/vin51435).
