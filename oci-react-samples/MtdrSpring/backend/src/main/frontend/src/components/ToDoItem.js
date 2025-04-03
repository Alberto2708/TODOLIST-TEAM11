import React, { useState } from "react";
import "../styles/ToDoItem.css";

const ToDoItem = ({ name, timestamp, statusColor, taskStatus, subTasks = [], onClick, subTaskOnClick }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const handleTaskClick = (e) => {
    e.stopPropagation(); // Prevent click from bubbling up to parent
    if (onClick) {
      onClick(); // Open modal with task details (only if onClick is defined)
    }
  };

  const toggleSubtasks = (e) => {
    e.stopPropagation(); // Prevent click from opening modal
    setIsExpanded(!isExpanded);
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
        <span className="task-status" style={{ color: statusColor }}>
          {taskStatus}
        </span>
      </div>

      {isExpanded && (
        <div className="subtask-container">
          {subTasks.map((subTask, index) => (
            <ToDoItem
              key={index}
              name={subTask.name}
              timestamp={subTask.deadline}
              statusColor={getStatusColor(subTask.status)}
              taskStatus={subTask.status}
              subTasks={subTask.subTasks || []} // Recursively pass subtasks
              onClick={(e) => {
                e.stopPropagation(); // Prevent parent task click
                if (subTaskOnClick) {
                  subTaskOnClick(subTask); // Pass the subtask to the handler (only if subTaskOnClick is defined)
                }
              }}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default ToDoItem;
