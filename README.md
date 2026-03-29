# StreamFlow – Music Streaming Platform
### CS 151 Spring 2026 | Project 2

---

## Overview

StreamFlow is a Java-based simulation of a music streaming platform similar to Spotify or Apple Music. Users can register as Free or Premium subscribers, browse a catalog of artists, albums, and songs, build playlists, stream content, and download tracks for offline listening (Premium only). The system enforces real-world business rules such as explicit-content restrictions for Free users, monthly listening caps, duplicate prevention in playlists, and a hard ceiling of 100 instances per class.

---

## Design

### Class Hierarchy

```
User  (Abstract)
├── FreeUser       – 30 hr/month cap, no explicit content, no downloads
└── PremiumUser    – Unlimited streaming, offline downloads, explicit content

Streamable  (Interface)
└── Song           – implements Streamable  (extensible to Podcast, AudioBook, etc.)

Platform           – Central registry / service layer
Artist             – Discography management
Album              – Groups songs, tracks total duration
Playlist           – User-owned, ordered song list
```

### Key Design Decisions

- **Abstract `User` class** — `FreeUser` and `PremiumUser` share identity fields (userId, email, password) but implement `streamContent()` differently. The abstract class enforces this contract while eliminating duplicate code for shared fields and getters/setters.
- **`Streamable` interface** — Decouples streaming logic from the concrete type. `Song` implements it today; `Podcast` or `AudioBook` could be added tomorrow without touching any User or Platform code.
- **Single `Platform` registry** — Acts as the service layer the UI delegates to. All object creation goes through `Platform`, which enforces the 100-instance cap, validates uniqueness (no duplicate emails), and manages ID generation.
- **Single `Scanner` via `InputHandler`** — One Scanner instance is shared across the entire application. All reads route through `InputHandler.readLine()`, which is the single place the `EXIT` keyword is caught globally.
- **Static instance counters per class** — Each class tracks its own `instanceCount` statically. The cap `ModelLimits.MAXIMUM_INSTANCES` (100) is defined in a single class per the project specification.
- **No persistent storage** — State lives in memory. `DataLoader` seeds sample data on startup so the system is immediately demoable.

### Custom Exceptions

| Exception | When Thrown |
|---|---|
| `InstanceLimitException` | Creating the 101st object of any class |
| `NotFoundException` | Looking up a user/artist/song/album by ID that does not exist |
| `StreamingPermissionException` | Free user attempts explicit content or exceeds monthly hour cap |

### OOP Principles Applied

| Principle | Where |
|---|---|
| Abstract class | `User` — enforces `streamContent()`, `getSubscriptionType()`, `getMonthlyHourLimit()` |
| Interface | `Streamable` — implemented by `Song` |
| Encapsulation | All fields `private`; exposed via validated getters/setters |
| Inheritance | `FreeUser`, `PremiumUser` extend `User` |
| Polymorphism | `streamContent(Streamable)` behaves differently per subclass |
| Method overrides | `toString()` in all 7 model classes; abstract methods in both user subclasses |
| Exception handling | 3 custom exceptions, each with at least one `catch` block |

---

## Installation Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.8+ (recommended)

### Build and Run with Maven

```bash
# Clone the repository
git clone https://github.com/VeedhiBhanushali/MusicStreamingPlatform-CS151-Spring2026.git
cd MusicStreamingPlatform-CS151-Spring2026

# Compile
mvn compile

# Run unit tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.musicstream.ui.MainMenu"
```

### Build and Run without Maven

```bash
# From project root — compile all source files
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt

# Run
java -cp out com.musicstream.ui.MainMenu
```

---

## Usage

When the program starts, a sample dataset is automatically loaded:
- **4 artists** — Taylor Swift, Kendrick Lamar, Billie Eilish, The Weeknd
- **10 songs** across multiple genres
- **5 albums** (4 published)
- **2 users** — alice (Free), bob (Premium)
- **2 public playlists**

### Navigation

All input uses numbered menus. Enter a number and press Enter. Type **EXIT** at any prompt to quit immediately.

