import React, { useState, useEffect } from "react";
// import { motion } from "framer-motion";
import "../../assets/styles/About.css";

export default function About() {
 const [isVisible, setIsVisible] = useState(false);

 useEffect(() => {
   const observer = new IntersectionObserver((entries) => {
     entries.forEach(entry => {
       if (entry.isIntersecting) {
         entry.target.classList.add('animate-in');
         setIsVisible(true);
       }
     });
   }, { threshold: 0.1 });

   const animatedElements = document.querySelectorAll('.animate-on-scroll');
   animatedElements.forEach(el => observer.observe(el));

   return () => {
     animatedElements.forEach(el => observer.unobserve(el));
   };
 }, []);

 const timeline = [
   {
     year: "2015",
     title: "Founded",
     description: "Shop Bóng Đá được thành lập bởi những người đam mê bóng đá"
   },
   {
     year: "2017",
     title: "First Store",
     description: "Mở cửa hàng đầu tiên tại TP.HCM"
   },
   {
     year: "2020",
     title: "Online Expansion",
     description: "Ra mắt website và mở rộng dịch vụ giao hàng toàn quốc"
   },
   {
     year: "2023",
     title: "Premium Partner",
     description: "Trở thành đối tác chính thức của Nike và Adidas"
   },
   {
     year: "2025",
     title: "National Leader",
     description: "Top 1 cửa hàng bóng đá uy tín nhất Việt Nam"
   }
 ];

 return (
   <div className="about-container">
     {/* Hero Section */}
     <section className="about-hero">
       <div className="hero-bg-animation">
         <div className="floating-ball ball-1"></div>
         <div className="floating-ball ball-2"></div>
         <div className="floating-ball ball-3"></div>
       </div>
       <div className="hero-content">
         <h1 className="hero-title">Về Shop Bóng Đá</h1>
         <p className="hero-subtitle">Nơi đam mê bóng đá trở thành hiện thực</p>
       </div>
     </section>

     {/* Main About Section */}
     <section className="about-main animate-on-scroll">
       <div className="container">
         <div className="about-grid">
           {/* About Text */}
           <div className="about-text">
             <div className="section-header">
               <div className="section-icon">⚽</div>
               <h2>Câu Chuyện Của Chúng Tôi</h2>
             </div>
             <p className="about-intro">
               Chào mừng đến với <strong>Shop Bóng Đá</strong> — điểm đến đáng tin cậy cho
               những người yêu bóng đá. Chúng tôi mang đến đam mê, chất lượng và phong cách
               cho mọi tín đồ bóng đá.
             </p>
             <p className="about-mission">
               Được thành lập bởi những người chơi bóng, sứ mệnh của chúng tôi là cung cấp
               những chiếc áo đấu, giày bóng đá và phụ kiện hàng đầu để giúp bạn phát huy
               tối đa khả năng trên sân cỏ.
             </p>
             <div className="stats-grid">
               <div className="stat-item">
                 <div className="stat-number">1000+</div>
                 <div className="stat-label">Sản phẩm</div>
               </div>
               <div className="stat-item">
                 <div className="stat-number">50000+</div>
                 <div className="stat-label">Khách hàng</div>
               </div>
               <div className="stat-item">
                 <div className="stat-number">5+</div>
                 <div className="stat-label">Năm kinh nghiệm</div>
               </div>
             </div>
           </div>

           {/* About Image */}
           <div className="about-image">
             <div className="image-container">
               <img
                 src="https://images.unsplash.com/photo-1552673594-d42f73b99e14?w=600&fit=crop"
                 alt="Đội ngũ Shop Bóng Đá"
                 className="about-img"
               />
               <div className="image-overlay">
                 <div className="overlay-content">
                   <h3>Đội Ngũ Chuyên Nghiệp</h3>
                   <p>Những người đam mê bóng đá thực thụ</p>
                 </div>
               </div>
             </div>
           </div>
         </div>
       </div>
     </section>

     {/* Timeline Section */}
     <section className="timeline-section animate-on-scroll">
       <div className="container">
         <div className="section-header">
           <div className="section-icon">📅</div>
           <h2>Hành Trình Phát Triển</h2>
         </div>
         <div className="timeline">
           {timeline.map((item, index) => (
             <div key={index} className={`timeline-item ${index % 2 === 0 ? 'left' : 'right'}`}>
               <div className="timeline-content">
                 <div className="timeline-year">{item.year}</div>
                 <h3>{item.title}</h3>
                 <p>{item.description}</p>
               </div>
               <div className="timeline-icon">
                 <span>⭐</span>
               </div>
             </div>
           ))}
         </div>
       </div>
     </section>

     {/* Team Section */}
     <section className="team-section animate-on-scroll">
       <div className="container">
         <div className="section-header">
           <div className="section-icon">👥</div>
           <h2>Đội Ngũ Của Chúng Tôi</h2>
         </div>
         <div className="team-grid">
           <div className="team-member">
             <div className="member-image">
               <img src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300" alt="Founder" />
               <div className="member-overlay">Founder</div>
             </div>
             <h3>Nguyễn Văn A</h3>
             <p>Người sáng lập & CEO</p>
           </div>
           <div className="team-member">
             <div className="member-image">
               <img src="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=300" alt="Manager" />
               <div className="member-overlay">Manager</div>
             </div>
             <h3>Trần Thị B</h3>
             <p>Quản lý cửa hàng</p>
           </div>
           <div className="team-member">
             <div className="member-image">
               <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300" alt="Expert" />
               <div className="member-overlay">Expert</div>
             </div>
             <h3>Lê Văn C</h3>
             <p>Chuyên gia sản phẩm</p>
           </div>
         </div>
       </div>
     </section>

     {/* Values Section */}
     <section className="values-section">
       <div className="container">
         <div className="section-header">
           <div className="section-icon">🎯</div>
           <h2>Giá Trị Cốt Lõi</h2>
         </div>
         <div className="values-grid">
           <div className="value-card">
             <div className="value-icon">🔥</div>
             <h3>Đam Mê</h3>
             <p>Chúng tôi sống và thở bằng bóng đá</p>
           </div>
           <div className="value-card">
             <div className="value-icon">⭐</div>
             <h3>Chất Lượng</h3>
             <p>Chỉ bán hàng chính hãng 100%</p>
           </div>
           <div className="value-card">
             <div className="value-icon">🤝</div>
             <h3>Tin Cậy</h3>
             <p>Đồng hành cùng bạn mọi trận đấu</p>
           </div>
         </div>
       </div>
     </section>
   </div>
 );
}