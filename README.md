佛教朝暮课诵
--------------------
音频来源：[http://www.shengyen.org/content/music/music_01.aspx?MType=3](http://www.shengyen.org/content/music/music_01.aspx?MType=3), 经书来自互联网，欢迎来信添加经书和梵呗.

功能特色：
 * 可根据梵呗自由组合早晚课内容



### 开发帮助

#### Logo(使用 dia)
    cd tools && sh png.sh ic_launcher

#### 去掉多余的空行
    sed -i  '/^$/d' file.txt

### Gradle环境
#### 安装 
    cd /tmp && wget https://services.gradle.org/distributions/gradle-2.2.1-bin.zip
    cd ~/local && unzip /tmp/gradle-2.2.1-bin.zip && ln -sv gradle-2.2.1 gradle

#### 设置(vi .bashrc)
    export GRADLE_HOME=$HOME/local/gradle
    export PATH=$PATH:$GRADLE_HOME/bin
    
#### 检查
    gradle help

### 编译打包
    gradle tasks # 列出所有任务
    gradle build # 编译
    ls app/build/outputs/apk # 列出apk包

## Contributing

1. Fork it ( https://github.com/chonglou/BuddhismHomework/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create a new Pull Request


感谢法鼓山
