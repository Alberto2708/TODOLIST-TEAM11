import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import React from 'react';
import ToDoItem from "../components/ToDoItem";
import ManagerModalTask from "../components/ManagerModalTask.js";
import TaskCreation from "../components/TaskCreation";
import SubTaskCreation from "../components/SubTaskCreation.js";

export default function ManagerTaskVis() {
    const [isTaskCreationModalOpen, setIsTaskCreationModalOpen] = useState(false); 
    const [isTaskDetailsModalOpen, setIsTaskDetailsModalOpen] = useState(false);
    const [isSubTaskCreationModalOpen, setIsSubTaskCreationModalOpen] = useState(false);
    const [selectedTaskIndex, setSelectedTaskIndex] = useState(null); 
    const [modalAction, setModalAction] = useState(null); 
    const [employeeId, setEmployeeId] = useState(null);
    const [passedProjectId, setPassedProjectId] = useState(null);
    const [employees, setEmployees] = useState([]);
    const [tasks, setTasks] = useState({});
    const [subTasks, setSubTasks] = useState({});
    const [kpis, setKpis] = useState({});
    const [percentageKpis, setPercentageKpis] = useState({});
    const [actualSprint, setActualSprint] = useState({});
    const [selectedTask, setSelectedTask] = useState(null);
    const [isScreenLoading, setScreenLoading] = useState(true); // State to track loading status
    const navigate = useNavigate();

    useEffect(() => {
        const employeeId = localStorage.getItem("employeeId");
        const projectId = localStorage.getItem("projectId");
        setEmployeeId(employeeId);
        setPassedProjectId(projectId);
        if (employeeId && projectId) {
            fetchActualSprint(projectId, employeeId); // Pass employeeId to fetchActualSprint
        }
        else{
            console.log("No employeeId or projectId found in localStorage");
            setScreenLoading(false);
        }
    }, []);

    const handleRefresh = (managerId, projectId) => {
        setScreenLoading(true); // Start loading when refreshing
        fetchActualSprint(projectId, managerId);
    }

    const fetchCompletedPercentage = async (employeeId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${employeeId}/sprint/${sprintId}/kpi`);
            if (response.ok) {
                const text = await response.text();
                const data = text ? JSON.parse(text) : null;
                if (data !== null && !isNaN(data)) {
                    setPercentageKpis((prevKpis) => ({ ...prevKpis, [employeeId]: data }));
                } else {
                    console.warn(`Invalid KPI data for employeeId ${employeeId}:`, data);
                    setPercentageKpis((prevKpis) => ({ ...prevKpis, [employeeId]: null }));
                }
            } else {
                console.error("Error fetching KPI:", response.statusText);
                setPercentageKpis((prevKpis) => ({ ...prevKpis, [employeeId]: null }));
            }
        } catch (error) {
            console.error("Error fetching KPIs:", error);
            setPercentageKpis((prevKpis) => ({ ...prevKpis, [employeeId]: null }));
        }
    };

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
            const taskPromises = data.map(employee => fetchTasks(employee.id, sprintId));
            const kpiPromises = data.map(employee => fetchKpi(employee.id));
            const percentageKpiPromises = data.map(employee => fetchCompletedPercentage(employee.id, sprintId)); // Fetch completed percentage for each employee
            await Promise.all([...taskPromises, ...kpiPromises]); // Wait for all tasks and KPIs to load
            setScreenLoading(false); // Stop loading after all data is fetched
        } catch (error) {
            console.error("Error fetching employees:", error);
            setScreenLoading(false); // Stop loading even if there's an error
        }
    };

    const fetchSubTasks = async (taskId, employeeId) => {
        try {
          console.log("Fetching subtasks for task ID:", taskId, "and employee ID:", employeeId);
          const response = await fetch(`subToDoItems/toDoItem/${taskId}/employee/${employeeId}`);
          if (!response.ok) {
            console.error("Error fetching subtasks:", response.statusText);
            return [];
          }
      
          const text = await response.text();
          if (!text) {
            // Handle empty response
            console.warn(`Empty response for task ID ${taskId}`);
            return [];
          }
      
          try {
            const data = JSON.parse(text); // Parse the JSON response
            console.log(`Fetched subtasks data for toDoItemId ${taskId}:`, data);
      
            // Adjust parsing logic based on the actual structure of the response
            const parsedSubTasks = await Promise.all(
              data.map(async (item) => ({
                id: item.id, // Adjusted to match the structure of your response
                name: item.name,
                deadline: new Date(item.deadline).toLocaleDateString(),
                description: item.description,
                status: item.status,
                subTasks: await fetchSubTasks(item.id, employeeId), // Fetch subtasks recursively
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


    const fetchTasks = async (assignedDevId, sprintId) => {
        try {
            console.log(`Fetching tasks for assignedDevId: ${assignedDevId}, sprintId: ${sprintId}`);
            const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${sprintId}/father`);
            if (!response.ok) {
                console.error(`Error fetching tasks: ${response.status} - ${response.statusText}`);
                return;
            }
            const data = await response.json();
            console.log("Fetched tasks data:", data);
    
            const parsedTasks = await Promise.all(
                data.map(async (item) => {
                    console.log(`Fetching subtasks for task ID: ${item.body.id}`);
                    const subTasks = await fetchSubTasks(item.body.id, assignedDevId); // Await the subtasks
                    console.log(`Fetched subtasks for task ID ${item.body.id}:`, subTasks);
    
                    return {
                        id: item.body.id,
                        name: item.body.name,
                        deadline: new Date(item.body.deadline).toLocaleDateString(),
                        description: item.body.description,
                        status: item.body.status,
                        subTasks: subTasks, // Assign resolved subtasks
                    };
                })
            );
    
            console.log(`Parsed tasks for assignedDevId ${assignedDevId}:`, parsedTasks);
            setTasks((prevTasks) => ({ ...prevTasks, [assignedDevId]: parsedTasks }));
        } catch (error) {
            console.error("Error in fetchTasks:", error);
        }
    };

    const fetchKpi = async (assignedDevId) => {
        try {
            const response = await fetch(`/assignedDev/kpi/${assignedDevId}`);
            if (response.ok) {
                const text = await response.text();
                const data = text ? JSON.parse(text) : null;
                if (data !== null && !isNaN(data)) {
                    setKpis((prevKpis) => ({ ...prevKpis, [assignedDevId]: data }));
                } else {
                    console.warn(`Invalid KPI data for assignedDevId ${assignedDevId}:`, data);
                    setKpis((prevKpis) => ({ ...prevKpis, [assignedDevId]: null }));
                }
            } else {
                console.error("Error fetching KPI:", response.statusText);
                setKpis((prevKpis) => ({ ...prevKpis, [assignedDevId]: null }));
            }
        } catch (error) {
            console.error("Error fetching KPIs:", error);
            setKpis((prevKpis) => ({ ...prevKpis, [assignedDevId]: null }));
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
        setSelectedTaskIndex(index); 
        setSelectedTask(tasks[employeeId][index]);
        setModalAction(null); 
    };
    
    const openSubTaskDetailsModal = (task) => {
        setIsTaskDetailsModalOpen(true);
        setSelectedTask(task); // Set the clicked task or subtask
        setModalAction(null);
    };

    const calculateKpi = (employeeId) => {
        if(kpis[employeeId] === null) {
            return "more information needed";
        } 
        else{
            return kpis[employeeId];
        }
    };
    
    const calculatePercentageKpi = (employeeId) => {
        if(percentageKpis[employeeId] === null) {
            return "more information needed";
        }
        else{
            return percentageKpis[employeeId];
        }
    };

    const closeTaskDetailsModal = () => {
        setIsTaskDetailsModalOpen(false); 
    };


    const openTaskCreationModal = () => {
        setIsTaskCreationModalOpen(true);
    };

    const openSubTaskCreationModal = () => {
        setIsSubTaskCreationModalOpen(true);
    };

    const closeTaskCreationModal = () => {
        setIsTaskCreationModalOpen(false);
    };

    const closeSubTaskCreationModal = () => {
        setIsSubTaskCreationModalOpen(false);
    };

    const handleSubTaskSaveClick = () => {
        setModalAction('save');
        closeSubTaskCreationModal();
    };

    const handleSaveClick = () => {
        setModalAction('save'); 
        closeTaskDetailsModal(); 
    };

    const handleSubTaskCancelClick = () => {
        setModalAction('cancel'); 
        closeSubTaskCreationModal();
    };

    const handleCancelClick = () => {
        setModalAction('cancel'); 
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
                    <div className="taskContainer">
                        <h1>TO DO LIST</h1>
                        <h2>{actualSprint.name}</h2>
                        <div className="dateContainer">
                            <h3>Start Date: {new Date(actualSprint.startDate).toLocaleDateString()}</h3>
                            <h3>End Date: {new Date(actualSprint.endDate).toLocaleDateString()}</h3>
                            <h3>Days Left: {Math.floor((new Date(actualSprint.endDate) - new Date()) / (1000 * 60 * 60 * 24))}</h3>
                        </div>
                        <button className="addButton" onClick={openTaskCreationModal}>
                            Create Task
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
                            </svg>
                        </button>
                        <button className="addButton" onClick={openSubTaskCreationModal}>
                            Create SubTask
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
                            </svg>
                        </button>
                    </div>

                    {employees.map((employee) => (
    tasks[employee.id] && tasks[employee.id].length > 0 && (
        <div key={employee.id} className="employee-task-list">
            <h2>{employee.name}'s to do list:</h2>
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
                    subTaskOnClick={() => openSubTaskDetailsModal(task)} // Pass the task to the subtask click handler
                />
            ))}
                                <h3>Average hours of completion before deadline: {calculateKpi(employee.id)}</h3>
                                <h3>Percentage of tasks completed: {calculatePercentageKpi(employee.id)}%</h3>
                            </div>
                        )
                    ))}

                    {isTaskCreationModalOpen && (
                        <div className="modal-overlay">
                            <div className="modal-content">
                                <TaskCreation onClose={closeTaskCreationModal}
                                onTaskCreated={() => handleRefresh(passedProjectId, employeeId)}
                                managerId={employeeId}
                                projectId={passedProjectId}
                                sprintId={actualSprint.id} />
                            </div>
                        </div>
                    )}

                    {isSubTaskCreationModalOpen && (
                        <div className="modal-overlay">
                            <div className="modal-content">
                                <SubTaskCreation onClose={closeSubTaskCreationModal}
                                onTaskCreated={() => handleRefresh(passedProjectId, employeeId)}
                                managerId={employeeId}
                                projectId={passedProjectId}
                                sprintId={actualSprint.id} />
                            </div>
                        </div>
                    )}

                    {isTaskDetailsModalOpen && selectedTask && (
                        <ManagerModalTask
                            setOpen={closeTaskDetailsModal}
                            handleDoneClick={handleSaveClick} 
                            handleCancelClick={handleCancelClick} 
                            task={selectedTask} // Pass the selected task as a prop
                        />
                    )}

                </>
            )}
        </div>
    );
}