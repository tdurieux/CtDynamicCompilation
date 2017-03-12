# CtDynamicCompilation [![Build Status](https://travis-ci.org/tdurieux/CtDynamicCompilation.svg?branch=master)](https://travis-ci.org/tdurieux/CtDynamicCompilation) [![Coverage Status](https://coveralls.io/repos/github/tdurieux/CtDynamicCompilation/badge.svg?branch=master)](https://coveralls.io/github/tdurieux/CtDynamicCompilation?branch=master)

This project is used to dynamically compile the changes made on Spoon model.

## Getting Started

### Maven dependency

```xml
<dependency>
    <groupId>com.github</groupId>
    <artifactId>CtDynamicCompilation</artifactId>
    <version>1-SNAPSHOT</version>
</dependency>

<repository>
    <id>codedance on Github</id>
    <url>https://tdurieux.github.io/maven-repository/snapshots</url>
</repository>
```


### Compile

```java
Launcher launcher = new Launcher();

final String className = "MyClass";

Factory factory = launcher.getFactory();
// create a new class
factory.Class().create(className);

final DynamicCompiler compiler = new DynamicCompiler(launcher);
// compile the model that contains the new class
final ClassLoader classLoader = compiler.compile();


new Thread(new Runnable() {
	@Override
	public void run() {
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
		    // get the new class
			Class<?> aClass = classLoader.loadClass(className);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}).run();
```

### Execute Junit tests

```java
Launcher launcher = new Launcher();
launcher.addInputResource(<path_to_project>);

// define the class path of the project
launcher.getModelBuilder().setSourceClasspath(System.getProperty("java.class.path").split(":"));
// build the model
launcher.buildModel();


DynamicTestRunner runner = new DynamicTestRunner(launcher);

// run all tests
List<Description> testResults = runner.runAllTests();

```


## TODO

* Support JUnit 5