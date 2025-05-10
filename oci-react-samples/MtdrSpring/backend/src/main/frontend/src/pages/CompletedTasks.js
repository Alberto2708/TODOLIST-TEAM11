
import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import React from 'react';
import ToDoItem from "../components/ToDoItem.js";
import ManagerModalTask from "../components/ManagerModalTask.js";
import HeaderMngr from "../components/HeaderMngr.js";
import { useAuth } from "../context/AuthContext.js"; // Import the AuthContext

export default function CompletedTasks() {
    const [isTaskDetailsModalOpen, setIsTaskDetailsModalOpen] = useState(false);
    const [/*employeeId*/, setEmployeeId] = useState(null);
    const [/*passedProjectId*/, setPassedProjectId] = useState(null);
    const [employees, setEmployees] = useState([]);
    const [tasks, setTasks] = useState({});
    const [/*subTasks*/, setSubTasks] = useState({});
    const [actualSprint, setActualSprint] = useState({});
    const [selectedTask, setSelectedTask] = useState(null);
    const [isScreenLoading, setScreenLoading] = useState(true);
    const navigate = useNavigate();
    const [selectedDeveloper, setSelectedDeveloper] = useState("all");
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
            } else {
                console.error("Error fetching actual sprint:", response.statusText);
            }
        } catch (error) {
            console.error("Error fetching actual sprint:", error);
        }
    };

    const fetchSubTasks = async (taskId, assignedDevId) => {
        try {
            console.log("Fetching subtasks for task ID:", taskId, "and employee ID:", assignedDevId);
            const response = await fetch(`/subToDoItems/toDoItem/${taskId}/employee/${assignedDevId}/completed`);
            if (!response.ok) {
                console.error("Error fetching subtasks:", response.statusText);
                return [];
            }
        
            const text = await response.text();
            if (!text) {
                console.warn(`Empty response for task ID ${taskId}`);
                return [];
            }
        
            try {
                const data = JSON.parse(text);
                console.log(`Fetched subtasks data for toDoItemId ${taskId}:`, data);
        
                const parsedSubTasks = await Promise.all(
                    data.map(async (item) => ({
                        id: item.id,
                        name: item.name,
                        deadline: new Date(item.deadline).toLocaleDateString(),
                        description: item.description,
                        status: item.status,
                        subTasks: await fetchSubTasks(item.id, assignedDevId),
                    }))
                );
        
                setSubTasks((prevSubTasks) => ({ ...prevSubTasks, [taskId]: parsedSubTasks }));
                return parsedSubTasks;
            } catch (error) {
                console.error("Invalid JSON response:", text);
                return [];
            }
        } catch (error) {
            console.error("Error fetching subtasks:", error);
            return [];
        }
    };

    const fetchEmployees = async (managerId, sprintId) => {
        try {
            const response = await fetch(`/employees/managerId/${managerId}`);
            const data = await response.json();
            setEmployees(data);
            const taskPromises = data.map(employee => fetchTasks(employee.id, sprintId));
            await Promise.all([...taskPromises]);
            setScreenLoading(false);
        } catch (error) {
            console.error("Error fetching employees:", error);
            setScreenLoading(false);
        }
    };

    const fetchTasks = async (assignedDevId, sprintId) => {
        try {
            console.log(`Fetching tasks for assignedDevId: ${assignedDevId}, sprintId: ${sprintId}`);
            const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${sprintId}/father/completed`);
            if (!response.ok) {
                console.error(`Error fetching tasks: ${response.status} - ${response.statusText}`);
                return;
            }
            const data = await response.json();
            console.log("Fetched tasks data:", data);
    
            const parsedTasks = await Promise.all(
                data.map(async (item) => {
                    console.log(`Fetching subtasks for task ID: ${item.id}`);
                    const subTasks = await fetchSubTasks(item.id, assignedDevId);
                    console.log(`Fetched subtasks for task ID ${item.id}:`, subTasks);
    
                    return {
                        id: item.id,
                        name: item.name,
                        deadline: new Date(item.deadline).toLocaleDateString(),
                        description: item.description,
                        status: item.status,
                        subTasks: subTasks,
                    };
                })
            );
    
            console.log(`Parsed tasks for assignedDevId ${assignedDevId}:`, parsedTasks);
            setTasks((prevTasks) => ({ ...prevTasks, [assignedDevId]: parsedTasks }));
        } catch (error) {
            console.error("Error in fetchTasks:", error);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case "PENDING":
                return "orange";
            case "COMPLETED":
                return "green";
            case "OVERDUE":
                return "red";
            default:
                return "black";
        }
    };

    const openTaskDetailsModal = (employeeId, index) => {
        setIsTaskDetailsModalOpen(true); 
        setSelectedTask(tasks[employeeId][index]);
    };
    
    const openSubTaskDetailsModal = (task) => {
        setIsTaskDetailsModalOpen(true);
        setSelectedTask(task);
    };

    const closeTaskDetailsModal = () => {
        setIsTaskDetailsModalOpen(false); 
    };

    const handleSaveClick = () => {
        closeTaskDetailsModal(); 
    };

    const handleCancelClick = () => {
        closeTaskDetailsModal(); 
    };

    
    const handleDeleteClick = (deletedTaskId) => {
        setTasks(prevTasks => {
            const newTasks = { ...prevTasks };
            for (const [devId, devTasks] of Object.entries(newTasks)) {
                newTasks[devId] = devTasks.filter(task => task.id !== deletedTaskId);
            }
            return newTasks;
        });

        closeTaskDetailsModal();
    };

    return (
        <div className="mtvContainer">
            {isScreenLoading ? (
                <div className="loading-screen">
                    <div className="spinner"></div>
                </div>
            ) : (
                <>
                    <HeaderMngr
                        actualSprint={actualSprint}
                        employees={employees}
                        selectedDeveloper={selectedDeveloper}
                        setSelectedDeveloper={setSelectedDeveloper}
                    />
                    
                    {employees
                        .filter(employee => selectedDeveloper === "all" || employee.id === selectedDeveloper)
                        .map((employee) => (
                            tasks[employee.id] && tasks[employee.id].length > 0 && (
                                <div key={employee.id} className="employee-task-list">
                                    <h2>{employee.name}'s completed tasks:</h2>
                                    {tasks[employee.id].map((task, index) => (
                                        <ToDoItem
                                            key={index}
                                            name={task.name}
                                            timestamp={task.deadline}
                                            statusColor={getStatusColor(task.status)}
                                            taskStatus={task.status}
                                            subTasks={task.subTasks}
                                            onClick={() => openTaskDetailsModal(employee.id, index)}
                                            userName={employee.name}
                                            subTaskOnClick={() => openSubTaskDetailsModal(task)}
                                        />
                                    ))}
                                </div>
                            )
                        ))}

                    {isTaskDetailsModalOpen && selectedTask && (
                        <ManagerModalTask
                            setOpen={closeTaskDetailsModal}
                            handleDoneClick={handleSaveClick} 
                            handleCancelClick={handleCancelClick} 
                            task={selectedTask} 
                            handleDeleteClick={(id) => handleDeleteClick(id)}
                            mode = "completed"
                        />
                    )}
                </>
            )}
        </div>
    );
}