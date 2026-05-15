import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import CreateOrder from "./pages/orders/CreateOrder.jsx";
import SearchOrder from "./pages/orders/SearchOrder.jsx";
import CustomerOrders from "./pages/orders/CustomerOrders.jsx";
import DispatchOrder from "./pages/orders/DispatchOrder.jsx";
import ManageOrder from "./pages/orders/ManageOrder.jsx";
import CalculatePriority from "./pages/orders/CalculatePriority";
import "./App.css";

function PrivateRoute({ children }) {
  const token = localStorage.getItem("token");

  if (!token) {
    return <Navigate to="/login" />;
  }

  return children;
}

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <Dashboard />
                </PrivateRoute>
              }
          />

          <Route
              path="/orders/new"
              element={
                <PrivateRoute>
                  <CreateOrder />
                </PrivateRoute>
              }
          />

          <Route
              path="/orders/search"
              element={
                <PrivateRoute>
                  <SearchOrder />
                </PrivateRoute>
              }
          />

          <Route
              path="/orders/customer"
              element={
                <PrivateRoute>
                  <CustomerOrders />
                </PrivateRoute>
              }
          />

          <Route
              path="/orders/dispatch"
              element={
                <PrivateRoute>
                  <DispatchOrder />
                </PrivateRoute>
              }
          />

        <Route
            path="/orders/manage"
            element={
                <PrivateRoute>
                    <ManageOrder />
                </PrivateRoute>
            }
        />
        <Route
            path="/orders/:id/priority"
            element={
                <PrivateRoute>
                    <CalculatePriority />
                </PrivateRoute>
            }
        />

        </Routes>
      </BrowserRouter>
  );
}

export default App;
