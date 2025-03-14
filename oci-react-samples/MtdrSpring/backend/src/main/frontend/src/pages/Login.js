import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Login.css"; // Import the CSS file
import EMPLOYEE_API from "../components/API"; // Import the API file

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setLoading] = useState(false);
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

            if (response.ok) {
                const result = await response.json();
                console.log("Login successful", result);
                // Handle successful login, e.g., store token, redirect, etc.
                localStorage.setItem("employeeId", result.employeeId);

                if(result.managerId==null){
                    console.log("Manager login");
                    navigate("/managertasks");
                }
                else{
                    console.log("Employee login");
                    navigate("/usertasks");
                }
            } else {
                const error = await response.json();
                console.error("Login failed", error);
                // Handle login failure, e.g., show error message
            }
        } catch (error) {
            console.error("Error during login", error);
            // Handle network or other errors
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="login-container">
            <form className="login-box" onSubmit={handleLogin}>
                <h2>Login</h2>
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
