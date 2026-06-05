import "../assets/styles/AdminLayout.css";
import Footer from "../components/client/Footer";
import Header from "../components/client/Header";


export default function ClientLayout({ children }) {
  return (
    <div >
          <Header />
          <main >{children}</main>
          <Footer />
        </div>  
  );
}
