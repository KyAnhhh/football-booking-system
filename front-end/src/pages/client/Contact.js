import "../../assets/styles/Contact.css";

export default function Contact() {
  return (
    <div className="contact-container">
      <section className="contact-section">
        <div className="contact-info">
          <h2>Contact Us</h2>
          <p>📍 123 Football Street, Sports City</p>
          <p>📞 +84 123 456 789</p>
          <p>✉️ contact@footballshop.com</p>
        </div>

        <div className="contact-form">
          <h3>Send us a message</h3>
          <form>
            <input type="text" placeholder="Your Name" required />
            <input type="email" placeholder="Your Email" required />
            <textarea rows="5" placeholder="Your Message" required></textarea>
            <button type="submit">Send Message</button>
          </form>
        </div>
      </section>
    </div>
  );
}
