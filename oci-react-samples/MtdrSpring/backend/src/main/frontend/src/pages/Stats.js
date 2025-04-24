import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, LabelList, Cell } from "recharts";
import "../styles/Stats.css";

export default function Stats() {
    const [passedemployeeId, setEmployeeId] = useState(null);
    const [passedProjectId, setPassedProjectId] = useState(null);
    const [actualSprint, setActualSprint] = useState(null);
    const [employees, setEmployees] = useState([]);
    const [screenLoading, setScreenLoading] = useState(true);
    const [chartData, setChartData] = useState([]); // State to hold chart data
    const [selectedDeveloper, setSelectedDeveloper] = useState("all");

    useEffect(() => {
        const employeeId = localStorage.getItem("employeeId");
        const projectId = localStorage.getItem("projectId");
        setEmployeeId(employeeId);
        setPassedProjectId(projectId);
        if (employeeId && projectId) {
            fetchActualSprint(projectId, employeeId); // Pass employeeId to fetchActualSprint
        } else {
            console.log("No employeeId or projectId found in localStorage");
            setScreenLoading(false);
        }
    }, []);

    const fetchActualSprint = async (projectId, managerId) => {
        try {
            const response = await fetch(`/sprint/project/${projectId}`);
            if (response.ok) {
                const data = await response.json();
                setActualSprint(data);
                console.log("Actual sprint:", data);
                fetchEmployees(managerId, data.id);
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

            // Fetch completed percentages for each employee and update chart data
            const completedPercentages = await Promise.all(
                data.map(async (employee) => {
                    const percentage = await fetchDeveloperCompletedPercentage(employee.id, sprintId);
                    return { name: employee.name, completed: percentage };
                })
            );

            setChartData(completedPercentages); // Update chart data
            setScreenLoading(false);
        } catch (error) {
            console.error("Error fetching employees:", error);
            setScreenLoading(false);
        }
    };

    const fetchDeveloperCompletedPercentage = async (developerId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${developerId}/sprint/${sprintId}/completedTasks/kpi`);
            const data = await response.json();
            console.log(developerId, "Developer completed percentage:", data);
            return data;
        } catch (error) {
            console.error("Error fetching developer completed percentage:", error);
            return 0; // Return 0 in case of error
        }
    };

    const fetchDeveloperWorkedHours = async (developerId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${developerId}/sprint/${sprintId}/workedHours/kpi`)
        }
        catch (error) {
            console.error("Error fetching developer worked hours:", error);
            return 0; // Return 0 in case of error
        }

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
                        <h1>KPI Reports</h1>
                        <h2 className="sprintname">{actualSprint.name}</h2>
                        <div className="dateContainer">
                            <h3>Start Date: {new Date(actualSprint.startDate).toLocaleDateString()}</h3>
                            <h3>End Date: {new Date(actualSprint.endDate).toLocaleDateString()}</h3>
                            <h3>Days Left: {Math.floor((new Date(actualSprint.endDate) - new Date()) / (1000 * 60 * 60 * 24))}</h3>
                        </div>
                        <div className="action-bar">
                            <div className="filter-container">
                                <label htmlFor="developer-filter">Filter by Developer:</label>
                                <select
                                id="developer-filter"
                                value={selectedDeveloper}
                                onChange={(e) => {
                                    const value = e.target.value === "all" ? "all" : parseInt(e.target.value);
                                    setSelectedDeveloper(value);
                                }}
                                
                                className="filter-select"
                            >
                                <option value="all">All Developers</option>
                                {employees.map((employee) => (
                                    <option key={employee.id} value={employee.id}>
                                        {employee.name}
                                    </option>
                                ))}
                            </select>
                            </div>
                        </div>
                        <h3 className="chartTitle">Percentage of tasks completed</h3>
                        <div className="barChartWrapper">
                            <ResponsiveContainer width="60%" height={300}>
                                <BarChart data={chartData} barCategoryGap={20}>
                                    <XAxis dataKey="name" />
                                    <YAxis tickFormatter={(value) => `${value}%`} />
                                    <Tooltip 
                                        contentStyle={{ backgroundColor: "#fff", border: "1px solid #ccc", borderRadius: "10px" }} 
                                        labelStyle={{ color: "#333", fontWeight: "bold" }} 
                                    />
                                    <Bar dataKey="completed" radius={[10, 10, 0, 0]}>
                                        {chartData.map((entry, index) => {
                                            let fillColor = "#FF6B6B"; // red by default
                                            if (entry.completed >= 80) fillColor = "#4CAF50"; // green
                                            else if (entry.completed >= 50) fillColor = "#FFC107"; // yellow
                                            return <Cell key={`cell-${index}`} fill={fillColor} />;
                                        })}
                                        <LabelList dataKey="completed" position="top" formatter={(val) => `${val}%`} />
                                    </Bar>
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}