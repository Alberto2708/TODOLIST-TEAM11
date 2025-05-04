import { useState, useEffect } from 'react';
import "../styles/ModelTask.css";
//import ToDoItem from './ToDoItem';

export default function ModalTask({ setOpen, handleDoneClick, task }) {
    const [open, setModalOpen] = useState(true);

    useEffect(() => {
        console.log("ModalTask opened with task details:", task);
    }, [task]);

    const markAsDone = (toDoItemId) => {
      try{
        fetch(`/todolist/complete/${toDoItemId} `, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
        });

      } catch (error) {
        console.error("Error marking task as done:", error);
      }
    }

    const closeModal = () => {
        console.log("Closing ModalTask");
        setModalOpen(false);
        setOpen(false);
    };

    const handleDone = () => {
        console.log("Done button clicked in ModalTask");
        markAsDone(task.id);
        handleDoneClick();
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
                                    Name: {task.name} <br />
                                    Status: {task.status}<br />
                                    Description: {task.description} <br />
                                    Due date: {task.deadline}
                                </p>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button onClick={closeModal} className="btn btn-cancel">
                                Cancel
                            </button>
                            <button onClick={handleDone} className="btn btn-done">
                                Done
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        )
    );
}