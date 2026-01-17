import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.subscribers = new Map();
  }

  connect() {
    return new Promise((resolve, reject) => {
      if (this.connected) {
        resolve();
        return;
      }

      this.client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        connectHeaders: {},
        debug: (str) => {
          console.log('WebSocket Debug:', str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      this.client.onConnect = (frame) => {
        console.log('WebSocket Connected:', frame);
        this.connected = true;
        resolve();
      };

      this.client.onStompError = (frame) => {
        console.error('WebSocket Error:', frame.headers['message']);
        console.error('Additional details:', frame.body);
        reject(new Error(frame.headers['message']));
      };

      this.client.onWebSocketError = (error) => {
        console.error('WebSocket connection error:', error);
        reject(error);
      };

      this.client.onDisconnect = () => {
        console.log('WebSocket Disconnected');
        this.connected = false;
      };

      this.client.activate();
    });
  }

  disconnect() {
    if (this.client && this.connected) {
      this.client.deactivate();
      this.connected = false;
    }
  }

  subscribe(destination, callback) {
    if (!this.connected) {
      console.warn('WebSocket not connected. Attempting to connect...');
      this.connect().then(() => {
        this.subscribe(destination, callback);
      });
      return;
    }

    const subscription = this.client.subscribe(destination, (message) => {
      try {
        const data = JSON.parse(message.body);
        callback(data);
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    });

    this.subscribers.set(destination, subscription);
    return subscription;
  }

  unsubscribe(destination) {
    const subscription = this.subscribers.get(destination);
    if (subscription) {
      subscription.unsubscribe();
      this.subscribers.delete(destination);
    }
  }

  // Specific methods for complaint notifications
  subscribeToComplaints(callback) {
    return this.subscribe('/topic/complaints', callback);
  }

  unsubscribeFromComplaints() {
    this.unsubscribe('/topic/complaints');
  }
}

// Create a singleton instance
const webSocketService = new WebSocketService();

export default webSocketService;