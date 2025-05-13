import React, { useState, useEffect, PureComponent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/HistoricalStats.css";
import HeaderMngr from "../components/HeaderMngr";
import { BarChart, Bar, Rectangle, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

export default function HistoricalStats() {
  const [passedemployeeId, setEmployeeId] = useState(null);
  const [passedProjectId, setPassedProjectId] = useState(null);
  const [actualSprint, setActualSprint] = useState(null);
  const [employees, setEmployees] = useState([]);
  const [screenLoading, setScreenLoading] = useState(true);
  const [sprints, setSprints] = useState([]);
  const [selectedTab, setSelectedTab] = useState("sprint");
  const [workedHoursObject, setWorkedHoursObject] = useState({});
  const navigate = useNavigate();
  const { authData } = useAuth();

  useEffect(() => {
    if (!authData) {
      console.log("No authData found, redirecting to login page.");
      navigate("/login");
      return;
    }
    const { employeeId, projectId } = authData;
    setEmployeeId(employeeId);
    setPassedProjectId(projectId);
    fetchActualSprint(projectId, employeeId);
  }, [authData, navigate]);

  useEffect(() => {
  if (employees.length > 0 && sprints.length > 0) {
    fetchWorkedHours(employees, sprints);
  }
}, [employees, sprints]);


  const fetchActualSprint = async (projectId, managerId) => {
    try {
      const response = await fetch(`/sprint/project/${projectId}`);
      if (response.ok) {
        const data = await response.json();
        setActualSprint(data);
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
    try {
      const response = await fetch(`/sprint/projects/${projectId}`);
      const data = await response.json();
      setSprints(data);
      console.log("Sprints data:", data);
    } catch (error) {
      console.error("Error fetching sprints:", error);
    }
  };

  const fetchWorkedHours = async (employeeList, sprintList) => {
  const workedHoursArray = [];

  for (const sprintF of sprintList) {
    let sprintEntry = {
      sprintName: sprintF.name,
    };

    let totalHoursPerSprint = 0;

    for (const employeeF of employeeList) {
      const response = await fetch(`/assignedDev/${employeeF.id}/sprint/${sprintF.id}/workedHours/kpi`);
      if (response.ok) {
        const data = await response.json();
        sprintEntry[employeeF.name] = data.workedHoursKpi;
        totalHoursPerSprint += data.workedHoursKpi;
      } else {
        console.error("Error fetching worked hours:", response.statusText);
      }
    }

    sprintEntry.totalWorkedHours = totalHoursPerSprint;
    workedHoursArray.push(sprintEntry);
  }

  console.log("Worked hours array for chart:", workedHoursArray);
  setWorkedHoursObject(workedHoursArray); // ✅ Important!
};


  return (
    <div className="statsContainer">
      {screenLoading ? (
        <div className="loading-screen">
          <div className="spinner"></div>
        </div>
      ) : (
        <>
          <div className="chartContainer">
            <HeaderMngr actualSprint={actualSprint} employees={employees} />

            <div className="segment-control">
            <button
              className={selectedTab === "sprint" ? "active-tab" : ""}
              onClick={() => setSelectedTab("sprint")}
            >
              Graphs
            </button>
            <button
              className={selectedTab === "historical" ? "active-tab" : ""}
              onClick={() => setSelectedTab("historical")}
            >
              Written report
            </button>
          </div>

            {selectedTab === "sprint" && (
              <div className="chart-container">
                <h3>Hours worked by sprint</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart
                    width={500}
                    height={300}
                    data={workedHoursObject}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="sprintName" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="totalWorkedHours" fill="#8884d8" />
                  </BarChart>
                </ResponsiveContainer>

                
              </div>
            )}

            {selectedTab === "historical" && (
              <div>
                <h3>Historical Statistics</h3>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
