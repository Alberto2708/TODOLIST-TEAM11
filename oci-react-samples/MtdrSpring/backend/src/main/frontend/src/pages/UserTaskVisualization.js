import React, { useState } from "react";
import "../styles/TaskVisualization.css";
import ToDoItem from "../components/ToDoItem.js";
import ModalTask from "../components/ModelTask.js";

const UserTaskVisualization = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [triggerClickIndex, setTriggerClickIndex] = useState(null); 
  const openModal = (index) => {
    setIsModalOpen(true);
    setTriggerClickIndex(index);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const tasks = Array(2).fill({
    name: "List item",
    timestamp: "March 11th ",
  });

  const handleDoneClick = () => {
    setTriggerClickIndex(null);
    closeModal();
  };

  return (
    <div>
      <div className="container">
        <div className="header">MY TO DO LIST</div>
        <div className="task-list">
          {tasks.map((task, index) => (
            <ToDoItem
              key={index}
              name={task.name}
              timestamp={task.timestamp}
              onClick={() => openModal(index)} 
              triggerClick={triggerClickIndex === index} 
            />
          ))}
        </div>
      </div>

      {isModalOpen && (
        <ModalTask
          setOpen={closeModal}
          handleDoneClick={handleDoneClick} 
        />
      )}
    </div>
  );
};

export default UserTaskVisualization;