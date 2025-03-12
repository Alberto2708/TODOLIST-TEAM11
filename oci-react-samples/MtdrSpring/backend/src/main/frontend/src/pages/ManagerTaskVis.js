import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import React from 'react';
import ToDoItem from "../components/ToDoItem";
import ManagerModalTask from "../components/ManagerModalTask.js";
import TaskCreation from "../components/TaskCreation";

export default function ManagerTaskVis() {
    const [users] = useState([
        { id: 1, name: 'Usuario 1' },
        { id: 2, name: 'Usuario 2' },
        { id: 3, name: 'Usuario 3' },
    ]);

    const [isTaskCreationModalOpen, setIsTaskCreationModalOpen] = useState(false); 
    const [isTaskDetailsModalOpen, setIsTaskDetailsModalOpen] = useState(false);
    const [selectedTaskIndex, setSelectedTaskIndex] = useState(null); 
    const [modalAction, setModalAction] = useState(null); 
    const navigate = useNavigate();

    const openTaskDetailsModal = (index) => {
        setIsTaskDetailsModalOpen(true); 
        setSelectedTaskIndex(index); 
        setModalAction(null); 
    };

    const closeTaskDetailsModal = () => {
        setIsTaskDetailsModalOpen(false); 
    };

    const openTaskCreationModal = () => {
        setIsTaskCreationModalOpen(true);
    };

    const closeTaskCreationModal = () => {
        setIsTaskCreationModalOpen(false);
    };

    const handleSaveClick = () => {
        setModalAction('save'); 
        closeTaskDetailsModal(); 
    };

    const handleCancelClick = () => {
        setModalAction('cancel'); 
        closeTaskDetailsModal(); 
    };

    return (
        <div className="mtvContainer">
            <div className="taskContainer">
                <h1>TO DO LIST</h1>
                <button className="addButton" onClick={openTaskCreationModal}>
                    Create Task
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
                    </svg>
                </button>
            </div>

            {users.map(user => (
                <div key={user.id} className="taskList">
                    <h2>{user.name}'s to do list:</h2>
                    <ToDoItem
                        name="Task 1"
                        timestamp="2021-10-01"
                        onClick={() => openTaskDetailsModal(0)} 
                        shouldTrigger={modalAction !== 'save'} 
                    />
                    <ToDoItem
                        name="Task 2"
                        timestamp="2021-10-02"
                        onClick={() => openTaskDetailsModal(1)} 
                        shouldTrigger={modalAction !== 'save'} 
                    />
                </div>
            ))}


            {isTaskCreationModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <TaskCreation onClose={closeTaskCreationModal} />
                    </div>
                </div>
            )}


            {isTaskDetailsModalOpen && (
                <ManagerModalTask
                    setOpen={closeTaskDetailsModal}
                    handleDoneClick={handleSaveClick} 
                    handleCancelClick={handleCancelClick} 
                />
            )}
        </div>
    );
}