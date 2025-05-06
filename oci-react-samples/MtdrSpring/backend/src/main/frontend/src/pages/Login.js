import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Login.css";
import { useAuth } from "../context/AuthContext";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();
  const { login, employeeId, managerId} = useAuth(); // Usa el contexto

  useEffect(() => {
    if (employeeId && managerId !== undefined) {
      if (managerId === null) {
        navigate("/managertasks");
      } else {
        navigate("/usertasks");
      }
    }
  }, [employeeId, managerId, navigate]);




  async function handleLogin(event) {
    event.preventDefault();
    setLoading(true);
    const data = { email, password };

    try {
      const response = await fetch("employees/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        const result = await response.json();
        login({
          employeeId: result.employeeId,
          managerId: result.managerId,
          projectId: result.projectId,
        });

        if (result.managerId == null) {
          navigate("/managertasks");
        } else {
          navigate("/usertasks");
        }
      } else {
        const errorText = await response.text();
        if (response.status === 401) {
          setErrorMessage("Invalid email or password.");
        } else {
          setErrorMessage("An error occurred.");
        }
        console.error("Login failed:", errorText);
      }
    } catch (error) {
      console.error("Error:", error);
      setErrorMessage("Network error.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-container">
      <form className="login-box" onSubmit={handleLogin}>
        <h2>Login</h2>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit" disabled={isLoading}>
          {isLoading ? "Logging in..." : "Login"}
        </button>
      </form>
    </div>
  );
}

export default Login;
