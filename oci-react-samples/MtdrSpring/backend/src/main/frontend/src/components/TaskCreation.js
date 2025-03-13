import React, { useState } from "react";
import "../styles/TaskCreation.css";

export default function TaskCreation({ onClose }) {
    const [taskName, setTaskName] = useState("");
    const [description, setDescription] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [responsible, setResponsible] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();

        if (taskName && description && dueDate && responsible) {
            alert(`Task created!\n\nTask: ${taskName}\nResponsible: ${responsible}\nDue Date: ${dueDate}\nDescription: ${description}`);
            // Aquí puedes hacer un fetch para guardar en la base de datos
        } else {
            alert("Please fill out all fields");
        }
    };

    return (
        <div className="tc-modal-overlay">
            <div className="tc-modal-container">
                <div className="tc-modal-content">
                    <div className="tc-modal-header">
                        <h3 className="tc-modal-title">Create New Task</h3>
                    </div>
                    <form className="tc-task-creation-form" onSubmit={handleSubmit}>
                        <div className="tc-form-group">
                            <label>Task Name</label>
                            <input
                                type="text"
                                placeholder="Task Name"
                                value={taskName}
                                onChange={(e) => setTaskName(e.target.value)}
                                required
                            />
                        </div>

                        <div className="tc-form-group">
                            <label>Responsible</label>
                            <select
                                value={responsible}
                                onChange={(e) => setResponsible(e.target.value)}
                                required
                            >
                                <option value="">Select Team Member</option>
                                <option value="John">John</option>
                                <option value="Jane">Jane</option>
                                <option value="Doe">Doe</option>
                            </select>
                        </div>

                        <div className="tc-form-group">
                            <label>Due Date</label>
                            <input
                                type="date"
                                value={dueDate}
                                onChange={(e) => setDueDate(e.target.value)}
                                required
                            />
                        </div>

                        <div className="tc-form-group">
                            <label>Description</label>
                            <textarea
                                placeholder="Task Description"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                required
                            />
                        </div>

                        <div className="tc-modal-footer">
                            <button type="button" onClick={onClose} className="tc-btn tc-btn-cancel">
                                Close
                            </button>
                            <button type="submit" className="tc-btn tc-btn-danger">
                                Create Task
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}