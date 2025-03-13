import React, { useState } from "react";
import "../styles/Login.css"; // Import the CSS file
import "../components/API"; // Import the API file

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = (e) => {
        e.preventDefault();
        console.log("Logging in with", email, password);
    };

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
                    required 
                    pattern="^(?=.*\d)[A-Za-z\d]{8,}$"
                />
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default Login;
