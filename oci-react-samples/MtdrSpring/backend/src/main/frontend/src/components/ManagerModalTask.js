import { useState } from 'react';
import "../styles/ModelTask.css";

export default function ManagerModalTask({ setOpen, handleDoneClick, task }) {
  const [open, setModalOpen] = useState(true);

  const closeModal = () => {
    setModalOpen(false);
    setOpen(false); 
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
                  <button className="btn btn-cancel">Edit</button>
                </div>
                <div className="modal-line">
                  Status: {task.status}<br />
                  <button className="btn btn-cancel">Edit</button>
                </div>
                <div className="modal-line">
                  Description: {task.description} <br />
                  <button className="btn btn-cancel">Edit</button>
                </div>
                <div className="modal-line">
                  Due date: {task.deadline}<br />
                  <button className="btn btn-cancel">Edit</button>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-danger">
                Save
              </button>
              <button className="btn btn-cancel">
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  );
}