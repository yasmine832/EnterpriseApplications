import React, { useEffect, useState } from "react";
import axios from "axios";
import './App.css';

function App() {

  //initialize state variable to hold lsit of messages fetched from backend
  const [messages, setMessages] = useState([]);


    useEffect(() => {
      //fetch data from the backend
        const fetchMessages = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/hello/")  // use axios to make get request to api/hello endpoint on spring boot backend
                setMessages(response.data)// set the fetched data (response.data) to the message state
            } catch(error) {
                console.error("Error fetching data:", error)
            }
    };

    fetchMessages(); // Call fetchMessages on component mount
    }, []); // Empty dependency array ensures the effect runs only once on mount



    //render messages
  return (
      <div className="App">
        <header className="App-header">
          <h1>Hello World Messages</h1>
          <ul>
            {messages.map(msg => ( //map over the messages array and render each message in a ul element
                <li key={msg.id}>{msg.message}</li>
            ))}
          </ul>
        </header>
      </div>
  );
}

export default App;
