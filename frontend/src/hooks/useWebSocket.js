import { useEffect, useRef, useState } from 'react';
import webSocketService from '../services/WebSocketService';

export const useWebSocket = () => {
  const [connected, setConnected] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const subscriptionRef = useRef(null);

  useEffect(() => {
    const connectWebSocket = async () => {
      try {
        await webSocketService.connect();
        setConnected(true);
        
        // Subscribe to complaint notifications
        subscriptionRef.current = webSocketService.subscribeToComplaints((notification) => {
          console.log('Received notification:', notification);
          setNotifications(prev => [notification, ...prev.slice(0, 49)]); // Keep last 50 notifications
        });
      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
        setConnected(false);
      }
    };

    connectWebSocket();

    return () => {
      if (subscriptionRef.current) {
        webSocketService.unsubscribeFromComplaints();
      }
      webSocketService.disconnect();
      setConnected(false);
    };
  }, []);

  const clearNotifications = () => {
    setNotifications([]);
  };

  return {
    connected,
    notifications,
    clearNotifications
  };
};

export const useComplaintNotifications = (onNotification) => {
  const [connected, setConnected] = useState(false);
  const subscriptionRef = useRef(null);

  useEffect(() => {
    const connectAndSubscribe = async () => {
      try {
        await webSocketService.connect();
        setConnected(true);
        
        subscriptionRef.current = webSocketService.subscribeToComplaints((notification) => {
          if (onNotification) {
            onNotification(notification);
          }
        });
      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
        setConnected(false);
      }
    };

    connectAndSubscribe();

    return () => {
      if (subscriptionRef.current) {
        webSocketService.unsubscribeFromComplaints();
      }
    };
  }, [onNotification]);

  return { connected };
};