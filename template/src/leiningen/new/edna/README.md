To build this project, you'll need the Clojure CLI tool:

https://clojure.org/guides/deps_and_cli


To develop in a browser with live code reloading:

`clj -M dev.clj`


To build a release version for the web:

`clj -M prod.clj`


To play the song once:

`clj -M dev.clj play`


To build an mp3:

`clj -M prod.clj mp3`

**NOTE:** OpenJDK 11 is required! Newer versions of the JDK removed the `com.sun.media.sound.*` classes.
