# JavaDoc Coverage Maven Plugin 
[![Build Status](https://img.shields.io/travis/manoelcampos/javadoc-coverage/master.svg)](https://travis-ci.org/manoelcampos/javadoc-coverage) [![Dependency Status](https://www.versioneye.com/user/projects/5968248d368b08001a803892/badge.svg?style=rounded-square)](https://www.versioneye.com/user/projects/5968248d368b08001a803892) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/0fef8ada2def4d239931f90a50a3f778)](https://www.codacy.com/app/manoelcampos/javadoc-coverage?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=manoelcampos/javadoc-coverage&amp;utm_campaign=Badge_Grade) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.manoelcampos/javadoc-coverage/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.manoelcampos/javadoc-coverage) [![GPL licensed](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

![](coverage-report-sample.png)

A Maven plugin to generate JavaDoc coverage reports. It parses the java source files and checks the percentage of the Java code covered by JavaDoc documentation, including:
- packages (*Java 9 modules not supported yet*)
- classes, inner classes, interfaces and enums
- class attributes
- methods, parameters, exceptions and return value.

A sample coverage report is available [here](https://manoelcampos.com/javadoc-coverage/sample-project/target/site/apidocs/javadoc-coverage.html).

Current IDEs warns about missing JavaDoc tags and documentation, allowing you to individually fix the issues. 
Similar to code coverage tools, this plugin provides a way to get a summarized overview of your project's documentation coverage.
It provides a [Doclet](http://docs.oracle.com/javase/7/docs/technotes/guides/javadoc/doclet/overview.html) to be used with the JavaDoc Tool
and the `maven-javadoc-plugin` to show JavaDoc documentation coverage of your project.

# Building the Plugin

The plugin is a Java Maven project which can be built directly from any IDE or using the following maven command:

```bash
mvn clean install
```

The command builds the plugin and install it at your local maven repository.

# Using the CoverageDoclet in the regular way

To generate the regular JavaDoc HTML files and the coverage report, you have to include two configurations for the `maven-javadoc-plugin` inside your project's `pom.xml` file, as the exemple below:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>2.10.4</version>
            <executions>
                <!-- Exports JavaDocs to regular HTML files -->
                <execution>
                    <id>javadoc-html</id>
                    <phase>package</phase>
                    <goals>
                        <goal>javadoc</goal>
                    </goals>
                </execution>

                <!-- Generates the JavaDoc coverage report -->
                <execution>
                    <id>javadoc-coverage</id>
                    <phase>package</phase>
                    <goals>
                        <goal>javadoc</goal>
                    </goals>
                    <configuration>
                        <doclet>com.manoelcampos.javadoc.coverage.CoverageDoclet</doclet>
                        <docletArtifact>
                            <groupId>com.manoelcampos</groupId>
                            <artifactId>javadoc-coverage</artifactId>
                            <version>1.1.0</version>
                        </docletArtifact>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    <plugins>
<build>
```

Now, to generate the regular JavaDocs in HTML and the documentation coverage report, you can execute the `package` goal in Maven, using your IDE or the command line inside your project's root directory:

```bash
mvn clean package
```

The JavaDoc coverage report is generated by default as `javadoc-coverage.html` at `target/site/apidocs/`.

There is a [sample project](sample-project) where you can test the plugin. Just execute the command above inside the project's directory to see the results.

# Using the CoverageDoclet with the maven-site-plugin
If you are generating a maven site and want to include the regular JavaDocs HTML and the JavaDoc Coverage Report into the "Reports" section of the site, the `maven-javadoc-plugin` must be included with slightly different configurations into the `<reporting>` tag (instead of the `<build>` tag), as the example below:

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>2.10.4</version>
            <reportSets>
                <reportSet>
                    <!-- Exports JavaDocs to regular HTML files -->
                    <id>javadoc-html</id>
                    <reports>
                        <report>javadoc</report>
                    </reports>
                    <configuration>
                        <encoding>${encoding}</encoding>
                    </configuration>
                </reportSet>

                <reportSet>
                    <!-- Generates the JavaDoc coverage report -->
                    <id>javadoc-coverage</id>
                    <reports>
                        <report>javadoc</report>
                    </reports>
                    <configuration>
                        <name>JavaDoc Coverage</name>
                        <description>Percentage of the code coverage by JavaDoc documentation.</description>
                        <encoding>${encoding}</encoding>
                        <doclet>com.manoelcampos.javadoc.coverage.CoverageDoclet</doclet>
                        <docletArtifact>
                            <groupId>com.manoelcampos</groupId>
                            <artifactId>javadoc-coverage</artifactId>
                            <version>1.1.0</version>
                        </docletArtifact>
                        <!-- This is the same as using -d into the additionalparam tag -->
                        <destDir>javadoc-coverage</destDir>
                        <!-- You can also use -o instead of -outputName to define
                        the name of the generated report. -->
                        <additionalparam>-outputName "index.html"</additionalparam>
                    </configuration>
                </reportSet>
            </reportSets>
        </plugin>
    </plugins>
</reporting>
```

Notice that in this case, the coverage report is being generated into the `target/site/javadoc-coverage` (as defined by the `destDir` tag) with the name of `index.html` (as defined by the `<additionalparam>` tag), as required for the maven site. More details of additional parameters is provided in the next section.

Now, to generate the site you can execute:

```bash
mvn clean site
```

The list of project's reports will be included into the `target/site/project-reports.html` file.

# Additional Configuration (optional)

## Changing the name of the coverage report file
The CoverageDoclet accepts the command line parameter `-outputName` (`-o` for short) to set the name of the report. The following example shows the code to be added to the `<configuration>` tag of the `maven-javadoc-plugin`:
```xml
<additionalparam>-outputName "my-project-javadoc-coverage-report.html"</additionalparam>
```

## Excluding packages from the coverage report
You can exclude some packages from the coverage report by adding the code example below into the `<configuration>` tag of the `maven-javadoc-plugin`.

```xml
<configuration>
    <doclet>com.manoelcampos.javadoc.coverage.CoverageDoclet</doclet>
    <docletArtifact>
        <groupId>com.manoelcampos</groupId>
        <artifactId>javadoc-coverage</artifactId>
        <version>1.1.0</version>
    </docletArtifact>
    <!-- Excludes packages from the coverage report. -->
    <excludePackageNames>com.manoelcampos.sample2</excludePackageNames>
</configuration>
```

The example shows how to ignore the package `com.manoelcampos.sample2` from the coverage report. The `<excludePackageNames>` tag accepts a list of packages separated by `:` and also wildcards such as `*`.
For more details, check this [link](https://maven.apache.org/plugins/maven-javadoc-plugin/examples/exclude-package-names.html).

If you are generating the regular JavaDoc HTML files, you have to include this configuration only where the CoverageDoclet is being used into your pom.xml, unless you want these packages to be excluded from the regular JavaDocs too.


