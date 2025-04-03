import React, { useState, useEffect } from "react";
import "../styles/TaskCreation.css";

export default function SubTaskCreation({ onClose, onTaskCreated, managerId, projectId, sprintId }) {
    const [taskName, setTaskName] = useState("");
    const [parent, setParent] = useState("");
    const [description, setDescription] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [responsibles, setResponsibles] = useState([{ id: "", name: "" }]);
    const [estHours, setEstHours] = useState(0.5);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isAssigning, setIsAssigning] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [employees, setEmployees] = useState([]);
    const [loadingEmployees, setLoadingEmployees] = useState(true);
    const [errorLoadingEmployees, setErrorLoadingEmployees] = useState(null);
    const [parentTasks, setParentTasks] = useState([]);
    const [loadingParentTasks, setLoadingParentTasks] = useState(true);
    const [errorLoadingParentTasks, setErrorLoadingParentTasks] = useState(null);

    useEffect(() => {
        const fetchEmployees = async () => {
            try {
                setLoadingEmployees(true);
                const response = await fetch(`/employees/managerId/${managerId}`);
                const data = await response.json();
                setEmployees(data);
            } catch (error) {
                setErrorLoadingEmployees(error.message);
            } finally {
                setLoadingEmployees(false);
            }
        };

        const fetchParentTasks = async () => {
            try {
                setLoadingParentTasks(true);
                const response = await fetch(`/todolist/manager/${managerId}/sprint/${sprintId}`);
                const data = await response.json();
                setParentTasks(data);
            } catch (error) {
                setErrorLoadingParentTasks(error.message);
            } finally {
                setLoadingParentTasks(false);
            }
        };

        fetchEmployees();
        fetchParentTasks();
    }, [managerId, sprintId]);

    const addResponsibleField = () => {
        setResponsibles([...responsibles, { id: "", name: "" }]);
    };

    const removeResponsibleField = (index) => {
        setResponsibles(responsibles.filter((_, i) => i !== index));
    };

    const handleResponsibleChange = (index, value) => {
        const updatedResponsibles = [...responsibles];
        updatedResponsibles[index].id = value;
        setResponsibles(updatedResponsibles);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        setErrorMessage("");

        if (!taskName || !description || !dueDate || responsibles.length === 0 || !estHours) {
            alert("Please fill all required fields");
            setIsSubmitting(false);
            return;
        }

        try {
            const taskData = {
                name: taskName,
                status: "PENDING",
                managerId,
                startDate: new Date().toISOString(),
                deadline: new Date(dueDate).toISOString(),
                description,
                projectId,
                sprintId,
                estHours,
            };

            const response = await fetch('/todolist', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(taskData)
            });

            if (!response.ok) {
                throw new Error('Failed to create task');
            }

            const taskId = await response.json();
            setIsAssigning(true);
            
            for (const responsible of responsibles) {
                const assignationData = {
                    id: {
                        toDoItemId: taskId,
                        employeeId: responsible.id
                    }
                };

                const assignation = await fetch(`/assignedDev`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(assignationData)
                });

                if (!assignation.ok) {
                    throw new Error(`Failed to assign task to employee ID: ${responsible.id}`);
                }
            }

            alert("Task created and assigned successfully!");
            onClose();
            if (onTaskCreated) onTaskCreated();
        } catch (error) {
            setErrorMessage(error.message);
        } finally {
            setIsSubmitting(false);
            setIsAssigning(false);
        }
    };

    return (
        <div className="tc-modal-overlay">
            <div className="tc-modal-container">
                <div className="tc-modal-content">
                    <h3 className="tc-modal-title">Create New Task</h3>
                    {errorMessage && <p className="tc-error-message">{errorMessage}</p>}
                    <form className="tc-task-creation-form" onSubmit={handleSubmit}>
                        <div className="tc-form-group">
                            <label>Task Name</label>
                            <input type="text" value={taskName} onChange={(e) => setTaskName(e.target.value)} required />
                        </div>
                        <div className="tc-form-group">
                            <label>Due Date</label>
                            <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} required />
                        </div>
                        <div className="tc-form-group">
                            <label>Estimated Hours</label>
                            <input type="number" min="0.5" step="0.5" max="4" value={estHours} onChange={(e) => setEstHours(e.target.value)} required />
                        </div>
                        <div className="tc-modal-footer">
                            <button type="button" onClick={onClose} className="tc-btn tc-btn-cancel" disabled={isSubmitting}>Cancel</button>
                            <button type="submit" className="tc-btn tc-btn-primary" disabled={isSubmitting || isAssigning || loadingEmployees}>Create Task</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}