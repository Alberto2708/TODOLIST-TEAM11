import React, { useEffect, useState } from "react";
import "../styles/TaskVisualization.css";
import ToDoItem from "../components/ToDoItem.js";
import ModalTask from "../components/ModelTask.js";

function UserTaskVisualization() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [triggerClickIndex, setTriggerClickIndex] = useState(null); 
  const [actualSprint, setActualSprint] = useState(null);
  const [employeeId, setEmployeeId] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [subTasks, setSubTasks] = useState({});
  const [passedProjectId, setPassedProjectId] = useState(null);
  const [selectedTask, setSelectedTask] = useState(null);
  const [isScreenLoading, setIsScreenLoading] = useState(true); // Add loading state

  useEffect(() => {
    const employeeId = localStorage.getItem("employeeId");
    const projectId = localStorage.getItem("projectId");
    console.log("employeeId:", employeeId, "projectId:", projectId); // Debugging
    setEmployeeId(employeeId);
    setPassedProjectId(projectId);
    if (employeeId && projectId) {
        fetchActualSprint(projectId, employeeId); // Pass employeeId to fetchActualSprint
    } else {
        console.log("No employeeId or projectId found in localStorage");
        setIsScreenLoading(false); // Stop loading if data is missing
    }
}, []);

const fetchActualSprint = async (projectId, employeeId) => {
  try {
      const response = await fetch(`/sprint/project/${projectId}`);
      if (response.ok) {
          const data = await response.json();
          setActualSprint(data);
          console.log("Actual sprint:", data);
          fetchTasks(employeeId, data.id); // Pass employeeId to fetchTasks
      } else {
          console.error("Error fetching actual sprint:", response.statusText);
          setIsScreenLoading(false); // Stop loading on error
      }
  } catch (error) {
      console.error("Error fetching actual sprint:", error);
      setIsScreenLoading(false); // Stop loading on error
  }
};

const fetchTasks = async (employeeId, sprintId) => {
  try {
      const response = await fetch(`/assignedDev/${employeeId}/sprint/${sprintId}/father`);
      if (!response.ok) {
          console.error("Error fetching tasks:", response.statusText);
          setIsScreenLoading(false); // Stop loading on error
          return;
      }
      const data = await response.json();
      console.log("Tasks:", data);

      const parseTasks = await Promise.all(
          data.map(async (task) => {
              const subTasks = await fetchSubTasks(task.body.id, employeeId);
              console.log(`Fetched subtasks for task ID ${task.body.id}:`, subTasks);

              return {
                  id: task.body.id,
                  name: task.body.name,
                  deadline: task.body.deadline,
                  status: task.body.status,
                  subTasks: subTasks, // Include the fetched subtasks
              };
          })
      );

      console.log("Parsed tasks:", parseTasks);
      setTasks(parseTasks);
      setIsScreenLoading(false); // Stop loading after tasks are fetched
  } catch (error) {
      console.error("Error fetching tasks:", error);
      setIsScreenLoading(false); // Stop loading on error
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

const handleRefresh = (projectId, employeeId) => {
  setIsScreenLoading(true); // Start loading when refreshing
  fetchActualSprint(projectId, employeeId);
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
    handleRefresh(passedProjectId, employeeId); // Refresh the task list after marking as done
};

  console.log("employeeId: ", employeeId);
  return (
    <div>
      {isScreenLoading ? (
        <div className="loading-screen">
          <div className="spinner"></div>
        </div>
      ) : (
      <div className="utv-container">
          <div className="header">MY TO DO LIST</div>
          <div className="sprintContainer"> {/* Nombre de clase corregido */}
              <h3>{actualSprint.name}</h3>
              <div className="dateContainer">
                  <p>Start Date: {new Date(actualSprint.startDate).toLocaleDateString()}</p>
                  <p>End Date: {new Date(actualSprint.endDate).toLocaleDateString()}</p>
                  <p>Days Left: {Math.floor((new Date(actualSprint.endDate) - new Date()) / (1000 * 60 * 60 * 24))}</p>
              </div>
          </div>
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
          task={selectedTask} // Pass the selected task as a prop
        />
      )}
    </div>
  );
};

export default UserTaskVisualization;