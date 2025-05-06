# Squircle CE

<img src="https://media.githubusercontent.com/media/massivemadness/Squircle-CE/refs/heads/master/.github/images/repository-icon.png" alt="Squircle CE" width="120" align="left">

<b>Squircle CE</b> is a fast and free multi-language code editor for Android.

This repository contains the complete source code and the build instructions for the project.  
You can contribute by reporting issues, suggesting features, or submitting pull requests.

![Android CI](https://github.com/massivemadness/Squircle-CE/workflows/Android%20CI/badge.svg) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Build instructions

### Prerequisites

* At least **1,11GB** of free disk space: **144,7MB** for source codes and around **965,3MB** for
  files generated after building all variants
* **4GB** of RAM
* **macOS** or **Linux**-based operating system. **Windows** platform is supported by
  using [Git Bash](https://gitforwindows.org/).

### Building

1. `$ git clone --recursive --depth=1 --shallow-submodules https://github.com/massivemadness/Squircle-CE Squircle-CE`
   — clone **Squircle CE** with submodules
2. In case you forgot the `--recursive` flag, `cd` into `Squircle-CE` directory
   and: `$ git submodule init && git submodule update --init --recursive --depth=1`
3. Create `local.properties` file with the following properties:  
   `KEYSTORE_PATH`: absolute path to the keystore file  
   `KEYSTORE_PASSWORD`: password for the keystore  
   `KEY_ALIAS`: key alias that will be used to sign the app  
   `KEY_PASSWORD`: key password  
   **Warning**: keep this file safe and make sure nobody, except you, has access to it.
4. `$ cd Squircle-CE`
5. Now you can open the project using **[Android Studio](https://developer.android.com/studio/)** or
   build manually from the command line: `./gradlew assembleRelease`.

#### Available flavors

* `gms`: A flavor used for publishing the app
  on [Google Play](https://play.google.com/store/apps/details?id=com.blacksquircle.ui)
* `foss`: A flavor without closed-source libraries, used for publishing the app
  on [F-Droid](https://f-droid.org/packages/com.blacksquircle.ui/) and GitHub

## Translations &middot; [![Crowdin](https://badges.crowdin.net/squircle-ce/localized.svg)](https://crowdin.com/project/squircle-ce)

If you'd like to translate **Squircle CE** to a new language or make a translation correction,
please register an account at [Crowdin](https://crowdin.com) and join the project here:

* https://crowdin.com/project/squircle-ce

If the language that you are interested in translating is not already listed, create a new account
on Crowdin, join the project and contact the project owner.

## Screenshots

<img src="https://media.githubusercontent.com/media/massivemadness/Squircle-CE/refs/heads/master/.github/images/repository-screenshots.png">

## License

```
Copyright 2025 Squircle CE contributors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
