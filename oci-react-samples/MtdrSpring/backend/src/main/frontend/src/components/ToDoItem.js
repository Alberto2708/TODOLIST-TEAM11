import React, { useState } from "react";
import "../styles/ToDoItem.css";

const ToDoItem = ({ name, timestamp, onClick }) => {
  const [isClicked, setIsClicked] = useState(false);

  const handleTaskClick = () => {
    onClick(); // Abre el modal de detalles de la tarea
  };

  const handleButtonClick = (e) => {
    e.stopPropagation(); // Evita que el clic en el botón propague al contenedor
    setIsClicked(!isClicked); // Cambia el estado del botón (marcar/desmarcar)
  };

  return (
    <div className="task-item" onClick={handleTaskClick}>
      <span className="task-name">{name}</span>
      <span className="task-timestamp">{timestamp}</span>
      <button
        className={`task-button ${isClicked ? "clicked" : ""}`}
        onClick={handleButtonClick}
      >
        ✔️
      </button>
    </div>
  );
};

export default ToDoItem;