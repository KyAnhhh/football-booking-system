export default function Dashboard() {
  return (
    <div>
      <h2>Admin Dashboard</h2>
      <p>Welcome to the Shop Bóng Đá Admin Panel 🎉</p>

      {/* Quick Stats */}
      <div className="stats-grid">
        <div className="stat-card">
          <h3>👤 Users</h3>
          <p>120</p>
        </div>
        <div className="stat-card">
          <h3>📦 Products</h3>
          <p>58</p>
        </div>
        <div className="stat-card">
          <h3>🛒 Orders</h3>
          <p>32</p>
        </div>
        <div className="stat-card">
          <h3>💰 Revenue</h3>
          <p>$5,200</p>
        </div>
      </div>

      {/* Recent Orders */}
      <div className="recent-orders">
        <h3>Recent Orders</h3>
        <table>
          <thead>
            <tr>
              <th>Order ID</th>
              <th>User</th>
              <th>Total</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>#1001</td>
              <td>Kỳ Anh</td>
              <td>$120</td>
              <td>Completed</td>
            </tr>
            <tr>
              <td>#1002</td>
              <td>Hoàng</td>
              <td>$80</td>
              <td>Pending</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
