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
import "../styles/Stats.css";
import HeaderMngr from "../components/HeaderMngr";

export default function Stats() {
    const [passedemployeeId, setEmployeeId] = useState(null);
    const [passedProjectId, setPassedProjectId] = useState(null);
    const [actualSprint, setActualSprint] = useState(null);
    const [employees, setEmployees] = useState([]);
    const [screenLoading, setScreenLoading] = useState(true);
    const [chartData, setChartData] = useState([]);
    const [selectedDeveloper, setSelectedDeveloper] = useState("all");
    const [workedHours, setWorkedHours] = useState(0);
    const [estimatedHours, setEstimatedHours] = useState(0);
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

    const fetchActualSprint = async (projectId, assignedDevId) => {
        try {
            const response = await fetch(`/sprint/project/${projectId}`);
            if (response.ok) {
                const data = await response.json();
                setActualSprint(data);
                console.log("Actual sprint:", data);
                fetchEmployees(assignedDevId, data.id);
            } else {
                console.error("Error fetching actual sprint:", response.statusText);
            }
        } catch (error) {
            console.error("Error fetching actual sprint:", error);
        }
    };

    const fetchEmployees = async (assignedDevId, sprintId) => {
        try {
            const response = await fetch(`/employees/managerId/${assignedDevId}`);
            const data = await response.json();
            setEmployees(data);

            let totalWorkedHours = 0;
            let totalEstHours = 0;

            const completedPercentages = await Promise.all(
                data.map(async (employee) => {
                    const percentage = await fetchDeveloperCompletedPercentage(employee.id, sprintId);
                    const workedHoursData = await fetchDeveloperWorkedHours(employee.id, sprintId);

                    totalWorkedHours += workedHoursData.workedHoursKpi;
                    totalEstHours += workedHoursData.totalEstHours;

                    return {
                        id: employee.id,
                        name: employee.name,
                        completed: percentage,
                        workedHours: workedHoursData.workedHoursKpi,
                        estimatedHours: workedHoursData.totalEstHours,
                    };
                })
            );

            setChartData(completedPercentages);
            setWorkedHours(totalWorkedHours);
            setEstimatedHours(totalEstHours);

            setScreenLoading(false);
        } catch (error) {
            console.error("Error fetching employees:", error);
            setScreenLoading(false);
        }
    };

    const fetchDeveloperCompletedPercentage = async (assignedDevId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${sprintId}/completedTasks/kpi`);
            const data = await response.json();
            console.log(assignedDevId, "Developer completed percentage:", data);
            return data;
        } catch (error) {
            console.error("Error fetching developer completed percentage:", error);
            return 0;
        }
    };

    const fetchDeveloperWorkedHours = async (assignedDevId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${sprintId}/workedHours/kpi`);
            const data = await response.json();
            console.log(assignedDevId, "Worked Hours Data:", data);
            return data;
        } catch (error) {
            console.error("Error fetching developer worked hours:", error);
            return { workedHoursKpi: 0, totalEstHours: 0 };
        }
    };

    const filteredChartData =
        selectedDeveloper === "all"
            ? chartData
            : chartData.filter((entry) => entry.id === selectedDeveloper);

    const filteredWorkedHours =
        selectedDeveloper === "all"
            ? workedHours
            : chartData.find((entry) => entry.id === selectedDeveloper)?.workedHours || 0;

    const filteredEstimatedHours =
        selectedDeveloper === "all"
            ? estimatedHours
            : chartData.find((entry) => entry.id === selectedDeveloper)?.estimatedHours || 0;

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
                            selectedDeveloper={selectedDeveloper}
                            setSelectedDeveloper={setSelectedDeveloper}
                        />
                        <div className="chartRow">
    <div className="barChartWrapper">
        <h3 className="chartTitle">Percentage of tasks completed</h3>
        <ResponsiveContainer width="100%" height={300}>
            <BarChart data={filteredChartData} barCategoryGap={20}>
                <XAxis dataKey="name" />
                <YAxis
                domain={[0, 100]} 
                tickFormatter={(value) => `${value}%`} />
                <Tooltip
                    contentStyle={{
                        backgroundColor: "#fff",
                        border: "1px solid #ccc",
                        borderRadius: "10px",
                    }}
                    labelStyle={{ color: "#333", fontWeight: "bold" }}
                />
                <Bar dataKey="completed" radius={[10, 10, 0, 0]}>
                    {filteredChartData.map((entry, index) => {
                        let fillColor = "#FF6B6B";
                        if (entry.completed >= 80) fillColor = "#4CAF50";
                        else if (entry.completed >= 50) fillColor = "#FFC107";
                        return <Cell key={`cell-${index}`} fill={fillColor} />;
                    })}
                    <LabelList
                        dataKey="completed"
                        position="top"
                        formatter={(val) => `${val}%`}
                    />
                </Bar>
            </BarChart>
        </ResponsiveContainer>
    </div>

    <div className="donutWrapper">
        <h3 className="chartTitle">Total Hours Progress</h3>
        <PieChart width={200} height={200}>
            <Pie
                data={[
                    { name: "Worked", value: filteredWorkedHours },
                    {
                        name: "Remaining",
                        value: Math.max(
                            filteredEstimatedHours - filteredWorkedHours,
                            0
                        ),
                    },
                ]}
                dataKey="value"
                innerRadius={60}
                outerRadius={80}
                startAngle={90}
                endAngle={-270}
            >
                <Cell fill="#4CAF50" />
                <Cell fill="#f0f0f0" />
            </Pie>
        </PieChart>
        <div className="centerLabel">
            {filteredEstimatedHours > 0
                ? `${Math.round(
                      (filteredWorkedHours / filteredEstimatedHours) * 100
                  )}%`
                : "0%"}
        </div>
    </div>
</div>
                    </div>
                </>
            )}
        </div>
    );
}
