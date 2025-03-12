import { useState } from 'react';
import "../styles/ModelTask.css";

export default function ModalTask({ setOpen, handleDoneClick }) {
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
              <div className="icon-container">
                <span className="info-symbol">ⓘ</span>
              </div>
              <div className="header-text">
                <h3 className="modal-title">Task Details</h3>
                <p className="modal-description">
                  Name: <br />
                  Responsible: <br />
                  Description: <br />
                  Due date:
                </p>
              </div>
            </div>
            <div className="modal-footer">
              <button onClick={handleDoneClick} className="btn btn-danger">
                Done
              </button>
              <button onClick={closeModal} className="btn btn-cancel">
                Cancel
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  );
}