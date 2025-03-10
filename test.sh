#!/bin/bash
###
 # @Author: bgcode
 # @Date: 2025-03-10 12:35:23
 # @LastEditTime: 2025-03-10 12:35:26
 # @LastEditors: bgcode
 # @Description: 描述
 # @FilePath: /catvod/test.sh
 # 本项目采用GPL 许可证，欢迎任何人使用、修改和分发。
### 

# 执行 Gradle 构建发布版本
./gradlew assembleRelease --no-daemon

# 调用 genJar.sh 脚本生成 JAR 文件
./jar/genJar.sh ec