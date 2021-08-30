import React from "react";
import AuthService from "../services/auth.service";
import Header from "./Header";

const Profile = () => {
    const currentUser = AuthService.getCurrentUser();

    return (
        <div className="col-md-12 mb-3">
                <Header/>
            <p>
                <strong>Token:</strong> {currentUser.accessToken.substring(0, 20)} ...{" "}
                {currentUser.accessToken.substr(currentUser.accessToken.length - 20)}
            </p>
            <p>
                <strong>Id:</strong> {currentUser.id}
            </p>
            <p>
                <strong>Email:</strong> {currentUser.email}
            </p>
            <strong>Authorities:</strong>
        </div>
    );
};

export default Profile;