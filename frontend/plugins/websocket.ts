import { EventEmitter } from 'events'

/**
 * Class resposible for the websocket connection
 */
class WebsocketWrapper extends EventEmitter {
  private webSocket: WebSocket
  private open: boolean
  private url: string
  private reconnectTimeout: number

  /**
   * creates new instance and sets default values
   * @param url url to which the websocket-client should connect
   */
  constructor(url: string) {
    super()
    this.webSocket = new WebSocket(url)
    this.registerListener()
    this.open = false
    this.url = url
    this.reconnectTimeout = 250
  }

  private registerListener() {
    // Run when there is an incoming message
    this.webSocket.onmessage = (event) => {
      // Parse message after custom scheme
      const msg: string = event.data
      const command: string = msg.substr(0, msg.indexOf(' '))
      const data: string = msg.replace(command + ' ', '')
      // This class is a child of the eventemitter, so it can emit events, to which other parts in the code can subscribe to
      this.emit(command, data)
    }
    // Run when the connection is sucessfully established
    this.webSocket.onopen = () => {
      // Set websocket open and reset reconnecttimer
      this.open = true
      this.reconnectTimeout = 250
    }
    // Run when the connection gets closed
    this.webSocket.onclose = () => {
      // Set open to false so there are now trys to send something while closed connection and start trying to reconnect to the server
      this.open = false
      console.log('Websocket closed, retrying connection')
      setTimeout(() => {
        this.webSocket = new WebSocket(this.url)
        console.log('Reconnecting to websocket')
        this.registerListener()
        this.reconnectTimeout = this.reconnectTimeout + 250
      }, Math.min(10000, this.reconnectTimeout)) // Interval gets greater every try, max out at 10 seconds
    }
  }

  // Public method used for sending data to server
  public send(invoke: string, data: string) {
    // If the connection is not oppen try again in 1 second, else send
    if (!this.open) {
      setTimeout(() => this.trySend(invoke, data), 1000)
    } else {
      this.trySend(invoke, data)
    }
  }

  // Internal method checking if sending is possible
  private trySend(invoke: string, data: string) {
    // If websocket is closed try the send method again and retry in 1 sec, else finally send to server
    if (!this.open) {
      this.send(invoke, data)
    } else {
      this.webSocket.send(invoke + ' ' + data)
    }
  }
}

export default (_: any, inject: any) => {
  const wraper = new WebsocketWrapper('ws://' + window.location.host + ':3333')
  // Inject the instance to the app, now everywhere available with this.$websocket
  inject('websocket', wraper)
}
