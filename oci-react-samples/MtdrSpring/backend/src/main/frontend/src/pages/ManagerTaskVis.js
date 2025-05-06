import React, { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import ToDoItem from "../components/ToDoItem";
import ManagerModalTask from "../components/ManagerModalTask";
import TaskCreation from "../components/TaskCreation";
import SubTaskCreation from "../components/SubTaskCreation";
import HeaderMngr from "../components/HeaderMngr";
import { useAuth } from "../context/AuthContext"; // Import the AuthContext

export default function ManagerTaskVis() {
    // States
    const [isTaskCreationModalOpen, setIsTaskCreationModalOpen] = useState(false);
    const [isTaskDetailsModalOpen, setIsTaskDetailsModalOpen] = useState(false);
    const [isSubTaskCreationModalOpen, setIsSubTaskCreationModalOpen] = useState(false);
    const [selectedTaskIndex, setSelectedTaskIndex] = useState(null);
    const [modalAction, setModalAction] = useState(null);
    const [authEmployeeId, setEmployeeId] = useState(null);
    const [passedProjectId, setPassedProjectId] = useState(null);
    const [employees, setEmployees] = useState([]);
    const [tasks, setTasks] = useState({});
    const [subTasks, setSubTasks] = useState({});
    const [actualSprint, setActualSprint] = useState({});
    const [selectedTask, setSelectedTask] = useState(null);
    const [isScreenLoading, setScreenLoading] = useState(true);
    const [selectedDeveloper, setSelectedDeveloper] = useState("all");
    const [filteredTasks, setFilteredTasks] = useState({});
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
                localStorage.setItem("sprintId", data.id);
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
            await Promise.all([...taskPromises]);
            setScreenLoading(false);
        } catch (error) {
            console.error("Error fetching employees:", error);
            setScreenLoading(false);
        }
    };

    const fetchTasks = async (assignedDevId, sprintId) => {
        try {
            const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${sprintId}/father/pending`);
            if (!response.ok) {
                console.error(`Error fetching tasks: ${response.status} - ${response.statusText}`);
                return;
            }
            const data = await response.json();
            
            const parsedTasks = await Promise.all(
                data.map(async (item) => {
                    const subTasks = await fetchSubTasks(item.id, assignedDevId); // Use item.id, not item.body.id
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
            
            setTasks(prevTasks => ({ ...prevTasks, [assignedDevId]: parsedTasks }));
        } catch (error) {
            console.error("Error in fetchTasks:", error);
        }
    };

    const fetchSubTasks = async (taskId, assignedDevId) => {
        try {
            const response = await fetch(`subToDoItems/toDoItem/${taskId}/employee/${assignedDevId}/pending`);
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
                        subTasks: await fetchSubTasks(item.id, assignedDevId),
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

    const handleCancelClick = () => {
        setModalAction('cancel');
        closeTaskDetailsModal();
    };

    const handleDeleteClick = () => {
        console.log("Delete button clicked in modal");
        setModalAction('delete');
        closeTaskDetailsModal();
        handleRefresh(passedProjectId, authEmployeeId); 
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
                        onCreateTask={openTaskCreationModal}
                    />

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
                                    onTaskCreated={() => handleRefresh(passedProjectId, authEmployeeId)}
                                    managerId={authEmployeeId}
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
                                    onTaskCreated={() => handleRefresh(passedProjectId, authEmployeeId)}
                                    managerId={authEmployeeId}
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
                            handleDeleteClick={handleDeleteClick}
                        />
                    )}
                </>
            )}
        </div>
    );
}