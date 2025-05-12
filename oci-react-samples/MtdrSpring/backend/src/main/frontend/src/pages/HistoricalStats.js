import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext"; // Import the AuthContext
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  LabelList,
  Cell,
  PieChart,
  Pie,
} from "recharts";
import "../styles/HistoricalStats.css";
import HeaderMngr from "../components/HeaderMngr";

export default function HistoricalStats() {
  const [passedemployeeId, setEmployeeId] = useState(null);
  const [passedProjectId, setPassedProjectId] = useState(null);
  const [actualSprint, setActualSprint] = useState(null);
  const [employees, setEmployees] = useState([]);
  const [screenLoading, setScreenLoading] = useState(true);
  const [sprints, setSprints] = useState([]);
  const navigate = useNavigate();
  const { authData } = useAuth(); // Use the AuthContext to get authData

  useEffect(() => {
    if (!authData) {
      console.log("No authData found, redirecting to login page.");
      navigate("/login");
      return;
    }
    const { employeeId, projectId } = authData;
    console.log("employeeId:", employeeId, "projectId:", projectId);
    setEmployeeId(employeeId);
    setPassedProjectId(projectId);
    fetchActualSprint(projectId, employeeId);
  }, [authData, navigate]);

  const fetchActualSprint = async (projectId, managerId) => {
    try {
      const response = await fetch(`/sprint/project/${projectId}`);
      if (response.ok) {
        const data = await response.json();
        setActualSprint(data);
        console.log("Actual sprint:", data);
        fetchEmployees(managerId, data.id);
        fetchSprints(projectId);
      } else {
        console.error("Error fetching actual sprint:", response.statusText);
      }
    } catch (error) {
      console.error("Error fetching actual sprint:", error);
    }
  };

  const fetchEmployees = async (managerId, sprintId) => {
    try {
      const response = await fetch(`/employees/managerId/${managerId}`);
      const data = await response.json();
      setEmployees(data);

      setScreenLoading(false);
    } catch (error) {
      console.error("Error fetching employees:", error);
      setScreenLoading(false);
    }
  };

  const fetchSprints = async (projectId) => {
    try{
        const response = await fetch(`/sprint/projects/${projectId}`);
        const data = await response.json();
        setSprints(data);
        console.log("Sprints:", data);
    }

    catch (error) {
        console.error("Error fetching sprints:", error);
    }
  }

  return (
    <div className="statsContainer">
      {screenLoading ? (
        <div className="loading-screen">
          <div className="spinner"></div>
        </div>
      ) : (
        <>
          <div className="chartContainer">
            <HeaderMngr
              actualSprint={actualSprint}
              employees={employees}
            />
        
          </div>
        </>
      )}
    </div>
  );
}
