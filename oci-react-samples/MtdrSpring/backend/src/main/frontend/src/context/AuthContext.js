// src/context/AuthContext.js
import React, { createContext, useContext, useState} from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authData, setAuthData] = useState(() => {
    // Recupera datos si están en localStorage
    const employeeId = localStorage.getItem("employeeId");
    const managerId = localStorage.getItem("managerId");
    const projectId = localStorage.getItem("projectId");

    return employeeId ? { employeeId, managerId, projectId } : null;
  });

  const login = ({ employeeId, managerId, projectId }) => {
    localStorage.setItem("employeeId", employeeId);
    localStorage.setItem("managerId", managerId);
    localStorage.setItem("projectId", projectId);
    setAuthData({ employeeId, managerId, projectId });
  };

  const logout = () => {
    localStorage.clear();
    setAuthData(null);
  };

  return (
    <AuthContext.Provider value={{ authData, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
