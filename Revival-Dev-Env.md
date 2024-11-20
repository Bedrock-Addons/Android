# bringing BA back to life (this was painful)

1] First you need a dinosaur java environment, or nothing will work

```
sudo apt update
sudo apt install openjdk-11-jdk
```

2] `update-alternatives --list java` Test that it's working

3] cd into the repo on a terminal

```
echo "org.gradle.java.home=/usr/lib/jvm/java-11-openjdk-amd64
android.useAndroidX=true
android.enableJetifier=true" > gradle.properties
```

after "amd64" you press enter for multi-line, which is a cool thing I was unaware of.

4] `chmod +x gradlew` make it executable

5] make sure it's working, `./gradlew clean build --stacktrace`

if it loads 100%, you're good!

6] If you're on Linux, make sure you have the android studio snap package (NOT the flatpak). If you're on Windows or Mac, you're on your own.

It should come back to life with these changes.

This could also be turned into a GitHub Action to save a lot of time lol
