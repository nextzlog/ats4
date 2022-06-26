ATS-4: Amateur-Radio Contest Administration System
====

![image](https://img.shields.io/badge/sbt-1.3.8-red.svg)
![image](https://img.shields.io/badge/Java-JDK11-red.svg)
![image](https://img.shields.io/badge/Scala-2.13-orange.svg)
![image](https://img.shields.io/badge/JRuby-9.2-orange.svg)
![image](https://img.shields.io/badge/PlayFramework-2.8-blueviolet.svg)
![image](https://img.shields.io/badge/license-GPL3-darkblue.svg)

ATS-4 is an Automatic Acceptance & Tabulation System for Amateur-Radio Contests, based on [QxSL](https://github.com/nextzlog/qxsl).

## Features

- provides a web interface for contest-log acceptance.
- verifies the uploaded logs according to the contest rules described in Ruby or LISP forms.

## Supports

ATS-4 supports many contests including:

- [UEC](https://www.ja1zgp.com/uectest_public_info/),
- [ALLJA1](http://ja1zlo.u-tokyo.org/allja1/),
- [REALTIME](http://ja1zlo.u-tokyo.org/rt/rt1.html),
- [TAMAGAWA](http://apollo.c.ooco.jp/).

## Documents

- [Javadoc](https://nextzlog.github.io/qxsl/doc/index.html)
- [ATS-4 (PDF)](https://pafelog.net/ats4.pdf)

## Demo

Feel free to visit [ALLJA1 ATS-4](https://allja1.org).

## Usage

First, clone this repository.

```sh
$ git clone https://github.com/nextzlog/ats4
$ cd ats4
```

To update ATS-4, first stop the system, then clear the database, and finally pull the latest version.

```sh
$ kill `cat target/universal/stage/RUNNING_PID`
$ ./destroy.sh
$ git reset --hard
$ git pull
```

### Start

Clear your mind and cast a spell.

```sh
$ sbt run # develop mode
$ sbt "start -Dhttp.port=8000"
```

Just wait and relax.
You will find the following message.

```
(Starting server. Type Ctrl+D to exit logs, the server will remain in background)
```

Then, type `Ctrl+D` and exit.
Browse the web page on port 8000.

### Stop

First, kill the server process, and delete the file as follows.

```sh
$ kill `cat target/universal/stage/RUNNING_PID`
$ rm target/universal/stage/RUNNING_PID
```

## Settings

[READ ME](CONFIG.md).

## Streaming

[READ ME](STREAM.md).

## Contribution

Feel free to contact [@nextzlog](https://twitter.com/nextzlog) on Twitter.

## License

### Author

[無線部開発班](https://pafelog.net)

- JG1VPP
- JJ2ULU
- JH1GEB
- JE6MDL
- JO4EFC
- JJ1IBY
- JS2FVO

### Clauses

- This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

- This program is distributed in the hope that it will be useful, but **without any warranty**; without even the implied warranty of **merchantability or fitness for a particular purpose**.
See the GNU General Public License for more details.

- You should have received a copy of the GNU General Public License along with this program.
If not, see <http://www.gnu.org/licenses/>.
