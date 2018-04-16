# Jenjinn chess engine

watch this space...

This project has taught me a great deal of things, maybe the most important is the value of unit tests. There is a paralysing lack of automated tests for the search and evaluation parts of the engine. This is preventing me from refactoring with confidence a few months after I finished this version. 

My tasks are now to add to my unit tests where appropriate (chess engines seem to be hard to test but my basic board representation is actually pretty thoroughly tested even though it's all in one file!), refactor the code because the quality is making me cringe and get to a point where I have a stable release and can maybe move on.

To play against the engine clone this repository and import is as an existing gradle project in Eclipse (Intellij will probably work too). Then run the class "FirstGameLauncher.java".