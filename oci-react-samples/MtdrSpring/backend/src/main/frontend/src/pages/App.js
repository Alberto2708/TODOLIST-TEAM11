import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/home" element={<h2>Welcome to Home Page</h2>} />
      </Routes>
    </Router>
  );
};

export default App;
