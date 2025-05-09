import { useState } from 'react';
import "../styles/ModelTask.css";

export default function ManagerModalTask({ setOpen, handleDeleteClick, task, onEditSaved }) {
  const [open, setModalOpen] = useState(true);
  const [isEditMode, setIsEditMode] = useState(false);

  // Editable fields
  const [editedName, setEditedName] = useState('');
  const [editedStatus, setEditedStatus] = useState('');
  const [editedDescription, setEditedDescription] = useState('');
  const [editedDeadline, setEditedDeadline] = useState('');
  
  

  const closeModal = () => {
    setModalOpen(false);
    setOpen(false);
  };

  const deleteTask = async (toDoItemId) => {
    try {
      const response = await fetch(`todolist/${toDoItemId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Failed to delete task: ${response.statusText}`);
      }

      console.log("Task successfully deleted.");
      handleDeleteClick(toDoItemId);
      closeModal();
    } catch (error) {
      console.error("Error deleting task:", error);
      alert("Failed to delete task. Please try again.");
    }
  };

  const handleDelete = () => {
    console.log("Delete button clicked in ModalTask");
    deleteTask(task.id);
  };

  const handleSaveEdit = async () => {
    try {
      
      const updatedTask = {
        name: editedName,
        status: editedStatus,
        description: editedDescription,
        deadline: new Date(editedDeadline).toISOString()
      };
      

      const response = await fetch(`todolist/${task.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedTask),
      });

      if (!response.ok) {
        throw new Error('Failed to update task');
      }

      console.log("Task updated successfully");
      setIsEditMode(false);
      onEditSaved();
      closeModal();
    } catch (error) {
      console.error("Update failed:", error);
      alert("Failed to update task. Please try again.");
    }
  };

  return (
    open && (
      <div className="modal-overlay">
        <div className="modal-container">
          <div className="modal-content">
            <div className="modal-header">
              <div className="header-text">
                <h3 className="modal-title">Task Details</h3>
              </div>
              <div className="close-btn" onClick={closeModal}>
                <span className="cross-symbol">x</span>
              </div>
            </div>

            <div className="modal-body">
              <div className="modal-description">
                <div className="modal-line">
                  Name: {isEditMode ? (
                    <input
                      value={editedName}
                      onChange={(e) => setEditedName(e.target.value)}
                    />
                  ) : task.name}
                </div>
                <div className="modal-line">
                  Status: {isEditMode ? (
                    <select
                      value={editedStatus}
                      onChange={(e) => setEditedStatus(e.target.value)}
                    >
                      <option value="PENDING">PENDING</option>
                      <option value="COMPLETED">COMPLETED</option>
                    </select>
                  ) : task.status}
                </div>
                <div className="modal-line">
                  Description: {isEditMode ? (
                    <textarea
                      value={editedDescription}
                      onChange={(e) => setEditedDescription(e.target.value)}
                    />
                  ) : task.description}
                </div>
                <div className="modal-line">
                  Due date: {isEditMode ? (
                    <input
                      type="date"
                      value={editedDeadline}
                      onChange={(e) => setEditedDeadline(e.target.value)}
                    />
                  ) : task.deadline}
                </div>
              </div>
            </div>

            <div className="modal-footer">
              {isEditMode ? (
                <button className="btn btn-danger" onClick={handleSaveEdit}>
                  Save
                </button>
              ) : (
                <button
                className="btn btn-cancel"
                onClick={() => {
                  setEditedName(task.name);
                  setEditedStatus(task.status);
                  setEditedDescription(task.description);
                  setEditedDeadline(new Date(task.deadline).toISOString().slice(0, 10));
                  setIsEditMode(true);
                }}
              >
                Edit task
              </button>

              )}
              <button className="btn btn-cancel" onClick={handleDelete}>
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  );
}