```
MAIN MENU
  1. User Management       – register, stream, playlists, downloads, password, renew, etc.
  2. Artist Management     – add, verify, update listener counts
  3. Song Management       – add, view, play songs
  4. Album Management      – create albums, add songs, publish
  5. Playlist Management   – add/remove songs, shuffle, reorder
  6. Platform Stats        – summary of all counts
  7. Top Songs             – ranked by total play count
  8. Search by Genre       – filter songs by genre
  0. Exit
```

### Preloaded Sample IDs

| Type | ID | Name |
|---|---|---|
| Artist | A001 | Taylor Swift |
| Artist | A002 | Kendrick Lamar |
| Artist | A003 | Billie Eilish |
| Artist | A004 | The Weeknd |
| Song | S001 | Anti-Hero |
| Song | S002 | Shake It Off |
| Song | S003 | Lover |
| Song | S004 | HUMBLE. (explicit) |
| Song | S005 | DNA. (explicit) |
| Song | S006 | Not Like Us (explicit) |
| Song | S007 | bad guy |
| Song | S008 | Happier Than Ever |
| Song | S009 | Blinding Lights |
| Song | S010 | Save Your Tears |
| Album | AL001 | Midnights |
| Album | AL002 | Lover |
| Album | AL003 | DAMN. |
| Album | AL004 | When We All Fall Asleep... |
| Album | AL005 | After Hours |
| User | U001 | alice (Free) |
| User | U002 | bob (Premium) |
| Playlist | P001 | Alice's Faves (public) |
| Playlist | P002 | Hip-Hop Heat (public) |

### Example Walkthrough

```
# Stream a clean song as Free user
Main Menu → 1 → 6
User ID: U001  |  Song ID: S001   ← plays fine

# Attempt explicit content as Free user (blocked with error)
Main Menu → 1 → 6
User ID: U001  |  Song ID: S004   ← StreamingPermissionException caught

# Download a song as Premium user
Main Menu → 1 → 9
User ID: U002  |  Song ID: S009

# View top 5 songs by play count
Main Menu → 7  |  Enter: 5

# Search songs by genre
Main Menu → 8  |  Genre: Hip-Hop

# Add a song to Alice's playlist
Main Menu → 5 → 2
User ID: U001  |  Playlist ID: P001  |  Song ID: S010
```

---

## Contributions

| Team Member | Classes & Responsibilities |
|---|---|
| **Veedhi Bhanushali** | `User` (abstract), `FreeUser`, `PremiumUser`, `Song`, `Artist`, `Album`, `Playlist`, `Platform`, `ModelLimits`, `Streamable` interface, all 3 custom exceptions, `DataLoader`, `InputHandler`, `MainMenu` UI, `PlatformTest` + `InstanceLimitTest`, README, UML class diagram |

---

## File Structure

```
MusicStreamingPlatform-CS151-Spring2026/
├── README.md
├── pom.xml
├── UML_ClassDiagram.png   (UML at repository root per spec; source also in uml/)
├── uml/
│   └── UML_ClassDiagram.svg
└── src/
    ├── main/java/com/musicstream/
    │   ├── interface_/
    │   │   └── Streamable.java
    │   ├── model/
    │   │   ├── User.java           (abstract)
    │   │   ├── FreeUser.java
    │   │   ├── PremiumUser.java
    │   │   ├── Song.java
    │   │   ├── Artist.java
    │   │   ├── Album.java
    │   │   ├── Playlist.java
    │   │   ├── Platform.java
    │   │   └── ModelLimits.java
    │   ├── exception/
    │   │   ├── InstanceLimitException.java
    │   │   ├── NotFoundException.java
    │   │   └── StreamingPermissionException.java
    │   ├── util/
    │   │   ├── DataLoader.java
    │   │   └── InputHandler.java
    │   └── ui/
    │       └── MainMenu.java
    └── test/java/com/musicstream/
        ├── PlatformTest.java
        ├── InstanceLimitTest.java
        └── ModelTestSupport.java
```
