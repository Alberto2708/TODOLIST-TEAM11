import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/HistoricalStats.css";
import HeaderMngr from "../components/HeaderMngr";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

export default function HistoricalStats() {
  const [passedemployeeId, setEmployeeId] = useState(null);
  const [passedProjectId, setPassedProjectId] = useState(null);
  const [actualSprint, setActualSprint] = useState(null);
  const [employees, setEmployees] = useState([]);
  const [screenLoading, setScreenLoading] = useState(true); // ✅ Show loading screen
  const [sprints, setSprints] = useState([]);
  const [selectedTab, setSelectedTab] = useState("sprint");
  const navigate = useNavigate();
  const { authData } = useAuth();
  const [workedHoursObject, setWorkedHoursObject] = useState({});
  const [completedTasksNumber, setcompletedTasksNumber] = useState({});
  const [completedTasks, setCompletedTasks] = useState({});

  const colors = ["#1e3888", "#47a8bd", "#f5e663", "#ffad69", "#9c3848"];

  useEffect(() => {
    if (!authData) {
      console.log("No authData found, redirecting to login page.");
      navigate("/login");
      return;
    }
    const { employeeId, projectId } = authData;
    setEmployeeId(employeeId);
    setPassedProjectId(projectId);
    fetchAllData(projectId, employeeId); // ✅ Load all data together
  }, [authData, navigate]);

  const fetchAllData = async (projectId, managerId) => {
    try {
      const sprintRes = await fetch(`/sprint/project/${projectId}`);
      if (!sprintRes.ok) throw new Error("Failed to fetch actual sprint");
      const actualSprintData = await sprintRes.json();
      setActualSprint(actualSprintData);

      const [employeesData, sprintsData] = await Promise.all([
        fetchEmployees(managerId),
        fetchSprints(projectId),
      ]);
      setEmployees(employeesData);
      setSprints(sprintsData);

      await Promise.all([
        fetchWorkedHours(employeesData, sprintsData),
        fetchcompletedTasksNumber(employeesData, sprintsData),
        fetchCompletedTasks(employeesData, sprintsData),
      ]);

      setScreenLoading(false); // ✅ Show UI after all data is loaded
    } catch (error) {
      console.error("Error loading data:", error);
      setScreenLoading(false); // ✅ Avoid infinite spinner on error
    }
  };

  const fetchEmployees = async (managerId) => {
    const response = await fetch(`/employees/managerId/${managerId}`);
    if (!response.ok) throw new Error("Failed to fetch employees");
    return await response.json();
  };

  const fetchSprints = async (projectId) => {
    const response = await fetch(`/sprint/projects/${projectId}`);
    if (!response.ok) throw new Error("Failed to fetch sprints");
    const data = await response.json();
    return data.sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
  };

  const fetchWorkedHours = async (employeeList, sprintList) => {
    const workedHoursArray = [];

    for (const sprint of sprintList) {
      let sprintEntry = { sprintName: sprint.name };
      let totalHoursPerSprint = 0;

      for (const employee of employeeList) {
        const res = await fetch(
          `/assignedDev/${employee.id}/sprint/${sprint.id}/workedHours/kpi`
        );
        if (res.ok) {
          const data = await res.json();
          sprintEntry[employee.name] = data.workedHoursKpi;
          totalHoursPerSprint += data.workedHoursKpi;
        }
      }

      sprintEntry.totalWorkedHours = totalHoursPerSprint;
      workedHoursArray.push(sprintEntry);
    }

    setWorkedHoursObject(workedHoursArray);
  };

  const fetchcompletedTasksNumber = async (employeeList, sprintList) => {
    const completedTasksNumberArray = [];

    for (const sprint of sprintList) {
      let sprintEntry = { sprintName: sprint.name };

      for (const employee of employeeList) {
        const res = await fetch(
          `/assignedDev/${employee.id}/sprint/${sprint.id}/completed/number`
        );
        if (res.ok) {
          const data = await res.json();
          sprintEntry[employee.name] = data;
        }
      }

      completedTasksNumberArray.push(sprintEntry);
    }

    setcompletedTasksNumber(completedTasksNumberArray);
  };

  const fetchCompletedTasks = async (employeeList, sprintList) => {
    const completedTasksArray = [];

    for (const sprint of sprintList) {
      let sprintEntry = { sprintName: sprint.name };

      for (const employee of employeeList) {
        const res = await fetch(
          `/assignedDev/${employee.id}/sprint/${sprint.id}/completed`
        );
        if (res.ok) {
          const data = await res.json();
          sprintEntry[employee.name] = data;
        }
      }

      completedTasksArray.push(sprintEntry);
    }

    setCompletedTasks(completedTasksArray);
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
            <HeaderMngr actualSprint={actualSprint} employees={employees} 
            showFilter={false}/>

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
                <ResponsiveContainer width="70%" height={300}>
                  <BarChart data={workedHoursObject}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="sprintName" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="totalWorkedHours" fill="#8884d8" />
                  </BarChart>
                </ResponsiveContainer>

                <h3>Hours worked by dev by sprint</h3>
                <ResponsiveContainer width="70%" height={300}>
                  <BarChart data={workedHoursObject}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="sprintName" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    {employees.map((employee, index) => (
                      <Bar
                        key={employee.id}
                        dataKey={employee.name}
                        fill={colors[index % colors.length]}
                      />
                    ))}
                  </BarChart>
                </ResponsiveContainer>

                <h3>Completed tasks by sprint</h3>
                <ResponsiveContainer width="70%" height={500}>
                  <BarChart data={completedTasksNumber}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="sprintName" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    {employees.map((employee, index) => (
                      <Bar
                        key={employee.id}
                        dataKey={employee.name}
                        fill={colors[index % colors.length]}
                      />
                    ))}
                  </BarChart>
                </ResponsiveContainer>
              </div>
            )}

            {selectedTab === "historical" && (
              <div className="historical-report">
                <h3>Completed Tasks Report</h3>
                {completedTasks.length === 0 ? (
                  <p>No completed tasks found.</p>
                ) : (
                  completedTasks.map((sprint, idx) => (
                    <div key={idx} className="sprint-section">
                      <h4>{sprint.sprintName}</h4>
                      <table className="historical-table">
                        <thead>
                          <tr>
                            <th>Employee</th>
                            <th>Task Name</th>
                            <th>Status</th>
                            <th>Hours</th>
                          </tr>
                        </thead>
                        <tbody>
                          {employees.map((employee) =>
                            sprint[employee.name]?.map((task, i) => (
                              <tr key={`${employee.id}-${i}`}>
                                <td>{employee.name}</td>
                                <td>{task.name}</td>
                                <td>{task.status}</td>
                                <td>{task.estHours}</td>
                              </tr>
                            ))
                          )}
                        </tbody>
                      </table>
                    </div>
                  ))
                )}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
