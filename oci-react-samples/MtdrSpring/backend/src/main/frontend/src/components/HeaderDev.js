import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import "../styles/HeaderDev.css"; 

function HeaderDev({ actualSprint }) {
  const location = useLocation();
  const navigate = useNavigate();

  const buttonText = location.pathname.includes('completedtasks') 
    ? 'Pending Tasks' 
    : 'Completed Tasks';

  const buttonPath = location.pathname.includes('completedtasks')
    ? '/usertasks'  
    : '/usercompletedtasks';

  return (
    <div className="header-container">
      <div className="header">MY TO DO LIST</div>
      <div className="sprint-header-container">
        <div className="sprintContainer">
          <h3>{actualSprint?.name}</h3>
          <div className="dateContainer">
            <p>Start Date: {new Date(actualSprint?.startDate).toLocaleDateString()}</p>
            <p>End Date: {new Date(actualSprint?.endDate).toLocaleDateString()}</p>
            <p>Days Left: {Math.floor((new Date(actualSprint?.endDate) - new Date()) / (1000 * 60 * 60 * 24))}</p>
          </div>
        </div>
        <button 
          className="completed-tasks-button"
          onClick={() => navigate(buttonPath)}
        >
          {buttonText}
        </button>
      </div>
    </div>
  );
}

export default HeaderDev;