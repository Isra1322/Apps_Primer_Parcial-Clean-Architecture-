# Fix Android Resource Linking Error

The project is missing essential resources referred to in `AndroidManifest.xml`, causing the `AAPT: error: resource mipmap/ic_launcher not found` and likely other linking errors for strings and themes.

## Proposed Changes

### [Component Name]

#### [NEW] [strings.xml](file:///C:/Users/Usuario/Desktop/AplicacionesMoviles/Practicas_2_3_4/CannonGame/app/src/main/res/values/strings.xml)
Define the `app_name` string.

#### [NEW] [themes.xml](file:///C:/Users/Usuario/Desktop/AplicacionesMoviles/Practicas_2_3_4/CannonGame/app/src/main/res/values/themes.xml)
Define the `Theme.Doodlz` style.

#### [NEW] [colors.xml](file:///C:/Users/Usuario/Desktop/AplicacionesMoviles/Practicas_2_3_4/CannonGame/app/src/main/res/values/colors.xml)
Define basic colors for the app and icons.

#### [NEW] [ic_launcher.xml](file:///C:/Users/Usuario/Desktop/AplicacionesMoviles/Practicas_2_3_4/CannonGame/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml)
Define the adaptive launcher icon.

#### [NEW] [ic_launcher_round.xml](file:///C:/Users/Usuario/Desktop/AplicacionesMoviles/Practicas_2_3_4/CannonGame/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml)
Define the round adaptive launcher icon.

#### [NEW] [ic_launcher_background.xml](file:///C:/Users/Usuario/Desktop/AplicacionesMoviles/Practicas_2_3_4/CannonGame/app/src/main/res/drawable/ic_launcher_background.xml)
Background for the adaptive icon.

#### [NEW] [ic_launcher_foreground.xml](file:///C:/Users/Usuario/Desktop/AplicacionesMoviles/Practicas_2_3_4/CannonGame/app/src/main/res/drawable/ic_launcher_foreground.xml)
Foreground for the adaptive icon.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:processDebugResources` to verify that resource linking succeeds.
