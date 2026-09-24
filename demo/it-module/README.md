# Vector-it Custom Demo Module

这是一个针对 **Vector-it** 框架定制开发的测试模块示例，用于验证二次开发新增特性：

## 特性演示
1. **模块加载与识别**：验证 Vector-it 能否正确解析、显示并在应用中加载该模块。
2. **作用域（Scope）生效**：预设了 `android`（System Server）与 `com.android.systemui`，验证作用域配置持久性。
3. **系统级拦截测试**：在 `android` 进程中拦截 `ActivityManagerService.systemReady`，在系统日志中打印带有 `Vector-it` 标签的执行跟踪。

## 使用与测试方法
1. 在 Android Studio 或通过 Gradle 构建生成此模块 APK：
   ```bash
   # 打包生成 APK
   ./gradlew :demo:it-module:assembleDebug
   ```
2. 安装到已运行 Vector-it 的设备上。
3. 打开 **Vector-it** 管理器，进入【模块】页面，启用此模块，并在【作用域】中勾选相应目标应用。
4. 软重启或重启目标应用，即可在【日志】页面实时查看到带有 `VectorItDemo` 的拦截输出。
