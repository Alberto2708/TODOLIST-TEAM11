import React, { useEffect, useState } from "react";
import "../styles/TaskVisualization.css";
import ToDoItem from "../components/ToDoItem.js";
import ModalTask from "../components/ModelTask.js";

function UserTaskVisualization() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [triggerClickIndex, setTriggerClickIndex] = useState(null); 
  const [employeeId, setEmployeeId] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [selectedTask, setSelectedTask] = useState(null);

  useEffect(() => {
    const employeeId = localStorage.getItem("employeeId");
    setEmployeeId(employeeId);
    if (employeeId) {
      fetchTasks(employeeId);
    }
  }, []);

  const fetchTasks = async (employeeId) => {
    try {
      const response = await fetch(`/assignedDev/${employeeId}/sprint/1`);
      const data = await response.json();
      const parsedTasks = data.map(item => ({
        name: item.body.name,
        deadline: new Date(item.body.deadline).toLocaleDateString(),
        description: item.body.description,
        status: item.body.status,
      }));
      setTasks(parsedTasks);
    } catch (error) {
      console.error("Error fetching tasks:", error);
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
    setTriggerClickIndex(index);
    setSelectedTask(tasks[index]);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const handleDoneClick = () => {
    setTriggerClickIndex(null);
    closeModal();
  };

  console.log("employeeId: ", employeeId);
  return (
    <div>
      <div className="container">
        <div className="header">MY TO DO LIST</div>
        <div className="task-list">
          {tasks.map((task, index) => (
            <ToDoItem
              key={index}
              name={task.name}
              timestamp={task.deadline}
              taskStatus={task.status}
              statusColor={getStatusColor(task.status)}
              onClick={() => openModal(index)} 
              triggerClick={triggerClickIndex === index} 
            />
          ))}
        </div>
      </div>

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