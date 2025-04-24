import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import CompletedItem from "../components/CompletedItem";
import ModalTask from "../components/ModelTask.js";
import "../styles/UserCompletedTasks.css";

export default function UserCompletedTasks() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [employeeId, setEmployeeId] = useState(null);
    const [passedProjectId, setPassedProjectId] = useState(null);
    const [tasks, setTasks] = useState([]);
    const [subTasks, setSubTasks] = useState({});
    const [actualSprint, setActualSprint] = useState({});
    const [selectedTask, setSelectedTask] = useState(null);
    const [isScreenLoading, setIsScreenLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const employeeId = localStorage.getItem("employeeId");
        const projectId = localStorage.getItem("projectId");
        setEmployeeId(employeeId);
        setPassedProjectId(projectId);
        if (employeeId && projectId) {
            fetchActualSprint(projectId, employeeId);
        } else {
            console.log("No employeeId or projectId found in localStorage");
            setIsScreenLoading(false);
        }
    }, []);

    const handleRefresh = (employeeId, projectId) => {
        setIsScreenLoading(true);
        fetchActualSprint(projectId, employeeId);
    }

    const fetchActualSprint = async (projectId, employeeId) => {
        try {
            const response = await fetch(`/sprint/project/${projectId}`);
            if (response.ok) {
                const data = await response.json();
                setActualSprint(data);
                console.log("Actual sprint:", data);
                fetchTasks(employeeId, data.id);
            } else {
                console.error("Error fetching actual sprint:", response.statusText);
                setIsScreenLoading(false);
            }
        } catch (error) {
            console.error("Error fetching actual sprint:", error);
            setIsScreenLoading(false);
        }
    };

    const fetchTasks = async (employeeId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${employeeId}/sprint/${sprintId}/father`);
            if (!response.ok) {
                console.error("Error fetching tasks:", response.statusText);
                setIsScreenLoading(false);
                return;
            }
            const data = await response.json();
            console.log("Tasks:", data);

            // Filter only completed tasks
            const completedTasks = data.filter(task => task.body.status === "COMPLETED");

            const parseTasks = await Promise.all(
                completedTasks.map(async (task) => {
                    const subTasks = await fetchSubTasks(task.body.id, employeeId);
                    console.log(`Fetched subtasks for task ID ${task.body.id}:`, subTasks);

                    return {
                        id: task.body.id,
                        name: task.body.name,
                        deadline: new Date(task.body.deadline).toLocaleDateString(),
                        description: task.body.description,
                        status: task.body.status,
                        subTasks: subTasks,
                    };
                })
            );

            console.log("Parsed completed tasks:", parseTasks);
            setTasks(parseTasks);
            setIsScreenLoading(false);
        } catch (error) {
            console.error("Error fetching tasks:", error);
            setIsScreenLoading(false);
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
                        subTasks: await fetchSubTasks(item.id, employeeId),
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

    const openModal = (index) => {
        setIsModalOpen(true);
        setSelectedTask(tasks[index]);
    };

    const openSubTaskModal = (task) => {
        setIsModalOpen(true);
        setSelectedTask(task);
    };

    const closeModal = () => {
        setIsModalOpen(false);
    };

    const handleDoneClick = () => {
        closeModal();
        handleRefresh(employeeId, passedProjectId);
    };

            return (
                <div className="uct-container">
                {isScreenLoading ? (
                    <div className="loading-screen">
                    <div className="spinner"></div>
                    </div>
                ) : (
                    <>
                    <div className="header-container">
                        <h1>MY COMPLETED TASKS</h1>
                        <div className="sprint-info">
                        <h2>{actualSprint.name}</h2>
                        <div className="date-container">
                            <p>Start Date: {new Date(actualSprint.startDate).toLocaleDateString()}</p>
                            <p>End Date: {new Date(actualSprint.endDate).toLocaleDateString()}</p>
                        </div>
                        </div>
                    </div>
                    
                    <button className="return-button" onClick={() => navigate('/usertasks')}>
                        Return to Current Tasks
                    </button>
                    
                    
                    <div className="completed-tasks-list">
                        {tasks.length > 0 ? (
                            tasks.map((task, index) => (
                                <CompletedItem
                                    key={index}
                                    name={task.name}
                                    timestamp={task.deadline}
                                    statusColor={getStatusColor(task.status)}
                                    taskStatus={task.status}
                                    subTasks={task.subTasks}
                                    onClick={() => openModal(index)}
                                    subTaskOnClick={() => openSubTaskModal(task)}
                                />
                            ))
                        ) : (
                            <p className="no-tasks-message">No completed tasks yet</p>
                        )}
                    </div>

                    {isModalOpen && selectedTask && (
                        <ModalTask
                            setOpen={closeModal}
                            handleDoneClick={handleDoneClick}
                            task={selectedTask}
                        />
                    )}
                </>
            )}
        </div>
    );
}   