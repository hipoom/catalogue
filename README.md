# Catalogue 


## 💡 Introduction
This is a lightweight file catalogue manager library.


## 🔨 How to use
You can use this library by gradle:
```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation 'com.hipoom:catalogue:0.0.2'
}
```


```
// initial
String workspace = "/Users/zhp/Workspace/test/catalogue";
Catalogue.initialOneWorkspace(workspace);

// add business file
Catalogue.addBusiness(workspace, "test1");


// get business file
File file = Catalogue.getBusinessFile(workspace, "test1");
```