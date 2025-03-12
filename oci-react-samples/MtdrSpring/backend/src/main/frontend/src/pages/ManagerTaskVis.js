import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import React from 'react';
import ToDoItem from "../components/ToDoItem";
import ManagerModalTask from "../components/ManagerModalTask.js";

export default function ManagerTaskVis() {
    const [users] = useState([
        { id: 1, name: 'Usuario 1' },
        { id: 2, name: 'Usuario 2' },
        { id: 3, name: 'Usuario 3' },
    ]);

    const [isModalOpen, setIsModalOpen] = useState(false); // Modal state
    const [selectedTaskIndex, setSelectedTaskIndex] = useState(null); // Track selected task
    const [modalAction, setModalAction] = useState(null); // To track if modal is saved or canceled
    const navigate = useNavigate();

    const openModal = (index) => {
        setIsModalOpen(true); // Open modal
        setSelectedTaskIndex(index); // Select task when clicked
        setModalAction(null); // Reset action before opening modal
    };

    const closeModal = () => {
        setIsModalOpen(false); // Close modal
    };

    const handleSaveClick = () => {
        setModalAction('save'); // Set the action to save when Save is clicked
        closeModal(); // Simply close the modal without interacting with ToDoItem
    };

    const handleCancelClick = () => {
        setModalAction('cancel'); // Set the action to cancel when Cancel is clicked
        closeModal(); // Close modal
    };

    return (
        <div className="mtvContainer">
            <div className="taskContainer">
                <h1>TO DO LIST</h1>
                <button className="addButton" onClick={() => navigate('../tc')}>
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
                        onClick={() => openModal(0)} // Open modal only when the task item is clicked
                        shouldTrigger={modalAction !== 'save'} // Only trigger button if modal action is not save
                    />
                    <ToDoItem
                        name="Task 2"
                        timestamp="2021-10-02"
                        onClick={() => openModal(1)} // Open modal only when the task item is clicked
                        shouldTrigger={modalAction !== 'save'} // Only trigger button if modal action is not save
                    />
                </div>
            ))}

            {isModalOpen && (
                <ManagerModalTask
                    setOpen={closeModal}
                    handleDoneClick={handleSaveClick} // Save button closes the modal
                    handleCancelClick={handleCancelClick} // Cancel button closes the modal
                />
            )}
        </div>
    );
}