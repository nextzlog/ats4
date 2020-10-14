ATS-4: Amateur-Radio Contest Administration System
====

![image](https://img.shields.io/badge/sbt-1.2.8-red.svg)
![image](https://img.shields.io/badge/Java-JDK11-red.svg)
![image](https://img.shields.io/badge/Scala-2.13-orange.svg)
![image](https://img.shields.io/badge/JRuby-9.2-orange.svg)
![image](https://img.shields.io/badge/PlayFramework-2.7-blueviolet.svg)
![image](https://img.shields.io/badge/license-GPL3-darkblue.svg)

ATS-4 is an Automatic Acceptance & Tabulation System for Amateur-Radio Contests, based on [qxsl: Amateur-Radio Logging Library & LISP](https://github.com/nextzlog/qxsl).

## Features

- provides a web interface for contest-log acceptance.
- scans the uploaded log and verifies its contents according to the contest rule described in Ruby or LISP forms.

## Demo

ATS-4 supports multiple contests including [UEC contest](https://www.ja1zgp.com/uectest_public_info/) although it was originally developed for [ALLJA1 contest](http://ja1zlo.u-tokyo.org/allja1/).
Feel free to visit [ALLJA1 ATS-4](https://allja1.org).

## Documents

- [Javadoc](https://nextzlog.github.io/qxsl/doc/index.html)
- [コンテスト運営を支援する自動集計システム (PDF)](https://pafelog.net/ats4.pdf)

## Setup & Start

First, clone this repository:

```sh
$ git clone https://github.com/nextzlog/ats4
$ cd ats4
```

Open the system configuration file as follows:

```sh
$ vim conf/application.conf
```

You will find the mail settings as below:

```ini
# Typesafe Mailer Plugin
play.mailer.host=mail.allja1.org
play.mailer.port=465
play.mailer.ssl=true
play.mailer.user="***********"
play.mailer.password="*******"

# Never forget to disable mock mode before the contest:
play.mailer.mock=true
```

Modify the mail settings properly.
In addition, disable the `mock` mode of the mailer plugin.
Then, open the contest configuration file as follows, and you will find the contest settings.
Modify the contest settings properly.

```sh
$ vim conf/application.rb
```

The time has come.
Clear your mind and cast a spell.

```sh
$ sbt run # develop mode
$ sbt "start -Dhttp.port=8000"
```

Just wait and relax.
After a period of time, you will find the following message:

```
(Starting server. Type Ctrl+D to exit logs, the server will remain in background)
```

Then, type Ctrl+D and exit.
Browse the system on port 8000.

## Stop

First, kill the process which is running the system, and delete the file.

```sh
$ kill `cat target/universal/stage/RUNNING_PID`
$ rm target/universal/stage/RUNNING_PID
```

## Upgrade

To upgrade ATS-4 components, first stop the system, then clear the database, pull the latest version, and finally restart the system:

```sh
$ kill `cat target/universal/stage/RUNNING_PID`
$ ./destroy.sh
$ git reset --hard
$ git pull
```

## Reverse Proxy

We expect that ATS-4 operates as a backend server, which is hidden behind a frontend server such as Apache and Nginx.
Make sure that unauthorized clients have no access to admin pages under `/admin` before you start the system.

## Contest Definition

A [`Contest`](https://nextzlog.github.io/qxsl/doc/qxsl/ruler/Contest) object is the entity of the contest rules defined in Ruby files, e.g., [`ja1.rb`](conf/ja1.rb) and [`uec.rb`](conf/uec.rb) in the `conf` directory.
ATS-4 supports any contest once you rewrite [`application.rb`](conf/application.rb).

```Ruby
# extends Contest class to access global variables defined here
class ExtendedALLJA1 < Contest
  def initialize()
    super(*ALLJA1.getSections)
  end
  def get(name)
    eval name
  end
  def invoke(name, args)
    method(name).call(*args)
  end
  def getStartDay(year)
    date(year, 'JUNE', 'SATURDAY', 4)
  end
  def getFinalDay(year)
    getStartDay(year)
  end
  def getDeadLine(year)
    date(year, 'JULY', 'SATURDAY', 3)
  end
  def getName()
    ALLJA1.getName
  end
  def getHost()
    ALLJA1.getHost
  end
  def getMail()
    ALLJA1.getMail
  end
  def getLink()
    ALLJA1.getLink
  end
end

# returns redefined ALLJA1 contest
TEST = ExtendedALLJA1.new
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
