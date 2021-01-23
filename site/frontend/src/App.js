import React, { Component } from "react";
import "./App.css";
import { Route, Switch, Redirect, withRouter } from "react-router-dom";
import TopNavbar from "./Components/TopNavbar";
import HomePage from "./Components/HomePage";
import ServerPage from "./Components/ServerPage";
import ServerVersionRouter from "./Components/Docs/ServerVersionRouter.js";
import AlarmPage from "./Components/AlarmPage";
import HelpPage from "./Components/HelpPage";
import ContributePage from "./Components/ContributePage";

class App extends Component {


  constructor(props) {
    super(props);
    this.state = {
      stack: "chirpstack"
    };
    this.setNewStack = this.setNewStack.bind(this);
  }

  setNewStack(newStack){
    this.setState({
      stack: newStack,
    });
  }  

  render() {

    return (
      <>
        <div className="App-header">
          <TopNavbar stackStatus={this.state.stack} setNewStack={this.setNewStack}></TopNavbar>
        </div>
        <div className="App-body" style={{ paddingTop: "50px" }}>
          <Switch>

            <Route exact path="/" render={props => (
              <HomePage {...props} stackStatus={this.state.stack} />)}/>
            <Route path="/alarms" render={props => (
              <AlarmPage {...props} stackStatus={this.state.stack} />)}/>
            <Route path="/server" render={props => (
              <ServerPage {...props} stackStatus={this.state.stack} />)}/>
            <Route exact path="/help" render={props => (
              <HelpPage {...props} stackStatus={this.state.stack}/>)}/>
            <Route exact path="/contribute" render={props => (
              <ContributePage {...props} stackStatus={this.state.stack}/>)}/>
            <Route path="/docs/server" render={props => (
              <ServerVersionRouter {...props} stackStatus={this.state.stack}/>)}/>
            <Redirect to="/" />
          </Switch>
        </div>
      </>
    );
  }
}

export default withRouter(App);
