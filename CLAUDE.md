# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Compile the shared Java library
./gradlew compileJava

# Build the Android app (debug)
./gradlew :android:assembleDebug

# Build the Android app (release)
./gradlew :android:assembleRelease

# Run all checks (no tests exist yet)
./gradlew build
```

There are no tests in this project. The project uses the Gradle wrapper (`gradlew`). Java 17 toolchain is enforced. The Gradle daemon is **disabled** (`org.gradle.daemon=false`).

## Architecture

BombusMod is a mobile XMPP (Jabber) client originally built for J2ME, now being migrated to Android with Jetpack Compose.

### Two-module structure

| Module | Path | Role |
|--------|------|------|
| Root (`:`) | `src/main/java/` | Shared XMPP protocol, UI framework, and business logic (300 Java files, Java 17) |
| Android (`:android`) | `android/src/main/java/` | Android UI layer using Jetpack Compose + Material 3 |

The root module is a `java-library`. The Android module depends on it via `implementation rootProject`. The shared code is **pure Java** — no Android dependencies.

### UI Framework (legacy canvas → Compose migration)

The legacy UI is built on a virtual-list pattern:

- **`VirtualElement`** (`ui/`) — Interface all list items implement. Key methods: `getVHeight()`, `getVWidth()`, `getColorBGnd()`, `getColor()`, `getTipString()`, `onSelect()`, `isSelectable()`, `handleEvent()`. The `drawItem()` default method was **removed** — Compose handles all rendering now.

- **`IconTextElement`** (`ui/`) — Abstract base class implementing `VirtualElement`. Most UI items extend this: `Contact`, `Group`, `MenuItem`, `MessageItem`, `TransferTask`, and form controls in `ui/controls/form/`.

- **`VirtualList`** (`ui/`) — Abstract vertical scrolling list. Subclasses override `getItemCount()` and `getItemRef(int index)` to provide data. Handles scroll, cursor navigation, key events.

- **`VirtualCanvas`** (`ui/`) — Singleton canvas/bridge between the J2ME `Canvas` and the UI framework. Now hooks into Compose.

- **`ComplexString`** (`ui/`) — Rich text renderer that composes strings, images, colors, and nested VirtualElements into a single line. Used by `MessageItem` to render chat messages.

- **`DefForm`** (`ui/controls/form/`) — Base class for form screens (settings, account config). Contains form controls like `TextInput`, `DropChoiceBox`, `TrackItem`, `ColorSelector`, `MultiLine`, `KeyInput`, etc.

**The migration direction**: Canvas-based `drawItem()` rendering is deprecated. New UI uses Compose. When modifying UI code, prefer removing drawItem overrides rather than fixing them.

### Core data model

- **`StaticData`** (`Client/`) — Central god-object singleton. Holds `roster` (contact list), `account` (current user), `config`, and references to most subsystems. Obtain via `StaticData.getInstance()`.

- **`Config`** (`Client/`) — User settings singleton (`Config.getInstance()`). Hundreds of boolean/int/String flags for features, appearance, and protocol options.

- **`Contact`** (`Client/`) — A roster contact. Extends `IconTextElement`. Holds JID, status, messages (`Vector msgs`), subscription state, etc.

- **`Group`** (`Client/`) — A roster group. Extends `IconTextElement`. Contains a `Vector contacts`. Has collapsed/expanded state.

- **`MessageItem`** (`Messages/`) — A chat message in a conversation. Wraps a `Msg` and renders via `ComplexString` lines.

- **`Msg`** (`Messages/`) — Raw message data: body, type, timestamps, read/delivered state.

### XMPP protocol layer

- **`com.alsutton.jabber`** — Low-level XMPP data block library. Key types: `JabberDataBlock` (base), `Iq`, `Message`, `Presence`. These are tree-structured XML elements with attributes, children, and namespaces.

- **`xmpp/`** — XMPP protocol implementation: login (including SASL mechanisms in `xmpp/login/sasl/`), roster synchronization, presence handling, message routing, MUC extensions, and XMPP entity capabilities.

- **`Jid`** (`xmpp/`) — JID parsing and manipulation. `JidUtils` provides transport detection.

### Other key packages

| Package | Purpose |
|---------|---------|
| `Account/` | Account configuration and multi-account selection |
| `Alerts/` | In-app alert/notification popups |
| `Archive/` | Message archiving to storage |
| `Colors/` | Color theme system (`ColorTheme` singleton, `ColorVisualItem`) |
| `Conference/` | Multi-User Chat (MUC) — room joining, bookmarks, member lists |
| `Fonts/` | Font configuration and caching (`FontCache`) |
| `History/` | Chat history persistence |
| `IE/` | Import/Export (data, templates, keys) |
| `io/` | File I/O, file transfer (SOCKS5 Bytestreams, IBB), TLS, persistent storage via NVS |
| `locale/` | Localization (`SR` holds string constants) |
| `PEP/` | Personal Eventing Protocol (user moods, activities, tunes, locations) |
| `PrivacyLists/` | XMPP privacy lists |
| `ServiceDiscovery/` | XEP-0030 service discovery |
| `VCard/` | User profile/vCard handling |
| `xml/` | Lightweight XML parser |
| `javax/microedition/` | J2ME compatibility shims (MIDlet, lcdui Canvas/Graphics/Image/Font, RMS storage). Being phased out. |
| `org/microemu/` | MicroEmulator platform layer for running J2ME code on Android |

### Preprocessor directives

The codebase uses C-style preprocessor directives (`//#ifdef FEATURE`, `//#ifndef`, `//#else`, `//#endif`) to conditionally include features. **These are broken and unused** — treat them as dead comments. Do not preserve or maintain them.

### No `@Override` annotations

The main source tree (not Android) does not consistently use `@Override`. When checking if a method overrides a parent, compare signatures structurally: same name + same parameter types + same return type across the class hierarchy.

## File patterns to know

- `//#ifdef ... //#endif` — Dead preprocessor guards, ignore them
- `cf` — Shorthand for `Config.getInstance()` (used in many classes)
- `sd` — Shorthand for `StaticData.getInstance()`
- `il` — ImageList instance (icons for roster elements)
