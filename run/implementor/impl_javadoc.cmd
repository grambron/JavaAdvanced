@echo off

SET wd=C:\Users\Anastasiia\IdeaProjects\JavaAdvanced1

SET mod_dir=ru\ifmo\badikova\implementor
SET mod_name=ru.ifmo.badikova.implementor

SET src=%wd%\src
SET k_dir=info\kgeorgiy\java\advanced\implementor

@echo on
javadoc -d javadoc -link https://docs.oracle.com/en/java/javase/13/docs/api^
 -cp %wd%\src -private -author^
 --source-path %wd%\src^
 %src%\%mod_dir%\*.java %src%\%k_dir%\*.java


