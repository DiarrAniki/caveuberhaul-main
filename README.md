# Minimal Mod

Quilt example mod, without the examples.

## Prerequisites
- JDK for Java 17 ([Eclipse Temurin](https://adoptium.net/temurin/releases/) recommended)
- IntelliJ IDEA
- Minecraft Development plugin (Optional, but highly recommended)

## Setup instructions

- Download or clone this repository and put it somewhere.
```
git clone https://github.com/MartinSVK12/bta-example-mod.git
```

- Import the project in IntelliJ IDEA, close it and open it again.

- Add `-Dloader.development=true` and change `-Dfabric.dli.main=` to `org.quiltmc.loader.impl.launch.knot.KnotClient` or `=org.quiltmc.loader.impl.launch.knot.KnotServer` in your run configurations.

- Create a new run configuration by going in `Run > Edit Configurations`  
   Then click on the plus icon and select Gradle. In the `Tasks and Arguments` field enter `build`  
   Running it will build your finished jar files and put them in `build/libs/`

- Open `File > Settings` and head to `Build, Execution, Development > Build Tools > Gradle`  
   Change `Build and run using` and `Run tests using` to `IntelliJ IDEA`


- Open `File > Project Structure`, select `Project` and set `Compiler output` to your project's path/out.


- Done! Now all that's left is to change every mention of `examplemod` to your own mod id. Happy modding!
