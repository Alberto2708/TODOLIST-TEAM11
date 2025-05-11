import React, { useState } from "react";
import "../styles/ToDoItem.css";

const CompletedItem = ({
  name,
  timestamp,
  statusColor,
  taskStatus,
  subTasks = [],
  onClick,
  subTaskOnClick,
}) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const handleTaskClick = (e) => {
    e.stopPropagation();
    if (onClick) {
      onClick();
    }
  };

  const toggleSubtasks = (e) => {
    e.stopPropagation();
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

  // Filter out non-completed subtasks
  const completedSubTasks = subTasks.filter(
    (subTask) => subTask.status === "COMPLETED"
  );

  // Don't render the component at all if the main task isn't completed
  if (taskStatus !== "COMPLETED") {
    return null;
  }

  return (
    <div className="task-item-container">
      <div className="task-item" onClick={handleTaskClick}>
        {completedSubTasks.length > 0 && (
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
          {completedSubTasks.map((subTask, index) => (
            <CompletedItem
              key={index}
              name={subTask.name}
              timestamp={subTask.deadline}
              statusColor={getStatusColor(subTask.status)}
              taskStatus={subTask.status}
              subTasks={subTask.subTasks || []}
              onClick={(e) => {
                e.stopPropagation();
                if (subTaskOnClick) {
                  subTaskOnClick(subTask);
                }
              }}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default CompletedItem;
