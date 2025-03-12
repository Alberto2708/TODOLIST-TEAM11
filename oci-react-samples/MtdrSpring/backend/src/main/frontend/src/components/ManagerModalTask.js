import { useState } from 'react';
import "../styles/ModelTask.css";

export default function ManagerModalTask({ setOpen, handleDoneClick }) {
  const [open, setModalOpen] = useState(true);

  const closeModal = () => {
    setModalOpen(false);
    setOpen(false); // Close modal without triggering anything else
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
                <div className="modal-description">
                  <div className="modal-line">
                    Name: <br />
                    <button className="btn btn-cancel">Edit</button>
                  </div>
                  <div className="modal-line">
                    Responsible: <br />
                    <button className="btn btn-cancel">Edit</button>
                  </div>
                  <div className="modal-line">
                    Description: <br />
                    <button className="btn btn-cancel">Edit</button>
                  </div>
                  <div className="modal-line">
                    Due date: <br />
                    <button className="btn btn-cancel">Edit</button>
                  </div>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              {/* Save button now just closes the modal */}
              <button onClick={handleDoneClick} className="btn btn-danger">
                Save
              </button>
              {/* Cancel button just closes the modal */}
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