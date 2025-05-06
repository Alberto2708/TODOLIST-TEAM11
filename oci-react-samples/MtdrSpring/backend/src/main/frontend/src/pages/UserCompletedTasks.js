import React, { useEffect, useState } from "react";
import "../styles/TaskVisualization.css";
import ToDoItem from "../components/ToDoItem.js";
import ModalTask from "../components/ModelTask.js";
import { useNavigate } from "react-router-dom";
import HeaderDev from "../components/HeaderDev.js";
import { useAuth } from "../context/AuthContext.js"; // Import the AuthContext

function UserCompletedTasks() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [triggerClickIndex, setTriggerClickIndex] = useState(null); 
  const [actualSprint, setActualSprint] = useState(null);
  const [authEmployeeId, setEmployeeId] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [subTasks, setSubTasks] = useState({});
  const [passedProjectId, setPassedProjectId] = useState(null);
  const [selectedTask, setSelectedTask] = useState(null);
  const [isScreenLoading, setIsScreenLoading] = useState(true); // Add loading state
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
          fetchTasks(assignedDevId, data.id); 
      } else {
          console.error("Error fetching actual sprint:", response.statusText);
          setIsScreenLoading(false); // Stop loading on error
      }
  } catch (error) {
      console.error("Error fetching actual sprint:", error);
      setIsScreenLoading(false); // Stop loading on error
  }
};

const fetchTasks = async (assignedDevId, sprintId) => {
  try {
      const response = await fetch(`/assignedDev/${assignedDevId}/sprint/${sprintId}/father/completed`);
      if (!response.ok) {
          console.error("Error fetching tasks:", response.statusText);
          setIsScreenLoading(false); // Stop loading on error
          return;
      }
      const data = await response.json();
      console.log("Tasks:", data);

      const parseTasks = await Promise.all(
          data.map(async (task) => {
              const subTasks = await fetchSubTasks(task.id, assignedDevId);
              console.log(`Fetched subtasks for task ID ${task.id}:`, subTasks);

              return {
                  id: task.id,
                  name: task.name,
                  deadline: task.deadline,
                  status: task.status,
                  subTasks: subTasks, // Include the fetched subtasks
              };
          })
      );

      console.log("Parsed tasks:", parseTasks);
      setTasks(parseTasks);
      setIsScreenLoading(false); // Stop loading after tasks are fetched
  } catch (error) {
      console.error("Error fetching tasks:", error);
      setIsScreenLoading(false); 
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

const handleRefresh = (projectId, assignedDevId) => {
  setIsScreenLoading(true);
  fetchActualSprint(projectId, assignedDevId);
}


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
    console.log("Opening modal for task at index:", index);
    console.log("Selected task details:", tasks[index]);
    setIsModalOpen(true);
    setTriggerClickIndex(index);
    setSelectedTask(tasks[index]);
};

const closeModal = () => {
    console.log("Closing modal");
    setIsModalOpen(false);
};

const handleDoneClick = () => {
    console.log("Done button clicked in modal");
    setTriggerClickIndex(null);
    closeModal();
    handleRefresh(passedProjectId, authEmployeeId); 
};

  console.log("employeeId: ", authEmployeeId);
  return (
    <div>
      {isScreenLoading ? (
        <div className="loading-screen">
          <div className="spinner"></div>
        </div>
      ) : (
      <div className="utv-container">
          <HeaderDev actualSprint={actualSprint} />
          <div className="task-list">
            {tasks.map((task, index) => (
              <ToDoItem
                key={index}
                name={task.name}
                timestamp={task.deadline}
                taskStatus={task.status}
                subTasks={task.subTasks}
                statusColor={getStatusColor(task.status)}
                onClick={() => openModal(index)} 
                triggerClick={triggerClickIndex === index} 
              />
            ))}
          </div>
        </div>
      )}

      {isModalOpen && selectedTask && (
        <ModalTask
          setOpen={closeModal}
          handleDoneClick={handleDoneClick}
          task={selectedTask} 
        />
      )}
    </div>
  );
};

export default UserCompletedTasks;