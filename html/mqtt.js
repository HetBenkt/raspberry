const topic = "test/hello";
let data = [];
let g;
let index = 20;

function MQTTConnect() {
    console.log("App started.");

    let host = "192.168.2.114";
    let port = 9001;
    let clientName = "mqtt_websock";

    // Create a client instance
    client = new Paho.MQTT.Client(host, Number(port), clientName);
    client.startTrace();

    // set callback handlers
    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;

    // connect the client
    let options = {
        timeout: 3,
        onSuccess: onConnect,
        onFailure: onFailure,
        useSSL: false
    };
    client.connect(options);
    console.log("Attempting to connect...");
}

function onConnect() {
    console.log("Connected...YES!");

    // Once a connection has been made, make a subscription and/or send a message.
    client.subscribe(topic);
    let btnSend = document.getElementById("btnSend");
    btnSend.disabled = false;

    // Fill graph with empty data to initialize
    for (let i = 0; i <= index; i++) {
        //data.push([i, Math.floor(Math.random() * 16) + 5]); // 5 -> 20
        data.push([i, 0]); // 5 -> 20
    }

    let options = {
        fractions: false,
        drawPoints: true,
        showRoller: false,
        valueRange: [0, 50],
        labels: ['Counter', 'Temperature']
    }
    g = new Dygraph(document.getElementById("graphdiv"), data, options);
}

function sendMessage() {
    let txtMessage = document.getElementById("txtMessage");
    message = new Paho.MQTT.Message(txtMessage.value);
    message.destinationName = topic;
    client.send(message);
    console.log(client.getTraceLog());
}

function MQTTDisconnect() {
    if(client.isConnected) {
        client.disconnect();
        let btnSend = document.getElementById("btnSend");
        btnSend.disabled = true;
        index = 20;
        data = [];
        console.log("Disconnected...DONE!");
    }
}

function onMessageArrived(message) {
    console.log("Message arrived: " + message.payloadString);

    //let dataLine = message.payloadString.split(",");
    index++;
    data.push([index, Number(message.payloadString)]);
    data.shift();
    g.updateOptions( { 'file': data } );
}

function onConnectionLost(response) {
    if (response.errorCode !== 0) {
        console.log("Connection lost: " + response.errorMessage);
    }
}

function onFailure() {
    console.log("Connection failed!");
}