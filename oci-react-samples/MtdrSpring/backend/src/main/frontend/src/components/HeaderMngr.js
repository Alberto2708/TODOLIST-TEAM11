import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/HeaderMngr.css';
import Filter from './Filter';

function HeaderMngr({ 
  actualSprint, 
  employees = [], 
  selectedDeveloper = "all", 
  setSelectedDeveloper = () => {},
  showFilter = true,
  onCreateTask
}) {
  const location = useLocation();
  const navigate = useNavigate();


  const getPageConfig = () => {
    if (location.pathname.includes('CompletedTasks')) {
      return {
        title: "COMPLETED TASKS",
        primaryButton: {
          text: 'Pending Tasks',
          path: '/ManagerTasks'
        },
        showCreateButton: false
      };
    } else if (location.pathname.includes('Stats')) {
      return {
        title: "KPI REPORTS",
        primaryButton: {
          text: 'Tasks',
          path: '/ManagerTasks'
        },
        showCreateButton: false,
        showFilter: false 
      };
    }
     else if (location.pathname.includes('SprintCarousel')) {
      return {
        title: "SPRINT CAROUSEL",
        primaryButton: {
          text: 'Tasks',
          path : '/ManagerTasks'
        },
        showCreateButton: false
      };
    } else {
      return {
        title: "TO DO LIST (PENDING TASKS)",
        primaryButton: {
          text: 'Completed Tasks',
          path: '/CompletedTasks'
        },
        secondaryButton: {
          text: 'View Statistics',
          path: '/Stats'
        },
        terciaryButton: {
          text: 'Sprint Carousel',
          path: '/SprintCarousel'
        },
        showCreateButton: true
      };
    }
  };

  const { 
    title, 
    primaryButton, 
    secondaryButton, 
    terciaryButton,
    showCreateButton 
  } = getPageConfig();

  return (
    <div className="taskContainer">
      <h1>{title}</h1>
      {actualSprint && (
        <>
          <h2 className="sprintname">{actualSprint.name}</h2>
          <div className="dateContainer">
            <h3>Start Date: {new Date(actualSprint.startDate).toLocaleDateString()}</h3>
            <h3>End Date: {new Date(actualSprint.endDate).toLocaleDateString()}</h3>
            {!location.pathname.includes('CompletedTasks') && (
              <h3>Days Left: {Math.floor((new Date(actualSprint.endDate) - new Date()) / (1000 * 60 * 60 * 24))}</h3>
            )}
          </div>
        </>
      )}
      <div className="action-bar">
        {showFilter && employees.length > 0 && (
          <Filter 
            employees={employees} 
            selectedDeveloper={selectedDeveloper} 
            setSelectedDeveloper={setSelectedDeveloper} 
          />
        )}
        <div className="button-group">
          {showCreateButton && (
            <button className="addButton" onClick={onCreateTask}>
              Create Task
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
              </svg>
            </button>
          )}
          <button 
            className="navButton" 
            onClick={() => navigate(primaryButton.path)}
          >
            {primaryButton.text}
          </button>
          {secondaryButton && (
            <button 
              className="navButton" 
              onClick={() => navigate(secondaryButton.path)}
            >
              {secondaryButton.text}
            </button>
          )}
          {terciaryButton && (
            <button 
              className="navButton" 
              onClick={() => navigate(terciaryButton.path)}
            >
              {terciaryButton.text}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default HeaderMngr;