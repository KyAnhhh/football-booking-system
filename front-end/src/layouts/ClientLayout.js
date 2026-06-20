import "../assets/styles/AdminLayout.css";
import Footer from "../components/client/Footer";
import Header from "../components/client/Header";


export default function ClientLayout({ children }) {
  return (
    <div className="client-shell">
      <Header />
      <main className="client-main">{children}</main>
      <Footer />
    </div>
  );
}
