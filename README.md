## Introduction

A Clojure library for making music with edn data. It uses [Alda](https://github.com/alda-lang/alda) underneath. There are probably bugs...or maybe your music just sounds bad. It could be that. Think about it.

### [Try the playable examples!](https://oakes.github.io/edna/cljs/edna.examples.html)

In addition to playing music, you can [export to mp3, wav, or midi](https://oakes.github.io/edna/clj/edna.core/export'e'.html) files, or even [export to a data URI](https://oakes.github.io/edna/clj/edna.core/edna-'g'data-uri.html) for playing on the web. With that, you can embed music in a [ClojureScript game](https://github.com/oakes/play-cljc) and the music will rebuild automatically when you edit it.

## Getting Started

There are several ways to create a project:

* [The Clojure CLI Tool](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools): `clj -Sdeps "{:deps {leiningen {:mvn/version \""2.9.0\""}}}" -m leiningen.core.main new edna my-music`
* [Nightcode](https://sekao.net/nightcode/): Choose "Music Project" from its start menu
* [Nightcoders.net](http://nightcoders.net/): Choose "Music" when creating a new project
* [Midibin.com](https://midibin.com): A code playground dedicated to making music with edna

## Documentation

* Check out [the example scores](https://github.com/oakes/edna/tree/master/examples)
* Read [the dynadocs](https://oakes.github.io/edna/clj/edna.core.html)
* Join the discussion on [r/midibin](https://www.reddit.com/r/midibin/)
* See the screencasts:
  * Intro to edna: https://www.youtube.com/watch?v=yNz-T3Ij0LI
  * Making music for the web with edna: https://www.youtube.com/watch?v=aZkoPNFVEkQ
  * Midibin.com - Turn code into music: https://www.youtube.com/watch?v=LiXcJr42CvQ
* Look at this commented example:

```clojure
; first hit middle c on the piano
[:piano :c]

; hit all twelve notes
[:piano :c :c# :d :d# :e :f :f# :g :g# :a :a# :b]

; by default you're on the 4th octave, but you can change it
[:piano {:octave 3} :c :d :e :f]

; maps let you change attributes for anything that comes after it
[:piano {:octave 3} :c :d {:octave 4} :e :f]

; notes are 1/4 length by default, but you can change that too
[:piano {:octave 3} :c :d {:octave 4, :length 1/2} :e :f]

; you have to change note lengths often so here's a shorthand
[:piano {:octave 3} :c :d {:octave 4} 1/2 :e :f]

; you can change individual notes' octave with + or - inside the keyword
[:piano {:octave 3} :c :d 1/2 :+e :+f]

; with all that, we can write the first line of dueling banjos
[:guitar {:octave 3} 1/8 :b :+c 1/4 :+d :b :+c :a :b :g :a]

; chords are just notes in a set
[:piano #{:c :e}]

; you can change the length of chords just like single notes
[:guitar {:octave 4}
  1/8 #{:d :-b :-g} #{:d :-b :-g}
  1/4 #{:d :-b :-g} #{:e :c :-g} #{:d :-b :-g}]

; to play two instruments simultaneously, put them in a set!
#{[:banjo {:octave 3} 1/16 :b :+c 1/8 :+d :b :+c :a :b :g :a]
  [:guitar {:octave 3} 1/16 :r :r 1/8 :g :r :d :r :g :g :d]}
```

The keyword at the beginning of your vector has to coorespond with something from Alda's [list of instruments](https://github.com/alda-lang/alda/blob/master/doc/list-of-instruments.md). After that, you can mix and match notes, chords, note lengths, and so on.

## Development

* Install [the Clojure CLI tool](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools)
* To develop with figwheel: `clj -A:dev`
* To install the release version: `clj -A:prod install`

## Licensing

All files that originate from this project are dedicated to the public domain. I would love pull requests, and will assume that they are also dedicated to the public domain.
