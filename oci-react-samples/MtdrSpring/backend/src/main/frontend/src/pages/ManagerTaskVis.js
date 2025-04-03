import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import React from 'react';
import ToDoItem from "../components/ToDoItem";
import ManagerModalTask from "../components/ManagerModalTask.js";
import TaskCreation from "../components/TaskCreation";

export default function ManagerTaskVis() {
    const [isTaskCreationModalOpen, setIsTaskCreationModalOpen] = useState(false); 
    const [isTaskDetailsModalOpen, setIsTaskDetailsModalOpen] = useState(false);
    const [selectedTaskIndex, setSelectedTaskIndex] = useState(null); 
    const [modalAction, setModalAction] = useState(null); 
    const [employeeId, setEmployeeId] = useState(null);
    const [projectId, setProjectId] = useState(null);
    const [employees, setEmployees] = useState([]);
    const [tasks, setTasks] = useState({});
    const [kpis, setKpis] = useState({});
    const [actualSprint, setActualSprint] = useState({});
    const [selectedTask, setSelectedTask] = useState(null);
    const [isScreenLoading, setScreenLoading] = useState(true); // State to track loading status
    const navigate = useNavigate();

    useEffect(() => {
        const employeeId = localStorage.getItem("employeeId");
        const projectId = localStorage.getItem("projectId");
        setEmployeeId(employeeId);
        setProjectId(projectId);
        if (employeeId && projectId) {
            fetchEmployees(employeeId, projectId);
            fetchActualSprint(projectId);
        }
        else{
            console.log("No employeeId or projectId found in localStorage");
            setScreenLoading(false);
        }
    }, []);

    const fetchActualSprint = async (projectId) => {
        try {
            const response = await fetch(`/sprint/project/${projectId}`);
            if (response.ok) {
                const data = await response.json();
                setActualSprint(data);
                console.log("Actual sprint:", data);
            } else {
                console.error("Error fetching actual sprint:", response.statusText);
            }
        } catch (error) {
            console.error("Error fetching actual sprint:", error);
        }
    };

    const fetchEmployees = async (managerId, projectId) => {
        try {
            const response = await fetch(`/employees/managerId/${managerId}`);
            const data = await response.json();
            setEmployees(data);
            const taskPromises = data.map(employee => fetchTasks(employee.id, projectId));
            const kpiPromises = data.map(employee => fetchKpi(employee.id));
            await Promise.all([...taskPromises, ...kpiPromises]); // Wait for all tasks and KPIs to load
            setScreenLoading(false); // Stop loading after all data is fetched
        } catch (error) {
            console.error("Error fetching employees:", error);
            setScreenLoading(false); // Stop loading even if there's an error
        }
    };


    const fetchTasks = async (assignedDevId, projectId) => {
        try {
            const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${projectId}/father`);
            const data = await response.json();
            const parsedTasks = data.map(item => ({
                name: item.body.name,
                deadline: new Date(item.body.deadline).toLocaleDateString(),
                description: item.body.description,
                status: item.body.status,
            }));
            setTasks(prevTasks => ({ ...prevTasks, [assignedDevId]: parsedTasks }));
        } catch (error) {
            console.error("Error fetching tasks:", error);
        }
    };

    const fetchKpi = async (assignedDevId) => {
        try {
            const response = await fetch(`/assignedDev/kpi/${assignedDevId}`);
            if (response.ok) {
                const text = await response.text();
                const data = text ? JSON.parse(text) : null;
                setKpis(prevKpis => ({ ...prevKpis, [assignedDevId]: data }));
            } else {
                console.error("Error fetching KPI:", response.statusText);
            }
        } catch (error) {
            console.error("Error fetching KPIs:", error);
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

    const calculateKpi = (employeeId) => {
        if(kpis[employeeId] === null) {
            return "more information needed";
        } 
        else{
            return kpis[employeeId];
        }
    };

    const closeTaskDetailsModal = () => {
        setIsTaskDetailsModalOpen(false); 
    };

    const openTaskCreationModal = () => {
        setIsTaskCreationModalOpen(true);
    };

    const closeTaskCreationModal = () => {
        setIsTaskCreationModalOpen(false);
    };

    const handleSaveClick = () => {
        setModalAction('save'); 
        closeTaskDetailsModal(); 
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
                />
            ))}
                                <h3>Average days of completion before deadline: {calculateKpi(employee.id)}</h3>
                            </div>
                        )
                    ))}

                    {isTaskCreationModalOpen && (
                        <div className="modal-overlay">
                            <div className="modal-content">
                                <TaskCreation onClose={closeTaskCreationModal}
                                onTaskCreated={fetchTasks}
                                managerId={employeeId}
                                projectId={projectId} />
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