import "../assets/styles/Footer.css"

export default function Footer() {
  return (
    <footer className="fs-footer">
      <div className="fs-footer-inner">
        <div className="fs-footer-brand">
          <a href="/" className="fs-footer-logo" aria-label="FootballShop Home">
            <span className="fs-footer-ball">⚽</span>
            <span className="fs-footer-title">Football Shop</span>
          </a>
          <p className="fs-footer-desc">
            Trusted destination for quality football gear — jerseys, boots, accessories.
            Play with confidence. Ship fast. Return easy.
          </p>
        </div>

        <div className="fs-footer-col">
          <h4>Shop</h4>
          <ul>
            <li><a href="/products">All Products</a></li>
            <li><a href="/sale">On Sale</a></li>
            <li><a href="/new">New Arrivals</a></li>
          </ul>
        </div>

        <div className="fs-footer-col">
          <h4>Support</h4>
          <ul>
            <li><a href="/contact">Contact</a></li>
            <li><a href="/faq">FAQs</a></li>
            <li><a href="/returns">Return Policy</a></li>
          </ul>
        </div>

        <div className="fs-footer-col">
          <h4>Follow</h4>
          <div className="fs-footer-socials">
            <a href="/" aria-label="Facebook">Facebook</a>
            <a href="/" aria-label="Instagram">Instagram</a>
            <a href="/" aria-label="Twitter">Twitter</a>
          </div>
        </div>
      </div>

      <div className="fs-footer-bottom">
        <div className="fs-footer-bottom-inner">
          <span>© {new Date().getFullYear()} FootballShop</span>
          <span className="fs-footer-sep">•</span>
          <a href="/privacy">Privacy</a>
          <span className="fs-footer-sep">•</span>
          <a href="/terms">Terms</a>
        </div>
      </div>
    </footer>
  );
}

