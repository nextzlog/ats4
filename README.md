ATS-4: WebSystem for Hamradio Contests
====

![image](https://img.shields.io/badge/Java-JDK8-green.svg)
![image](https://img.shields.io/badge/Scala-2.12-green.svg)
![image](https://img.shields.io/badge/license-GPL3-green.svg)

ATS-4 is an Automatic Acceptance & Tabulation System for Amateur-Radio Contests.
ATS-4 consists of the web components (ATS-4 itself) and the logging & scoring framework [qxsl](https://github.com/nextzlog/qxsl).

## Features

- ATS-4 provides a web interface for contest-log acceptance.
- ATS-4 scans the log and verifies its contents according to the contest rule described in LISP forms.

## Demo

ATS-4 was originally developed for [ALLJA1 contest](http://ja1zlo.u-tokyo.org/allja1/).
Feel free to visit [ALLJA1 ATS-4](https://allja1.org), but **never submit a dummy log**.

## Documents

- [Javadoc of qxsl Library](https://pafelog.net/qxsl/index.html)
- [History and Usage of ATS-4](https://pafelog.net/ats4.pdf)

## Setup & Start

First, clone this repository:

```sh
$ git clone https://github.com/nextzlog/ats4
$ cd ats4
```

Next, open the configuration file as follows:

```sh
$ alias emacs='vim'
$ vim conf/application.conf
```

You will find the mail settings as below:

```ini
# Typesafe Mailer Plugin
play.mailer.host=smtp.gmail.com
play.mailer.port=465
play.mailer.ssl=true
play.mailer.user="***********"
play.mailer.password="*******"
```

Modify the mail settings properly.
Don't forget to update the rule URL:

```ini
contest.rule="ja1zlo.u-tokyo.org/allja1/31rule.html"
# Do not include the scheme as http://host/path
```

**The time has come! Clear your mind and cast a spell!**

```sh
$ sbt start
```

Just wait and relax.
After a period of time, you will find the following message:

```
(Starting server. Type Ctrl+D to exit logs, the server will remain in background)
```

Then, type Ctrl+D and exit.
If you would like to run it in the development mode:

```
$ sbt run
```

Good luck!

## Stop

First, find the process which runs the system.

```sh
$ cat target/universal/stage/RUNNING_PID
```

Then, kill the process:

```sh
$ kill `target/universal/stage/RUNNING_PID`
```

Finally, delete the file.

```
$ rm target/universal/stage/RUNNING_PID
```

## Contribution

Feel free to contact [@nextzlog](https://twitter.com/nextzlog) on Twitter.

## License

### Author

[無線部開発班](https://pafelog.net)

### Clauses

- This program is free software; you can redistribute it and / or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

- This program is distributed in the hope that it will be useful, but **without any warranty**; without even the implied warranty of **merchant ability or fitness for a particular purpose**.
See the GNU General Public License for more details.

- You should have received a copy of the GNU General Public License, along with this program.
If not, see <http://www.gnu.org/licenses/>.
