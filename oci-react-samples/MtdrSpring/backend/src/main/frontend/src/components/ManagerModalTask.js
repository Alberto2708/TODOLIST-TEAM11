import { useState } from 'react';
import "../styles/ModelTask.css";

export default function ManagerModalTask({ setOpen, handleDeleteClick, task }) {
  const [open, setModalOpen] = useState(true);

  const closeModal = () => {
    setModalOpen(false);
    setOpen(false); 
  };

  const deleteTask = (toDoItemId) => {
    console.log("Delete task:", toDoItemId);
    try{
      fetch(`todolist/${toDoItemId}`,{
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });
    }
    catch (error) {
      console.error("Error deleting task:", error);
    }
    closeModal();
  }

  const handleDelete = () => {
    console.log("Delete button clicked in ModalTask");
    deleteTask(task.id);
    handleDeleteClick(); 
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