import { useState } from 'react';
import "../styles/ModelTask.css";

export default function ManagerModalTask({ setOpen, handleDeleteClick, task }) {
  const [open, setModalOpen] = useState(true);

  const closeModal = () => {
    setModalOpen(false);
    setOpen(false); 
  };

  const deleteTask = async (toDoItemId) => {
    console.log("Delete task:", toDoItemId);
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
      handleDeleteClick(toDoItemId); // Trigger parent to refresh only after success
      closeModal(); // Close modal after deletion
    } catch (error) {
      console.error("Error deleting task:", error);
      alert("Failed to delete task. Please try again.");
    }
  };

  const handleDelete = () => {
    console.log("Delete button clicked in ModalTask");
    deleteTask(task.id);
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
                  Name: {task.name}<br />
                </div>
                <div className="modal-line">
                  Status: {task.status}<br />
                </div>
                <div className="modal-line">
                  Description: {task.description} <br />
                </div>
                <div className="modal-line">
                  Due date: {task.deadline}<br />
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-danger">
                Save
              </button>
              <button className="btn btn-cancel" onClick={handleDelete}>
                Delete
              </button>
              <button className="btn btn-cancel">
                Edit task
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  );
}
