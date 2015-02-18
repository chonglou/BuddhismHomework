佛教朝暮课诵助手(For Android)
--------------------
音频来源：[http://www.shengyen.org/content/music/music_01.aspx?MType=3](http://www.shengyen.org/content/music/music_01.aspx?MType=3), 经书来自互联网，欢迎来信添加经书和梵呗.


扫描二维码打开play市场链接：


![扫描下载Android App](https://raw.githubusercontent.com/chonglou/BuddhismHomework/master/tools/images/play.png)


功能特色：
 * 早课/晚课
 * 打坐计时
 * 梵呗
 * 开示视频
 * 禅宗七经



### 开发帮助

#### 横屏/竖屏切换

    Ctrl+F12

#### Logo(使用 dia)
    cd tools && sh png.sh ic_launcher

#### 去掉多余的空行
    sed -i  '/^$/d' file.txt

#### 异体字说明

 * 左口右犁 [http://chardb.iis.sinica.edu.tw/char/31563] (http://chardb.iis.sinica.edu.tw/char/31563)
 * 左赤右皮 [http://chardb.iis.sinica.edu.tw/char/60089] (http://chardb.iis.sinica.edu.tw/char/60089)
 * 左合右牛 [http://chardb.iis.sinica.edu.tw/char/45725] (http://chardb.iis.sinica.edu.tw/char/45725)
 * 同「兔」[http://chardb.iis.sinica.edu.tw/char/7490] (http://chardb.iis.sinica.edu.tw/char/7490)

#### 调试设备启动
 * android版本小于3.2 点击 设置->应用->开发
 * android本本大于4.0小于4.2 点击 设置->开发者选项
 * android版本大于4.2 开发者选项默认隐藏。如果需要显示，需要点击 设置->关于手机，并连续点击版本号7次，然后返回上一页才可以看到开发者选项。


    adb connect YOUR_IP:5555
    拔下设备
    adb kill-server 
    adb start-server
    接上设备

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
