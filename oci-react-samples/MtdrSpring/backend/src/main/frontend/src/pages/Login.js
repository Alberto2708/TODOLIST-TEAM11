import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Login.css"; // Import the CSS file
import EMPLOYEE_API from "../components/API"; // Import the API file

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const navigate = useNavigate();

    async function handleLogin(event) {
        event.preventDefault();
        console.log("handleLogin(" + email + ", " + password + ")");
        setLoading(true);

        const data = { email, password };

        try {
            const response = await fetch("employees/login", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });
        
            console.log("Response status:", response.status);
        
            if (response.ok) {
                const result = await response.json();
                console.log("Login successful", result);
                localStorage.setItem("employeeId", result.employeeId);
                localStorage.setItem("managerId", result.managerId);
                localStorage.setItem("projectId", result.projectId);
        
                if (result.managerId == null) {
                    console.log("Manager login");
                    navigate("/managertasks");
                } else {
                    console.log("Employee login");
                    navigate("/usertasks");
                }
            } else {
                const errorText = await response.text();
                console.error("Login failed, response:", errorText);
                
                if (response.status === 401) {
                    setErrorMessage("Invalid email or password. Please try again.");
                } else {
                    setErrorMessage("An error occurred. Please try again later.");
                }
            }
        } catch (error) {
            console.error("Error during login", error);
            setErrorMessage("An error occurred. Please try again later.");
        } finally {
            setLoading(false);
        }
        
    }

    return (
        <div className="login-container">
            <form className="login-box" onSubmit={handleLogin}>
                <h2>Login</h2>
                {errorMessage && <p className="error-message">{errorMessage}</p>}{/* Display error message if any */}
                <input 
                    type="email" 
                    placeholder="Email" 
                    value={email} 
                    onChange={(e) => setEmail(e.target.value)}
                    required 
                    pattern="[a-z0-9.~!$%^&*_=+}{'?-.]+@[a-z0-9.-]+\.[a-z]{2,}$"
                    title="Please enter a valid email address"
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
};

export default Login;
