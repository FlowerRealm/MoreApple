# More Apple

More Apple 是一个 Minecraft Fabric mod。当前项目是基础底座，面向 Minecraft `1.20.1`，使用 Java `21`，并以 Hephaestus 作为匠魂 Fabric 底座。

当前还没有添加实际苹果内容。

## 基础架构

- Minecraft：`1.20.1`
- Java：`21`
- Mod Loader：Fabric
- Fabric API：`0.92.9+1.20.1`
- 匠魂底座：Hephaestus `1.20.1-3.6.4.305`

注意：Hephaestus 是 Fabric 生态下的匠魂底座。不要把 Forge/NeoForge 版 Tinkers' Construct 装到这个 Fabric 环境里。

## 从源码构建

确保本机有 Java 21：

```bash
java -version
```

构建并运行默认测试：

```bash
./gradlew clean build --no-daemon
```

本地开发会复用用户级缓存，避免多个 worktree 重复下载 Gradle 依赖、HeadlessMC、Minecraft 运行时和测试 jar。More Apple 的开发缓存默认位于：

```text
${XDG_CACHE_HOME:-~/.cache}/moreapple
```

如果需要指定其它缓存目录：

```bash
./gradlew build --no-daemon -Pmoreapple.devCacheDir=/path/to/moreapple-cache
```

`clean` 只清理当前 worktree 的 `build/`，不会删除用户级开发缓存。需要彻底清理时，手动删除上面的缓存目录。

默认测试会通过 HeadlessMC 启动一个隔离的 Minecraft `1.20.1` Fabric 客户端，并加载 More Apple、Fabric API、Hephaestus 与 mc-runtime-test。启动成功时日志里会出现：

```text
More Apple initialized
```

构建完成后，mod 文件在：

```text
build/libs/moreapple-0.1.0.jar
```

## 安装使用

准备一个 Minecraft `1.20.1` 的 Fabric 客户端或服务端环境。

需要安装：

- Fabric Loader `0.17.2` 或更新版本
- Fabric API `0.92.9+1.20.1`
- Hephaestus `1.20.1-3.6.4.305`
- More Apple 构建产物：`moreapple-0.1.0.jar`

把这些 `.jar` 文件放进 Minecraft 实例的 `mods/` 目录：

```text
.minecraft/mods/
  fabric-api-0.92.9+1.20.1.jar
  Hephaestus-1.20.1-3.6.4.305.jar
  moreapple-0.1.0.jar
```

然后启动游戏。

如果启动成功，日志里会出现：

```text
More Apple initialized
```
