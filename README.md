# AtelierAndroid_TP05

Application Android développée en Kotlin dans le cadre du module **Mobile Development**, consacrée à la gestion des participants, aux menus, au cycle de vie Android et à la restauration d'état.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2034-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.5-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

## Table des matières

- [1. Présentation](#1-présentation)
- [2. Fonctionnalités](#2-fonctionnalités)
  - [2.1. Gestion des participants](#21-gestion-des-participants)
  - [2.2. Ressources](#22-ressources)
  - [2.3. Menus et filtres](#23-menus-et-filtres)
  - [2.4. Cycle de vie](#24-cycle-de-vie)
  - [2.5. Préservation de l'état](#25-préservation-de-létat)
- [3. Technologies utilisées](#3-technologies-utilisées)
- [4. Structure du projet](#4-structure-du-projet)
- [5. Prérequis et configuration](#5-prérequis-et-configuration)
- [6. Compilation et installation](#6-compilation-et-installation)
- [7. Observations du cycle de vie](#7-observations-du-cycle-de-vie)
- [8. Scénarios de test T01–T12](#8-scénarios-de-test-t01t12)
- [9. Réponses aux questions du TP Q1–Q9](#9-réponses-aux-questions-du-tp-q1q9)
- [10. Dépannage](#10-dépannage)
- [11. Auteur](#11-auteur)

---

## 1. Présentation

**AtelierAndroid_TP05** est une application Android pédagogique qui prolonge les fonctionnalités du TP03 de gestion des participants.

Cette version met en pratique plusieurs concepts fondamentaux du développement Android :

- Gestion d'un formulaire d'inscription.
- Ajout, consultation, recherche, modification et suppression de participants.
- Utilisation de ressources XML pour les chaînes, dimensions et menus.
- Filtrage dynamique des participants par groupe.
- Utilisation de `PopupMenu` et `AlertDialog`.
- Observation des callbacks du cycle de vie d'une activité.
- Sauvegarde et restauration des données à l'aide d'un `Bundle`.
- Adaptation de l'interface à l'orientation de l'écran.

Le projet est développé en **Kotlin**, compilé avec **Gradle 8.5** et **Android Gradle Plugin 8.2.2**. L'environnement de développement utilisé est **Visual Studio Code**, sans dépendance à Android Studio comme IDE.

---

## 2. Fonctionnalités

### 2.1. Gestion des participants

Le formulaire d'inscription comprend les champs suivants :

- **Nom** : nom du participant.
- **Prénom** : prénom du participant.
- **Âge** : âge du participant.
- **Groupe** : sélection parmi G1, G2 et G3 à l'aide d'un `Spinner`.
- **Support du test** : sélection à l'aide de boutons radio.
- **Confirmation** : validation à l'aide d'une case à cocher.

Les opérations disponibles sont les suivantes :

| Opération | Description |
|---|---|
| Enregistrer | Ajoute un participant à la collection `mutableListOf`. |
| Afficher | Présente un aperçu des informations saisies. |
| Liste | Affiche les participants dans un `ListView`. |
| Rechercher | Recherche un participant selon les critères prévus par l'application. |
| Effacer | Réinitialise les champs du formulaire. |
| Modifier | Met à jour le participant sélectionné sans créer de doublon. |
| Supprimer | Affiche une boîte de dialogue de confirmation avant la suppression. |

Les données sont conservées en mémoire pendant l'exécution de l'application. La sauvegarde dans un `Bundle` permet de restaurer les participants lors d'une recréation de l'activité, notamment après une rotation de l'écran.

### 2.2. Ressources

Les ressources de l'application sont externalisées dans des fichiers XML afin de faciliter la maintenance et l'adaptation de l'interface.

#### `strings.xml`

Centralise les chaînes de caractères utilisées dans l'application, notamment les libellés des menus.

#### `values/dimens.xml`

Définit les dimensions utilisées par défaut :

| Ressource | Valeur | Utilisation |
|---|---|---|
| `espace_page` | `20dp` | Espacement principal de la page. |
| `espace_section` | `12dp` | Espacement entre les sections. |

#### `values-land/dimens.xml`

Adapte l'interface en orientation paysage :

| Ressource | Valeur | Utilisation |
|---|---|---|
| `espace_page` | `12dp` | Réduction de l'espacement principal pour optimiser l'espace disponible. |

Android sélectionne automatiquement les ressources adaptées à la configuration de l'appareil.

### 2.3. Menus et filtres

#### Menu d'options

Le menu est défini dans `res/menu/menu_atelier.xml` et contient cinq éléments :

| Élément | Description |
|---|---|
| Tous | Affiche tous les participants. |
| G1 | Affiche les participants du groupe G1. |
| G2 | Affiche les participants du groupe G2. |
| G3 | Affiche les participants du groupe G3. |
| À propos | Affiche une boîte de dialogue présentant le module Mobile Development. |

Le menu est affiché par un `PopupMenu` ancré au bouton `btnOptions`.

Il est construit à l'aide de `menuInflater.inflate()`. Une instruction `when(item.itemId)` permet ensuite de distribuer les actions selon l'élément sélectionné.

#### Filtrage par groupe

Le `Spinner` `spFiltreGroupe` utilise le tableau de ressources `filtres_groupes`, contenant les choix suivants :

- Tous
- G1
- G2
- G3

Le `ListView` `lvParticipants` affiche les participants correspondant au filtre sélectionné.

Le filtrage ne nécessite pas de supprimer les participants des données originales : il modifie uniquement la collection utilisée pour l'affichage.

#### Modification et suppression

Le bouton **Modifier** met à jour directement le participant sélectionné dans la collection d'origine. Il ne crée donc pas une nouvelle entrée.

Le bouton **Supprimer** affiche une `AlertDialog` proposant deux actions :

- **Supprimer** : confirme la suppression.
- **Annuler** : ferme la boîte de dialogue sans modifier les données.

### 2.4. Cycle de vie

Les principaux callbacks du cycle de vie de `MainActivity` sont journalisés dans Logcat avec le tag :

```kotlin
private val TAG = "AtelierCycle"
```

Les méthodes observées sont :

- `onCreate()`
- `onStart()`
- `onResume()`
- `onPause()`
- `onStop()`
- `onDestroy()`

Ces journaux permettent d'observer les transitions entre les états de l'activité lors de son lancement, de son passage en arrière-plan, de son retour au premier plan et d'un changement d'orientation.

### 2.5. Préservation de l'état

L'application utilise `onSaveInstanceState()` et `onRestoreInstanceState()` pour préserver son état lors d'une recréation de l'activité.

#### Sauvegarde des participants

Chaque participant est sérialisé sous la forme de cinq chaînes :

1. `nom`
2. `prenom`
3. `age`
4. `groupe`
5. `support`

Les valeurs sont stockées dans une `StringArrayList` intégrée au `Bundle`. L'index du filtre sélectionné est également sauvegardé.

#### Restauration sécurisée

La méthode `restaurerParticipants(bundle)` reconstruit la collection des participants à partir des données sauvegardées.

Elle applique les contrôles suivants :

- Retour immédiat si le nombre de champs n'est pas un multiple de cinq.
- Ignorance des âges non numériques ou situés en dehors de l'intervalle 1–120.
- Ignorance des participants dont le nom ou le prénom est vide.
- Ignorance des doublons possédant le même nom et le même prénom.

#### Restauration du filtre et actualisation

Après la restauration, l'index sauvegardé est appliqué au `Spinner`, puis l'affichage est actualisé.

Le projet utilise un callback privé nullable :

```kotlin
private var rafraichirVue: (() -> Unit)? = null
```

La fonction locale `actualiserAffichage()`, définie dans `onCreate()`, est exposée à travers ce callback. Cela permet notamment à `onRestoreInstanceState()` et au `PopupMenu` de déclencher la mise à jour de l'interface sans dupliquer le code d'actualisation.

---

## 3. Technologies utilisées

| Technologie | Version / configuration | Rôle |
|---|---|---|
| Kotlin | Selon la configuration du projet | Langage de programmation principal. |
| Android SDK | API 34 | Plateforme cible de l'application. |
| Android Gradle Plugin | 8.2.2 | Intégration des outils de compilation Android. |
| Gradle | 8.5 | Automatisation de la compilation et de la génération de l'APK. |
| Java Development Kit | JDK 21 | Environnement Java utilisé pour les outils de développement. |
| Visual Studio Code | Version compatible | Édition du code source et exécution des commandes. |
| XML | Ressources Android | Définition de l'interface, des chaînes, des dimensions et des menus. |
| Android Debug Bridge | `adb` | Installation et lancement de l'application sur un appareil Android. |

---

## 4. Structure du projet

```text
AtelierAndroid_TP05/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── <package>/
│           │       ├── MainActivity.kt
│           │       └── Participant.kt
│           └── res/
│               ├── layout/
│               │   └── activity_main.xml
│               ├── values/
│               │   ├── arrays.xml
│               │   ├── strings.xml
│               │   └── dimens.xml
│               ├── values-land/
│               │   └── dimens.xml
│               └── menu/
│                   └── menu_atelier.xml
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle
```

### Rôle des fichiers principaux

| Fichier | Description |
|---|---|
| `Participant.kt` | Définit le modèle de données d'un participant. |
| `MainActivity.kt` | Contient la logique de l'application, les opérations sur les participants, les menus, les filtres et la restauration de l'état. |
| `activity_main.xml` | Définit l'interface de l'activité principale. |
| `arrays.xml` | Définit les tableaux utilisés par les `Spinner`, notamment les filtres de groupe. |
| `strings.xml` | Centralise les chaînes de caractères. |
| `values/dimens.xml` | Définit les dimensions par défaut. |
| `values-land/dimens.xml` | Définit les dimensions adaptées à l'orientation paysage. |
| `res/menu/menu_atelier.xml` | Définit les cinq éléments du menu d'options. |
| `AndroidManifest.xml` | Déclare l'application et ses composants Android. |
| `app/build.gradle` | Configure le module Android, ses SDK et ses dépendances. |
| `gradle/wrapper/gradle-wrapper.properties` | Configure la distribution Gradle utilisée par le Wrapper. |
| `local.properties` | Indique le chemin local du SDK Android. |

---

## 5. Prérequis et configuration

Avant de compiler le projet, vérifier la présence des outils suivants :

- JDK 21.
- Android SDK avec les composants nécessaires à l'API 34.
- Gradle Wrapper fourni avec le projet.
- `adb`, inclus dans les Android SDK Platform-Tools.
- Visual Studio Code ou un autre éditeur de texte.

### 5.1. Vérifier Java

```bash
java -version
```

La version utilisée doit être compatible avec la configuration Gradle du projet.

Vérifier également le compilateur Java :

```bash
javac -version
```

### 5.2. Configurer `local.properties`

Le fichier `local.properties` doit contenir le chemin du SDK Android installé sur la machine.

Exemple sous Linux :

```properties
sdk.dir=/home/<utilisateur>/Android/Sdk
```

Adapter le chemin à l'installation locale.

Ce fichier est spécifique à l'environnement de développement et ne doit pas contenir de chemin propre à une autre machine.

### 5.3. Configurer `gradle.properties`

Le fichier `gradle.properties` contient les propriétés Gradle du projet.

Exemple minimal :

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
```

Conserver les autres propriétés nécessaires si elles existent déjà dans le projet.

### 5.4. Vérifier le SDK

Vérifier que la configuration Android du module utilise les valeurs attendues :

- `compileSdk`: 34.
- `targetSdk`: 34.
- `minSdk`: 24.

L'API minimale 24 correspond à Android 7.0.

---

## 6. Compilation et installation

Toutes les commandes suivantes sont exécutées depuis la racine du dépôt.

### 6.1. Récupérer le projet

```bash
git clone https://github.com/fakherneila/AtelierAndroid_TP05.git
cd AtelierAndroid_TP05
```

### 6.2. Vérifier Gradle

Sous Linux ou macOS :

```bash
chmod +x gradlew
./gradlew --version
```

Sous Windows :

```bash
gradlew.bat --version
```

Le Wrapper doit utiliser Gradle 8.5.

### 6.3. Compiler l'application

```bash
./gradlew assembleDebug
```

Pour vérifier la configuration et les tâches disponibles :

```bash
./gradlew tasks
```

En cas de problème de dépendances, il est possible de forcer leur actualisation :

```bash
./gradlew assembleDebug --refresh-dependencies
```

### 6.4. Localiser l'APK

Après une compilation réussie, l'APK de débogage se trouve normalement à l'emplacement suivant :

```text
app/build/outputs/apk/debug/app-debug.apk
```

### 6.5. Installer l'application

Connecter un appareil Android avec le débogage USB activé ou démarrer un émulateur.

Vérifier que l'appareil est détecté :

```bash
adb devices
```

Installer l'APK :

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

L'option `-r` permet de réinstaller l'application en conservant ses données lorsque cela est possible.

### 6.6. Lancer l'application

Remplacer `<package>` par le véritable identifiant d'application déclaré dans le projet.

```bash
adb shell am start -n <package>/.MainActivity
```

### 6.7. Observer les journaux

Afficher les messages du cycle de vie :

```bash
adb logcat -s AtelierCycle
```

Pour arrêter l'affichage des journaux, utiliser `Ctrl+C`.

---

## 7. Observations du cycle de vie

Les callbacks ci-dessous correspondent au comportement Android attendu. L'ordre exact peut varier selon la version du système, la configuration de l'appareil et la manière dont l'activité est lancée ou arrêtée.

| ID | Action | Callbacks attendus | Observation |
|---|---|---|---|
| D1 | Lancer l'application pour la première fois | `onCreate()` → `onStart()` → `onResume()` | L'activité est créée, devient visible puis interactive. |
| D2 | Appuyer sur le bouton Accueil | `onPause()` → `onStop()` | L'activité perd le premier plan puis devient invisible. |
| D3 | Revenir à l'application depuis les applications récentes | Généralement `onRestart()` → `onStart()` → `onResume()` si l'activité existe toujours | L'activité revient au premier plan sans nécessairement être recréée. |
| D4 | Changer l'orientation de l'appareil | Généralement `onPause()` → `onStop()` → `onDestroy()`, puis `onCreate()` → `onStart()` → `onResume()` | L'activité est recréée et son état peut être restauré à partir du `Bundle`. |

**Remarque :** Android peut détruire un processus en arrière-plan pour récupérer des ressources. Il ne faut donc pas supposer que les données conservées uniquement en mémoire survivent à toutes les situations. La sauvegarde d'état améliore la restauration, mais ne remplace pas une base de données persistante.

---

## 8. Scénarios de test T01–T12

Les scénarios suivants permettent de vérifier les principales fonctionnalités de l'application.

| ID | Action | Résultat attendu |
|---|---|---|
| T01 | Saisir un participant valide et appuyer sur Enregistrer. | Le participant est ajouté à la collection en mémoire et apparaît dans la liste. |
| T02 | Saisir les informations d'un participant et appuyer sur Afficher. | Un aperçu des informations saisies est présenté. |
| T03 | Enregistrer plusieurs participants puis ouvrir Liste. | Les participants enregistrés sont affichés dans le `ListView`. |
| T04 | Rechercher un participant existant. | Le participant correspondant aux critères de recherche est retrouvé. |
| T05 | Saisir des informations puis appuyer sur Effacer. | Les champs du formulaire sont réinitialisés. |
| T06 | Sélectionner un participant, modifier ses informations et appuyer sur Modifier. | Les informations sont mises à jour dans la collection d'origine, sans duplication. |
| T07 | Sélectionner un participant, appuyer sur Supprimer puis sur Annuler. | La boîte de dialogue se ferme et le participant reste dans la liste. |
| T08 | Sélectionner un participant, appuyer sur Supprimer puis confirmer la suppression. | Le participant est supprimé et la liste est actualisée. |
| T09 | Sélectionner successivement Tous, G1, G2 et G3 dans le filtre. | Le `ListView` affiche tous les participants ou uniquement ceux du groupe choisi. |
| T10 | Ouvrir le menu d'options puis sélectionner À propos. | Une `AlertDialog` présentant le module Mobile Development apparaît. |
| T11 | Changer l'orientation de l'appareil après avoir enregistré des participants et sélectionné un filtre. | Les participants et le filtre sont restaurés, puis la liste est actualisée. |
| T12 | Examiner les journaux Logcat pendant le lancement, la mise en arrière-plan et le retour à l'application. | Les callbacks du cycle de vie sont visibles avec le tag `AtelierCycle`. |

---

## 9. Réponses aux questions du TP Q1–Q9

### Q1. Quelle est la différence entre `participants` et `participantsAffiches` ?

`participants` représente la collection principale contenant tous les participants enregistrés. `participantsAffiches` représente la collection utilisée pour afficher les résultats, notamment après l'application d'un filtre. La séparation permet de filtrer la liste sans supprimer les participants de la collection originale.

### Q2. Quel est le rôle des ressources `@string` et `@dimen` ?

`@string` permet de référencer des chaînes de caractères définies dans `strings.xml`, tandis que `@dimen` référence des dimensions définies dans les fichiers de ressources correspondants. Cette séparation centralise les valeurs et facilite la maintenance, la traduction et l'adaptation de l'interface.

### Q3. Pourquoi la position d'un élément dans le `ListView` filtré peut-elle différer de son index dans `participants` ?

La position sélectionnée dans le `ListView` correspond à la collection affichée, qui peut être filtrée. Elle ne correspond donc pas nécessairement à l'index du participant dans la collection principale. Il faut retrouver le participant sélectionné dans la collection d'origine avant de le modifier ou de le supprimer.

### Q4. Que fait la boîte de dialogue « À propos » ?

L'élément À propos du `PopupMenu` déclenche l'affichage d'une `AlertDialog` présentant le module Mobile Development. Cette boîte de dialogue fournit une information à l'utilisateur sans modifier les données des participants.

### Q5. Quelle est la différence entre `onCreate()`, `onResume()` et `onStop()` ?

`onCreate()` initialise l'activité et ses composants. `onResume()` est appelé lorsque l'activité revient au premier plan et devient interactive. `onStop()` est appelé lorsque l'activité n'est plus visible.

### Q6. Pourquoi une rotation déclenche-t-elle généralement `onCreate()` et pourquoi l'ordre des callbacks peut-il varier ?

Lors d'un changement de configuration, Android détruit généralement l'ancienne instance de l'activité et en crée une nouvelle afin de charger les ressources adaptées. Les callbacks observés peuvent varier selon l'état de l'activité, la configuration du système et le déroulement des transitions. Il faut donc se baser sur les journaux réellement observés plutôt que supposer un ordre universel.

### Q7. Pourquoi vérifier `champs.size % 5` et utiliser `toIntOrNull()` ?

Chaque participant est sérialisé en cinq champs. La vérification `champs.size % 5 != 0` permet de détecter des données incomplètes avant de reconstruire les participants. `toIntOrNull()` évite une exception lorsque l'âge n'est pas un entier valide ; la valeur obtenue peut ensuite être vérifiée pour s'assurer qu'elle est comprise entre 1 et 120.

### Q8. Quelle est la différence entre un `Bundle` et une base de données persistante ?

Un `Bundle` permet de sauvegarder temporairement des données d'état, notamment pour restaurer une activité après une recréation. Il n'est pas destiné à conserver durablement les données entre toutes les exécutions de l'application. Une base de données persistante, comme Room ou SQLite, permet de conserver les participants même après la fermeture de l'application ou la destruction de son processus.

### Q9. Pourquoi actualiser la vue après la restauration du filtre ?

Restaurer uniquement l'index du `Spinner` ne garantit pas que le `ListView` affiche immédiatement les résultats correspondant au filtre. L'appel à `rafraichirVue?.invoke()` déclenche la logique commune d'actualisation, ce qui synchronise la liste affichée avec le filtre restauré et les données disponibles.

---

## 10. Dépannage

| Symptôme | Cause possible | Solution |
|---|---|---|
| `array/filtres_groupes` introuvable | Le tableau `filtres_groupes` n'est pas défini dans `arrays.xml`, ou son nom ne correspond pas à celui utilisé dans le code Kotlin. | Déclarer le tableau dans `res/values/arrays.xml` et vérifier son identifiant ainsi que sa référence dans le `Spinner`. |
| Plusieurs erreurs de compilation liées à `Participant.kt` | La classe est absente, mal nommée, placée dans un package incorrect ou possède une déclaration incompatible avec son utilisation. | Vérifier le nom du fichier, le package, la déclaration de la classe et les propriétés attendues dans `MainActivity.kt`. Corriger la première erreur de compilation avant d'examiner les erreurs en cascade. |
| `Unsupported class file major version` ou incompatibilité Java/Gradle | Le JDK utilisé pour exécuter Gradle n'est pas compatible avec la configuration du projet. Une ancienne version, comme Java 11, peut être sélectionnée par défaut. | Installer JDK 21, vérifier `java -version`, puis contrôler `JAVA_HOME` et le `PATH`. Vérifier également la JVM réellement utilisée par `./gradlew --version`. |
| `Cannot mutate dependencies` | Une dépendance Gradle est modifiée trop tard, après la résolution du graphe de dépendances, ou une configuration de build utilise une API incompatible avec la version de Gradle. | Rechercher les modifications tardives des configurations de dépendances dans les scripts Gradle et les plugins. Déclarer les dépendances dans les blocs de configuration appropriés et vérifier la compatibilité des plugins avec Gradle 8.5. |
| `SDK location not found` | Le chemin du SDK Android n'est pas configuré ou le fichier `local.properties` est incorrect. | Créer ou corriger `local.properties` avec la propriété `sdk.dir` correspondant au chemin réel du SDK. |
| `Permission denied` lors de l'exécution de `./gradlew` | Le script Gradle Wrapper n'est pas exécutable. | Exécuter `chmod +x gradlew`, puis relancer la compilation. |
| `SDK platform 34` introuvable | La plateforme Android API 34 n'est pas installée. | Installer Android SDK Platform 34 avec le gestionnaire de SDK Android, puis relancer la compilation. |
| `adb devices` ne détecte aucun appareil | Le débogage USB est désactivé, l'appareil n'est pas autorisé ou aucun émulateur n'est lancé. | Activer le débogage USB, accepter l'autorisation sur l'appareil ou démarrer un émulateur, puis relancer `adb devices`. |
| Les participants ne sont pas restaurés après une rotation | La sauvegarde n'inclut pas les données nécessaires ou la clé utilisée pour les sauvegarder diffère de celle utilisée pour les restaurer. | Vérifier `onSaveInstanceState()`, les clés du `Bundle`, `restaurerParticipants(bundle)` et l'ordre de restauration du filtre. |
| Le filtre est restauré, mais la liste ne change pas | Le `Spinner` est restauré sans déclencher l'actualisation de la liste. | Appeler `rafraichirVue?.invoke()` après avoir appliqué l'index du filtre. |
| La modification crée un doublon | Le code ajoute un nouvel objet au lieu de modifier le participant sélectionné. | Retrouver le participant original dans `participants` et mettre à jour ses propriétés. |
| Annuler supprime malgré tout le participant | La suppression est exécutée avant la confirmation ou le bouton Annuler déclenche une modification des données. | Placer la suppression uniquement dans l'action de confirmation. Le bouton Annuler doit fermer la boîte de dialogue sans modifier la collection. |
| Les dimensions ne changent pas en paysage | Le fichier `values-land/dimens.xml` est absent, mal nommé ou ne définit pas la ressource attendue. | Vérifier le chemin `res/values-land/dimens.xml`, le nom `espace_page` et la référence `@dimen/espace_page` dans le layout. |

### Commandes de diagnostic utiles

Afficher la version de Java :

```bash
java -version
```

Afficher la version de Gradle et la JVM utilisée :

```bash
./gradlew --version
```

Nettoyer puis reconstruire le projet :

```bash
./gradlew clean assembleDebug
```

Afficher les appareils Android connectés :

```bash
adb devices
```

Afficher les journaux du cycle de vie :

```bash
adb logcat -s AtelierCycle
```

---

## 11. Auteur

**Fakher Neila**

- **Classe :** ING-A3-GL-AL-04
- **Module :** Mobile Development
- **Langage :** Kotlin
- **Dépôt GitHub :** [AtelierAndroid_TP05](https://github.com/fakherneila/AtelierAndroid_TP05)

Projet universitaire réalisé dans le cadre des travaux pratiques de développement mobile Android.
