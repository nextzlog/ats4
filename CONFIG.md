Settings
====

Follow the instructions below.

## Proxy

We expect that ATS-4 operates as a backend server, which is hidden behind a frontend server such as Apache and Nginx.
Make sure that unauthorized clients have no access to admin pages under `/admin` before you start the system as follows.

```nginx
server {
  server_name allja1.org;
  location / {
    proxy_pass http://localhost:9000;
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

We recommend that you utilize the BASIC authentication in addition to SSH authentication to protect the private pages.

## Mail

Open the system configuration file [`conf/application.conf`](conf/application.conf).
You will find the mail settings as follows.

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
In addition, disable the `mock` mode of the mailer plugin.

## Contest

Open the contest configuration file [`conf/application.rb`](conf/application.rb).
You will find the contest settings as follows.

```Ruby
# Load ja1.rb in the conf directory.
require 'ja1'

# Returns the contest object.
TEST
```

Modify the contest settings properly.

## Regulation

The [`Contest`](https://nextzlog.github.io/qxsl/doc/qxsl/ruler/Contest) object is the entity of the contest rules.
See [`ja1.rb`](conf/rules/ja1.rb) and [`rtc.rb`](conf/rules/rtc.rb) and [`uec.rb`](conf/rules/uec.rb) for example.

```Ruby
TEST = ContestJA1.new(ALLJA1)
```
