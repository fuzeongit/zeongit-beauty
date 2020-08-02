
# Zeongit Account

  灵感主要是来自Google，Google业务范围很多，譬如YouTube，Google Keep，Gmail等，他们都是使用一个账号为登录，所以从想仿照Google的方式实现同域名下token通用实现账号共享，所以该项目只是第一步，接下来所有项目都须通过该项目来实现登录，然后跳转到对应的二级域名项目下。
  
  开发语言选用的是[Kotlin](https://www.kotlincn.net/)，这门语言能完美兼容[Java](https://www.Java.com/zh_CN/)，并且比Java更加安全和简便，用过这门语言后基本就没有什么想用Java的想法了。

  注：   
  由于网站不是处于正式使用的情况，所以会导致账号丢失情况，所以如果要注册账号请联系作者吧。
  
#### 快速链接  
官网：[account.secdra.com](http://account.secdra.com)  

#### 相关应用
[Zeongit Beauty](http://beauty.secdra.com/)
  
#### 技术栈  
 - Kotlin
 - Spring Boot  
 - Mysql 
 - Redis  
 - Elasticsearch  
  
#### 项目结构  
  
#### 前端网站提供
[zeongit-beauty-web](https://github.com/JunJieFu/zeongit-beauty-web)

#### 项目依赖
[zeongit-share](https://github.com/JunJieFu/zeongit-share)

#### 构建  
``` bash  
$ mvn spring-boot:run
  
$ mvn clean package -Dmaven.test.skip=true
```  

#### 开源协议  
[MIT](https://opensource.org/licenses/MIT)
