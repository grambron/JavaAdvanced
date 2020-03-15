@echo off

SET mod_dir=ru\ifmo\badikova\implementor
SET k=info\kgeorgiy\java\advanced\implementor
SET mod_name=ru.ifmo.badikova.implementor

SET wd=C:\Users\Anastasiia\IdeaProjects\JavaAdvanced1

SET src=%wd%\src\
SET out=%wd%\out\production\JavaAdvanced1
SET run=%wd%\run\implementor

javac -cp %wd%\src -d %wd%\out\production %wd%\src\%mod_dir%\*.java^
 %wd%\src\%k%\Impler.java %wd%\src\%k%\JarImpler.java %wd%\src\%k%\ImplerException.java
cd %out%
jar -c --file=%run%\implementor.jar --main-class=%mod_name%.Implementor %mod_dir%\*.class %k%\*.class
cd %run%