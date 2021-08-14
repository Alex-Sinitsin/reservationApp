import React, { useState  } from 'react';

export default class Alarm extends React.Component {
  constructor() {
    super();
    this.state = {
      currentTime: '',
      alarmTime: AlarmTime
    };
  }

  componentDidMount(){
    this.clock = setInterval(() => this.setState({
                       currentTime: new Date().toLocaleTimeString('ru', { hour12: false })
                     }), 1000)
    this.interval = setInterval(
      () => this.checkAlarmClock(), 1000)
  }

  checkAlarmClock(){
      if(this.state.currentTime === this.state.alarmTime) {
        alert("its time!");
      } else {
        console.log("not yet");
      }
    }

  render() {
    return (
      <div>
        <h2>{this.alarmMessage}</h2>
      </div>
    );
  }
}
