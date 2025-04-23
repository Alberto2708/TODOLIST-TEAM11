import React, { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import ToDoItem from "../components/ToDoItem";
import ManagerModalTask from "../components/ManagerModalTask";
import TaskCreation from "../components/TaskCreation";
import SubTaskCreation from "../components/SubTaskCreation";

export default function ManagerTaskVis() {
    // States
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
    const [totalCompletedTasks, setTotalCompletedTasks] = useState(null);
    const [percentageKpis, setPercentageKpis] = useState({});
    const [sumOverdueTasks, setSumOverdueTasks] = useState({});
    const [actualSprint, setActualSprint] = useState({});
    const [selectedTask, setSelectedTask] = useState(null);
    const [isScreenLoading, setScreenLoading] = useState(true);
    const [selectedDeveloper, setSelectedDeveloper] = useState("all");
    const [filteredTasks, setFilteredTasks] = useState({});
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
            setScreenLoading(false);
        }
    }, []);

    useEffect(() => {
        console.log("Selected Developer:", selectedDeveloper);
        console.log("Tasks before filtering:", tasks);
    
        if (selectedDeveloper === "all") {
            setFilteredTasks(tasks);
            console.log("Filtered Tasks (All Developers):", tasks);
        } else {
            const newFilteredTasks = {};
            if (tasks[selectedDeveloper]) {
                newFilteredTasks[selectedDeveloper] = tasks[selectedDeveloper];
            }
            setFilteredTasks(newFilteredTasks);
            console.log(`Filtered Tasks (Developer ${selectedDeveloper}):`, newFilteredTasks);
        }
    }, [selectedDeveloper, tasks]);

    const handleRefresh = (managerId, projectId) => {
        setScreenLoading(true);
        fetchActualSprint(projectId, managerId);
    };

    const fetchActualSprint = async (projectId, managerId) => {
        try {
            const response = await fetch(`/sprint/project/${projectId}`);
            if (response.ok) {
                const data = await response.json();
                setActualSprint(data);
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
            const percentageKpiPromises = data.map(employee => fetchCompletedPercentage(employee.id, sprintId));
            const sumOverdueTasksPromises = data.map(employee => fetchSumOverdueTasks(employee.id));
            const totalCompletedTasksPromise = fetchTotalCompletedTasksbySprint(sprintId);
            await Promise.all([...taskPromises, ...kpiPromises, ...percentageKpiPromises, ...sumOverdueTasksPromises]);
            setScreenLoading(false);
        } catch (error) {
            console.error("Error fetching employees:", error);
            setScreenLoading(false);
        }
    };

    const fetchTasks = async (assignedDevId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${sprintId}/father`);
            if (!response.ok) {
                console.error(`Error fetching tasks: ${response.status} - ${response.statusText}`);
                return;
            }
            const data = await response.json();
            
            const parsedTasks = await Promise.all(
                data.map(async (item) => {
                    const subTasks = await fetchSubTasks(item.body.id, assignedDevId);
                    return {
                        id: item.body.id,
                        name: item.body.name,
                        deadline: new Date(item.body.deadline).toLocaleDateString(),
                        description: item.body.description,
                        status: item.body.status,
                        subTasks: subTasks,
                    };
                })
            );
            
            setTasks(prevTasks => ({ ...prevTasks, [assignedDevId]: parsedTasks }));
        } catch (error) {
            console.error("Error in fetchTasks:", error);
        }
    };

    const fetchSubTasks = async (taskId, employeeId) => {
        try {
            const response = await fetch(`subToDoItems/toDoItem/${taskId}/employee/${employeeId}`);
            if (!response.ok) {
                console.error("Error fetching subtasks:", response.statusText);
                return [];
            }
            
            const text = await response.text();
            if (!text) {
                return [];
            }
            
            try {
                const data = JSON.parse(text);
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
                
                setSubTasks(prevSubTasks => ({ ...prevSubTasks, [taskId]: parsedSubTasks }));
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

    const fetchKpi = async (assignedDevId) => {
        try {
            const response = await fetch(`/assignedDev/kpi/${assignedDevId}`);
            if (response.ok) {
                const text = await response.text();
                const data = text ? JSON.parse(text) : null;
                if (data !== null && !isNaN(data)) {
                    setKpis(prevKpis => ({ ...prevKpis, [assignedDevId]: data }));
                } else {
                    setKpis(prevKpis => ({ ...prevKpis, [assignedDevId]: null }));
                }
            } else {
                setKpis(prevKpis => ({ ...prevKpis, [assignedDevId]: null }));
            }
        } catch (error) {
            setKpis(prevKpis => ({ ...prevKpis, [assignedDevId]: null }));
        }
    };

    const fetchCompletedPercentage = async (employeeId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${employeeId}/sprint/${sprintId}/kpi`);
            if (response.ok) {
                const text = await response.text();
                const data = text ? JSON.parse(text) : null;
                if (data !== null && !isNaN(data)) {
                    setPercentageKpis(prevKpis => ({ ...prevKpis, [employeeId]: data }));
                } else {
                    setPercentageKpis(prevKpis => ({ ...prevKpis, [employeeId]: null }));
                }
            } else {
                setPercentageKpis(prevKpis => ({ ...prevKpis, [employeeId]: null }));
            }
        } catch (error) {
            setPercentageKpis(prevKpis => ({ ...prevKpis, [employeeId]: null }));
        }
    };

    const fetchSumOverdueTasks = async (employeeId) => {
        try {
            const response = await fetch(`/assignedDev/kpi/${employeeId}/overdue`);
            if (response.ok) {
                const text = await response.text();
                const data = text ? JSON.parse(text) : null;
                if (data !== null && !isNaN(data)) {
                    setSumOverdueTasks(prevKpis => ({ ...prevKpis, [employeeId]: data }));
                } else {
                    setSumOverdueTasks(prevKpis => ({ ...prevKpis, [employeeId]: null }));
                }
            } else {
                setSumOverdueTasks(prevKpis => ({ ...prevKpis, [employeeId]: null }));
            }
        } catch (error) {
            setSumOverdueTasks(prevKpis => ({ ...prevKpis, [employeeId]: null }));
        }
    };

    const fetchTotalCompletedTasksbySprint = async (sprintId) => {
        try {
            const response = await fetch(`sprint/${sprintId}/kpi`);
            if (response.ok) { 
                const text = await response.text();
                const data = text ? JSON.parse(text) : null;
                if (data !== null && !isNaN(data)) {
                    setTotalCompletedTasks(data);
                }
            }
        } catch (error) {
            console.error("Error fetching total completed tasks by sprint:", error);
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
        setSelectedTask(task);
        setModalAction(null);
    };

    const calculateKpi = (employeeId) => {
        if (kpis[employeeId] === null) {
            return "more information needed";
        }
        return kpis[employeeId];
    };

    const calculatePercentageKpi = (employeeId) => {
        if (percentageKpis[employeeId] === null) {
            return "more information needed";
        }
        return percentageKpis[employeeId];
    };

    const calculateTotalCompletedTasks = () => {
        if (totalCompletedTasks === null) {
            return "No data available";
        } else if (isNaN(totalCompletedTasks)) {
            return "Invalid data";
        }
        return totalCompletedTasks;
    };

    const calculateSumOverdueTasks = (employeeId) => {
        const overdueTasks = sumOverdueTasks[employeeId];
        if (overdueTasks === null || overdueTasks === undefined || isNaN(overdueTasks)) {
            return "No overdue tasks data available";
        }
        return overdueTasks;
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
                        <h2 className="sprintname">{actualSprint.name}</h2>
                        <div className="dateContainer">
                            <h3>Start Date: {new Date(actualSprint.startDate).toLocaleDateString()}</h3>
                            <h3>End Date: {new Date(actualSprint.endDate).toLocaleDateString()}</h3>
                            <h3>Days Left: {Math.floor((new Date(actualSprint.endDate) - new Date()) / (1000 * 60 * 60 * 24))}</h3>
                            <h3>Percentage of completed tasks: {calculateTotalCompletedTasks()}</h3>
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
                            <div className="button-group">
                                <button className="addButton" onClick={openTaskCreationModal}>
                                    Create Task
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
                                    </svg>
                                </button>
                                <button className="completedTasksButton" onClick={() => navigate('/CompletedTasks')}>
                                    Completed Tasks
                                </button>
                                <button className="viewStatsButton" onClick={() => navigate('/Stats')}>
                                    View Statistics
                                </button>
                            </div>
                        </div>
                    </div>

                    {employees.length === 0 ? (
                        <div className="no-employees-message">
                            No developers assigned to this project
                        </div>
                    ) : (
                        employees
                            .filter(employee => selectedDeveloper === "all" || employee.id === selectedDeveloper)
                            .map((employee) => {
                                const employeeTasks = filteredTasks[employee.id] || [];
                                
                                return (
                                    <div key={employee.id} className="employee-task-list">
                                        <h2>{employee.name}'s to do list:</h2>
                                        {employeeTasks.length > 0 ? (
                                            employeeTasks.map((task, index) => (
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
                                            ))
                                        ) : (
                                            <div className="no-tasks-message">
                                                {selectedDeveloper === "all" 
                                                    ? "No tasks assigned to this developer"
                                                    : "This developer has no tasks"}
                                            </div>
                                        )}
                                    </div>
                                );
                            })
                    )}

                    {isTaskCreationModalOpen && (
                        <div className="modal-overlay">
                            <div className="modal-content">
                                <TaskCreation 
                                    onClose={closeTaskCreationModal}
                                    onTaskCreated={() => handleRefresh(passedProjectId, employeeId)}
                                    managerId={employeeId}
                                    projectId={passedProjectId}
                                    sprintId={actualSprint.id}
                                />
                            </div>
                        </div>
                    )}

                    {isSubTaskCreationModalOpen && (
                        <div className="modal-overlay">
                            <div className="modal-content">
                                <SubTaskCreation 
                                    onClose={closeSubTaskCreationModal}
                                    onTaskCreated={() => handleRefresh(passedProjectId, employeeId)}
                                    managerId={employeeId}
                                    projectId={passedProjectId}
                                    sprintId={actualSprint.id}
                                />
                            </div>
                        </div>
                    )}

                    {isTaskDetailsModalOpen && selectedTask && (
                        <ManagerModalTask
                            setOpen={closeTaskDetailsModal}
                            handleDoneClick={handleSaveClick}
                            handleCancelClick={handleCancelClick}
                            task={selectedTask}
                        />
                    )}
                </>
            )}
        </div>
    );
}