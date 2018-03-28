## Introduction

A Clojure library that wraps [Alda](https://github.com/alda-lang/alda) so you can write musical scores as edn data. You can start a [Boot](http://boot-clj.com/) project with the following command:

`boot -d boot/new new -t edna -n hello-world`

## Tutorial

```clojure
; first hit middle c on the piano
[:piano :c]

; hit a few more keys in succession
[:piano :c :d :e :f]

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

There are probably bugs...or maybe your music just sounds bad. It could be that. Think about it.

## Licensing

All files that originate from this project are dedicated to the public domain. I would love pull requests, and will assume that they are also dedicated to the public domain.
