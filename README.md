佛教朝暮课诵
--------------------
音频来源：[http://www.shengyen.org/content/music/music_01.aspx?MType=3](http://www.shengyen.org/content/music/music_01.aspx?MType=3)

### 开发帮助

#### Logo(使用 dia)
    cd tools && sh logo.sh

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


感谢法鼓山
