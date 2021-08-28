
import "bootstrap/dist/css/bootstrap.min.css";
import React from "react";
import { Switch, Route} from "react-router-dom";
import "./App.css";
import Login from "./components/Login";
import Register from "./components/Register";
import Profile from "./components/Profile";
import BoardUser from "./components/BoardUser";
import BoardAdmin from "./components/BoardAdmin";
import Home from './Home';
import PageNotFound from './PageNotFound';
import Header from "./Header";



function App() {
        return (
            <div id="appContent">
                    <Switch>
                        <Route exact path="/" component={Login} />
                        <Route exact path="/register" component={Register} />
                        <Route exact path="/profile" component={Profile} />
                        <Route path="/user" component={BoardUser} />
                        <Route path="/admin" component={BoardAdmin} />
                        <Route path="/home" component={Home} />
                        <Route component={PageNotFound} />
                    </Switch>
            </div>
        );
}
export default App;

