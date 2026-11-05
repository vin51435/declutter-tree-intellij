# Declutter Tree

<p align="center">
  <strong>Intelligent Project View file organization for IntelliJ IDEA.</strong><br>
  <em>Organize Java elements by type and sink auxiliary files (like <code>package-info.java</code>) to the bottom of folders with zero UI lag.</em>
</p>

<p align="center">
  <a href="https://github.com/vin51435/declutter-tree-intellij/actions"><img src="https://img.shields.io/github/actions/workflow/status/vin51435/declutter-tree-intellij/build.yml?branch=main&style=flat-square&logo=github" alt="Build Status"></a>
  <a href="https://plugins.jetbrains.com/"><img src="https://img.shields.io/badge/IntelliJ%20Platform-2024.2+-blue?style=flat-square&logo=intellij-idea" alt="IntelliJ Platform"></a>
  <a href="https://adoptium.net/"><img src="https://img.shields.io/badge/JDK-21-orange?style=flat-square&logo=openjdk" alt="JDK 21"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat-square" alt="License"></a>
</p>

---

## 🌟 Why Declutter Tree?

In typical Java and Kotlin projects, crucial package-level documentation (`package-info.java`) or auxiliary files alphabetically mix with your core business classes. Furthermore, directories often become a disorganized list where interfaces, abstract classes, entities, records, and enums are jumbled together purely by name.

**Declutter Tree** restores clean visual hierarchy to your IntelliJ Project View:
1. **Folders Always on Top:** Directory structures remain untouched at the top of your package.
2. **Type-Based Ordering:** Standard files are cleanly grouped by their Java declaration kind:
   - 🔹 **Interfaces**
   - 🔹 **Abstract Classes**
   - 🔹 **Standard Classes**
   - 🔹 **Final Classes**
   - 🔹 **Records**
   - 🔹 **Enums**
   - 🔹 **Annotations**
3. **Alphabetical Intra-Group Sorting:** Elements within each group remain strictly sorted A-Z.
4. **Bottom File Anchoring:** Auxiliary files (such as `package-info.java`) are gracefully pushed to the very bottom.
5. **Configurable Settings UI:** Easily customize your preferred sorting priorities or add custom bottom files right inside IntelliJ Settings (`Settings → Appearance & Behavior → Declutter Tree`).

---

## 🔍 Visual Comparison

### Before (Standard Alphabetical Jumble)
```text
📁 com.example.order/
├── 📁 service/
├── Order.java                    (Class)
├── OrderCancelledEvent.java      (Record)
├── OrderDTO.java                 (Class)
├── OrderRepository.java          (Interface)
├── OrderStatus.java              (Enum)
├── package-info.java             <-- Trapped right in the middle!
└── ValidOrder.java               (Annotation)
```

### After (Declutter Tree Active)
```text
📁 com.example.order/
├── 📁 service/                   <-- Folders stay on top
├── OrderRepository.java          <-- 1. Interfaces
├── Order.java                    <-- 2. Classes
├── OrderDTO.java                 <-- 2. Classes (sorted A-Z)
├── OrderCancelledEvent.java      <-- 3. Records
├── OrderStatus.java              <-- 4. Enums
├── ValidOrder.java               <-- 5. Annotations
└── package-info.java             <-- 6. Anchored to the bottom!
```

---

## ⚡ Performance & Zero-Lag Architecture

Because this plugin executes inside the IntelliJ Project Tool Window tree rendering loop, it was engineered for **extreme efficiency**:

* **Fast-Path Zero Allocations:** Checks children in-memory using lightweight fast scans. In folders without target files, the collection is returned immediately with 0 object allocations.
* **Stub-Backed Metadata Reading:** Queries Java element types using compiled index stubs (`PsiJavaFile.classes`), avoiding heavy AST syntax tree building or disk I/O.
* **$O(1)$ Lookups:** Fast hash set evaluations for bottom-pinned filenames.
* **DumbAware Resilience:** Implements `DumbAware` so tree rendering is instantaneous and does not freeze while background project indexing is underway.
* **Tree State Stability:** Inherits from `PsiFileNode` and delegates `getEqualityObject()` directly to the underlying `PsiFile`. Tree node expansion, selection, Git/VCS status badges, and bookmarks remain 100% stable.

---

## 🚀 Installation

### Option 1: Install from JetBrains Marketplace *(Upcoming)*
Search for **Declutter Tree** in `Settings → Plugins → Marketplace` and click **Install**.

### Option 2: Install from Disk (.zip)
1. Download the latest `declutter-tree-intellij-x.x.x.zip` from [Releases](https://github.com/vin51435/declutter-tree-intellij/releases).
2. In IntelliJ IDEA: Go to `Settings (Ctrl+Alt+S) → Plugins`.
3. Click the gear icon (**⚙️**) at the top right → select **Install Plugin from Disk...**.
4. Choose the downloaded `.zip` file and restart your IDE.

---

## 🛠️ Building & Developing Locally

### Prerequisites
* **Java 21 LTS**
* IntelliJ IDEA (2024.2+ or 2025.x)

### Commands
```bash
# Clone the repository
git clone https://github.com/vin51435/declutter-tree-intellij.git
cd declutter-tree-intellij

# Run unit tests
./gradlew test

# Launch an isolated development sandbox IDE with the plugin active
./gradlew runIde

# Package the distribution ZIP
./gradlew buildPlugin
```
The packaged plugin will be generated inside `build/distributions/`.

---

## 🔒 Security & Code Ownership

This repository enforces **GitHub Code Owners** (`.github/CODEOWNERS`):
* All pull requests touching plugin descriptors, extension points, or build configurations require review and approval by [@vin51435](https://github.com/vin51435).
* Continuous Integration via GitHub Actions automatically validates API compatibility and test suites on every pull request.

---

## 📄 License

This project is licensed under the [Apache License 2.0](LICENSE).
Created with ❤️ by **Vin** ([@vin51435](https://github.com/vin51435)).
