Streaming API
====

ATS-4 provides the streaming API for the [REAL-TIME CONTEST](http://ja1zlo.u-tokyo.org/rt/rt1.html).

## Registration

Contest participants will register their account information with ATS-4 in advance.
ATS-4 returns a transmission security key containing a UUID in a hidden form.

```html
<input type="hidden" id="UUID" value="/agent/e6dc32fb-6c3b-44e7-88b1-0f5cfdaf2494">
```

Clients should retrieve this security key by some means such as HTML scraping.

## Upstream

When the contest starts, the client always connects to the server via WebSocket.
Each time a participant contacts another participant on air, the client sends the difference in the QSO records to the server.
Messages from the client to the server must follow the format below.

|position|field                 |
|--------|----------------------|
|1st byte|number of QSOs deleted|
|sequence|header of the QSO data|
|sequence|QSO entities to delete|
|sequence|QSO entities to append|

The second and subsequent bytes of the messages are formatted as a single electronic log file.
The format must be officially supported by the [QXSL](https://github.com/nextzlog/qxsl) library.

## Downstream

The server receives the QSO records, scores it, wait a few seconds, and then notifies all clients of the score update.
Messages from the server to the client only contain the call sign of the participant who updates the QSO data.

## Demonstration

A simple WebSocket client for ATS-4 may be written as follows.

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

The JavaScript program referenced may be written as follows.

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
