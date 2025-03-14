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
    const [employees, setEmployees] = useState([]);
    const [tasks, setTasks] = useState({});
    const [kpis, setKpis] = useState({});
    const [selectedTask, setSelectedTask] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const employeeId = localStorage.getItem("employeeId");
        setEmployeeId(employeeId);
        if (employeeId) {
            fetchEmployees(employeeId);
        }
    }, []);

    const fetchEmployees = async (managerId) => {
        try {
            const response = await fetch(`/employees/managerId/${managerId}`);
            const data = await response.json();
            setEmployees(data);
            data.forEach(employee => {
                fetchTasks(employee.id);
                fetchKpi(employee.id);
            });
        } catch (error) {
            console.error("Error fetching employees:", error);
        }
    };

    const fetchTasks = async (assignedDevId) => {
        try {
            const response = await fetch(`/devassignedtasks/${assignedDevId}`);
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
            const response = await fetch(`/devassignedtasks/kpi/${assignedDevId}`);
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
            <div className="taskContainer">
                <h1>TO DO LIST</h1>
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
                        <TaskCreation onClose={closeTaskCreationModal} />
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
        </div>
    );
}