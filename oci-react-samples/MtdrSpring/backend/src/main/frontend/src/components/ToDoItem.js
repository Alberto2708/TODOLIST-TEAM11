import React, { useState } from "react";
import "../styles/ToDoItem.css";

const ToDoItem = ({ name, timestamp, onClick, triggerClick }) => {
  const [isClicked, setIsClicked] = useState(false);

  React.useEffect(() => {
    if (triggerClick) {
      setIsClicked(true);
    }
  }, [triggerClick]);

  const handleClick = () => {
    setIsClicked(!isClicked);
    onClick(); 
  };

  return (
    <div className="task-item" onClick={handleClick}>
      <span className="task-name">{name}</span>
      <span className="task-timestamp">{timestamp}</span>
      <button className={`task-button ${isClicked ? "clicked" : ""}`}>
        ✔️
      </button>
    </div>
  );
};

export default ToDoItem;