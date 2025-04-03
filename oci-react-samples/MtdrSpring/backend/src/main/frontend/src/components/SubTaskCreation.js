import React, { useState, useEffect, use } from "react";
import "../styles/TaskCreation.css";

export default function SubTaskCreation({ onClose, onTaskCreated, managerId, projectId, sprintId }) {
    const [taskName, setTaskName] = useState("");
    const [parent, setParent] = useState(""); // State for parent task
    const [description, setDescription] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [responsibles, setResponsibles] = useState([{ id: "", name: "" }]); // Array of assignments
    const [estHours, setEstHours] = useState(0.5);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isAssigning, setIsAssigning] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [employees, setEmployees] = useState([]);
    const [loadingEmployees, setLoadingEmployees] = useState(true);
    const [errorLoadingEmployees, setErrorLoadingEmployees] = useState(null);
    const [parentTasks, setParentTasks] = useState([]); // Array of parent tasks
    const [loadingParentTasks, setLoadingParentTasks] = useState(true);
    const [errorLoadingParentTasks, setErrorLoadingParentTasks] = useState(null);
    const [isAddingSubTask, setIsAddingSubTask] = useState(false);
    useEffect(() => {
        console.log("Selected parent task:", parent);
    }, [parent]);

    useEffect(() => {
        const fetchEmployees = async () => {
            try {
                setLoadingEmployees(true);
                setErrorLoadingEmployees(null);

                const response = await fetch(`/employees/managerId/${managerId}`);
                const data = await response.json();
                setEmployees(data);
            } catch (error) {
                console.error("Error loading employees:", error);
                setErrorLoadingEmployees(error.message);
            } finally {
                setLoadingEmployees(false);
            }
        };


        const fetchParentTasks = async () => {
            try {
                setLoadingParentTasks(true);
                setErrorLoadingParentTasks(null);

                const response = await fetch(`/todolist/manager/${managerId}/sprint/${sprintId}`);
                const data = await response.json();
                setParentTasks(data);
            } catch (error) {
                console.error("Error loading parent tasks:", error);
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

        if (responsibles.length === 0 || responsibles.some(r => !r.id)) {
            alert("Please assign the task to at least one team member");
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
                status: "PENDING",
                managerId: managerId,
                startDate: new Date().toISOString(),
                deadline: new Date(dueDate).toISOString(),
                description: description,
                projectId: projectId,
                sprintId: sprintId,
                estHours: estHours,
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
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || 'Failed to create task');
            }

            const taskId = await response.json();
            console.log("Task created successfully with ID:", taskId);

            setIsAssigning(true);
            try {
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
                        const errorData = await assignation.json().catch(() => ({}));
                        throw new Error(errorData.message || `Failed to assign task to employee ID: ${responsible.id}`);
                    }
                }
            
                alert("Task created and assigned successfully!");
                onClose();
                if (onTaskCreated) {
                    onTaskCreated();
                }
            } catch (error) {
                console.error("Error assigning task:", error);
                alert(`Error assigning task: ${error.message}`);
            } finally {
                setIsAssigning(false);
            }

            setIsAddingSubTask(true);
            try {
                const subTaskData = {
                    id:{
                    toDoItemId: parent,
                    subToDoItemId: taskId
                    }
                };

                const response = await fetch('/subToDoItems', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(subTaskData)
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || 'Failed to create subtask');
                }

                alert("Subtask created successfully!");
            } catch (error) {
                console.error("Error creating subtask:", error);
                setErrorMessage(error.message);
            } finally {
                setIsAddingSubTask(false);
            }
        } catch (error) {
            console.error("Error creating task:", error);
            setErrorMessage(error.message);
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
                        {errorMessage && <p className="tc-error-message">{errorMessage}</p>}

                        <div className="tc-form-group">
                        <label>Select parent Task</label>
                        {loadingParentTasks ? (
                            <p>Loading parent tasks...</p>
                        ) : errorLoadingParentTasks ? (
                            <p>Error loading parent tasks</p>
                        ) : parentTasks.length === 0 ? (
                            <p className="tc-error-message">No parent tasks available</p>
                        ) : (
                            <select
                            value={parent}
                            onChange={(e) => setParent(e.target.value)}
                            required
                        >
                            <option value ="">Select parent task</option>
                            {parentTasks.map((task) => (
                                <option key={task.id} value={task.id}>
                                    {task.name}
                                </option>
                            ))}
                            </select>
                        )}
                            
                        </div>


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
    <div className="responsible-field-container">
        {responsibles.map((responsible, index) => (
            <div key={index} className="responsible-field">
                <select
                    value={responsible.id}
                    onChange={(e) => handleResponsibleChange(index, e.target.value)}
                    required
                >
                    <option value="">Select Team Member</option>
                    {employees.map(employee => (
                        <option key={employee.id} value={employee.id}>
                            {employee.name}
                        </option>
                    ))}
                </select>
                <button
                    type="button"
                    onClick={() => removeResponsibleField(index)}
                    className="remove-button"
                >
                    Remove
                </button>
            </div>
        ))}
    </div>
    <button type="button" onClick={addResponsibleField} className="add-button">
        Add Another
    </button>
</div>

                        <div className="tc-form-group">
                            <label>Due Date</label>
                            <input
                                type="date"
                                value={dueDate}
                                onChange={(e) => setDueDate(e.target.value)}
                                min={new Date().toISOString().split('T')[0]}
                                required
                            />
                        </div>

                        <div className="tc-form-group">
                            <label>Estimated Hours</label>
                            <input
                                type="number"
                                min="0.5"
                                step="0.5"
                                max="4"
                                value={estHours}
                                onChange={(e) => setEstHours(e.target.value)}
                                required
                            />
                        </div>

                        <div className="tc-form-group">
                            <label>Description</label>
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
                                disabled={isSubmitting || isAssigning || loadingEmployees || responsibles.length === 0}
                            >
                                {isSubmitting ? 'Creating...' : isAssigning ? 'Assigning...' : 'Create Task'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}