import React from "react";
import { useState } from "react";
import "../styles/ToDoItem.css";

const ToDoItem = ({ name, timestamp }) => {
    const [isClicked, setIsClicked] = useState(false);

  const handleClick = () => {
    // Toggle the clicked state
    setIsClicked(!isClicked);
  };
  return (
    <div className="task-item">
      <span className="task-name">{name}</span>
      <span className="task-timestamp">{timestamp}</span> 
      <button
        className={`task-button ${isClicked ? 'clicked' : ''}`}
        onClick={handleClick}
      >
        ✔️
      </button>
    </div>
  );
};

export default ToDoItem;