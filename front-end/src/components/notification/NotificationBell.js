import { useCallback, useEffect, useRef, useState } from "react";
import { Bell, CheckCheck } from "lucide-react";
import { useNavigate } from "react-router-dom";
import {
  getNotifications,
  getUnreadNotificationCount,
  markAllNotificationsAsRead,
  markNotificationAsRead,
} from "../../services/notificationService";
import "../../assets/styles/NotificationBell.css";

const formatTime = (value) =>
  value
    ? new Date(value).toLocaleString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      })
    : "";

export default function NotificationBell({ context = "client" }) {
  const navigate = useNavigate();
  const wrapperRef = useRef(null);
  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadUnreadCount = useCallback(async () => {
    try {
      const count = await getUnreadNotificationCount();
      setUnreadCount(Number(count || 0));
    } catch (error) {
      setUnreadCount(0);
    }
  }, []);

  const loadNotifications = useCallback(async () => {
    try {
      setLoading(true);
      const data = await getNotifications(10);
      setNotifications(data || []);
    } catch (error) {
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadUnreadCount();
  }, [loadUnreadCount]);

  useEffect(() => {
    if (open) {
      loadNotifications();
    }
  }, [open, loadNotifications]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const getTargetPath = (notification) => {
    if (!notification.referenceId) {
      return context === "admin" ? "/admin" : "/";
    }

    if (notification.referenceType === "ORDER") {
      return context === "admin" ? "/admin/orders" : "/my-orders";
    }

    if (notification.referenceType !== "BOOKING") {
      return context === "admin" ? "/admin" : "/";
    }

    return context === "admin"
      ? `/admin/bookings/${notification.referenceId}`
      : `/my-bookings/${notification.referenceId}`;
  };

  const handleNotificationClick = async (notification) => {
    try {
      if (!notification.isRead) {
        await markNotificationAsRead(notification.id);
      }
    } catch (error) {
      console.error("Mark notification failed:", error);
    } finally {
      setOpen(false);
      navigate(getTargetPath(notification));
    }
  };

  const handleReadAll = async () => {
    try {
      await markAllNotificationsAsRead();
      await Promise.all([loadUnreadCount(), loadNotifications()]);
    } catch (error) {
      console.error("Mark all notifications failed:", error);
    }
  };

  return (
    <div className={`notification-bell ${context}`} ref={wrapperRef}>
      <button
        type="button"
        className="notification-trigger"
        aria-label="Mở thông báo"
        aria-expanded={open}
        onClick={() => setOpen((current) => !current)}
      >
        <Bell size={20} />
        {unreadCount > 0 && (
          <span className="notification-count">
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      <div className={`notification-dropdown ${open ? "show" : ""}`}>
        <div className="notification-head">
          <div>
            <strong>Thông báo</strong>
            <span>{unreadCount} chưa đọc</span>
          </div>

          <button type="button" onClick={handleReadAll} disabled={unreadCount === 0}>
            <CheckCheck size={16} />
            Đọc hết
          </button>
        </div>

        <div className="notification-list">
          {loading ? (
            <div className="notification-empty">Đang tải thông báo...</div>
          ) : notifications.length === 0 ? (
            <div className="notification-empty">Chưa có thông báo nào.</div>
          ) : (
            notifications.map((notification) => (
              <button
                key={notification.id}
                type="button"
                className={`notification-item ${notification.isRead ? "read" : "unread"}`}
                onClick={() => handleNotificationClick(notification)}
              >
                <span className="notification-dot" />
                <span>
                  <strong>{notification.title}</strong>
                  <small>{notification.message}</small>
                  <em>{formatTime(notification.createdAt)}</em>
                </span>
              </button>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
