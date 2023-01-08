ATS-4: Amateur-Radio Contest Administration System
====

![image](https://img.shields.io/badge/sbt-1.5.5-red.svg)
![image](https://img.shields.io/badge/Java-JDK11-red.svg)
![image](https://img.shields.io/badge/Scala-2.13-orange.svg)
![image](https://img.shields.io/badge/JRuby-9.3-orange.svg)
![image](https://img.shields.io/badge/PlayFramework-2.8-blueviolet.svg)
![image](https://img.shields.io/badge/license-GPL3-darkblue.svg)
![badge](https://github.com/nextzlog/ats4/actions/workflows/build.yaml/badge.svg)

ATS-4 is an Automatic Acceptance & Tabulation System for Amateur-Radio Contests, based on [QxSL](https://github.com/nextzlog/qxsl).
Feel free to visit [ALLJA1 ATS-4](https://allja1.org).

## Features

- provides a web interface for contest-log acceptance.
- verifies the uploaded logs according to the contest rules described in Ruby or LISP forms.
- supports many contests including [UEC](https://www.ja1zgp.com/uectest_public_info), [ALLJA1](https://ja1zlo.u-tokyo.org/allja1), [REAL-TIME](https://ja1zlo.u-tokyo.org/rt/rt1.html), [1-Area 6m AM](https://6mnet.jp/mannaka/blog) and [TAMAGAWA](http://apollo.c.ooco.jp).

## Documents

- [Scaladoc](https://nextzlog.github.io/ats4/api)
- [ATS-4 (PDF)](https://pafelog.net/ats4.pdf)

## Usage

Docker image is available.
Create `docker-compose.yaml` as follows:

```yaml
version: '3'
services:
  ATS4:
    image: ghcr.io/nextzlog/ats4:master
    ports:
    - 9000:9000
    volumes:
    - ./ats/data:/ats/data
    - ./ats/logs:/ats/logs
    command: /ats/bin/ats4
```

Then, create a container as follows:

```sh
$ docker-compose up -d
```

To kill the container, enter the following command:

```sh
$ docker-compose kill
```

## Configuration

First, create `docker-compose.yaml` as follows:

```yaml
version: '3'
services:
  ATS4:
    image: ghcr.io/nextzlog/ats4:master
    ports:
    - 9000:9000
    volumes:
    - ./ats/data:/ats/data
    - ./ats/logs:/ats/logs
    - ./ats.conf:/ats/conf/ats.conf
    - ./rules.rb:/ats/conf/rules.rb
    command: /ats/bin/ats4
  www:
    image: nginx:latest
    ports:
    - 80:80
    volumes:
    - ./proxy.conf:/etc/nginx/conf.d/default.conf
```

Then, follow the instructions below.

### Proxy

Create `proxy.conf` as follows:

```nginx
server {
  server_name localhost;
  location / {
    proxy_pass http://ATS4:9000;
    location /admin/ {
      allow 127.0.0.1;
      deny all;
    }
    location ~ /admin {
      allow 127.0.0.1;
      deny all;
    }
  }
}
```

Make sure that unauthorized clients cannot access administration pages under `/admin`.
Expose port 80 of the container to the internet so that the administration page cannot be accessed.

### Email

Create the system configuration file `ats.conf` as follows:

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

Modify the settings properly.

### Regulation

Add the path to the contest definition file to `ats.conf` like this:

```ini
# Contest
# ats4.rules=/rules/JA1ZLO/ja1.rb
# ats4.rules=/rules/JA1ZGP/uec.rb
ats4.rules=/rules.rb
```

In addition, create `rules.rb` as follows:

```rb
require 'rules/sample/plain'
RULE
```

Of course, you can also modify `rules.rb` to customize it for your contest.
See [`plain.rb`](conf/rules/sample/plain.rb) for example.

### Run

Finally, create a container as follows:

```sh
$ docker-compose up -d
```

Access 80 port of the container.

## Development Mode

You can change Scala code and configuration without restarting by starting ATS-4 in development mode as follows:

```sh
$ sbt run
```

Then, access http://localhost:9000/admin/shell to develop contest rules interactively.
You can test the scoring algorithm by attaching QSO data to the web form.

## Stream API

ATS-4 provides the streaming API for the [REAL-TIME CONTEST](http://ja1zlo.u-tokyo.org/rt/rt1.html).

### Registration

Contest participants will register their account information with ATS-4 in advance.
ATS-4 returns a security key (UUID) by sending a `GET` request to `http://localhost:8873?id=<UUID>`.
Clients may retrieve the key by listening on the 8873 port and access `/agent/<UUID>`.

### Upstream

When the contest starts, the client always connects to the server via WebSocket.
Each time a participant contacts another participant on air, the client sends the difference in the QSO records to the server.
Messages from the clients to the server must follow the format below.

|position|field                 |
|--------|----------------------|
|1st byte|number of QSOs deleted|
|sequence|header of the QSO data|
|sequence|QSO entities to delete|
|sequence|QSO entities to append|

The second and subsequent bytes of the messages are formatted as a single electronic log file.
The format must be officially supported by the [QXSL](https://github.com/nextzlog/qxsl) library.

### Downstream

The server receives the QSO records, scores it, wait a few seconds, and then notifies all clients of the score update.
JSON messages from the server to the clients are formatted as follows:

```JSON
{
  "14MHz": [
    {"call": "JA1ZLO", "score": 200, "total": 2200},
    {"call": "JA1YWX", "score": 100, "total": 2100}
  ]
}
```

### Demonstration

A simple WebSocket client for ATS-4 may be written as follows:

```html
<!DOCTYPE html>
<html lang='ja'>
  <head>
    <title>ATS-4</title>
    <script type="application/javascript" src="client.js"></script>
  </head>
  <body>
    <h1>Streaming Demo</h1>
    <textarea cols='160' rows='30' id='QSOs'></textarea>
    <p>
      <label>Delete <input type='number' id='trim' min='0' max='255' value='0'>QSOs,</label>
      <label>Submission Key: <input type='text' id='UUID' placeholder='/agent/UUID'></label>
      <button type='button' onclick='access();'>Access</button>
      <button type='button' onclick='submit();'>Submit</button>
    </p>
    <div id='messages'></div>
  </body>
</html>
```

The JavaScript program referenced may be written as follows:

```js
let sock;
function access() {
  const uuid = document.getElementById('UUID').value;
  sock = new WebSocket('ws://localhost:9000' + uuid);
  sock.binaryType = 'arraybuffer';
  sock.onmessage = function(msg) {
    const decoder = new TextDecoder();
    const data = decoder.decode(new Uint8Array(msg.data));
    const text = document.createTextNode(data);
    const node = document.createElement('div');
    document.getElementById('messages').appendChild(node);
    node.appendChild(text);
  };
}
function submit() {
  const encoder = new TextEncoder();
  const QSOs = document.getElementById('QSOs').value;
  const trim = document.getElementById('trim').value;
  const data = new TextEncoder().encode(QSOs);
  const full = new (data.constructor)(data.length + 1);
  full[0] = parseInt(trim);
  full.set(data, 1);
  sock.send(full);
}
```

## Contribution

Feel free to make issues at [nextzlog/todo](https://github.com/nextzlog/todo).
Follow [@nextzlog](https://twitter.com/nextzlog) on Twitter.

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
