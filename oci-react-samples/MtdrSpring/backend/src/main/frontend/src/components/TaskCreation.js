import React, { useState, useEffect } from "react";
import "../styles/TaskCreation.css";

export default function TaskCreation({ onClose, onTaskCreated, managerId }) {
    const [taskName, setTaskName] = useState("");
    const [description, setDescription] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [responsible, setResponsible] = useState("");
    const [estHours, setEstHours] = useState(1);
    const [isSubmitting, setIsSubmitting] = useState(false);
    
    const [employees, setEmployees] = useState([]);
    const [loadingEmployees, setLoadingEmployees] = useState(true);
    const [errorLoadingEmployees, setErrorLoadingEmployees] = useState(null);

    useEffect(() => {
        const fetchEmployees = async () => {
            try {
                setLoadingEmployees(true);
                setErrorLoadingEmployees(null);
                
                const response = await fetch(`http://localhost:8081/employees/managerId/${managerId}`);
                
                const contentType = response.headers.get('content-type');
                if (!contentType || !contentType.includes('application/json')) {
                    const text = await response.text();
                    throw new Error(`Server returned ${contentType} instead of JSON`);
                }

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                setEmployees(data);
            } catch (error) {
                console.error("Error loading employees:", error);
                setErrorLoadingEmployees(error.message);
            } finally {
                setLoadingEmployees(false);
            }
        };

        fetchEmployees();
    }, [managerId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        if (!taskName || !description || !dueDate || !responsible || !estHours) {
            alert("Please fill all required fields");
            setIsSubmitting(false);
            return;
        }

        if (isNaN(estHours) || estHours <= 0) {
            alert("Estimated hours must be a positive number");
            setIsSubmitting(false);
            return;
        }

        try {
            const taskData = {
                name: taskName,
                description: description,
                deadline: new Date(dueDate).toISOString(),
                managerId: managerId,
                status: "PENDING",
                estHours: parseInt(estHours),
                assignedDevId: parseInt(responsible)
            };

            const response = await fetch('http://localhost:8081/api/todolist', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(taskData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || 'Failed to create task');
            }

            alert("Task created successfully!");
            onClose();
            if (onTaskCreated) {
                onTaskCreated();
            }
        } catch (error) {
            console.error("Error creating task:", error);
            alert(`Error creating task: ${error.message}`);
        } finally {
            setIsSubmitting(false);
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
                                placeholder="Enter task name"
                                value={taskName}
                                onChange={(e) => setTaskName(e.target.value)}
                                required
                            />
                        </div>

                        <div className="tc-form-group">
                            <label>Assign To</label>
                            {loadingEmployees ? (
                                <p>Loading team members...</p>
                            ) : errorLoadingEmployees ? (
                                <p className="tc-error-message">Error: {errorLoadingEmployees}</p>
                            ) : employees.length === 0 ? (
                                <p className="tc-error-message">No team members available</p>
                            ) : (
                                <select
                                    value={responsible}
                                    onChange={(e) => setResponsible(e.target.value)}
                                    required
                                >
                                    <option value="">Select Team Member</option>
                                    {employees.map(employee => (
                                        <option key={employee.ID} value={employee.ID}>
                                            {employee.name} (ID: {employee.ID})
                                        </option>
                                    ))}
                                </select>
                            )}
                        </div>

                        <div className="tc-form-group">
                            <label>Due Date </label>
                            <input
                                type="date"
                                value={dueDate}
                                onChange={(e) => setDueDate(e.target.value)}
                                min={new Date().toISOString().split('T')[0]}
                                required
                            />
                        </div>

  
                        <div className="tc-form-group">
                            <label>Estimated Hours </label>
                            <input
                                type="number"
                                min="1"
                                step="0.5"
                                value={estHours}
                                onChange={(e) => setEstHours(e.target.value)}
                                required
                            />
                        </div>

                        <div className="tc-form-group">
                            <label>Description </label>
                            <textarea
                                placeholder="Enter task description"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                rows="4"
                                required
                            />
                        </div>

                        <div className="tc-modal-footer">
                            <button 
                                type="button" 
                                onClick={onClose} 
                                className="tc-btn tc-btn-cancel"
                                disabled={isSubmitting}
                            >
                                Cancel
                            </button>
                            <button 
                                type="submit" 
                                className="tc-btn tc-btn-primary"
                                disabled={isSubmitting || loadingEmployees || !responsible}
                            >
                                {isSubmitting ? 'Creating...' : 'Create Task'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}