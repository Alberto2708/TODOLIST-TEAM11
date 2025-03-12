import React from "react";
import { useState } from "react";
import "../styles/TaskVisualization.css"; 
import ToDoItem from "../components/ToDoItem.js";

const UserTaskVisualization = () => {
  const tasks = Array(2).fill({
    name: "List item",
    timestamp: "March 11th ",
  });

  return (
    <div className="container">
      <div className="header">MY TO DO LIST</div>
      <div className="task-list">
        {tasks.map((task, index) => (
          <ToDoItem key={index} name={task.name} timestamp={task.timestamp} />
        ))}
      </div>
    </div>
  );
};

export default UserTaskVisualization;