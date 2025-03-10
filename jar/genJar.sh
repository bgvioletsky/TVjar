#!/bin/bash
###
 # @Author: bgcode
 # @Date: 2025-03-10 12:38:35
 # @LastEditTime: 2025-03-10 13:20:08
 # @LastEditors: bgcode
 # @Description: 描述
 # @FilePath: /bgcode/jar/genJar.sh
 # 本项目采用GPL 许可证，欢迎任何人使用、修改和分发。
### 

# 删除旧的 JAR 文件
rm -f ./jar/bgcode_spider.jar

# 删除旧的 Smali 类目录
rm -rf ./jar/Smali_classes

# 反编译 DEX 文件为 Smali 代码
java -jar ./jar/3rd/baksmali-2.5.2.jar d ./app/build/intermediates/dex/release/minifyReleaseWithR8/classes.dex -o ./jar/Smali_classes

# 删除旧的 spider 和 parser 目录
rm -rf ./jar/spider.jar/smali/com/github/bgcode/spider
rm -rf ./jar/spider.jar/smali/com/github/bgcode/parser

# 创建必要的目录
mkdir -p ./jar/spider.jar/smali/com/github/bgcode/

# 执行加密操作（如果参数为 ec）
if [ "$1" = "ec" ]; then
    java -Dfile.encoding=utf-8 -jar ./jar/3rd/oss.jar ./jar/Smali_classes
fi

# 移动 spider 和 parser 目录
mv ./jar/Smali_classes/com/github/bgcode/spider ./jar/spider.jar/smali/com/github/bgcode/
mv ./jar/Smali_classes/com/github/bgcode/parser ./jar/spider.jar/smali/com/github/bgcode/

# 删除临时的 Smali 类目录
rm -rf ./jar/Smali_classes

# 重新打包为 APK
java -jar ./jar/3rd/apktool_2.4.1.jar b ./jar/spider.jar -c

# 移动生成的 DEX JAR 文件
mv ./jar/spider.jar/dist/dex.jar ./jar/bgcode_spider.jar

# 计算 MD5 哈希值
md5sum ./jar/bgcode_spider.jar | awk '{print $1}' > ./jar/bgcode_spider.jar.md5

# 删除临时目录
rm -rf ./jar/spider.jar/smali/com/github/bgcode/spider
rm -rf ./jar/spider.jar/smali/com/github/bgcode/parser
rm -rf ./jar/spider.jar/build
rm -rf ./jar/spider.jar/dist