# RootUpgradeAd
模拟Android广告机root下在线自动升级的功能

## 前提

开发板已经ROOT掉，而且未安装SuperSU这样的root管理工具

## 思路

之前尝试过监听app被remove的广播消息，发现这种情况不可用，于是延伸出这个思路。

在主App上放一个类似插件apk的角色，放在Assets目录下，这个插件apk只负责主app的安装。

这里在InstallPlugin里面有所体现，需要注意的是放在InstallPlugin项目下assets目录中的apk的版本号一定要大于目标app的版本号（versionCode和versionName）

基本的功能就是把app从assets目录拷贝到sd卡，然后执行pm命令升级覆盖，完成后调用am命令启动目标app

Practice就是主app了，也就是要升级的app。这里需要做的就是检测InstallPlugin是否安装，未安装先pm命令安装下，安装后用am命令启动。升级逻辑放在InstallPlugin下了。

如果真正实现网络升级，需要在am命令中传递一个参数，就是app所在的路径，传入后逻辑类似

## 测试

1. RK3399 Firefly系列，系统Android7.0，测试通过
2. RK3288 自己项目中用的芯片，系统Android5.1，测试通过

## TODO

出现过一次内存泄漏的问题，尚需要排查下！
