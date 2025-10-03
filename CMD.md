### Important commands to build and run this project

Delete the cache and build clean project
```shell
./gradlew --stop
rm -rf .gradle build
./gradlew clean build --refresh-dependencies
```


Build and release commands

| Purpose               | Command                                        |
| --------------------- | ---------------------------------------------- |
| Clean & build dev mod | `./gradlew clean build`                        |
| Run dev client        | `./gradlew runClient`                          |
| Run dev server        | `./gradlew runServer`                          |
| Full clean & rebuild  | `./gradlew clean build --refresh-dependencies` |

built jar will be found under this path
`build/libs/f3-lite-1.1.0+MC_1.21.9.jar`