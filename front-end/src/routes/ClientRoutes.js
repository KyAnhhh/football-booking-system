import { Route } from "react-router-dom";

import ClientLayout from "../layouts/ClientLayout";

import Home from "../pages/client/Home";
import Product from "../pages/client/Product";
import ProductDetail from "../pages/client/ProductDetail";
import About from "../pages/client/About";
import Contact from "../pages/client/Contact";
import Profile from "../pages/client/Profile";
import Cart from "../pages/client/Cart";
import Checkout from "../pages/client/Checkout";
import Collection from "../pages/client/Collection";
import Service from "../pages/client/Service";
import MyOrders from "../pages/client/MyOrders";
import MyBookings from "../pages/client/MyBookings";
import BookingDetailPage from "../pages/client/BookingDetailPage";
import Review from "../pages/client/Review";
import FootballField from "../pages/client/FootballField";
import FootballFieldDetail from "../pages/client/FootballFieldDetail";
import PaymentFootballField from "../pages/client/PaymentFootballField";
import OrderPaymentPage from "../pages/client/OrderPaymentPage";

import ReviewSection from "../components/review/ReviewSection";

const ClientRoutes = (
  <>
    <Route
      path="/"
      element={
        <ClientLayout>
          <Home />
        </ClientLayout>
      }
    />

    <Route
      path="/products"
      element={
        <ClientLayout>
          <Product />
        </ClientLayout>
      }
    />

    <Route
      path="/products/product-details/:id"
      element={
        <ClientLayout>
          <ProductDetail />
        </ClientLayout>
      }
    />

    <Route
      path="/about"
      element={
        <ClientLayout>
          <About />
        </ClientLayout>
      }
    />

    <Route
      path="/contact"
      element={
        <ClientLayout>
          <Contact />
        </ClientLayout>
      }
    />

    <Route
      path="/profile"
      element={
        <ClientLayout>
          <Profile />
        </ClientLayout>
      }
    />

    <Route
      path="/cart"
      element={
        <ClientLayout>
          <Cart />
        </ClientLayout>
      }
    />

    <Route
      path="/collections"
      element={
        <ClientLayout>
          <Collection />
        </ClientLayout>
      }
    />

    <Route
      path="/service"
      element={
        <ClientLayout>
          <Service />
        </ClientLayout>
      }
    />

    <Route
      path="/my-orders"
      element={
        <ClientLayout>
          <MyOrders />
        </ClientLayout>
      }
    />

    <Route
      path="/my-bookings"
      element={
        <ClientLayout>
          <MyBookings />
        </ClientLayout>
      }
    />

    <Route
      path="/my-bookings/:bookingId"
      element={
        <ClientLayout>
          <BookingDetailPage />
        </ClientLayout>
      }
    />

    <Route
      path="/cart/checkout"
      element={
        <ClientLayout>
          <Checkout />
        </ClientLayout>
      }
    />

    <Route
      path="/orders/:orderId/review/:productId"
      element={
        <ClientLayout>
          <ReviewSection />
        </ClientLayout>
      }
    />

    <Route
      path="/review/:id"
      element={
        <ClientLayout>
          <Review />
        </ClientLayout>
      }
    />

    <Route
      path="/football-fields"
      element={
        <ClientLayout>
          <FootballField />
        </ClientLayout>
      }
    />

    <Route
      path="/football-fields/:id/detail"
      element={
        <ClientLayout>
          <FootballFieldDetail />
        </ClientLayout>
      }
    />

    <Route
      path="/football-fields/:id/payment"
      element={
        <ClientLayout>
          <PaymentFootballField />
        </ClientLayout>
      }
    />

    <Route
      path="/orders/:id/payment"
      element={
        <ClientLayout>
          <OrderPaymentPage />
        </ClientLayout>
      }
    />

  </>
);

export default ClientRoutes;
