# TimelyTrack

An app that helps track duration-based events and visualize the data for analysis. 
Created with Kotlin, Jetpack Compose and follows MVVM structure.


# ⏱️ TimelyTrack

A sleek, modern Android application that lets users log and visualize duration-based events. Built entirely with Jetpack Compose and the latest Material 3 design principles, this app was developed to practice advanced Android development patterns while solving a real-world problem: tracking how we spend our time, or in some cases tracking certain life events that interrupt and take up our time. 

---

## ✨ Features

- 🏠 **Home Screen**  
  Add, group, and manage time logs with a single tap. Swipe to delete, long-press to multi-select, or edit via a bottom sheet.

- 📊 **History Screen**  
  Dynamic charts powered by [Vico](https://github.com/Patryk27/Vico) show event frequency across days, weeks, hours, and months. Includes a date range selector and visibility filters.

- 📁 **Persistent Storage**  
  Uses Room for local storage of logs and supports complete CRUD operations.

- ⚙️ **Architecture**  
  MVVM with clean separation between UI, ViewModels, and data layers via dependency injection and repository patterns.

- 🎨 **Jetpack Compose + Material 3**  
  UI is 100% declarative, responsive, and built using the latest Material Design guidelines.

---
## 🔎 Use cases
- Monitor interruptions or medical events such as narcoleptic episodes, interruptions, etc.

<!-- ## 📷 Screenshots -->
<!-- Add screenshots or animated GIFs -->
<!-- ![Screenshot1](screens/screen1.png) -->

<!-- --- -->

## 🚧 Upcoming Features and Ideas

I’m continuing to expand this project to deepen my expertise in modern Android development. Some potential ideas and work-in-progress features include:

- ✨ **Local AI to analyze logs** - Let users give prompts to contextually define what is being tracked and interpret data
- 🏷️ **Custom Categories** - Let users assign categories/tags to entries
- 🔄 **Backup & Export** - Export logs to CSV
---

## 💡 Why I Built This

This project serves as both a productivity tool and a learning sandbox aiming for clean code, modularity, and thoughtful UX. I built TimelyTrack to:

- Strengthen my Android and Kotlin fundamentals using real-world patterns
- Explore Material 3 and Jetpack Compose in a hands-on environment
- Demonstrate practical app architecture and UI/UX decisions
- 
---

## 🔧 Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3, with MVVM architecture
- **Data:** Room Database
- **Charts:** Vico

