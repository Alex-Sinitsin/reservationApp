import React, {useState, useRef, useEffect} from "react";
import validator from 'validator';
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";

import AuthService from "../services/auth.service";
import Header from "./Header";
import "../Login.css";


const required = value => {
  if (!value) {
    return (
      <div className="invalid-feedback d-block">
        Заполните поле
      </div>
    );
  }
};

const emailCheck = (value) => {
  if (!validator.isEmail(value)) {
    return (
      <div className="invalid-feedback d-block">
        {value} - недействительный Email
      </div>
    );
  }
};

const Login = (props) => {
  const form = useRef(null);
  const checkBtn = useRef(null);

  useEffect(() => {
    document.title = 'Пользователь';
  });

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");


  const onChangeEmail = (e) => {
    const email = e.target.value;
    setEmail(email);
  }

  const onChangePassword = (e) => {
    const password = e.target.value;
    setPassword(password);
  };

  const handleLogin = (e) => {
    e.preventDefault();

    setMessage("");
    setLoading(true);

    form.current.validateAll();

    if (checkBtn.current.context._errors.length === 0) {
      AuthService.login(email, password).then(
        () => {
          props.history.push("/home");
          window.location.reload();
        },
        error => {
          const resMessage = error.response.data.message
          setMessage(resMessage);
          setLoading(false)
        }
      );
    } else {
      setLoading(false);
    }
  };

  return (
    <section className="page-wrap">
      <header className="header">
        <Header/>
      </header>
      <div className="container loginContainer">
        <div className="row">
          <div className="col-md-12">
            <div className="card card-container login-dorm-card">
              <img
                src="/avatar.png"
                alt="profile-img"
                className="login-form-img"
              />

              {message ? (
                <div className="form-group">
                  <div className="alert alert-danger" role="alert">
                    {message}
                  </div>
                </div>
              ) : null}

              <Form className="login-form" onSubmit={handleLogin} ref={form}>
                <div className="form-group">
                  <label htmlFor="email">E-mail:</label>
                  <Input
                    type="text"
                    className="form-control"
                    name="email"
                    id="email"
                    value={email}
                    onChange={onChangeEmail}
                    validations={[required, emailCheck]}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="Password">Пароль:</label>
                  <Input
                    type="password"
                    className="form-control"
                    name="password"
                    id="password"
                    value={password}
                    onChange={onChangePassword}
                    validations={[required]}
                  />
                </div>


                <div className="form-group">
                  <button className="btn loginBtn btn-primary btn-block" disabled={loading}>
                    {loading && (
                      <span className="spinner-border spinner-border-sm me-2"></span>
                    )}
                    <span>Войти</span>
                  </button>
                </div>

                <CheckButton style={{display: "none"}} ref={checkBtn}/>
              </Form>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

export default Login;