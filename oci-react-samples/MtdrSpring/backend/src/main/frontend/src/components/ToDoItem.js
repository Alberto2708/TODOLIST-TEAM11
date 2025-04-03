import React, { useState } from "react";
import "../styles/ToDoItem.css";

const ToDoItem = ({ name, timestamp, statusColor, taskStatus, subTasks = [], onClick }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isClicked, setIsClicked] = useState(false);

  const handleTaskClick = () => {
    onClick(); // Open modal with task details
  };

  const handleButtonClick = (e) => {
    e.stopPropagation(); // Prevent click from bubbling up
    setIsClicked(!isClicked);
  };

  const toggleSubtasks = (e) => {
    e.stopPropagation(); // Prevent click from opening modal
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="task-item-container">
      <div className="task-item" onClick={handleTaskClick}>
        {subTasks.length > 0 && (
          <button className="expand-button" onClick={toggleSubtasks}>
            {isExpanded ? "▼" : "▶"}
          </button>
        )}
        <span className="task-name">{name}</span>
        <span className="task-timestamp">{timestamp}</span>
        <span className="task-status" style={{ color: statusColor }}>{taskStatus}</span>
        <button
          className={`task-button ${isClicked ? "clicked" : ""}`}
          onClick={handleButtonClick}
        >
          ➕
        </button>
      </div>

      {isExpanded && (
        <div className="subtask-container">
          {subTasks.map((subTask, index) => (
            <ToDoItem
              key={index}
              name={subTask.name}
              timestamp={subTask.timestamp}
              statusColor={subTask.statusColor}
              taskStatus={subTask.taskStatus}
              subTasks={subTask.subTasks || []} // Recursively pass subtasks
              onClick={onClick} // Pass the same onClick function
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default ToDoItem;
